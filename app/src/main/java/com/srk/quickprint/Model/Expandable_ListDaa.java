package com.srk.quickprint.Model;

/**
 * Created by SRk on 4/13/2018.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.srk.quickprint.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_MEDIA_PAUSE;

public class Expandable_ListDaa {

    private static final String MyPREFERENCES = "MyPrefs";
    private static SharedPreferences sharedpreferences;

    public static LinkedHashMap<String, List<SizeList>> getData(Context context) {
        Resources r = context.getResources();
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, 0);
        LinkedHashMap<String, List<SizeList>> expandableListDetail = new LinkedHashMap();
        List<SizeList> previousList = new ArrayList();
        try {
            JSONObject obj = new JSONObject(sharedpreferences.getString("prevSize", ""));
            previousList.add(new SizeList(obj.getInt("width"), obj.getInt("height"), obj.getInt("type"), obj.getInt("drawableId"), obj.getInt("percentage"), obj.getString("displayText"), obj.getString("headText"), obj.getString("priceText")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<SizeList> commonList = new ArrayList();
        commonList.add(new SizeList(51, 51, SizeList.MM, 0, r.getString(R.string.cs_h_1), r.getString(R.string.cs_d_1) + "\n" + r.getString(R.string.cs_d_11),""));
        commonList.add(new SizeList(35, 45, SizeList.MM, 0, r.getString(R.string.cs_h_2), r.getString(R.string.cs_d_1) + "\n" + r.getString(R.string.cs_d_11),""));
        commonList.add(new SizeList(51, 51, SizeList.MM, 0, r.getString(R.string.cs_h_3), r.getString(R.string.cs_d_1) + "\n" + r.getString(R.string.cs_d_11),""));

        List<SizeList> countryListIndia = new ArrayList();
        countryListIndia.add(new SizeList(51, 51, SizeList.MM, R.drawable.ic_india_flag, 65, "2in, 2in, 1.4in-1.6in", r.getString(R.string.cc_h_india1), r.getString(R.string.cc_d_india1)));
        countryListIndia.add(new SizeList(51, 51, SizeList.MM, R.drawable.ic_india_flag, 65, "2in, 2in, 1in-1 3/8in", r.getString(R.string.cc_h_india2), r.getString(R.string.cc_d_india2)));
        countryListIndia.add(new SizeList(35, 35, SizeList.MM, R.drawable.ic_india_flag, 70, "35mm, 35mm, 24-26mm", r.getString(R.string.cc_h_india3), r.getString(R.string.cc_d_india3)));
        countryListIndia.add(new SizeList(25, 35, SizeList.MM, R.drawable.ic_india_flag, 70, "25mm, 35mm, 24-26mm", r.getString(R.string.cc_h_india4), r.getString(R.string.cc_d_india4)));

        List<SizeList> socialList = new ArrayList();
        socialList.add(new SizeList(612, 612, SizeList.PIXEL, 0, r.getString(R.string.sn_h_insta), r.getString(R.string.sn_d_insta),""));
        socialList.add(new SizeList(851, 315, SizeList.PIXEL, 0, r.getString(R.string.sn_h_fb), r.getString(R.string.sn_d_fb),""));
        socialList.add(new SizeList(736, 736, SizeList.PIXEL, 0, r.getString(R.string.sn_h_pinterest), r.getString(R.string.sn_d_pinterest),""));
        socialList.add(new SizeList(2120, 1192, SizeList.PIXEL, 0, r.getString(R.string.sn_h_google), r.getString(R.string.sn_d_google),""));
        socialList.add(new SizeList(646, 220, SizeList.PIXEL, 0, r.getString(R.string.sn_h_linkedin), r.getString(R.string.sn_d_linkedin),""));
        socialList.add(new SizeList(520, 260, SizeList.PIXEL, 0, r.getString(R.string.sn_h_twitter), r.getString(R.string.sn_d_twitter),""));

        expandableListDetail.put(r.getString(R.string.list_title_1), previousList);
        expandableListDetail.put(r.getString(R.string.list_title_4), socialList);
        expandableListDetail.put(r.getString(R.string.list_title_3), countryListIndia);

        return expandableListDetail;
    }

    public static ArrayList<SizeList> getPageSizeList(Context context) {
        Resources r = context.getResources();
        ArrayList<SizeList> pageSiseList = new ArrayList();
        pageSiseList.add(new SizeList(89, KEYCODE_MEDIA_PAUSE, SizeList.MM, 0, r.getString(R.string.ps_h_1), r.getString(R.string.ps_d_1),r.getString(R.string.ps_p_1)));
        pageSiseList.add(new SizeList(102, 152, SizeList.MM, 0, r.getString(R.string.ps_h_2), r.getString(R.string.ps_d_2),r.getString(R.string.ps_p_2)));
        pageSiseList.add(new SizeList(KEYCODE_MEDIA_PAUSE, 178, SizeList.MM, 0, r.getString(R.string.ps_h_3), r.getString(R.string.ps_d_3),r.getString(R.string.ps_p_3)));
        pageSiseList.add(new SizeList(152, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, SizeList.MM, 0, r.getString(R.string.ps_h_4), r.getString(R.string.ps_d_4),r.getString(R.string.ps_p_4)));
        pageSiseList.add(new SizeList(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, 254, SizeList.MM, 0, r.getString(R.string.ps_h_5), r.getString(R.string.ps_d_5),r.getString(R.string.ps_p_5)));
        pageSiseList.add(new SizeList(210, 297, SizeList.MM, 0, r.getString(R.string.ps_h_6), r.getString(R.string.ps_d_6),r.getString(R.string.ps_p_6)));
        pageSiseList.add(new SizeList(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, 305, SizeList.MM, 0, r.getString(R.string.ps_h_7), r.getString(R.string.ps_d_7),r.getString(R.string.ps_p_7)));
        pageSiseList.add(new SizeList(254, 305, SizeList.MM, 0, r.getString(R.string.ps_h_8), r.getString(R.string.ps_d_8),r.getString(R.string.ps_p_8)));
        pageSiseList.add(new SizeList(90, 205, SizeList.MM, 0, r.getString(R.string.ps_h_9), r.getString(R.string.ps_d_9),r.getString(R.string.ps_p_9)));
        pageSiseList.add(new SizeList(100, 148, SizeList.MM, 0, r.getString(R.string.ps_h_10), r.getString(R.string.ps_d_10),r.getString(R.string.ps_p_10)));
        pageSiseList.add(new SizeList(120, 235, SizeList.MM, 0, r.getString(R.string.ps_h_11), r.getString(R.string.ps_d_11),r.getString(R.string.ps_p_11)));
        return pageSiseList;
    }
}