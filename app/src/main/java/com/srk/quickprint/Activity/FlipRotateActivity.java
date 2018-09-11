package com.srk.quickprint.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srk.quickprint.Adapter.ExpandableList_AD;
import com.srk.quickprint.Model.Expandable_ListDaa;
import com.srk.quickprint.Custom_Lib.ImageUtils;
import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FlipRotateActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String MyPREFERENCES = "FlipRotate";
    public static boolean isCustomAdded = false;
    public static SizeList sizeList;
    public static Bitmap bitmap;
    SharedPreferences sharedpreferences;
    LinkedHashMap<String, List<SizeList>> expandable_listDaa;
    ImageView imageView;
    android.support.v7.widget.Toolbar toolbar, toolbar_chooseSize;
    TextView toolBartitle, toolChooseSize;
    private ExpandableListView expandableListView;
    private ExpandableList_AD expandableList_ad;
    private List<String> expandableListTitle;
    private EditText editTextHeight, editTextWidth;
    private Uri imageUri;
    private int maxCMheight;
    private int maxCMwidth;
    private int maxInchHeight;
    private int maxInchWidth;
    private int maxMMheight;
    private int maxMMwidth;
    private int maxPixelHeight;
    private int maxPixelWidth;
    private int screenHeight = 0;
    private int screenWidth = 0;
    private int selection = 0;
    private int centerRelHeight;
    private int centerRelWidth;
    private String widthText = "";
    private String heightText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_flip_rotate);

        this.sharedpreferences = getSharedPreferences(MyPREFERENCES, 0);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        isCustomAdded = false;

        this.screenWidth = displaymetrics.widthPixels;
        this.screenHeight = displaymetrics.heightPixels;
        this.maxPixelWidth = this.screenWidth;
        this.maxPixelHeight = this.screenHeight;
        this.maxMMwidth = this.screenWidth / 12;
        this.maxMMheight = this.screenHeight / 12;
        this.maxCMwidth = this.screenWidth / 120;
        this.maxCMheight = this.screenHeight / 120;
        this.maxInchWidth = (int) (((double) this.screenWidth) / 304.79999999999995d);
        this.maxInchHeight = (int) (((double) this.screenHeight) / 304.79999999999995d);

        this.centerRelWidth = displaymetrics.widthPixels;
        this.centerRelHeight = displaymetrics.widthPixels;

        Intent intent = getIntent();

        imageView = (ImageView) findViewById(R.id.imgMain);
        imageUri = intent.getData();

        Log.d("Image--",imageUri.toString());


        try {

            bitmap = ImageUtils.getResampleImageBitmap(this.imageUri, this, this.centerRelWidth);


            if (bitmap.getWidth() > this.centerRelWidth || bitmap.getHeight() > this.centerRelHeight || (bitmap.getWidth() < this.centerRelWidth && bitmap.getHeight() < this.centerRelHeight)) {


                bitmap = ImageUtils.resizeBitmap(bitmap, this.centerRelWidth, this.screenHeight);

            }
            if (bitmap != null) {
                this.imageView.setImageBitmap(bitmap);
            }

        } catch (OutOfMemoryError outOfMemoryError) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_flip);
        this.setSupportActionBar(toolbar);


        toolBartitle = (TextView) findViewById(R.id.toolbar_title);
        toolBartitle.setText("Flip & Rotate");

        showStandardRatioDialog();

    }

    @Override
    public void onClick(View view) {

        Matrix matrix;

        switch (view.getId()) {

            case R.id.btnLeft:
                matrix = new Matrix();
                matrix.postRotate(270.0f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                this.imageView.setImageBitmap(bitmap);
                return;


            case R.id.btnRight:
                matrix = new Matrix();
                matrix.postRotate(90.0f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                this.imageView.setImageBitmap(bitmap);
                return;

            case R.id.btnFlipH:
                matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                this.imageView.setImageBitmap(bitmap);
                return;

            case R.id.btnFlipV:
                matrix = new Matrix();
                matrix.preScale(1.0f, -1.0f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                this.imageView.setImageBitmap(bitmap);
                return;

            case R.id.btnDone:
                startActivity(new Intent(this, StraightenActivity.class));

                return;

            case R.id.btn_back:
                finish();
                return;

            default:
                return;
        }
    }

    private void showStandardRatioDialog() {

        final Dialog dialog = new Dialog(this, R.style.AppTheme);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.choose_size_menu);

        toolbar_chooseSize = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_choose_size);
        this.setSupportActionBar(toolbar);

        this.expandableListView = (ExpandableListView) dialog.findViewById(R.id.expandableListView);

        this.expandable_listDaa = Expandable_ListDaa.getData(this);

        this.expandableListTitle = new ArrayList(this.expandable_listDaa.keySet());

        expandableList_ad = new ExpandableList_AD(this, this.expandableListTitle, this.expandable_listDaa);

        this.expandableListView.setAdapter(this.expandableList_ad);

        this.expandableListView.collapseGroup(0);
        this.expandableListView.collapseGroup(1);
        this.expandableListView.expandGroup(2);

        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_up_slide;
        dialog.show();

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FlipRotateActivity.this.expandableListView.collapseGroup(0);
                FlipRotateActivity.this.expandableListView.collapseGroup(1);
                FlipRotateActivity.this.expandableListView.collapseGroup(2);

            }
        });

        dialog.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                FlipRotateActivity.this.showCustomeRatio();

            }
        });

        this.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                FlipRotateActivity.sizeList = (SizeList) expandableListView.getExpandableListAdapter().getChild(i, i1);

                SharedPreferences.Editor edit = FlipRotateActivity.this.sharedpreferences.edit();
                edit.putString("prevSize", FlipRotateActivity.sizeList.toString());
                edit.commit();
                dialog.dismiss();
                return false;
            }
        });

    }

    public void showCustomeRatio() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.custom_ratio);
        this.editTextWidth = (EditText) dialog.findViewById(R.id.dimen_width);
        this.editTextHeight = (EditText) dialog.findViewById(R.id.dimen_height);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Montserrat-Light.otf");
        editTextHeight.setTypeface(typeface);
        editTextWidth.setTypeface(typeface);

        TextView headerText = (TextView) dialog.findViewById(R.id.headertext);
        headerText.setTypeface(typeface);

        Button btnok = (Button) dialog.findViewById(R.id.button_ok);
        Button btnCancle = (Button) dialog.findViewById(R.id.button_cancel);

        btnok.setTypeface(typeface);
        btnCancle.setTypeface(typeface);


        final Spinner spinner = (Spinner) dialog.findViewById(R.id.unit_spinner);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        spinner.setSelection(this.selection);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter(this, R.layout.spiner_list, new String[]{getResources().getString(R.string.pixels), getResources().getString(R.string.milli), getResources().getString(R.string.centi), getResources().getString(R.string.inch)});
        unitAdapter.setDropDownViewResource(R.layout.spiner_list);
        spinner.setAdapter(unitAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String strWidth = FlipRotateActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = FlipRotateActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    FlipRotateActivity.this.inValidateWidthText(Float.parseFloat(strWidth), selectedItemPosition);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {
                    FlipRotateActivity.this.inValidateHeightText(Float.parseFloat(strHeight), selectedItemPosition);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strWidth = FlipRotateActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = FlipRotateActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    FlipRotateActivity.this.inValidateWidthText(Float.parseFloat(strWidth), selectedItemPosition);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {
                    FlipRotateActivity.this.inValidateHeightText(Float.parseFloat(strHeight), selectedItemPosition);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
            }

            public void afterTextChanged(Editable editable) {
            }
        };

        this.editTextWidth.addTextChangedListener(textWatcher);
        this.editTextHeight.addTextChangedListener(textWatcher);

        ((Button) dialog.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String strWidth = FlipRotateActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = FlipRotateActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    float width = Float.parseFloat(strWidth);
                    float height = Float.parseFloat(strHeight);
                    Log.i("testing", "Width : " + width + "  and Height : " + height + " Selected Postion : " + selectedItemPosition);

                    if (selectedItemPosition == 0 && (FlipRotateActivity.this.inValidateWidthText(width, selectedItemPosition) & FlipRotateActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList((int) width, (int) height, SizeList.PIXEL, 0, "PIXEL", "PIXEL","");
                        dialog.dismiss();
                        FlipRotateActivity.this.widthText = strWidth;
                        FlipRotateActivity.this.heightText = strHeight;
                        FlipRotateActivity.this.selection = selectedItemPosition;
                        FlipRotateActivity.isCustomAdded = true;
                    }
                    if (selectedItemPosition == 1 && (FlipRotateActivity.this.inValidateWidthText(width, selectedItemPosition) & FlipRotateActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList((int) width, (int) height, SizeList.MM, 0, "MM", "MM","");
                        dialog.dismiss();
                        FlipRotateActivity.this.widthText = strWidth;
                        FlipRotateActivity.this.heightText = strHeight;
                        FlipRotateActivity.this.selection = selectedItemPosition;
                        FlipRotateActivity.isCustomAdded = true;
                    }
                    if (selectedItemPosition == 2 && (FlipRotateActivity.this.inValidateWidthText(width, selectedItemPosition) & FlipRotateActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList(FlipRotateActivity.this.cmtoMM(width), FlipRotateActivity.this.cmtoMM(height), SizeList.MM, 0, "CM", "CM","");
                        dialog.dismiss();
                        FlipRotateActivity.this.widthText = strWidth;
                        FlipRotateActivity.this.heightText = strHeight;
                        FlipRotateActivity.this.selection = selectedItemPosition;
                        FlipRotateActivity.isCustomAdded = true;
                    }
                    if (selectedItemPosition == 3 && (FlipRotateActivity.this.inValidateWidthText(width, selectedItemPosition) & FlipRotateActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList(FlipRotateActivity.this.inchesToMM(width), FlipRotateActivity.this.inchesToMM(height), SizeList.MM, 0, "INCHES", "INCHES","");
                        dialog.dismiss();
                        FlipRotateActivity.this.widthText = strWidth;
                        FlipRotateActivity.this.heightText = strHeight;
                        FlipRotateActivity.this.selection = selectedItemPosition;
                        FlipRotateActivity.isCustomAdded = true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(FlipRotateActivity.this, FlipRotateActivity.this.getResources().getString(R.string.fill_details), Toast.LENGTH_SHORT).show();
                }
            }
        });


        ((Button) dialog.findViewById(R.id.button_cancel)).

                setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

        dialog.getWindow().

                getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.show();

    }

    private boolean inValidateHeightText(float height, int selectedItemPosition) {
        if (selectedItemPosition == 0) {
            if (((int) height) <= this.maxPixelHeight) {
                return true;
            }
            this.editTextHeight.setError(getResources().getString(R.string.size_warn) + " " + this.maxPixelHeight);
        }
        if (selectedItemPosition == 1) {
            if (((int) height) <= this.maxMMheight) {
                return true;
            }
            this.editTextHeight.setError(getResources().getString(R.string.size_warn) + " " + this.maxMMheight);
        }
        if (selectedItemPosition == 2) {
            if (((int) height) <= this.maxCMheight) {
                return true;
            }
            this.editTextHeight.setError(getResources().getString(R.string.size_warn) + " " + this.maxCMheight);
        }
        if (selectedItemPosition == 3) {
            if (((int) height) <= this.maxInchHeight) {
                return true;
            }
            this.editTextHeight.setError(getResources().getString(R.string.size_warn) + " " + this.maxInchHeight);
        }
        return false;

    }

    private int inchesToMM(float inches) {
        return (int) (((double) inches) * 25.4d);
    }

    private int cmtoMM(float cm) {
        return (int) (10.0f * cm);
    }

    private boolean inValidateWidthText(float width, int selectedItemPosition) {

        if (selectedItemPosition == 0) {
            if (((int) width) <= this.maxPixelWidth) {
                return true;
            }
            this.editTextWidth.setError(getResources().getString(R.string.size_warn) + " " + this.maxPixelWidth);
        }
        if (selectedItemPosition == 1) {
            if (((int) width) <= this.maxMMwidth) {
                return true;
            }
            this.editTextWidth.setError(getResources().getString(R.string.size_warn) + " " + this.maxMMwidth);
        }
        if (selectedItemPosition == 2) {
            if (((int) width) <= this.maxCMwidth) {
                return true;
            }
            this.editTextWidth.setError(getResources().getString(R.string.size_warn) + " " + this.maxCMwidth);
        }
        if (selectedItemPosition == 3) {
            if (((int) width) <= this.maxInchWidth) {
                return true;
            }
            this.editTextWidth.setError(getResources().getString(R.string.size_warn) + " " + this.maxInchWidth);
        }
        return false;

    }
}
