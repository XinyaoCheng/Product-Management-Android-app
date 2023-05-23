package com.example.a82;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button beverages_button, dairy_button, snacks_button, personal_care_button, household_clean_button;
    LinearLayout add_button;
    ProductModel productModel;
    String API_KEY="AIzaSyD49T7ONAW6nPMGpsoz52BNwj_nBRv5xoU";


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        add_button = rootView.findViewById(R.id.add_linearLayout);


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .build();
                GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(getContext());
                scanner.startScan()
                        .addOnSuccessListener(barcode -> {
                             String rawValue= barcode.getRawValue();
                            Log.v("扫描成功",rawValue);
                            ProductModel product = getProduct(rawValue);
                            startActivity(new Intent(getContext(),AddProductActivity.class));

                        })
                        .addOnFailureListener(e -> {
                            Log.v("扫描失败",e.getMessage());
                        });

            }
        });
        return rootView;
    }

    private ProductModel getProduct(String rawValue) {
        String url = "https://api.barcodelookup.com/v3/products?barcode="
                +rawValue
                +"&formatted=y&key="+API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.v("url地址",url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.v("获取商品失败",e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    JSONObject good = response_json.getJSONObject("product");
                    String name = good.optString("title");
                    String category = good.optString("category");
                    String manufacturer = good.optString("manufacturer");
                    String image_url = good.optString("images");
                    Log.v("Product MOdel", productModel.toString());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });
        return productModel;
    }


}
