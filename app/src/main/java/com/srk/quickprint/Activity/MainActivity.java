package com.srk.quickprint.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.desmond.squarecamera.CameraActivity;
import com.novoda.merlin.MerlinsBeard;
import com.srk.quickprint.GuidActivity;
import com.srk.quickprint.Lib.IUploadAPI;
import com.srk.quickprint.R;

public class MainActivity extends AppCompatActivity {

    private static final String MyPREFERENCES = "MyPrefs";
    private static final int PERMISSIONS_REQUEST = 922;
    private static final int REQUEST_CAMERA = 872;
    private static final int REQUEST_GALLERY = 764;
    MerlinsBeard merlin;
    private boolean isOpenFisrtTime = false;
    ProgressDialog progressDialog;
    IUploadAPI iUploadAPIService;
    String macid;


    SharedPreferences preferences;

    private Uri selectedImageUri;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);

        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 23 && !(checkSelfPermission("android.permission.CAMERA") == 0 && checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
            permissionDialog();
        }


    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnCamera:
                lauchCamera();
                return;

            case R.id.btnGallery:
                galleryIntent();
                return;

            case R.id.btnMyPictures:
                startActivity(new Intent(this, GalleryActivity.class));
                return;

            case R.id.btnRateus:
                rateUs();
                return;

            case R.id.btnInfo:
                startActivity(new Intent(this, GuidActivity.class));
                return;

            case R.id.aboutTerms:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://quickprint.xyz/terms.html"));
                startActivity(browserIntent);
                return;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            Intent i;

            if (requestCode == REQUEST_GALLERY) {
                this.selectedImageUri = data.getData();

                i = new Intent(this, PrintActivity.class);
                i.setData(this.selectedImageUri);

                String gallery = "Gallery";
                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo", gallery);
                i.putExtras(bundle);

                startActivity(i);

            }
            if (requestCode == REQUEST_CAMERA) {
                    this.selectedImageUri = data.getData();
                    i = new Intent(this, FlipRotateActivity.class);
                    i.setData(this.selectedImageUri);

                    startActivity(i);
                    finish();
                }

        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST) {
            return;
        }
        if (grantResults[0] == 0) {
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }
            if (checkSelfPermission("android.permission.CAMERA") != 0 || checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                this.isOpenFisrtTime = true;
                permissionDialog();
            }
        } else if (Build.VERSION.SDK_INT < 23) {
        } else {
            if (checkSelfPermission("android.permission.CAMERA") != 0 || checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                this.isOpenFisrtTime = true;
                permissionDialog();
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void permissionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.permission_box);
        dialog.setTitle(getResources().getString(R.string.permission).toString());
        dialog.setCancelable(false);

        ((Button) dialog.findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    MainActivity.this.requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, MainActivity.PERMISSIONS_REQUEST);
                }
                dialog.dismiss();
            }
        });
        if (this.isOpenFisrtTime) {
            Button setting = (Button) dialog.findViewById(R.id.settings);
            setting.setVisibility(0);
            setting.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(intent);
                    dialog.dismiss();
                }
            });
        }
        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;

        dialog.show();
    }

    //   Method Camera

    private void lauchCamera() {
        startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), REQUEST_GALLERY);
    }

    private void rateUs() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Rate Quick Print..!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("If You Like Quick Print..? Give 5 Star Rating And Also Give Suggestion About Quick Print..Thank You For be Us..");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String url = "https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName();
                Intent i = new Intent("android.intent.action.VIEW");
                i.setData(Uri.parse(url));
                MainActivity.this.startActivity(i);
            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;

        alertDialog.show();
    }

    public void onBackPressed() {
        exitAlertDialog();
    }

    public void exitAlertDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.exit_alert);
        dialog.setCancelable(true);

//        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                String url = "" + MainActivity.this.getPackageName();
//                Intent i = new Intent("android.intent.action.VIEW");
//                i.setData(Uri.parse(url));
//                MainActivity.this.startActivity(i);
//                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.thank_toast), 0).show();
//                dialog.dismiss();
//            }
//        });
        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.this.finish();
                System.exit(0);
            }
        });

        dialog.getWindow().

                getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.show();

    }


}

