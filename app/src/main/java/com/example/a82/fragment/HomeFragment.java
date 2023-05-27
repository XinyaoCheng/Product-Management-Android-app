package com.example.a82.fragment;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.a82.R;
import com.example.a82.activity.MainActivity;
import com.example.a82.model.ProductModel;
import com.example.a82.util.TranslateUtil;
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
public class HomeFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout beverages_button, dairy_button, snacks_button, personal_care_button, household_clean_button;
    LinearLayout add_button;
    Context context;
    TranslateUtil translator = new TranslateUtil();
    String API_ID = "zqmvtlempyythelg";
    String API_SECRET = "ZXFMSklEUmFhbWJzTkRZMCtaSCsxdz09";

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
        beverages_button = rootView.findViewById(R.id.beverages_linearLayout);
        dairy_button = rootView.findViewById(R.id.dairy_linearLayout);
        snacks_button = rootView.findViewById(R.id.snacks_linearLayout);
        personal_care_button = rootView.findViewById(R.id.personal_care_linearLayout);
        household_clean_button = rootView.findViewById(R.id.household_and_cleaning_linearLayout);

        beverages_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToProductList("Beverages");
            }
        });
        dairy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToProductList("Dairy");
            }
        });
        snacks_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToProductList("Snacks");
            }
        });
        personal_care_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToProductList("Personal Care");
            }
        });
        household_clean_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToProductList("Household and Cleaning");
            }
        });
        add_button.setOnClickListener(this);
        return rootView;
    }

    private void jumpToProductList(String category) {
        Bundle mBundle= new Bundle();
        mBundle.putString("categorySelected", category);
        ProductListFragment productFragment = new ProductListFragment();
        productFragment.setArguments(mBundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragmentView, productFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void getProduct(String rawValue) {
        String url = "https://www.mxnzp.com/api/barcode/goods/details?barcode="
                +rawValue
                +"&app_id="+API_ID
                +"&app_secret="+API_SECRET;
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
                    if(response_json.optString("code").equals("1")){
                        JSONObject good = response_json.getJSONObject("data");

                        jumpToProduct(good);

                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Fail to find this good")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        });

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }

    public void jumpToProduct(JSONObject good) {
        Bundle mBundle= new Bundle();
        mBundle.putInt("new_product?", 1);
        mBundle.putString("name", good.optString("goodsName"));
        mBundle.putString("price", good.optString("price"));
        mBundle.putString("supplier", good.optString("supplier"));
        mBundle.putString("standard", good.optString("standard"));
        ProductFragment productFragment = new ProductFragment();
        productFragment.setArguments(mBundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragmentView, productFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

                }


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
                    getProduct(rawValue);


                })
                .addOnFailureListener(e -> {
                    Log.v("扫描失败",e.getMessage());
                });
    }
}
