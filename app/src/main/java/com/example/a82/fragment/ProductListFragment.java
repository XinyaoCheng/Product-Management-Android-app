package com.example.a82.fragment;

import android.app.DownloadManager;
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

import com.example.a82.R;
import com.example.a82.model.ProductModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView product_list_recycleview;
    Query query;
    FirebaseRecyclerAdapter productAdapter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductListFragment newInstance(String param1, String param2) {
        ProductListFragment fragment = new ProductListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        product_list_recycleview = rootView.findViewById(R.id.products_recycleview);
        String categoty = getArguments().getString("categorySelected");

        if(categoty!=null){
            setRecycleView(categoty);

        }

        return rootView;
    }

    private void setRecycleView(String category) {
        query = FirebaseDatabase.getInstance().getReference("products")
                .orderByChild("category")
                .equalTo(category);

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
                        Bundle mBundle= new Bundle();
                        Log.v("在product list里",model.toString());
                        mBundle.putString("name", model.getName());
                        mBundle.putString("price", model.getPrice());
                        mBundle.putString("supplier", model.getSupplier());
                        mBundle.putString("standard", model.getStandard());
                        mBundle.putString("amount", model.getAmount());
                        mBundle.putString("id",model.getId());
                        mBundle.putString("category",model.getCategory());
                        mBundle.putLong("expiry_time",model.getExpiry_time());
                        mBundle.putString("barcode",model.getBarcode());
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
        product_list_recycleview.setLayoutManager(manager);
        product_list_recycleview.setAdapter(productAdapter);

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