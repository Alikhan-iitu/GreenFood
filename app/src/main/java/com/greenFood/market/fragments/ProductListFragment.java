package com.greenFood.market.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.greenFood.market.databinding.FragmentProductListBinding;
import com.greenFood.market.databinding.ProductItemBinding;
import com.greenFood.market.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    private FragmentProductListBinding mBinding;
    private CollectionReference mReference;
    private List<Product> mProductList;
    private List<Product> mBasketProductList;
    private BasketRepository mRepository;
    private String catalog;


    public ProductListFragment() {
        // Required empty public constructor
    }

    public static ProductListFragment newInstance(String catalog) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.REQUEST_PRODUCT_FROM_CATALOG,catalog);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProductListBinding.inflate(inflater,container,false);
        this.catalog = getArguments().getString(Const.REQUEST_PRODUCT_FROM_CATALOG);
        mProductList = new ArrayList<>();
        mBasketProductList = new ArrayList<>();


        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_PRODUCT);

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    Product product = snapshot.toObject(Product.class);
                    if (product.getCatalogId().equals(catalog)){
                        Log.d("TAG", "onSuccess: "+catalog);
                        Log.d("TAG", "onSuccess: "+product.getCatalogId());
                        mProductList.add(product);
                    }
                }
                if (!mProductList.isEmpty()){
                    mBinding.productsRecyclerView.setAdapter(new ProductAdapter(mProductList));
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        mBinding.productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));


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
                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_productListFragment_to_productDetailsFragment,args);
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