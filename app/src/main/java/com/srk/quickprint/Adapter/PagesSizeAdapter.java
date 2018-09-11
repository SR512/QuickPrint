package com.srk.quickprint.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import java.util.ArrayList;

public class PagesSizeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SizeList> mDataSource;

    public PagesSizeAdapter(Context context, ArrayList<SizeList> items) {
        this.mContext = context;
        this.mDataSource = items;
    }

    public int getCount() {
        return this.mDataSource.size();
    }

    public Object getItem(int position) {
        return this.mDataSource.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SizeList sizeInfo = (SizeList) getItem(position);
        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Light.otf");


        TextView txt_head = (TextView) convertView.findViewById(R.id.txt_head);
        txt_head.setTypeface(typeface);

        TextView txt_desc = (TextView) convertView.findViewById(R.id.txt_desc);
        txt_desc.setTypeface(typeface);

        TextView txt_price = (TextView) convertView.findViewById(R.id.txt_price);
        txt_desc.setTypeface(typeface);

        ((ImageView) convertView.findViewById(R.id.image)).setImageResource(sizeInfo.getDrawableId());

        txt_head.setText(sizeInfo.getHeadText());
        txt_desc.setText(sizeInfo.getDescText());
        txt_price.setText(sizeInfo.getPriceText());

        return convertView;
    }
}