package com.lifeMap.lifemap.DIY_Kit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lifeMap.lifemap.R;

public class InfoItemBar extends LinearLayout {

    private LinearLayout mLinearLayout,mitembar;
    private ImageView Image;

    public InfoItemBar(Context mContext, String title)
    {
        super(mContext);
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_bar, this);
        this.mLinearLayout = (LinearLayout)view.findViewById(R.id.itemBar_content);
        this.mitembar=(LinearLayout) view.findViewById(R.id.itembar);
        this.Image = (ImageView) view.findViewById(R.id.item_img_title);
        TextView titleTextView = (TextView) view.findViewById(R.id.itemBar_title);
        titleTextView.setText(title + " :");

        mitembar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLinearLayout.getVisibility() == View.GONE) {
                    mLinearLayout.setVisibility(View.VISIBLE);
                    Image.setImageResource(R.drawable.item_pressed);
                } else {
                    mLinearLayout.setVisibility(View.GONE);
                    Image.setImageResource(R.drawable.item_unpressed);
                }
            }

        });
    }
    /**
     * 設置 羡項目集 是否展開
     *
     * @param isShow
     */
    public void setShow(boolean isShow) {
        if (isShow) {
            mLinearLayout.setVisibility(View.VISIBLE);
            Image.setImageResource(R.drawable.item_pressed);
        } else {
            mLinearLayout.setVisibility(View.GONE);
            Image.setImageResource(R.drawable.item_unpressed);
        }
    }
    /**
     * 添加 infoItem項目
     *
     * @param item
     */
    public void addView(View item) {

        mLinearLayout.addView(item);

    }
}
