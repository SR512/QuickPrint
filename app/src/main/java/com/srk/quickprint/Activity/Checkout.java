package com.srk.quickprint.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.novoda.merlin.MerlinsBeard;
import com.srk.quickprint.Lib.IUploadAPI;
import com.srk.quickprint.Lib.MyApplication;
import com.srk.quickprint.Lib.ProgressRequestBody;
import com.srk.quickprint.Lib.RetrofitClient;
import com.srk.quickprint.Lib.SharedPref;
import com.srk.quickprint.Lib.UplodCallBacks;
import com.srk.quickprint.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Checkout extends AppCompatActivity implements UplodCallBacks, AdapterView.OnItemSelectedListener {

    Button check_out;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    public static final String Url = "http://quickprint.xyz/quickprint/";
    IUploadAPI iUploadAPIService;
    private Uri photoUri;
    EditText eName, eMobile, eAddress;
    String sizein, size;
    MultipartBody.Part body;
    String filename;
    String PageSize = "No Size";
    MerlinsBeard merlin;
    String HeaderText;
    String strPapersize = "Select Paper Thickness";
    private boolean isFirst = false;
    int count;
    SharedPreferences sharedPref;

    private IUploadAPI getAPIUpload() {
        return RetrofitClient.getClient(Url).create(IUploadAPI.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        SharedPref.init(getApplicationContext(), "Count");

        Intent intent = getIntent();
        sizein = intent.getExtras().getString("sizeInfo");

        merlin = MerlinsBeard.from(getApplicationContext());

        eName = (EditText) findViewById(R.id.textView);
        eMobile = (EditText) findViewById(R.id.c_mobileno);
        eAddress = (EditText) findViewById(R.id.editText);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.papertinkness);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> paper_size = new ArrayList<String>();
        paper_size.add("Select Paper Thickness");
        paper_size.add("50 GSM");
        paper_size.add("64 GSM");
        paper_size.add("75 GSM");
        paper_size.add("80 GSM");
        paper_size.add("90 GSM");
        paper_size.add("100 GSM");
        paper_size.add("105 GSM");
        paper_size.add("110 GSM");
        paper_size.add("120 GSM");
        paper_size.add("135 GSM");
        paper_size.add("148 GSM");
        paper_size.add("150 GSM");
        paper_size.add("163 GSM");
        paper_size.add("170 GSM");
        paper_size.add("176 GSM");
        paper_size.add("190 GSM");
        paper_size.add("199 GSM");
        paper_size.add("203 GSM");
        paper_size.add("216 GSM");
        paper_size.add("244 GSM");
        paper_size.add("250 GSM");
        paper_size.add("253 GSM");
        paper_size.add("264 GSM");
        paper_size.add("271 GSM");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paper_size);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        if (sizein.equals("GalleryFrom")) {
            this.photoUri = intent.getData();
            size = intent.getExtras().getString("size");

            try {

                JSONObject object = new JSONObject(size);

                int width = object.getInt("width");
                int height = object.getInt("height");
                HeaderText = object.getString("headText");
                String descText = object.getString("descText");
                String prictText = object.getString("priceText");

                PageSize = "Width:" + String.valueOf(width) + "," + "height:" + String.valueOf(height) + "," + "PageSize:" + HeaderText + "Desc:" + descText + "Price:" + prictText;


//               Toast.makeText(getApplicationContext(), PageSize.toString(), Toast.LENGTH_LONG).show();
                Log.d("Size", PageSize);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            this.photoUri = intent.getData();

        }

        iUploadAPIService = getAPIUpload();

        check_out = (Button) findViewById(R.id.checkout);

        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String MobilePattern = "[0-9]{10}";

                if (TextUtils.isEmpty(eName.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Name...!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(eMobile.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile...!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(eAddress.getText())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Address...!", Toast.LENGTH_LONG).show();
                } else if (!eMobile.getText().toString().matches(MobilePattern)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid 10 digit phone number...!", Toast.LENGTH_LONG).show();
                } else if (strPapersize.equals("Select Paper Thickness")) {
                    Toast.makeText(getApplicationContext(), "Please Select Paper GSM", Toast.LENGTH_LONG).show();
                } else {
                    if (merlin.isConnected()) {


                    } else {
                        Toast.makeText(getApplicationContext(), "Please Connect Network..!", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


        //        Toolbar

        toolbar = (Toolbar) findViewById(R.id.checkout_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onPause() {

        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
    }

    private String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);

        return orderId;
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressDialog.setProgress(percentage);
    }

    private void uploadfile() {

        PageSize += "Paper GSM :" + strPapersize;


        if (photoUri != null) {
            progressDialog = new ProgressDialog(Checkout.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.show();


            if (sizein.equals("GalleryFrom")) {
                File file = FileUtils.getFile(this, photoUri);

                ProgressRequestBody requestBody = new ProgressRequestBody(file, this);

                body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestBody);
            } else {
                File file = new File(String.valueOf(photoUri));

                filename = file.getName();

                Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_LONG).show();

                ProgressRequestBody requestBody = new ProgressRequestBody(file, this);

                body = MultipartBody.Part.createFormData("uploaded_file", filename, requestBody);

            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    iUploadAPIService.uploadFile(body, eName.getText().toString(), eMobile.getText().toString(), eAddress.getText().toString(), PageSize)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    progressDialog.dismiss();
                                    Log.d("Response", response.toString());
                                    Toast.makeText(Checkout.this, "Order SuccessFully Place...!", Toast.LENGTH_LONG).show();
                                    finish();

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Checkout.this, "Try Again..!", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }).start();
        } else {
            Toast.makeText(Checkout.this, "Try Again..!", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        strPapersize = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}