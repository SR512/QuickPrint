package com.srk.quickprint.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.srk.quickprint.EnhanceActivity;
import com.srk.quickprint.Custom_Lib.ImageUtils;
import com.srk.quickprint.R;

import java.io.File;
import java.io.FileOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_ERASER_ACTIVITY = 223;
    private Bitmap bitmap;
    private int borderWidth = 0;
    private int color = -1;
    private String filename;
    private ImageView imageView;
    SharedPreferences preferences;
    private SeekBar seekbar;
    SharedPreferences sharedpreferences;
    private TextView txt_head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_save);

        this.bitmap = EnhanceActivity.tempBit;
        this.imageView = (ImageView) findViewById(R.id.image);
        this.imageView.setImageBitmap(this.bitmap);

        this.txt_head = (TextView) findViewById(R.id.headertext);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
        txt_head.setTypeface(typeface);

        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                SaveActivity.this.borderWidth = progress;
                if (SaveActivity.this.borderWidth == 0) {
                    SaveActivity.this.bitmap = EnhanceActivity.tempBit;
                    SaveActivity.this.imageView.setImageBitmap(SaveActivity.this.bitmap);
                    return;
                }
                SaveActivity.this.bitmap = SaveActivity.this.addBorder(EnhanceActivity.tempBit);
                SaveActivity.this.imageView.setImageBitmap(SaveActivity.this.bitmap);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        findViewById(R.id.done).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_blink));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tutorial:
                //showTutorialDialog();
                return;
            case R.id.btn_back:
                finish();
                return;
            case R.id.done:
                saveBitmap(false);
                return;
            case R.id.btn_color_palete:
                new AmbilWarnaDialog(this, this.color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    public void onOk(AmbilWarnaDialog dialog, int cr) {
                        SaveActivity.this.color = cr;
                        SaveActivity.this.bitmap = SaveActivity.this.addBorder(EnhanceActivity.tempBit);
                        SaveActivity.this.imageView.setImageBitmap(SaveActivity.this.bitmap);
                    }

                    public void onCancel(AmbilWarnaDialog dialog) {
                    }
                }).show();
                return;
            default:
                return;
        }

    }
    private synchronized Bitmap addBorder(Bitmap bmp) {
        Bitmap bmpWithBorder;
        int borderSize = ImageUtils.dpToPx(this, this.borderWidth);
        bmpWithBorder = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() - (borderSize * 2), bmp.getHeight() - (borderSize * 2), true);
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(this.color);
        canvas.drawBitmap(bmp, (float) borderSize, (float) borderSize, null);
        return bmpWithBorder;
    }

    private void saveBitmap(final boolean inPNG) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.save_image_), true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/.Quick Print");
                    if (pictureFileDir.exists() || pictureFileDir.mkdirs()) {
                        String photoFile = "Photo_" + System.currentTimeMillis();
                        if (inPNG) {
                            photoFile = photoFile + ".png";
                        } else {
                            photoFile = photoFile + ".jpg";
                        }
                        SaveActivity.this.filename = pictureFileDir.getPath() + File.separator + photoFile;
                        File pictureFile = new File(SaveActivity.this.filename);
                        try {
                            if (!pictureFile.exists()) {
                                pictureFile.createNewFile();
                            }
                            FileOutputStream ostream = new FileOutputStream(pictureFile);
                            if (inPNG) {
                                SaveActivity.this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);

                            } else {
                                SaveActivity.this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            }
                            ostream.flush();
                            ostream.close();
                            SaveActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(pictureFile)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(1000);
                        ringProgressDialog.dismiss();
                        return;
                    }
                    Log.d("", "Can't create directory to save image.");
                    Toast.makeText(SaveActivity.this.getApplicationContext(), SaveActivity.this.getResources().getString(R.string.create_dir_err), 1).show();
                } catch (Exception e2) {
                }
            }
        }).start();
        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {

                Toast.makeText(SaveActivity.this.getApplicationContext(), SaveActivity.this.getString(R.string.saved).toString() + " " + SaveActivity.this.filename, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SaveActivity.this, PrintActivity.class);
                intent.putExtra("uri", SaveActivity.this.filename);

                String gallery = "Save";

                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo",gallery);

                intent.putExtras(bundle);

                SaveActivity.this.startActivity(intent);
                finish();
            }
        });
    }

}
