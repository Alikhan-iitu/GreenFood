package com.greenFood.market.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.greenFood.market.databinding.FragmentProfileAuthenticatedUserBinding;


public class ProfileAuthenticatedUserFragment extends Fragment {
    private FragmentProfileAuthenticatedUserBinding mBinding;
    private FirebaseAuth mAuth;
    private Callbacks mCallbacks;

    public interface Callbacks{
        public void authenticated();
    }

    public ProfileAuthenticatedUserFragment() {
        // Required empty public constructor
    }

    public static ProfileAuthenticatedUserFragment newInstance() {
        ProfileAuthenticatedUserFragment fragment = new ProfileAuthenticatedUserFragment();
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
        mBinding = FragmentProfileAuthenticatedUserBinding.inflate(inflater,container,false);

        mAuth = FirebaseAuth.getInstance();


        mBinding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mCallbacks.authenticated();
            }
        });

        return mBinding.getRoot();
    }
}