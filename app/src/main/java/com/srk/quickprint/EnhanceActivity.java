package com.srk.quickprint;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.srk.quickprint.Activity.CropActivity;
import com.srk.quickprint.Activity.SaveActivity;
import com.srk.quickprint.Custom_Lib.GPUImageFilterTools;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

public class EnhanceActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_ERASER_ACTIVITY = 223;
    public static Bitmap tempBit;
    private Bitmap bitmap;
    private Animation blinkAnim;
    private Animation bottomDown;
    private Animation bottomUp;
    private int brightnessProg = 50;
    ImageView btn_flag;
    private ImageButton btn_ok;
    private RelativeLayout buttons_lay;
    private int contrastProg = 50;
    private int currentActive = 0;
    private Button done;
    private int exposureProg = 50;
    private GPUImage gpuImage;
    private TextView headerText;
    private HorizontalScrollView horizontalscrollview;
    ImageView imageView;
    private boolean isReadyToSave = true;
    boolean isRewarded = false;
    SharedPreferences preferences;
    private RelativeLayout rel;
    private int saturationProg = 50;
    private SeekBar seekBar;
    private RelativeLayout seekbar_lay;
    SharedPreferences sharedpreferences;
    private int sharpnessProg = 50;
    boolean tutOpen;
    private int whiteBalProg = 50;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_enhance);


        this.bitmap = CropActivity.cropedBitmap;

        this.headerText = (TextView) findViewById(R.id.headertext);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
        headerText.setTypeface(typeface);

        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.btn_flag = (ImageView) findViewById(R.id.btn_flag);
        this.btn_ok = (ImageButton) findViewById(R.id.btn_ok);
        this.done = (Button) findViewById(R.id.done);
        this.seekBar = (SeekBar) findViewById(R.id.seekbar);
        this.rel = (RelativeLayout) findViewById(R.id.rel);
        this.seekbar_lay = (RelativeLayout) findViewById(R.id.seekbar_lay);
        this.buttons_lay = (RelativeLayout) findViewById(R.id.buttons_lay);
        this.gpuImage = new GPUImage(this);
        setImageBitmap(this.bitmap);
        this.horizontalscrollview = (HorizontalScrollView) findViewById(R.id.horizontalscrollview);


        this.horizontalscrollview.postDelayed(new Runnable() {
            public void run() {
                EnhanceActivity.this.horizontalscrollview.getChildAt(0).startAnimation(AnimationUtils.loadAnimation(EnhanceActivity.this.getApplicationContext(), R.anim.anim_test));
            }
        }, 500);
        this.blinkAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_blink);
        findViewById(R.id.done).startAnimation(this.blinkAnim);
        this.bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.editor_bottom_up);
        this.bottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.editor_bottom_down);

        this.sharedpreferences = getSharedPreferences(MyPREFERENCES, 0);


    }



    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    private void setImageBitmap(Bitmap bitmap) {
        float wd = (float) bitmap.getWidth();
        float he = (float) bitmap.getHeight();
        Log.i("Input Size: ", wd + " " + he);
        float rat1 = wd / he;
        tempBit = bitmap;
        this.imageView.setImageBitmap(bitmap);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tutorial:
                // showTutorialDialog();
                return;
            case R.id.btn_back:
                finish();
                return;
            case R.id.done:
                try {
                    startActivity(new Intent(this, SaveActivity.class));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.contrast:
                this.headerText.setText(getResources().getString(R.string.contrast));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 1;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.contrastProg);
                return;
            case R.id.brightness:
                this.headerText.setText(getResources().getString(R.string.brightness));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 2;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.brightnessProg);
                return;
            case R.id.sharpness:
                this.headerText.setText(getResources().getString(R.string.sharpness));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 3;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.sharpnessProg);
                return;
            case R.id.saturation:
                this.headerText.setText(getResources().getString(R.string.saturation));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 4;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.saturationProg);
                return;
            case R.id.exposure:
                this.headerText.setText(getResources().getString(R.string.exposure));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 5;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.exposureProg);
                return;
            case R.id.whitebalance:
                this.headerText.setText(getResources().getString(R.string.white_balance));
                typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");
                headerText.setTypeface(typeface);
                this.currentActive = 6;
                this.seekBar.setOnSeekBarChangeListener(null);
                showSeekbarLayout(this.whiteBalProg);
                return;
            case R.id.btn_cancel:
                if (this.currentActive == 1) {
                    this.contrastProg = 50;
                } else if (this.currentActive == 2) {
                    this.brightnessProg = 50;
                } else if (this.currentActive == 3) {
                    this.sharpnessProg = 50;
                } else if (this.currentActive == 4) {
                    this.saturationProg = 50;
                } else if (this.currentActive == 5) {
                    this.exposureProg = 50;
                } else if (this.currentActive == 6) {
                    this.whiteBalProg = 50;
                }
                applyProgress();
                this.seekBar.setProgress(50);
                return;
            case R.id.btn_ok:
                try {
                    hideSeekbarLayout(true);
                    return;
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.currentActive == 1) {
            this.contrastProg = seekBar.getProgress();
        } else if (this.currentActive == 2) {
            this.brightnessProg = seekBar.getProgress();
        } else if (this.currentActive == 3) {
            this.sharpnessProg = seekBar.getProgress();
        } else if (this.currentActive == 4) {
            this.saturationProg = seekBar.getProgress();
        } else if (this.currentActive == 5) {
            this.exposureProg = seekBar.getProgress();
        } else if (this.currentActive == 6) {
            this.whiteBalProg = seekBar.getProgress();
        }
        applyProgress();
    }


    @SuppressLint("WrongConstant")
    private void showSeekbarLayout(int progress) {
        this.seekbar_lay.setVisibility(0);
        this.seekbar_lay.startAnimation(this.bottomUp);
        this.seekBar.setProgress(progress);
        this.bottomUp.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                EnhanceActivity.this.buttons_lay.setVisibility(8);
                EnhanceActivity.this.done.clearAnimation();
                EnhanceActivity.this.done.setVisibility(4);
                EnhanceActivity.this.btn_ok.startAnimation(EnhanceActivity.this.blinkAnim);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.seekBar.setOnSeekBarChangeListener(this);
    }

    private void applyProgress() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.processing_image), true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    GPUImageFilter filter1 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.CONTRAST);
                    GPUImageFilter filter2 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.BRIGHTNESS);
                    GPUImageFilter filter3 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.SHARPEN);
                    GPUImageFilter filter4 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.SATURATION);
                    GPUImageFilter filter5 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.EXPOSURE);
                    GPUImageFilter filter6 = GPUImageFilterTools.createFilterForType(EnhanceActivity.this, GPUImageFilterTools.FilterType.WHITE_BALANCE);
                    new GPUImageFilterTools.FilterAdjuster(filter1).adjust(EnhanceActivity.this.contrastProg);
                    new GPUImageFilterTools.FilterAdjuster(filter2).adjust(EnhanceActivity.this.brightnessProg);
                    new GPUImageFilterTools.FilterAdjuster(filter3).adjust(EnhanceActivity.this.sharpnessProg);
                    new GPUImageFilterTools.FilterAdjuster(filter4).adjust(EnhanceActivity.this.saturationProg);
                    new GPUImageFilterTools.FilterAdjuster(filter5).adjust(EnhanceActivity.this.exposureProg);
                    new GPUImageFilterTools.FilterAdjuster(filter6).adjust(EnhanceActivity.this.whiteBalProg);
                    GPUImageFilterGroup group = new GPUImageFilterGroup();
                    group.addFilter(filter1);
                    group.addFilter(filter2);
                    group.addFilter(filter3);
                    group.addFilter(filter4);
                    group.addFilter(filter5);
                    group.addFilter(filter6);
                    EnhanceActivity.this.gpuImage.setFilter(group);
                    EnhanceActivity.this.gpuImage.requestRender();
                    EnhanceActivity.tempBit = EnhanceActivity.this.gpuImage.getBitmapWithFilterApplied(EnhanceActivity.this.bitmap);

                    Log.i("testings", "Bitmap Congfig : " + EnhanceActivity.tempBit.getConfig());
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
        ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                EnhanceActivity.this.imageView.setImageBitmap(EnhanceActivity.tempBit);
            }
        });
    }

    private void hideSeekbarLayout(boolean isDoneClicked) throws InterruptedException {
        this.seekbar_lay.startAnimation(this.bottomDown);
        this.bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @SuppressLint("WrongConstant")
            public void onAnimationStart(Animation animation) {
                EnhanceActivity.this.buttons_lay.setVisibility(0);
            }

            @SuppressLint("WrongConstant")
            public void onAnimationEnd(Animation animation) {
                EnhanceActivity.this.seekbar_lay.setVisibility(8);
                EnhanceActivity.this.btn_ok.clearAnimation();
                EnhanceActivity.this.done.setVisibility(0);
                EnhanceActivity.this.done.startAnimation(EnhanceActivity.this.blinkAnim);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

}
//    private void goToEraserActivity() {
//        startActivityForResult(new Intent(this, EraserActivity.class), REQUEST_ERASER_ACTIVITY);
//    }



