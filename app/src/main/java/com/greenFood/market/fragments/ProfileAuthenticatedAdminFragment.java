package com.greenFood.market.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentProfileAuthenticatedAdminBinding;


public class ProfileAuthenticatedAdminFragment extends Fragment {
    private FragmentProfileAuthenticatedAdminBinding mBinding;
    private FirebaseAuth mAuth;
    private Callbacks mCallbacks;

    public interface Callbacks{
        public void authenticated();
    }


    public ProfileAuthenticatedAdminFragment() {
        // Required empty public constructor
    }

    public static ProfileAuthenticatedAdminFragment newInstance() {
        ProfileAuthenticatedAdminFragment fragment = new ProfileAuthenticatedAdminFragment();
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileAuthenticatedAdminBinding.inflate(inflater,container,false);
        mAuth = FirebaseAuth.getInstance();


        mBinding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mCallbacks.authenticated();
            }
        });


        mBinding.addCatalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addCatalogFragment);
            }
        });

        mBinding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addProductFragment);
            }
        });
        return mBinding.getRoot();
    }
}