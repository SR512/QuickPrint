package com.srk.quickprint.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srk.quickprint.Adapter.ExpandableList_AD;
import com.srk.quickprint.EnhanceActivity;
import com.srk.quickprint.Model.Expandable_ListDaa;
import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MyPREFERENCES = "MyPrefs";
    public static Bitmap cropedBitmap;
    private Animation bottomDown;
    private ImageView btn_flag;
    private LinearLayout buttons_lay;
    private Button change;
    private CropImageView cropImageView;
    private int dialogHeight = 800;
    private Dialog dialogRatio = null;
    private int dialogWidth = 600;
    private EditText editTextHeight;
    private EditText editTextWidth;
    private ExpandableListAdapter expandableListAdapter;
    private LinkedHashMap<String, List<SizeList>> expandableListDetail;
    private List<String> expandableListTitle;
    private ExpandableListView expandableListView;
    private RelativeLayout flag_lay;
    private String heightText = "";
    private HorizontalScrollView horizontalscrollview;
    private ImageView image;
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
    SharedPreferences sharedpreferences;
    private SizeList sizeInfo;
    private TextView txt_desc;
    private TextView txt_head;
    private String widthText = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_crop);

        this.sharedpreferences = getSharedPreferences(MyPREFERENCES, 0);
        this.cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        this.image = (ImageView) findViewById(R.id.image);
        this.btn_flag = (ImageView) findViewById(R.id.btn_flag);
        this.change = (Button) findViewById(R.id.change);
        this.txt_head = (TextView) findViewById(R.id.txt_head);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
        txt_head.setTypeface(typeface);
        this.txt_desc = (TextView) findViewById(R.id.txt_desc);
        this.buttons_lay = (LinearLayout) findViewById(R.id.buttons_lay);
        this.flag_lay = (RelativeLayout) findViewById(R.id.flag_lay);
        this.cropImageView.setImageBitmap(StraightenActivity.outputBitmap);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        this.dialogWidth = (int) (((double) displaymetrics.widthPixels) * 0.9d);
        this.dialogHeight = (int) (((double) displaymetrics.heightPixels) * 0.9d);
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
        this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");

        try {
            JSONObject obj = new JSONObject(this.sharedpreferences.getString("prevSize", ""));
            this.sizeInfo = new SizeList(obj.getInt("width"), obj.getInt("height"), obj.getInt("type"), obj.getInt("drawableId"), obj.getInt("percentage"), obj.getString("displayText"), obj.getString("headText"), obj.getString("descText"));
            this.cropImageView.setAspectRatio(this.sizeInfo.getWidth(), this.sizeInfo.getHeight());
            this.image.setImageResource(this.sizeInfo.getDrawableId());
            //this.btn_flag.setImageResource(this.sizeInfo.getDrawableId());
            this.txt_head.setText(this.sizeInfo.getHeadText());
            this.txt_desc.setText(this.sizeInfo.getDescText());
        } catch (JSONException e) {
            e.printStackTrace();
            this.buttons_lay.setVisibility(0);
            this.flag_lay.setVisibility(8);
        }
        if (FlipRotateActivity.isCustomAdded) {
            this.sizeInfo = FlipRotateActivity.sizeList;
            this.cropImageView.setAspectRatio(this.sizeInfo.getWidth(), this.sizeInfo.getHeight());
            this.buttons_lay.setVisibility(0);
            this.flag_lay.setVisibility(8);
        }
        this.bottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.editor_bottom_down);
        if (!this.sharedpreferences.getBoolean("cropphoto", false)) {
            //    showTutorialDialog();
        }

    }

    @SuppressLint("WrongConstant")
    private void showStandardRatioDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.choose_size_menu);
        this.expandableListView = (ExpandableListView) dialog.findViewById(R.id.expandableListView);
        this.expandableListDetail = Expandable_ListDaa.getData(this);
        this.expandableListTitle = new ArrayList(this.expandableListDetail.keySet());
        this.expandableListAdapter = new ExpandableList_AD(this, this.expandableListTitle, this.expandableListDetail);
        this.expandableListView.setAdapter(this.expandableListAdapter);
        this.expandableListView.collapseGroup(0);
        this.expandableListView.collapseGroup(1);
        this.expandableListView.expandGroup(2);

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CropActivity.this.expandableListView.collapseGroup(0);
                CropActivity.this.expandableListView.collapseGroup(1);
                CropActivity.this.expandableListView.collapseGroup(2);
            }
        });
        dialog.findViewById(R.id.footer).setVisibility(8);

        dialog.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String packageName = "package com.srk.quickprint";
                Intent intent = CropActivity.this.getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent == null) {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName));
                }
                CropActivity.this.startActivity(intent);
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.getWindow().getAttributes().width = this.dialogWidth;
        dialog.getWindow().getAttributes().height = this.dialogHeight;
        dialog.show();

        this.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                CropActivity.this.sizeInfo = (SizeList) expandableListView.getExpandableListAdapter().getChild(i, i1);
                CropActivity.this.cropImageView.setAspectRatio(CropActivity.this.sizeInfo.getWidth(), CropActivity.this.sizeInfo.getHeight());
                dialog.dismiss();
                SharedPreferences.Editor edit = CropActivity.this.sharedpreferences.edit();

                CropActivity.this.image.setImageResource(CropActivity.this.sizeInfo.getDrawableId());
                CropActivity.this.btn_flag.setImageResource(CropActivity.this.sizeInfo.getDrawableId());
                CropActivity.this.txt_head.setText(CropActivity.this.sizeInfo.getHeadText());
                CropActivity.this.txt_desc.setText(CropActivity.this.sizeInfo.getDescText());
                edit.putString("prevSize", CropActivity.this.sizeInfo.toString());
                CropActivity.this.buttons_lay.setVisibility(8);
                CropActivity.this.flag_lay.setVisibility(0);
                edit.commit();
                return false;
            }
        });
    }
    @SuppressLint("WrongConstant")
    public void onClick(View view) {
        Throwable e;
        switch (view.getId()) {
            case R.id.standard:
                showStandardRatioDialog();
                return;
            case R.id.tutorial:
               // showTutorialDialog();
                return;
            case R.id.btn_back:
                finish();
                return;
            case R.id.done:
                try {
                    cropedBitmap = this.cropImageView.getCroppedImage();
                    if (cropedBitmap != null) {
                        if (this.sizeInfo.getType() != SizeList.DEFAULT) {
                            if (this.sizeInfo.getType() == SizeList.MM) {
                                cropedBitmap = Bitmap.createScaledBitmap(cropedBitmap, this.sizeInfo.getWidth() * 12, this.sizeInfo.getHeight() * 12, true);
                            } else if (this.sizeInfo.getType() == SizeList.PIXEL) {
                                cropedBitmap = Bitmap.createScaledBitmap(cropedBitmap, this.sizeInfo.getWidth(), this.sizeInfo.getHeight(), true);
                            }
                        }
                        startActivity(new Intent(this, EnhanceActivity.class));
                        return;
                    }
                    showErrorDialog(getResources().getString(R.string.invalid_ratio_title), getResources().getString(R.string.invalid_ratio_msg));
                    return;
                } catch (OutOfMemoryError e2) {
                    e = e2;
                    e.printStackTrace();
                    showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
                    return;
                } catch (RuntimeException e3) {
                    e = e3;
                    e.printStackTrace();
                    showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
                    return;
                }
            case R.id.cutom:
                showCustomeRatio();
                return;
            case R.id.ratio:
                showRatioDialog();
                return;
            case R.id.change:
                this.flag_lay.startAnimation(this.bottomDown);
                this.bottomDown.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    @SuppressLint("WrongConstant")
                    public void onAnimationEnd(Animation animation) {
                        CropActivity.this.flag_lay.setVisibility(8);
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                this.buttons_lay.setVisibility(0);
                return;
            case R.id.free:
                this.dialogRatio.dismiss();
                this.cropImageView.clearAspectRatio();
                this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");
                return;
            case R.id.ratio1:
                this.dialogRatio.dismiss();
                this.cropImageView.setAspectRatio(1, 1);
                this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");
                return;
            case R.id.ratio2:
                this.dialogRatio.dismiss();
                this.cropImageView.setAspectRatio(2, 3);
                this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");
                return;
            case R.id.ratio3:
                this.dialogRatio.dismiss();
                this.cropImageView.setAspectRatio(4, 3);
                this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");
                return;
            case R.id.ratio4:
                this.dialogRatio.dismiss();
                this.cropImageView.setAspectRatio(9, 16);
                this.sizeInfo = new SizeList(40, 40, SizeList.DEFAULT, 0, "default", "default","");
                return;
              default:
                return;
        }
    }
    private void showRatioDialog() {
        this.dialogRatio = new Dialog(this);
        this.dialogRatio.getWindow().requestFeature(1);
        this.dialogRatio.setContentView(R.layout.ratio_dialog);
        this.dialogRatio.findViewById(R.id.free).setOnClickListener(this);
        this.dialogRatio.findViewById(R.id.ratio1).setOnClickListener(this);
        this.dialogRatio.findViewById(R.id.ratio2).setOnClickListener(this);
        this.dialogRatio.findViewById(R.id.ratio3).setOnClickListener(this);
        this.dialogRatio.findViewById(R.id.ratio4).setOnClickListener(this);
        this.dialogRatio.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        this.dialogRatio.show();
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

                String strWidth = CropActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = CropActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    CropActivity.this.inValidateWidthText(Float.parseFloat(strWidth), selectedItemPosition);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {
                    CropActivity.this.inValidateHeightText(Float.parseFloat(strHeight), selectedItemPosition);
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
                String strWidth = CropActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = CropActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    CropActivity.this.inValidateWidthText(Float.parseFloat(strWidth), selectedItemPosition);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {
                    CropActivity.this.inValidateHeightText(Float.parseFloat(strHeight), selectedItemPosition);
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
                String strWidth = CropActivity.this.editTextWidth.getText().toString().trim();
                String strHeight = CropActivity.this.editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    float width = Float.parseFloat(strWidth);
                    float height = Float.parseFloat(strHeight);
                    Log.i("testing", "Width : " + width + "  and Height : " + height + " Selected Postion : " + selectedItemPosition);

                    if (selectedItemPosition == 0 && (CropActivity.this.inValidateWidthText(width, selectedItemPosition) & CropActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList((int) width, (int) height, SizeList.PIXEL, 0, "PIXEL", "PIXEL","");
                        dialog.dismiss();
                        CropActivity.this.widthText = strWidth;
                        CropActivity.this.heightText = strHeight;
                        CropActivity.this.selection = selectedItemPosition;

                    }
                    if (selectedItemPosition == 1 && (CropActivity.this.inValidateWidthText(width, selectedItemPosition) & CropActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList((int) width, (int) height, SizeList.MM, 0, "MM", "MM","");
                        dialog.dismiss();
                        CropActivity.this.widthText = strWidth;
                        CropActivity.this.heightText = strHeight;
                        CropActivity.this.selection = selectedItemPosition;

                    }
                    if (selectedItemPosition == 2 && (CropActivity.this.inValidateWidthText(width, selectedItemPosition) & CropActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList(CropActivity.this.cmtoMM(width), CropActivity.this.cmtoMM(height), SizeList.MM, 0, "CM", "CM","");
                        dialog.dismiss();
                        CropActivity.this.widthText = strWidth;
                        CropActivity.this.heightText = strHeight;
                        CropActivity.this.selection = selectedItemPosition;

                    }
                    if (selectedItemPosition == 3 && (CropActivity.this.inValidateWidthText(width, selectedItemPosition) & CropActivity.this.inValidateHeightText(height, selectedItemPosition))) {
                        FlipRotateActivity.sizeList = new SizeList(CropActivity.this.inchesToMM(width), CropActivity.this.inchesToMM(height), SizeList.MM, 0, "INCHES", "INCHES","");
                        dialog.dismiss();
                        CropActivity.this.widthText = strWidth;
                        CropActivity.this.heightText = strHeight;
                        CropActivity.this.selection = selectedItemPosition;
                        FlipRotateActivity.isCustomAdded = true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(CropActivity.this, CropActivity.this.getResources().getString(R.string.fill_details), Toast.LENGTH_SHORT).show();
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
    private void showErrorDialog(String title, String msg) {
        int style;
        if (Build.VERSION.SDK_INT >= 14) {
            style = R.style.AppTheme;
        } else {
            style = R.style.AppTheme;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this, style).setTitle(title).setIcon(R.mipmap.ic_launcher).setMessage(msg).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        alertDialog.show();
    }
    private int inchesToMM(float inches) {
        return (int) (((double) inches) * 25.4d);
    }

    private int cmtoMM(float cm) {
        return (int) (10.0f * cm);
    }

    public boolean inValidateWidthText(float width, int selectedItemPosition) {
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

    public boolean inValidateHeightText(float height, int selectedItemPosition) {
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


}
