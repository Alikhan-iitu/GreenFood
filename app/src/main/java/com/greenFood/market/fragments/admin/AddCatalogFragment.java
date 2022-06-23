package com.greenFood.market.fragments.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
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
import com.greenFood.market.databinding.FragmentAddCatalogBinding;
import com.greenFood.market.entities.Catalog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AddCatalogFragment extends Fragment {
    private FragmentAddCatalogBinding mBinding;
    private CollectionReference mReference;
    private List<Catalog> mCatalogList;
    boolean isExist = false;


    public AddCatalogFragment() {
        // Required empty public constructor
    }

    public static AddCatalogFragment newInstance() {
        AddCatalogFragment fragment = new AddCatalogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_CATALOG);
        mCatalogList = new ArrayList<>();


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
                    mCatalogList.add(snapshot.toObject(Catalog.class));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddCatalogBinding.inflate(inflater,container,false);



        mBinding.addCatalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCatalog();
            }
        });

        mBinding.catalogNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(mCatalogList != null && !mCatalogList.isEmpty()){
                    for (Catalog c: mCatalogList){
                        if (c.equals(charSequence.toString())){
                            mBinding.catalogNameEt.setError("Такой уже сущесвует");
                            mBinding.catalogNameEt.requestFocus();
                            isExist = true;
                            return;
                        }
                    }
                }
                isExist = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.catalogImageUrlEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String url = charSequence.toString();

                if (URLUtil.isValidUrl(url)){
                    Picasso.get().load(url).into(mBinding.catalogImageIv);
                }
                else {
                    mBinding.catalogImageUrlEt.setError("Введите правильную ссылку");
                    mBinding.catalogImageUrlEt.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return mBinding.getRoot();
    }

    private void addCatalog(){
        String name = mBinding.catalogNameEt.getText().toString().trim();
        String url = mBinding.catalogImageUrlEt.getText().toString().trim();

        if (name.isEmpty()){
            mBinding.catalogNameEt.setError("Введите название каталога");
            mBinding.catalogNameEt.requestFocus();
            return;
        }
        if (url.isEmpty()){
            mBinding.catalogImageUrlEt.setError("Введите ссылку изображения каталога");
            mBinding.catalogImageUrlEt.requestFocus();
            return;
        }

        if (!isExist && URLUtil.isValidUrl(url)){
            Catalog catalog = new Catalog(name,url);
            mReference.add(catalog).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.profileAuthenticatedAdminFragment);
                }
            });
        }
    }
}