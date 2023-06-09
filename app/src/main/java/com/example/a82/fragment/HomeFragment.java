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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.a82.R;
import com.example.a82.activity.MainActivity;
import com.example.a82.model.ProductModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

    RecyclerView product_expiry_rv;
    Query query;
    FirebaseRecyclerAdapter productAdapter;
    //product libery require ID and SECRET to access
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
        product_expiry_rv = rootView.findViewById(R.id.expiry_recycleView);
        //we have five icons to classify products:
        //beverages, dairy,snacks, personal care product and household cleaning product
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
        //when click add button, it'll bring you tou scan barcode
        add_button.setOnClickListener(this);

        //set recycleview of expiry time approached products
        setRecycleView();
        return rootView;
    }

    private void setRecycleView() {

        Date currentDate = new Date();

        // get current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Log.v("current time",String.valueOf(currentDate.getTime()));
        // the date of one month later
        calendar.add(Calendar.MONTH, 1);
        Date oneMonthLater = calendar.getTime();
        Log.v("a month later from now",String.valueOf(oneMonthLater.getTime()-1));

        //create a new query, to select the expiry date between now and one month later
        query = FirebaseDatabase.getInstance().getReference("products")
                .orderByChild("expiry_time")
                .startAt(currentDate.getTime())
                .endAt(oneMonthLater.getTime());

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(query, new SnapshotParser<ProductModel>() {
                            @NonNull
                            @Override
                            public ProductModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                ProductModel model = new ProductModel(snapshot.child("name").getValue().toString(),
                                        snapshot.child("price").getValue().toString(),
                                        snapshot.child("supplier").getValue().toString(),
                                        snapshot.child("standard").getValue().toString(),
                                        snapshot.child("amount").getValue().toString(),
                                        snapshot.child("category").getValue().toString(),
                                        snapshot.child("id").getValue().toString(),
                                        snapshot.child("expiry_time").getValue(Long.class),
                                        snapshot.child("barcode").getValue().toString());
                                Log.v("时间戳测试",String.valueOf(model.getExpiry_time()));
                                return model;
                            }
                        })
                        .build();

        productAdapter = new FirebaseRecyclerAdapter<ProductModel, MyViewHodler>(options) {
            @NonNull
            @Override
            public MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item, parent, false);

                return new MyViewHodler(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHodler holder, int position, @NonNull ProductModel model) {

                    holder.nameView.setText(model.getName());
                    holder.descVide.setText(model.toString());
                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //translate all product information to profuct Fragment
                            Bundle mBundle= new Bundle();
                            mBundle.putString("name", model.getName());
                            mBundle.putString("price", model.getPrice());
                            mBundle.putString("supplier", model.getSupplier());
                            mBundle.putString("standard", model.getStandard());
                            mBundle.putString("id",model.getId());
                            mBundle.putString("barcode",model.getBarcode());
                            mBundle.putString("amount", model.getAmount());
                            mBundle.putString("category",model.getCategory());
                            mBundle.putLong("expiry_time",model.getExpiry_time());
                            ProductFragment productFragment = new ProductFragment();
                            productFragment.setArguments(mBundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.home_fragmentView, productFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                }


        };
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(),1);
        product_expiry_rv.setLayoutManager(manager);
        product_expiry_rv.setAdapter(productAdapter);

    }

    //when click each icon, it'll jump to correct product list
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


    //after get barcode from google barcode scanner,
    //we will make a request to product libery to get correct product info
    public void getProduct(String rawValue) {
        String url = "https://www.mxnzp.com/api/barcode/goods/details?barcode="
                +rawValue
                +"&app_id="+API_ID
                +"&app_secret="+API_SECRET;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.v("url for product libery",url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.v("fail to get product info",e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    //code = 1 --->success
                    //code = 0 --->there's no such a product in libery
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

    //jump to product detailed page
    public void jumpToProduct(JSONObject good) {
        Bundle mBundle= new Bundle();
        mBundle.putInt("new_product?", 1);
        mBundle.putString("name", good.optString("goodsName"));
        mBundle.putString("price", good.optString("price"));
        mBundle.putString("supplier", good.optString("supplier"));
        mBundle.putString("standard", good.optString("standard"));
        mBundle.putString("barcode",good.optString("barcode"));
        ProductFragment productFragment = new ProductFragment();
        productFragment.setArguments(mBundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragmentView, productFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    //google barcode scanner
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
                    Log.v("success to scan",rawValue);
                    getProduct(rawValue);


                })
                .addOnFailureListener(e -> {
                    Log.v("fail to scan",e.getMessage());
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        productAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}