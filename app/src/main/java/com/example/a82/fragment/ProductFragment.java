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
import com.example.a82.activity.BarcodeShowActivity;
import com.example.a82.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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
    Button edit_button,save_button, delete_button,generate_button;

    EditText name, supplier,standard,price, amount, expiry_year, expiry_month,expiry_day;
    Spinner category_spinner;
    //tranlaition api's KEY, URL
    private String API_KEY = "AIzaSyCxDc0s5_Qor-_kzKXCfof5esTuwR7csxI";
    private static String REQUEST_URL = "https://www.googleapis.com/language/translate/v2";
    //this two is for generating signiture
    private static String PACKAGE_KEY = "X-Android-Package";
    private static String SHA1_KEY = "X-Android-Cert";
    //need ID and SECRET to access product bibery
    String API_ID = "zqmvtlempyythelg";
    String API_SECRET = "ZXFMSklEUmFhbWJzTkRZMCtaSCsxdz09";
    ProductModel productModel;
    Boolean isNewProduct = true;
    String id, barcode;

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
        //get barcode
        barcode = bundle.getString("barcode");

        //need to see if it exist in database or add a new one
        if(bundle.getString("id")==null){
            isNewProduct = true;
            setInfo(name,bundle.getString("name","unknown"));
            setInfo(price,bundle.getString("price","unknown"));
            setInfo(supplier,bundle.getString("supplier","unknown"));
            setInfo(standard,bundle.getString("standard","unknown"));

        }else{
            id = bundle.getString("id").toString();
            category = bundle.getString("category").toString();
            setSpinner(category);
            isNewProduct = false;
            name.setText(bundle.getString("name","unknown"));
            price.setText(bundle.getString("price","unknown"));
            supplier.setText(bundle.getString("supplier","unknown"));
            standard.setText(bundle.getString("standard","unknown"));
            amount.setText(bundle.getString("amount","unknown"));
            if(bundle.containsKey("expiry_time")) {
                Date date = new Date(bundle.getLong("expiry_time"));
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                //convert timestamp to year, month and day
                String year = yearFormat.format(date);
                String month = monthFormat.format(date);
                String day = dayFormat.format(date);
                expiry_year.setText(year);
                expiry_month.setText(month);
                expiry_day.setText(day);
            }
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
                            Log.v("即将删除的是", snapshot.toString());
                            snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getContext(), "success to remove this item", Toast.LENGTH_SHORT).show();
                                        HomeFragment homeFragment = new HomeFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.home_fragmentView, homeFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        setUnEditable();
                                    } else{
                                        Toast.makeText(getContext(), "fail to remove this item"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }


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

        //generate barcode
        generate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateBarcode();
            }
        });
        return rootview;
    }

    private void generateBarcode() {
        String url = "https://www.mxnzp.com/api/barcode/create?content="
                +barcode
                +"&width=500&height=300&type=0"
                +"&app_id="+API_ID
                +"&app_secret="+API_SECRET;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.v("url to generate barcode",url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.v("no such product in libery",e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    if(response_json.optString("code").equals("1")){
                        JSONObject data = response_json.getJSONObject("data");
                        //go on to barcode showing page with barcode image url
                        Intent intent = new Intent(getContext(), BarcodeShowActivity.class);
                        intent.putExtra("barcode_url",data.optString("barCodeUrl"));
                        startActivity(intent);

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

    //set category into spinner
    private void setSpinner(String category) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) category_spinner.getAdapter();
        if(adapter!=null){
            int position = adapter.getPosition(category);
            category_spinner.setSelection(position);
        }
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
        generate_button = rootview.findViewById(R.id.generate_button);

    }

    //set all box enable to edit
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

    //set all box unenable to edit
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
        //Determine whether he needs to add new items or modify
        if(isNewProduct){
            //it's new, need to be added to database
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
        }else{
            //there has already exited this product, so just update its info
            DatabaseReference update_ref = databaseRef.child(id);
            productModel.setId(id);
            Map<String,Object> updateData = productModel.toMap();
            update_ref.updateChildren(updateData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "success on updating item", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "fail to update, please try again, error:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

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
                    standard.getText().toString(),
                    amount.getText().toString(),
                    category,
                    "",
                    timestamp,
                    barcode);
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
                Log.e("fail to translate", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    JSONObject resObject = response_json.getJSONObject("data");
                    JSONArray translations = resObject.getJSONArray("translations");
                    String result = translations.getJSONObject(0).getString("translatedText");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(result);
                        }
                    });

                } catch (JSONException e) {
                    Log.e("fail to translate",e.getMessage());
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