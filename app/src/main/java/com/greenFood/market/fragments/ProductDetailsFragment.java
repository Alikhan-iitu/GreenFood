package com.greenFood.market.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentProductDetailsBinding;
import com.greenFood.market.databinding.ProductItemBinding;
import com.greenFood.market.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ThisClassReceiver;


public class ProductDetailsFragment extends Fragment {
    private FragmentProductDetailsBinding mBinding;
    private CollectionReference mReference;
    private List<Product> mProductList;
    private String productName;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    public static ProductDetailsFragment newInstance() {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.productName = getArguments().getString(Const.REQUEST_PRODUCT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProductDetailsBinding.inflate(inflater,container,false);
        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_PRODUCT);

        mProductList = new ArrayList<>();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);



        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    Product product = snapshot.toObject(Product.class);
                    mProductList.add(product);
                }
                if (!mProductList.isEmpty()){
                    for (Product product: mProductList){
                        if (product.getName().equals(productName)){
                            mBinding.productNameEt.setText(product.getName());
                            mBinding.productDescriptionEt.setText(product.getDescription());
                            mBinding.productPriceEt.setText(String.valueOf(product.getPrice()+" tg/kg"));
//                            mBinding.productCountEt.setText(String.valueOf(product.getCount()));
//                            mBinding.productCatalogEt.setText(product.getCatalogId());
//                            mBinding.productImageEt.setText(product.getImage());
                            Picasso.get().load(product.getImage()).into(mBinding.productImageEtp);
                            mBinding.productCountryEt.setText("Producing country: "+product.getCountry());
                            mBinding.pesticideCb.setChecked(product.getPesticide());
//                            mBinding.addToCard.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    boolean inBasket = false;
//
//
//                                    for (Product p: mProductList){
//                                        Log.d("TAG", "onClick: "+p.getName());
//                                        Log.d("TAG", "onClick: "+product.getName());
//                                        if (p.getName().equals(product.getName())){
//                                            inBasket = true;
//                                            pr = p;
//                                        }
//                                    }
//
//                                    if (inBasket){
//                                        pr.setCount(pr.getCount()+1);
//                                        mRepository.updateProduct(pr);
//                                    }
//                                    else{
//                                        product.setCount(1);
//                                        mRepository.insertProduct(product);
//                                    }
//                                }
//                            });
                            break;
                        }
                    }
                }
            }
        });

        return mBinding.getRoot();
    }
}