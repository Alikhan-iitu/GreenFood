package com.greenFood.market.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.CatalogItemBinding;
import com.greenFood.market.databinding.FragmentMarketCatalogBinding;
import com.greenFood.market.entities.Catalog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MarketCatalogFragment extends Fragment {
    private FragmentMarketCatalogBinding mFragmentMarketCatalogBinding;
    private String addToBackStackMarketCatalog = "addToBackStackMarketCatalog";
    private CollectionReference mReference;
    private ArrayList<Catalog> mCatalogList;

    public MarketCatalogFragment() {
        // Required empty public constructor
    }

    public static MarketCatalogFragment newInstance(String param1, String param2) {
        MarketCatalogFragment fragment = new MarketCatalogFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentMarketCatalogBinding = FragmentMarketCatalogBinding.inflate(inflater,container,false);

        RecyclerView recyclerView = mFragmentMarketCatalogBinding.catalogRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_CATALOG);


        mCatalogList = new ArrayList<>();


        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mCatalogList.add(snapshot.toObject(Catalog.class));
                }

                if (!mCatalogList.isEmpty() && mCatalogList != null){
                    recyclerView.setAdapter(new MarketCatalogAdapter(mCatalogList));
                    Log.d("TAG", "onSuccess: mCatalogList class" + mCatalogList.get(0).getName());
                }
            }
        });




        return mFragmentMarketCatalogBinding.getRoot();
    }

    private class MarketCatalogViewHolder extends RecyclerView.ViewHolder{
        private CatalogItemBinding catalogItemBinding;
//        private Catalog mCatalog;
//        private TextView mTextView;
        public MarketCatalogViewHolder(CatalogItemBinding catalogItemBinding) {
            super(catalogItemBinding.getRoot());
            this.catalogItemBinding = catalogItemBinding;
            Log.d("TAG", "MarketCatalogViewHolder: "+ this.catalogItemBinding.toString());
        }

        public void bind(Catalog catalog){
            Picasso.get().load(catalog.getCategoryImage()).into(catalogItemBinding.productImageIv);
            catalogItemBinding.textView.setText(catalog.getName());

            catalogItemBinding.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString(Const.REQUEST_PRODUCT_FROM_CATALOG, catalog.getName());
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_marketCatalogFragment_to_productListFragment,args);
                }
            });
        }


//        @Override
//        public void onClick(View view) {
//            Bundle args = new Bundle();
//            args.putString(Const.REQUEST_PRODUCT_FROM_CATALOG,mCatalog.getName());
//            Navigation.findNavController(view).navigate(R.id.action_marketCatalogFragment_to_productListFragment, args);
//        }
    }

    private class MarketCatalogAdapter extends RecyclerView.Adapter<MarketCatalogViewHolder>{
        private ArrayList<Catalog> mCatalogs;

        public MarketCatalogAdapter(ArrayList<Catalog> catalogs) {
            mCatalogs = catalogs;
        }

        @NonNull
        @Override
        public MarketCatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            TextView textView = new TextView(getActivity());
//            textView.setTextSize(18);
            CatalogItemBinding catalogItemBinding = CatalogItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new MarketCatalogViewHolder(catalogItemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull MarketCatalogViewHolder holder, int position) {
            holder.bind(mCatalogs.get(position));
        }

        @Override
        public int getItemCount() {
            return mCatalogs.size();
        }
    }
}