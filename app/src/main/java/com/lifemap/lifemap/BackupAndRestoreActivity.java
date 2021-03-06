package com.lifeMap.lifemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lifeMap.lifemap.R;
import com.lifeMap.lifemap.DIY_Kit.InfoItemBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupAndRestoreActivity extends AppCompatActivity {

    private File dir = Environment.getExternalStorageDirectory();
    private File dataFile = new File(dir, "lifeMap");
    private String db_dir = "/data/data/com.lifeMap.lifemap/databases/";
    private String markerImage_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/markerImage/";
    private String db_file_name = "pinDB";
    private static int BUFFER_SIZE = 1444;
    private static String parentPath = "";

    // 鎖住操作返回動作
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
            } else {
                onBackPressed(); // 是其他按鍵則再Call Back方法
            }
        }
        return false;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mianliner);
        View view= LayoutInflater.from(BackupAndRestoreActivity.this).inflate(R.layout.main_bar, null);

        TextView content = (TextView) view.findViewById(R.id.tvContent);
        content.setText(getApplicationContext().getResources().getString(R.string.backup_guideContent));
        InfoItemBar mbar=new InfoItemBar(BackupAndRestoreActivity.this, getApplicationContext().getResources().getString(R.string.backup_guideTitle));
        mbar.addView(view);
        mbar.setShow(false);
        mLinearLayout.addView(mbar);

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_backupAndRestore);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 執行備分
    public void doBackup(View view) {
        String result = "";
        try {

            // Check LifeMap Folder Exist
            String dirMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/";
            File mainFile = new File(dirMain);

            // 資料夾是否存在，不存在則建立資料夾
            if(!mainFile.exists()) {
                mainFile.mkdirs();
            }

            // Backup DB
            String toDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/pinDB.db";
            File file = new File(toDir);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.flush();
            outputStream.close();
            goCopyFile(db_dir+db_file_name, toDir);

            // Backup MarkerImage
            String toDir_markerImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/markerImage/";
            goCopyFolder(markerImage_dir, toDir_markerImage);

            zip(dirMain, Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/", "backup.zip", false);

            try {
                //exporting
                Context context = getApplicationContext();
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup.zip");
                Uri path = FileProvider.getUriForFile(context, "com.lifeMap.lifemap.fileprovider", filelocation);
                this.grantUriPermission(getPackageName(), path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                fileIntent.setType("application/zip");
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                startActivity(fileIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            result = getApplicationContext().getResources().getString(R.string.backup_success);
            Toast toast = Toast.makeText(BackupAndRestoreActivity.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        } catch (Exception e) {
            e.printStackTrace();
            result = getApplicationContext().getResources().getString(R.string.backup_fail);
            Toast toast = Toast.makeText(BackupAndRestoreActivity.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    // 執行還原
    public void doRestore(View view) {

        String result = "";
        // Check LifeMap Folder Exist
        String dirMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup";
        File mainFile = new File(dirMain);

        // 資料夾是否存在，不存在則跳出提示訊息
        if(!mainFile.exists()) {
            result = getApplicationContext().getResources().getString(R.string.lifeMap_folder_not_exist);
            Toast toast = Toast.makeText(BackupAndRestoreActivity.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        } else {
            try {
                // Restore DB
                String fromDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/pinDB.db";
                goCopyFile(fromDir, db_dir + db_file_name);

                // Restore MarkerImage
                String fromDir_markerImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/markerImage/";
                goCopyFolder(fromDir_markerImage, markerImage_dir);

                result = getApplicationContext().getResources().getString(R.string.restore_success);
                Toast toast = Toast.makeText(BackupAndRestoreActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            } catch (Exception e) {
                e.printStackTrace();
                result = getApplicationContext().getResources().getString(R.string.restore_fail);
                Toast toast = Toast.makeText(BackupAndRestoreActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    // 複製文件
    public void goCopyFile(String comepath, String gopath) throws IOException {
        try {
            File wantfile = new File(comepath);
            File newfile = new File(gopath);

            InputStream in = new FileInputStream(wantfile);
            OutputStream out = new FileOutputStream(newfile);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            Log.e("copy file error", e.toString());
        }
    }

    // 整個資料夾的複製，comePath輸入想要複製的資料夾路徑，goPath是目的地路徑拉
    public void goCopyFolder(String comePath, String goPath){
        try {
            ( new File(goPath)).mkdirs(); //弄資料夾拉
            File a= new File(comePath);
            String[] file=a.list();
            File temp= null ;

            for ( int i = 0 ; i < file.length; i++) {
                if (comePath.endsWith(File.separator)){
                    temp= new File(comePath+file[i]);

                } else  {
                    temp= new File(comePath+File.separator+file[i]);
                }

                if (temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(goPath + "/" +(temp.getName()).toString());
                    byte [] b = new byte [1024];
                    int len;

                    while ( (len = input.read(b)) != - 1 ) {
                        output.write(b, 0 , len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }

                if (temp.isDirectory()){
                    //裡面還有東西要繼續幹
                    goCopyFolder(comePath+ "/" +file[i],goPath+ "/" +file[i]);
                }
            }
        }
        catch (Exception e) {
            Log.e("folder error", e.toString());
        }
    }

    // 返回
    public void goBack(View view) {
        finish();
    }

    // 檔案壓縮成ZIP
    public static boolean zip( String sourcePath, String destinationPath, String destinationFileName, Boolean includeParentFolder)  {
        new File(destinationPath ).mkdirs();
        FileOutputStream fileOutputStream ;
        ZipOutputStream zipOutputStream =  null;
        try{
            if (!destinationPath.endsWith("/")) destinationPath+="/";
            String destination = destinationPath + destinationFileName;
            File file = new File(destination);

            if (!file.exists()) file.createNewFile();

            fileOutputStream = new FileOutputStream(file);
            zipOutputStream =  new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            if (includeParentFolder)
                parentPath=new File(sourcePath).getParent() + "/";
            else
                parentPath=sourcePath;

            zipFile(zipOutputStream, sourcePath);

        }
        catch (IOException ioe){
            return false;
        }finally {
            if(zipOutputStream!=null)
                try {
                    zipOutputStream.close();
                } catch(IOException e) {}
        }
        return true;
    }

    // ZIP解壓縮
    private static void zipFile(ZipOutputStream zipOutputStream, String sourcePath) throws  IOException{
        java.io.File files = new java.io.File(sourcePath);
        java.io.File[] fileList = files.listFiles();

        String entryPath="";
        BufferedInputStream input;
        for (java.io.File file : fileList) {
            if (file.isDirectory()) {
                zipFile(zipOutputStream, file.getPath());
            } else {
                byte data[] = new byte[BUFFER_SIZE];
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                entryPath=file.getAbsolutePath().replace( parentPath,"");

                ZipEntry entry = new ZipEntry(entryPath);
                zipOutputStream.putNextEntry(entry);

                int count;
                while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }
                input.close();
            }
        }
    }
}
