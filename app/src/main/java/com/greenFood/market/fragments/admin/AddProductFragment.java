package com.greenFood.market.fragments.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentAddProductBinding;
import com.greenFood.market.entities.Catalog;
import com.greenFood.market.entities.Product;

import java.util.ArrayList;
import java.util.List;


public class AddProductFragment extends Fragment {
    private FragmentAddProductBinding mBinding;
    private CollectionReference mReference;
    private List<Product> mProductList;
    private List<String> mCatalogList;
    boolean isExist = false;

    public AddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance() {
        AddProductFragment fragment = new AddProductFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddProductBinding.inflate(inflater,container,false);
        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_CATALOG);
        mCatalogList = new ArrayList<>();
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
                    mCatalogList.add(snapshot.toObject(Catalog.class).getName());
                }
//                if (!mCatalogList.isEmpty() && mCatalogList != null){
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),R.layout.catalog_item,mCatalogList);
//                    mBinding.productCatalogSpinner.setAdapter(adapter);
//                }
            }
        });



        mBinding.addProductBtnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });


        mBinding.productImageEtt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String url = charSequence.toString();

                if (!URLUtil.isValidUrl(url)){
                    mBinding.productImageEtt.setError("Введите правильную ссылку");
                    mBinding.productImageEtt.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return mBinding.getRoot();
    }

    private void addProduct(){
        String name = mBinding.productNameEtt.getText().toString().trim();
        String price = mBinding.productPriceEtt.getText().toString().trim();
        String image = mBinding.productImageEtt.getText().toString().trim();
        String description = mBinding.productDescriptionEtt.getText().toString().trim();
        String country = mBinding.productCountryEtt.getText().toString().trim();
        String count = mBinding.productCountEtt.getText().toString().trim();
        boolean pesticide = mBinding.pesticideCb.isChecked();
//        String catalog = (String) mBinding.productCatalogSpinner.getSelectedItem();
        String catalog = mBinding.productCatalogEtt.getText().toString().trim();

        if (name.isEmpty()){
            mBinding.productNameEtt.setError("Введите название продукта");
            mBinding.productNameEtt.requestFocus();
            return;
        }
        if (price.isEmpty()){
            mBinding.productPriceEtt.setError("Введите цену продукта");
            mBinding.productPriceEtt.requestFocus();
            return;
        }
        if (image.isEmpty()){
            mBinding.productImageEtt.setError("Введите ссыдку изображения продукта");
            mBinding.productImageEtt.requestFocus();
            return;
        }
        if (description.isEmpty()){
            mBinding.productDescriptionEtt.setError("Введите описание продукта");
            mBinding.productDescriptionEtt.requestFocus();
            return;
        }
        if (country.isEmpty()){
            mBinding.productCountryEtt.setError("Введите страну производства продукта");
            mBinding.productCountryEtt.requestFocus();
            return;
        }
        if (count.isEmpty()){
            mBinding.productCountEtt.setError("Введите количество продукта");
            mBinding.productCountEtt.requestFocus();
            return;
        }

        if (URLUtil.isValidUrl(image)){
            Product product = new Product(Integer.parseInt(price),image,description,name,country,Integer.parseInt(count),pesticide, catalog);

            CollectionReference mRef = FirebaseFirestore.getInstance().collection(Const.NODE_PRODUCT);
            mRef.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.profileAuthenticatedAdminFragment);
                }
            });
        }

    }
}