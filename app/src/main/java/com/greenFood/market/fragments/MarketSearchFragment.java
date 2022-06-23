package com.greenFood.market.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.DB.BasketRepository;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentMarketSearchBinding;
import com.greenFood.market.databinding.ProductItemBinding;
import com.greenFood.market.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MarketSearchFragment extends Fragment {
    private FragmentMarketSearchBinding mBinding;
    private CollectionReference mReference;
    private List<Product> mProductList;
    private List<Product> mBasketProductList;
    private BasketRepository mRepository;
    public MarketSearchFragment() {
        // Required empty public constructor
    }

    public static MarketSearchFragment newInstance(String param1, String param2) {
        MarketSearchFragment fragment = new MarketSearchFragment();
;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMarketSearchBinding.inflate(inflater,container,false);


        mProductList = new ArrayList<>();
        mBasketProductList = new ArrayList<>();


        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_PRODUCT);



        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    Product product = snapshot.toObject(Product.class);
                    mProductList.add(product);
                }
                if (!mProductList.isEmpty()){
                    mBinding.productRecyclerView.setAdapter(new ProductAdapter(mProductList));
                }
            }
        });

        mBinding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchProduct();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.pesticideCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                searchProduct();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        mBinding.productRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));


        mRepository = BasketRepository.getBasketRepository();
        mBasketProductList = mRepository.getAllProduct();

        return mBinding.getRoot();
    }

    private void searchProduct(){
        String name = mBinding.searchEt.getText().toString().trim();
        boolean pesticide = mBinding.pesticideCb.isChecked();
        if (!mProductList.isEmpty()){
            List<Product> products = new ArrayList<>();
            if (name.isEmpty()){
                for (Product p: mProductList){
                    if (p.getPesticide() == pesticide){
                        products.add(p);
                    }
                }
            }
            else{
                for (Product p: mProductList){
                    if (!name.isEmpty() && name.equals(p.getName()) && p.getPesticide() == pesticide){
                        products.add(p);
                    }
                }
            }

            mBinding.productRecyclerView.setAdapter(new ProductAdapter(products));
        }
    }


    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        private ProductItemBinding mProductItemBinding;

        public ProductsViewHolder(ProductItemBinding productItemBinding) {
            super(productItemBinding.getRoot());
            mProductItemBinding = productItemBinding;
            Log.d("TAG", "ProductsViewHolder: "+mProductItemBinding.toString());
        }

        public void bind(Product product){
            mProductItemBinding.productName.setText(product.getName());
            mProductItemBinding.productAddToBasket.setText((product.getPrice()+" тг"));
            Picasso.get().load(product.getImage()).into(mProductItemBinding.productImage);
            mProductItemBinding.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString(Const.REQUEST_PRODUCT, product.getName());
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_marketSearchFragment_to_productDetailsFragment,args);

                }
            });
            mProductItemBinding.productAddToBasket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean inBasket = false;
                    Product pr = product;

                    for (Product p: mBasketProductList){
                        if (p.getName().equals(product.getName())){
                            inBasket = true;
                            pr = p;
                        }
                    }

                    if (inBasket){
                        pr.setCount(pr.getCount()+1);
                        mRepository.updateProduct(pr);
                    }
                    else{
                        product.setCount(1);
                        mRepository.insertProduct(product);
                    }
                }
            });
        }
    }


    private class ProductAdapter extends RecyclerView.Adapter<ProductsViewHolder>{
        private List<Product> mProducts;

        public ProductAdapter(List<Product> products) {
            mProducts = products;
        }

        @NonNull
        @Override
        public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            ProductItemBinding productItemBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new ProductsViewHolder(productItemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
            holder.bind(mProducts.get(position));

        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }
    }
}