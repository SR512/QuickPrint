package com.srk.quickprint.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by SRk on 4/11/2018.
 */

public class ExpandableList_AD extends BaseExpandableListAdapter {

    private Context context;
    private LinkedHashMap<String, List<SizeList>> expandableListDetail;
    private List<String> expandableListTitle;


    public ExpandableList_AD(Context context, List<String> expandableListTitle, LinkedHashMap<String, List<SizeList>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

    }


    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return ((List) this.expandableListDetail.get(this.expandableListTitle.get(listPosition))).size();
    }


    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return ((List) this.expandableListDetail.get(this.expandableListTitle.get(listPosition))).get(expandedListPosition);
    }



    @Override
    public long getGroupId(int listPosition) {
        return (long) listPosition;
    }


    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return (long) expandedListPosition;
    }



    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int listPosition, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(listPosition);
        if(view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view  = layoutInflater.inflate(R.layout.list_group,null);
        }

        TextView lblListHeader = (TextView) view.findViewById(R.id.lblListHeader);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
        lblListHeader.setTypeface(typeface);
        lblListHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean b, View view, ViewGroup viewGroup) {

        SizeList sizeList = (SizeList) getChild(listPosition, expandedListPosition);;

        if(view == null)
       {
           LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view  = layoutInflater.inflate(R.layout.list_item,null);
        }

        TextView lbl_header = (TextView) view.findViewById(R.id.txt_head);
        TextView lbl_desc = (TextView) view.findViewById(R.id.txt_desc);

        ((ImageView) view.findViewById(R.id.image)).setImageResource(sizeList.getDrawableId());

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");

        lbl_header.setTypeface(typeface);
        lbl_desc.setTypeface(typeface);

        lbl_header.setText(sizeList.getHeadText());
        lbl_desc.setText(sizeList.getDescText());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
