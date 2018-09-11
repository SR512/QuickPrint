package com.srk.quickprint.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.srk.quickprint.Adapter.PagesSizeAdapter;
import com.srk.quickprint.Custom_Lib.CustomSquareFrameLayout;
import com.srk.quickprint.Model.Expandable_ListDaa;
import com.srk.quickprint.R;
import com.srk.quickprint.Model.SizeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private int dialogHeight = 800;
    private int dialogWidth = 600;
    String[] extensions = new String[]{"jpg", "jpeg", "JPG", "JPEG"};
    private GridView galleryGridView;
    private List<Uri> images;
    private ImageGalleryAdapter mGalleryAdapter;
    SharedPreferences preferences;


    public class ImageGalleryAdapter extends ArrayAdapter<Uri> {
        Context context;

        public ImageGalleryAdapter(Context context, List<Uri> images) {
            super(context, 0, images);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.gride_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Uri mUri = (Uri) getItem(position);
            Glide.with(this.context).load(mUri.toString()).thumbnail(0.1f).dontAnimate().centerCrop().placeholder((int) R.drawable.no_image).error((int) R.drawable.no_image).into(holder.mThumbnail);
            holder.uri = mUri;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView mThumbnail;
        CustomSquareFrameLayout root;
        Uri uri;

        public ViewHolder(View view) {
            this.root = (CustomSquareFrameLayout) view.findViewById(R.id.root);
            this.mThumbnail = (ImageView) view.findViewById(R.id.thumbnail_image);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_gallery);

        this.galleryGridView = (GridView) findViewById(R.id.gallery_grid);
        this.images = loadAllImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        this.mGalleryAdapter = new ImageGalleryAdapter(this, this.images);
        this.galleryGridView.setAdapter(this.mGalleryAdapter);
        this.galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GalleryActivity.this.showOptionsDialog((Uri) GalleryActivity.this.images.get(i));
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GalleryActivity.this.finish();
            }
        });
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        this.dialogWidth = (int) (((double) displaymetrics.widthPixels) * 0.9d);
        this.dialogHeight = (int) (((double) displaymetrics.heightPixels) * 0.9d);


    }

    protected void onResume() {
        super.onResume();
        this.images = loadAllImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        this.mGalleryAdapter = new ImageGalleryAdapter(this, this.images);
        this.galleryGridView.setAdapter(this.mGalleryAdapter);
    }

    @SuppressLint("WrongConstant")
    private List<Uri> loadAllImages(String rootFolder) {
        int i;
        HashMap<Long, Uri> hashMap = new HashMap();
        File file = new File(rootFolder, "/.Quick Print");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (!f.isDirectory()) {
                        for (String endsWith : this.extensions) {
                            if (f.getAbsolutePath().endsWith(endsWith)) {
                                hashMap.put(Long.valueOf(f.lastModified()), Uri.fromFile(f));
                            }
                        }
                    }
                }
            }
        }
        if (hashMap.size() == 0) {
            return new ArrayList();
        }
        findViewById(R.id.txt_nopics).setVisibility(8);
        List sortedKeys = new ArrayList(hashMap.keySet());
        Collections.sort(sortedKeys);
        int len = sortedKeys.size();
        List<Uri> images = new ArrayList();
        for (i = len - 1; i >= 0; i--) {
            images.add(hashMap.get(sortedKeys.get(i)));
        }
        return images;
    }

    private int inchesToMM(float inches) {
        return (int) (((double) inches) * 25.4d);
    }

    private int cmtoMM(float cm) {
        return (int) (10.0f * cm);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean deleteFile(Uri uri) {
        File file = new File(uri.getPath());
        if (!file.exists()) {
            return false;
        }
        boolean delete = file.delete();
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
        Toast.makeText(this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        return delete;
    }

    private void showOptionsDialog(final Uri uri) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.option_dialoge);

        ((TextView) dialog.findViewById(R.id.print_copies)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                GalleryActivity.this.showPageSizeDialog(uri);
            }
        });

        ((TextView) dialog.findViewById(R.id.open)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, PrintActivity.class);
                intent.putExtra("uri", uri.getPath());

                String gallery = "Open";
                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo",gallery);
                intent.putExtras(bundle);

                GalleryActivity.this.startActivity(intent);
                dialog.dismiss();
            }
        });



        ((TextView) dialog.findViewById(R.id.edit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(GalleryActivity.this, FlipRotateActivity.class);
                i.setData(uri);
                GalleryActivity.this.startActivity(i);

                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.delete)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View v) {
                if (GalleryActivity.this.deleteFile(uri)) {
                    GalleryActivity.this.mGalleryAdapter.remove(uri);
                    GalleryActivity.this.mGalleryAdapter.notifyDataSetChanged();
                    if (GalleryActivity.this.images.size() == 0) {
                        GalleryActivity.this.findViewById(R.id.txt_nopics).setVisibility(0);
                    } else {
                        GalleryActivity.this.findViewById(R.id.txt_nopics).setVisibility(8);
                    }
                } else {
                    Toast.makeText(GalleryActivity.this, GalleryActivity.this.getResources().getString(R.string.del_error_toast), 0).show();
                }
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.anim.editor_bottom_up;
        dialog.show();
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
                Intent printIntent = new Intent(GalleryActivity.this, PrintPhotosActivity.class);
                printIntent.setData(uri);
                Bundle bundle = new Bundle();
                bundle.putString("sizeInfo", adapter.getItem(i).toString());
                printIntent.putExtras(bundle);
                GalleryActivity.this.startActivity(printIntent);
            }
        });
        dialog.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                GalleryActivity.this.showCustomRatioDialog(uri);
            }
        });
        dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                GalleryActivity.this.showCustomRatioDialog(uri);
            }
        });
    }


    public void showCustomRatioDialog(Uri uri) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.setContentView(R.layout.custom_ratio);
        final EditText editTextWidth = (EditText) dialog.findViewById(R.id.dimen_width);
        final EditText editTextHeight = (EditText) dialog.findViewById(R.id.dimen_height);
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.unit_spinner);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter(this, R.layout.spiner_list, new String[]{getResources().getString(R.string.pixels), getResources().getString(R.string.milli), getResources().getString(R.string.centi), getResources().getString(R.string.inch)});
        unitAdapter.setDropDownViewResource(R.layout.spiner_list);
        spinner.setAdapter(unitAdapter);

        final Uri uri2 = uri;

        ((Button) dialog.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
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
                        sizeInfo = new SizeList(GalleryActivity.this.cmtoMM(width), GalleryActivity.this.cmtoMM(height), SizeList.MM, 0, "CM", "CM","");
                    }
                    if (selectedItemPosition == 3) {
                        sizeInfo = new SizeList(GalleryActivity.this.inchesToMM(width), GalleryActivity.this.inchesToMM(height), SizeList.MM, 0, "INCHES", "INCHES","");
                    }
                    dialog.dismiss();
                    Intent printIntent = new Intent(GalleryActivity.this, PrintPhotosActivity.class);
                    printIntent.setData(uri2);
                    Bundle bundle = new Bundle();
                    bundle.putString("sizeInfo", sizeInfo.toString());
                    printIntent.putExtras(bundle);
                    GalleryActivity.this.startActivity(printIntent);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(GalleryActivity.this, "Fill all details", 0).show();
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


}
