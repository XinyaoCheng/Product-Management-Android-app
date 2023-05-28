package com.example.a82.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a82.R;
import com.example.a82.model.ProductModel;
import com.example.a82.util.TranslateUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.BaseEncoding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String category;
    Button edit_button,save_button, delete_button;

    EditText name, supplier,standard,price, amount, expiry_year, expiry_month,expiry_day;
    Spinner category_spinner;
    private String API_KEY = "AIzaSyCxDc0s5_Qor-_kzKXCfof5esTuwR7csxI";
    private static String REQUEST_URL = "https://www.googleapis.com/language/translate/v2";
    private static String PACKAGE_KEY = "X-Android-Package";
    private static String SHA1_KEY = "X-Android-Cert";
    private String result = "";
    ProductModel productModel;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_product, container, false);
        initial(rootview);
        Bundle bundle = getArguments();
//        if(bundle.getInt("new_product?",0)==1){
        setInfo(name,bundle.getString("name","unknown"));
        setInfo(price,bundle.getString("price","unknown"));
        setInfo(supplier,bundle.getString("supplier","unknown"));
        setInfo(standard,bundle.getString("standard","unknown"));
        if(bundle.getString("expiry_year")!=null){
            expiry_year.setText(bundle.getString("expiry_year"));
        }
        if(bundle.getString("expiry_month")!=null){
            expiry_month.setText(bundle.getString("expiry_month"));
        }
        if(bundle.getString("expiry_day")!=null){
            expiry_day.setText(bundle.getString("expiry_day"));
        }
        setUnEditable();
        //spinner slected
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (String) adapterView.getItemAtPosition(i);
                Log.v("选择的种类是",category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        //save clicked:
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLegality()){
                    Log.v("点击save按钮","合法");
                    saveInFirebase();

                }
            }
        });

        //edit clicked:
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditable();
            }
        });

        //delete clicked
        delete_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String delete_id = bundle.getString("id");
                if(delete_id!=null){
                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("products").child(delete_id);
                    deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "success to remove this item", Toast.LENGTH_SHORT).show();
                                    HomeFragment homeFragment = new HomeFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.home_fragmentView, homeFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    setUnEditable();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        return rootview;
    }

    private void initial(View rootview) {
        edit_button = rootview.findViewById(R.id.edit_button);
        save_button = rootview.findViewById(R.id.save_button);
        name = rootview.findViewById(R.id.product_name);
        supplier = rootview.findViewById(R.id.product_supplier);
        standard = rootview.findViewById(R.id.product_standard);
        price = rootview.findViewById(R.id.product_price);
        amount = rootview.findViewById(R.id.product_amount);
        expiry_year = rootview.findViewById(R.id.product_expiry_year);
        expiry_month = rootview.findViewById(R.id.product_expiry_month);
        expiry_day = rootview.findViewById(R.id.product_expiry_day);
        category_spinner = rootview.findViewById(R.id.category_spinner);
        delete_button = rootview.findViewById(R.id.delete_button);

    }

    private void setEditable() {
        name.setEnabled(true);
        name.setBackgroundResource(R.drawable.border);
        price.setEnabled(true);
        price.setBackgroundResource(R.drawable.border);
        supplier.setEnabled(true);
        supplier.setBackgroundResource(R.drawable.border);
        standard.setEnabled(true);
        standard.setBackgroundResource(R.drawable.border);
        amount.setEnabled(true);
        amount.setBackgroundResource(R.drawable.border);
        expiry_day.setEnabled(true);
        expiry_day.setBackgroundResource(R.drawable.border);
        expiry_month.setEnabled(true);
        expiry_month.setBackgroundResource(R.drawable.border);
        expiry_year.setEnabled(true);
        expiry_year.setBackgroundResource(R.drawable.border);
        category_spinner.setEnabled(true);
    }

    public void setUnEditable(){
        name.setEnabled(false);
        name.setBackgroundResource(R.drawable.blank);
        price.setEnabled(false);
        price.setBackgroundResource(R.drawable.blank);
        supplier.setEnabled(false);
        supplier.setBackgroundResource(R.drawable.blank);
        standard.setEnabled(false);
        standard.setBackgroundResource(R.drawable.blank);
        amount.setEnabled(false);
        amount.setBackgroundResource(R.drawable.blank);
        expiry_day.setEnabled(false);
        expiry_day.setBackgroundResource(R.drawable.blank);
        expiry_month.setEnabled(false);
        expiry_month.setBackgroundResource(R.drawable.blank);
        expiry_year.setEnabled(false);
        expiry_year.setBackgroundResource(R.drawable.blank);
        category_spinner.setEnabled(false);
    }
    private void saveInFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("products/");
        //TODO:select from existing product
        String id = databaseRef.push().getKey();
        productModel.setId(id);
        databaseRef.child(id).setValue(productModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "success to create your new product", Toast.LENGTH_SHORT).show();
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.home_fragmentView, homeFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        setUnEditable();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "sorry, failed to add this item, error:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private boolean checkLegality() {
        if(TextUtils.isEmpty(name.getText())){
            name.setError("product name is requied");
            return false;
        }
        if(TextUtils.isEmpty(price.getText())){
            price.setError("price is requied");
            return false;
        }
        if(TextUtils.isEmpty(supplier.getText())){
            supplier.setError("supplier is requied");
            return false;
        }
        if(TextUtils.isEmpty(standard.getText())){
            standard.setError("standard is requied");
            return false;
        }
        if(TextUtils.isEmpty(amount.getText())){
            amount.setError("amount is requied");
            return false;
        }
        if(TextUtils.isEmpty(expiry_year.getText())){
            expiry_year.setError("expiry data is requied");
            return false;
        }
        if(TextUtils.isEmpty(expiry_month.getText())){
            expiry_month.setError("expiry month is requied");
            return false;
        }
        if(category==null){
            new AlertDialog.Builder(getActivity())
                    .setTitle("can't save")
                    .setMessage("you haven't chose its category!")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }else{

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(expiry_year.getText().toString()));
            calendar.set(Calendar.MONTH, Integer.parseInt(expiry_month.getText().toString()) - 1); // 月份从0开始，需要减去1
            if(TextUtils.isEmpty(expiry_day.getText())){
                //user didn't enter expiry day
                //defaultly set it 1
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            }else{
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(expiry_day.getText().toString()));
            }
            Date date = calendar.getTime();
            long timestamp = date.getTime();
            productModel = new ProductModel(name.getText().toString(),
                    price.getText().toString(),
                    supplier.getText().toString(),
                    amount.getText().toString(),
                    standard.getText().toString(),
                    category,
                    "",
                    timestamp);
        }



        return true;

    }
// translate the information of product with tranlation api
// and set txt to EditView

    public void setInfo(EditText view,String content){
        JSONArray array = new JSONArray();
        array.put(content);
        OkHttpClient okHttpClient  = new OkHttpClient();
        String signiture = getSignature(getContext().getPackageManager(), getContext().getPackageName());
        FormBody.Builder builder = new FormBody.Builder()
                .add("key",API_KEY)
                .add("target", "en")
                .add("q",content);
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .header(PACKAGE_KEY,getContext().getPackageName())
                .header(SHA1_KEY,signiture)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("翻译错误", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    Log.v("翻译相应结果",response_json.toString());
                    JSONObject resObject = response_json.getJSONObject("data");
                    JSONArray translations = resObject.getJSONArray("translations");
                    result = translations.getJSONObject(0).getString("translatedText");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(result);
                            Log.v("翻译结果",result);
                        }
                    });

                } catch (JSONException e) {
                    Log.e("翻译错误",e.getMessage());
                }
            }
        });
    }

    //signature for translator
    private String getSignature(PackageManager packageManager, String packageName) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }


    }

    private String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }
}