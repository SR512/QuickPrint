package com.srk.quickprint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class GuidActivity extends AppCompatActivity {

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);

        this.back = (ImageButton) findViewById(R.id.btn_back_info);

        this.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GuidActivity.this.finish();
            }
        });

    }

}
