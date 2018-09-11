package com.srk.quickprint.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.srk.quickprint.Adapter.PagesSizeAdapter;
import com.srk.quickprint.Model.Expandable_ListDaa;
import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import java.io.File;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MyPREFERENCES = "MyPrefs";
    private static final int OPEN_HELP_ACITIVITY = 2299;
    public static int count;
    private int dialogHeight = 800;
    private int dialogWidth = 600;
    private ImageView imageView;
    private Uri phototUri = null;
    SharedPreferences preferences;
    SharedPreferences sharedpreferences;
    String sizein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_print);

        this.imageView = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();

        sizein = intent.getExtras().getString("sizeInfo");
        ImageView done = (ImageView)findViewById(R.id.done);
        ImageView print = (ImageView)findViewById(R.id.btn_more);

        if(sizein.equals("Gallery"))
        {
            this.phototUri =  intent.getData();
             Log.d("Photo",this.phototUri.toString());
            done.setVisibility(View.INVISIBLE);

            this.imageView.setImageURI(phototUri);

        }
        else
        {
            initUI();
        }


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        this.dialogWidth = (int) (((double) displaymetrics.widthPixels) * 0.9d);
        this.dialogHeight = (int) (((double) displaymetrics.heightPixels) * 0.9d);


    }

    public void initUI() {

        this.imageView = (ImageView) findViewById(R.id.image);
        this.phototUri = Uri.parse(getIntent().getStringExtra("uri"));
        this.imageView.setImageURI(this.phototUri);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);
    }

    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:

                Intent printIntent = new Intent(PrintActivity.this, Checkout.class);
                printIntent.setData(this.phototUri);

                String gallery = "GalleryFrom";

                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo",gallery);
                printIntent.putExtras(bundle);

                PrintActivity.this.startActivity(printIntent);

                return;
            case R.id.btn_back:
                finish();
                return;
            case R.id.btn_more:
                showPageSizeDialog(Uri.parse(this.phototUri.toString()));
                return;
            case R.id.btn_share:
//                if (count<5)
//                {
//                    ImageView button = (ImageView) findViewById(R.id.btn_share);
//                    button.setEnabled(false);
//                    count = 0;
//                }
                final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "", getString(R.string.plzwait), true);
                ringProgressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {

                            Intent intent = new Intent("android.intent.action.SEND");
                            File file = new File(PrintActivity.this.phototUri.getPath());
                            intent.setType("image/*");
                            intent.putExtra("android.intent.extra.SUBJECT", PrintActivity.this.getResources().getString(R.string.app_name));
                            intent.putExtra("android.intent.extra.TEXT", (PrintActivity.this.getResources().getString(R.string.sharetext) + " " + PrintActivity.this.getResources().getString(R.string.app_name) + ". " + PrintActivity.this.getResources().getString(R.string.sharetext1)) + "https://play.google.com/store/apps/details?id=" + PrintActivity.this.getPackageName());
                        //  intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                            count++;
                            PrintActivity.this.startActivity(Intent.createChooser(intent,PrintActivity.this.getString(R.string.shareusing).toString()));


                        } catch (Exception e) {
                        }
                        ringProgressDialog.dismiss();
                    }
                }).start();
                return;
            case R.id.btn_home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showPageSizeDialog(final Uri uri) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.page_size_menu);
        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        final PagesSizeAdapter adapter = new PagesSizeAdapter(this, Expandable_ListDaa.getPageSizeList(this));
        listView.setAdapter(adapter);

        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.getWindow().getAttributes().width = this.dialogWidth;
        dialog.getWindow().getAttributes().height = this.dialogHeight;
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();

                if(sizein.equals("Gallery"))
                {
                    Intent printIntent = new Intent(PrintActivity.this, Checkout.class);
                    printIntent.setData(phototUri);

                    String gallery = "GalleryFrom";

                    Bundle bundle = new Bundle();
                    bundle.putString("size", adapter.getItem(i).toString());
                    bundle.putString("sizeInfo",gallery);

                    printIntent.putExtras(bundle);

                    PrintActivity.this.startActivity(printIntent);

                }
                else
                {
                    Intent printIntent = new Intent(PrintActivity.this, PrintPhotosActivity.class);
                    printIntent.setData(uri);

                    Bundle bundle = new Bundle();
                    bundle.putString("sizeInfo", adapter.getItem(i).toString());
                    printIntent.putExtras(bundle);

                    PrintActivity.this.startActivity(printIntent);
                }


            }
        });

//        dialog.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                dialog.dismiss();
//                PrintActivity.this.showCustomRatioDialog(uri);
//            }
//        });
//        dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                dialog.dismiss();
//                PrintActivity.this.showCustomRatioDialog(uri);
//            }
//        });
    }
    public void showCustomRatioDialog(Uri uri) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.custom_ratio);
        final EditText editTextWidth = (EditText) dialog.findViewById(R.id.dimen_width);
        final EditText editTextHeight = (EditText) dialog.findViewById(R.id.dimen_height);

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
        ArrayAdapter<String> unitAdapter = new ArrayAdapter(this, R.layout.spiner_list, new String[]{getResources().getString(R.string.pixels), getResources().getString(R.string.milli), getResources().getString(R.string.centi), getResources().getString(R.string.inch)});
        unitAdapter.setDropDownViewResource(R.layout.spiner_list);
        spinner.setAdapter(unitAdapter);

        final Uri uri2 = uri;

        ((Button) dialog.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String strWidth = editTextWidth.getText().toString().trim();
                String strHeight = editTextHeight.getText().toString().trim();
                int selectedItemPosition = spinner.getSelectedItemPosition();
                try {
                    float width = Float.parseFloat(strWidth);
                    float height = Float.parseFloat(strHeight);
                    Log.i("testing", "Width : " + width + "  and Height : " + height + " Selected Postion : " + selectedItemPosition);
                    SizeList sizeInfo = new SizeList((int) width, (int) height, SizeList.PIXEL, 0, "PIXEL", "PIXEL","");
                    if (selectedItemPosition == 0) {
                        sizeInfo = new SizeList((int) width, (int) height, SizeList.PIXEL, 0, "PIXEL", "PIXEL","");
                    }
                    if (selectedItemPosition == 1) {
                        sizeInfo = new SizeList((int) width, (int) height, SizeList.MM, 0, "MM", "MM","");
                    }
                    if (selectedItemPosition == 2) {
                        sizeInfo = new SizeList(PrintActivity.this.cmtoMM(width), PrintActivity.this.cmtoMM(height), SizeList.MM, 0, "CM", "CM","");
                    }
                    if (selectedItemPosition == 3) {
                        sizeInfo = new SizeList(PrintActivity.this.inchesToMM(width), PrintActivity.this.inchesToMM(height), SizeList.MM, 0, "INCHES", "INCHES","");
                    }
                    dialog.dismiss();

                    if(sizein.equals("Gallery"))
                    {
                        Intent printIntent = new Intent(PrintActivity.this, Checkout.class);
                        printIntent.setData(phototUri);

                        String gallery = "GalleryFrom";

                        Bundle bundle = new Bundle();
                        bundle.putString("size", sizeInfo.toString());
                        bundle.putString("sizeInfo",gallery);

                        printIntent.putExtras(bundle);

                        PrintActivity.this.startActivity(printIntent);

                    }
                    else
                    {
                        Intent printIntent = new Intent(PrintActivity.this, PrintPhotosActivity.class);
                        printIntent.setData(uri2);
                        Bundle bundle = new Bundle();
                        bundle.putString("sizeInfo", sizeInfo.toString());
                        printIntent.putExtras(bundle);
                        PrintActivity.this.startActivity(printIntent);

                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(PrintActivity.this, "Fill all details", Toast.LENGTH_LONG).show();
                }
            }
        });
        ((Button) dialog.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.show();
    }

    private int inchesToMM(float inches) {
        return (int) (((double) inches) * 25.4d);
    }

    private int cmtoMM(float cm) {
        return (int) (10.0f * cm);
    }
}
