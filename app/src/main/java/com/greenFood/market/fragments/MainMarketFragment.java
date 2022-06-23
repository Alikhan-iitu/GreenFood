package com.greenFood.market.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.greenFood.market.databinding.FragmentMainMarketBinding;
import com.greenFood.market.databinding.ProductItemBinding;
import com.greenFood.market.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MainMarketFragment extends Fragment {
    private FragmentMainMarketBinding mBinding;
    private CollectionReference mReference;
    private List<Product> mProductList;
    private List<Product> mBasketProductList;
    private BasketRepository mRepository;


    public MainMarketFragment() {
        // Required empty public constructor
    }

    public static MainMarketFragment newInstance() {
        MainMarketFragment fragment = new MainMarketFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMainMarketBinding.inflate(inflater,container,false);

        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_PRODUCT);
        mBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        mProductList = new ArrayList<>();

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mProductList.add(snapshot.toObject(Product.class));
                }
                mBinding.productsRecyclerView.setAdapter(new ProductAdapter(mProductList));
            }
        });



        mRepository = BasketRepository.getBasketRepository();
        mBasketProductList = mRepository.getAllProduct();


        return mBinding.getRoot();
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


            mProductItemBinding.productAddToBasket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean inBasket = false;
                    Product pr = product;

                    for (Product p: mBasketProductList){
                        Log.d("TAG", "onClick: "+p.getName());
                        Log.d("TAG", "onClick: "+product.getName());
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

            mProductItemBinding.productDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putString(Const.REQUEST_PRODUCT, product.getName());
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_mainMarketFragment_to_productDetailsFragment,args);
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