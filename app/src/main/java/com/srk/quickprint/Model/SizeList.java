package com.srk.quickprint.Model;

/**
 * Created by SRk on 4/12/2018.
 */
import org.json.JSONException;
import org.json.JSONObject;

public class SizeList {
    public static int DEFAULT = 0;
    public static int MM = 2;
    public static int PIXEL = 1;
    private String descText = "";
    private String displayText = "";
    private int drawableId = 0;
    private String headText = "";
    private String priceText = "";
    private int height = 0;
    private int percentage = 70;
    private int type = 0;
    private int width = 0;

    public SizeList(int dId, String hText, String dText,String dPrice) {
        this.drawableId = dId;
        this.headText = hText;
        this.descText = dText;
        this.priceText = dPrice;

    }

    public SizeList(int width, int height, int type, int dId, String hText, String dText,String dPrice) {
        this.width = width;
        this.height = height;
        this.type = type;
        this.drawableId = dId;
        this.headText = hText;
        this.descText = dText;
        this.priceText = dPrice;

    }

    public SizeList(int width, int height, int type, int dId, int percentge, String desc, String hText, String dText) {
        this.width = width;
        this.height = height;
        this.type = type;
        this.drawableId = dId;
        this.percentage = percentge;
        this.displayText = desc;
        this.headText = hText;
        this.descText = dText;


    }

    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("width", this.width);
            obj.put("height", this.height);
            obj.put("type", this.type);
            obj.put("drawableId", this.drawableId);
            obj.put("percentage", this.percentage);
            obj.put("displayText", this.displayText);
            obj.put("headText", this.headText);
            obj.put("descText", this.descText);
            obj.put("descText", this.descText);
            obj.put("priceText", this.priceText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public int getDrawableId() {
        return this.drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getHeadText() {
        return this.headText;
    }

    public String getPriceText() {
        return this.priceText;
    }

    public String setPriceText(String priceText) {
        return this.priceText = priceText;
    }

    public void setHeadText(String headText) {
        this.headText = headText;
    }

    public String getDescText() {
        return this.descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDisplayText() {
        return this.displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
