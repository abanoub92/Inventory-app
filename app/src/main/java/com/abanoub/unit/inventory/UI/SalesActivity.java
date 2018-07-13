package com.abanoub.unit.inventory.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.abanoub.unit.inventory.R;

public class SalesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
    }


    @Override
    public void finish() {
        super.finish();
        // add some transitions when activity finish
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
