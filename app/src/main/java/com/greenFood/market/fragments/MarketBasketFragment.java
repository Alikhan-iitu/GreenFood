package com.greenFood.market.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.greenFood.market.Const;
import com.greenFood.market.DB.BasketRepository;
import com.greenFood.market.R;
import com.greenFood.market.databinding.BasketItemBinding;
import com.greenFood.market.databinding.FragmentMarketBasketBinding;
import com.greenFood.market.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MarketBasketFragment extends Fragment {
    private FragmentMarketBasketBinding mBinding;
    private List<Product> mBasketProductList;
    private BasketRepository mRepository;
    private FirebaseAuth mAuth;

    public MarketBasketFragment() {
        // Required empty public constructor
    }

    public static MarketBasketFragment newInstance(String param1, String param2) {
        MarketBasketFragment fragment = new MarketBasketFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMarketBasketBinding.inflate(inflater,container,false);
        mAuth = FirebaseAuth.getInstance();
        mRepository = BasketRepository.getBasketRepository();
        mBasketProductList = mRepository.getAllProduct();

        if (mBasketProductList.isEmpty()){
            mBinding.emptyBasketLayout.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.emptyBasketLayout.setVisibility(View.INVISIBLE);
            mBinding.basketRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mBinding.basketRecyclerView.setAdapter(new ProductAdapter(mBasketProductList));
        }

        mBinding.goToCatalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.marketCatalogFragment);
            }
        });

        return mBinding.getRoot();
    }


    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        private BasketItemBinding basketItemBinding;

        public ProductsViewHolder(BasketItemBinding basketItemBinding) {
            super(basketItemBinding.getRoot());
            this.basketItemBinding = basketItemBinding;
            Log.d("TAG", "ProductsViewHolder: "+ this.basketItemBinding.toString());
        }

        public void bind(Product product, ProductAdapter adapter){
            basketItemBinding.productNameEt.setText(product.getName());
            basketItemBinding.productPriceEt.setText((product.getPrice()+" тг"));
            Picasso.get().load(product.getImage()).into(basketItemBinding.productImageIv);

            basketItemBinding.productCountEt.setText(String.valueOf(product.getCount()));

            basketItemBinding.minusCountImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product pr = product;
                    pr.setCount(pr.getCount()-1);
                    if (pr.getCount() <1){
                        mRepository.deleteProduct(pr);
                        adapter.deleteUI(pr);
                    }
                    else {
                        mRepository.updateProduct(pr);
                        adapter.updateUI(pr);
                    }
                }
            });


            basketItemBinding.plusCountImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product pr = product;
                    pr.setCount(pr.getCount()+1);
                    mRepository.updateProduct(pr);
                    adapter.updateUI(pr);
                }
            });

            basketItemBinding.removeProductImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRepository.deleteProduct(product);
                    adapter.deleteUI(product);
                }
            });

            basketItemBinding.productImageIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString(Const.REQUEST_PRODUCT, product.getName());
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_mainMarketFragment_to_productDetailsFragment,args);
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

            BasketItemBinding basketItemBinding = BasketItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new ProductsViewHolder(basketItemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
            holder.bind(mProducts.get(position), this);

        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        private void updateUI(Product product){
            int i = -1;
            for (int j=0;j<mProducts.size();j++){
                if (mProducts.get(j).getName().equals(product.getName())){
                    i = j;
                    break;
                }
            }
            mProducts.set(i, product);
            notifyItemChanged(i);

        }
        private void deleteUI(Product product){
            Product prd = null;
            int i = -1;
            for (int j=0;j<mProducts.size();j++){
                if (mProducts.get(j).getName().equals(product.getName())){
                    i = j;
                    prd = mProducts.get(j);
                    break;
                }
            }
            if (prd != null){
                mProducts.remove(prd);
                notifyItemRemoved(i);
            }
            if (mProducts.size()==0){
                mBinding.emptyBasketLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}