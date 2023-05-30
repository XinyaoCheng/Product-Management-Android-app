package com.example.a82.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.a82.R;
import com.squareup.picasso.Picasso;

public class BarcodeShowActivity extends AppCompatActivity {

    ImageView barcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_show);
        barcodeView = findViewById(R.id.barcode_image);
        String barcodeUrl = getIntent().getStringExtra("barcode_url");
        if(barcodeUrl!=null){
            //use Picasso libery to get the image with its url and then put that on ImageView
            Picasso.get().load(barcodeUrl).into(barcodeView);
        }
    }
}