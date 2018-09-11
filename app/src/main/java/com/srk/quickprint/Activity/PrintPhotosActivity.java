package com.srk.quickprint.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.srk.quickprint.Custom_Lib.ImageUtils;
import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PrintPhotosActivity extends AppCompatActivity {

    private int actualHeight = 0;
    private int actualWidth = 0;
    private int curDim = 0;
    private String filename = "";
    private ImageView imageView;
    private boolean isFisrtTime = true;
    private boolean isPortrait = true;
    private int maxDim = 0;
    private Bitmap photoBitmap;
    private Uri photoUri;
    SharedPreferences preferences;
    private Bitmap preparedBitmap;
    private int screenHeight = 0;
    private int screenWidth = 0;
    String sizein;
    public  static String photoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_print_photos);

        this.imageView = (ImageView) findViewById(R.id.image);
        Intent intent = getIntent();
        this.photoUri = intent.getData();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        this.screenWidth = displaymetrics.widthPixels;
        this.screenHeight = displaymetrics.heightPixels;

        sizein = intent.getExtras().getString("sizeInfo");

        if (sizein.equals("Print")) {
            this.imageView.setImageURI(photoUri);
        } else {
            try {

                initHeigthWidth(intent.getExtras().getString("sizeInfo"));
                BitmapFactory.Options bitmapDims = ImageUtils.getBitmapDims(this.photoUri, this);
                int i = bitmapDims.outWidth > bitmapDims.outHeight ? bitmapDims.outWidth : bitmapDims.outHeight;
                this.curDim = i;
                this.maxDim = i;
                this.photoBitmap = ImageUtils.getResampleImageBitmap(this.photoUri, this, this.maxDim);
                prepareBitmap();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void initHeigthWidth(String str) {
        try {
            JSONObject object = new JSONObject(str);
            int width = object.getInt("width");
            int height = object.getInt("height");
            if (object.getInt("type") == SizeList.PIXEL) {
                this.actualWidth = width;
                this.actualHeight = height;
            }
            if (object.getInt("type") == SizeList.MM) {
                this.actualWidth = width * 12;
                this.actualHeight = height * 12;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareBitmap() {
        int width;
        int height;
        if (this.isPortrait) {
            width = this.actualWidth;
            height = this.actualHeight;
        } else {
            height = this.actualWidth;
            width = this.actualHeight;
        }
        Bitmap paperBit = null;
        float dimen = 1.0f;
        for (float i = 1.0f; i >= 0.5f; i -= 0.1f) {
            dimen = i;
            paperBit = createEmptyBitmap(width, height);
            if (paperBit != null) {
                break;
            }
        }
        if (paperBit != null) {
            try {
                Bitmap orgBit = Bitmap.createScaledBitmap(this.photoBitmap, (int) (((float) this.photoBitmap.getWidth()) * dimen), (int) (((float) this.photoBitmap.getHeight()) * dimen), true);
                width = (int) (((float) width) * dimen);
                height = (int) (((float) height) * dimen);
                int wc = width / orgBit.getWidth();
                int hc = height / orgBit.getHeight();
                if (wc != 0 && hc != 0) {
                    int wg = (width % orgBit.getWidth()) / (wc + 1);
                    int hg = (height % orgBit.getHeight()) / (hc + 1);
                    Canvas canvas = new Canvas(paperBit);
                    canvas.drawColor(-1);
                    for (int i2 = 0; i2 < hc; i2++) {
                        float top = ((float) (orgBit.getHeight() * i2)) + ((float) ((i2 + 1) * hg));
                        for (int j = 0; j < wc; j++) {
                            canvas.drawBitmap(orgBit, ((float) (orgBit.getWidth() * j)) + ((float) ((j + 1) * wg)), top, null);
                        }
                    }
                    this.preparedBitmap = paperBit;
                    this.imageView.setImageBitmap(ImageUtils.resizeBitmap(this.preparedBitmap, this.screenWidth, this.screenHeight));
                    return;
                } else if (this.isFisrtTime) {
                    this.isFisrtTime = false;
                    this.isPortrait = false;
                    prepareBitmap();
                    return;
                } else {
                    showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
                    return;
                }
            } catch (OutOfMemoryError e) {
                showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
                return;
            } catch (RuntimeException e2) {
                showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
                return;
            }
        }
        showErrorDialog(getResources().getString(R.string.invalid_size_title), getResources().getString(R.string.invalid_size_msg));
    }

    private Bitmap createEmptyBitmap(int width, int height) {
        Throwable e;
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, this.photoBitmap.getConfig());
        } catch (OutOfMemoryError e2) {
            e = e2;
            e.printStackTrace();
            return bitmap;
        } catch (RuntimeException e3) {
            e = e3;
            e.printStackTrace();
            return bitmap;
        }
        return bitmap;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                return;
            case R.id.done:

                saveBitmap(false);

//                if (PrintActivity.count == 5) {
//
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Share Quick Print Application " + PrintActivity.count + " But Share Min 5 Time.", Toast.LENGTH_LONG).show();
//                }
                return;
            case R.id.btn_rotation:
                if (this.isPortrait) {
                    this.isPortrait = false;
                } else {
                    this.isPortrait = true;
                }
                prepareBitmap();
                return;
            default:
                return;
        }
    }

    private void saveBitmap(final boolean inPNG) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.save_image_), true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/.Quick Print");
                    if (pictureFileDir.exists() || pictureFileDir.mkdirs()) {
                        photoFile = "Photo_" + System.currentTimeMillis();
                        if (inPNG) {
                            photoFile = photoFile + ".png";
                        } else {
                            photoFile = photoFile + ".jpg";
                        }
                        PrintPhotosActivity.this.filename = pictureFileDir.getPath() + File.separator + photoFile;
                        File pictureFile = new File(PrintPhotosActivity.this.filename);
                        try {
                            if (!pictureFile.exists()) {
                                pictureFile.createNewFile();
                            }
                            FileOutputStream ostream = new FileOutputStream(pictureFile);
                            if (inPNG) {
                                PrintPhotosActivity.this.preparedBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                            } else {
                                PrintPhotosActivity.this.preparedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            }
                            ostream.flush();
                            ostream.close();
                            PrintPhotosActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(pictureFile)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(1000);
                        ringProgressDialog.dismiss();
                        return;
                    }
                    Log.d("", "Can't create directory to save image.");
                    Toast.makeText(PrintPhotosActivity.this.getApplicationContext(), PrintPhotosActivity.this.getResources().getString(R.string.create_dir_err), Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {
                }
            }
        }).start();
        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {

//                Toast.makeText(PrintPhotosActivity.this.getApplicationContext(), PrintPhotosActivity.this.getString(R.string.saved).toString() + " " + PrintPhotosActivity.this.filename, Toast.LENGTH_SHORT).show();


                Intent printIntent = new Intent(ringProgressDialog.getContext(), Checkout.class);
                printIntent.setData(Uri.parse(PrintPhotosActivity.this.filename));

                String gallery = "CameraFrom";

                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo",gallery);
                printIntent.putExtras(bundle);


                startActivity(printIntent);
                PrintPhotosActivity.this.finish();
            }
        });
    }

    private void showErrorDialog(String title, String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setIcon(R.mipmap.ic_launcher).setMessage(msg).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                PrintPhotosActivity.this.finish();
            }
        }).create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        alertDialog.show();
    }

}
