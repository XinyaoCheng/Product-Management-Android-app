package com.example.a82.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a82.R;

public class MyViewHodler extends RecyclerView.ViewHolder{
    TextView nameView;
    TextView descVide;
    CardView card;
    public MyViewHodler(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.recycle_product_name);
        descVide = itemView.findViewById(R.id.recycle_product_desc);
        card = itemView.findViewById(R.id.recycle_product_item);
    }
}
