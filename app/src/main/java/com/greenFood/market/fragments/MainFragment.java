package com.greenFood.market.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.ActivityMainBinding;
import com.greenFood.market.entities.User;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private ActivityMainBinding mActivityMainBinding;
    private FirebaseAuth mAuth;
    private CollectionReference mReference;
    private List<User> mUserList;

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    public void authenticated(){
        mActivityMainBinding.bottomNavView.getMenu().clear();
        boolean admin = false;
        if (mAuth.getCurrentUser() == null){
            mActivityMainBinding.bottomNavView.inflateMenu(R.menu.bottom_menu);
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.profileFragment);
            return;
        }

        for (User u: mUserList){
            if (u.getEmail().equals(mAuth.getCurrentUser().getEmail()) || u.getPhoneNumber().equals(mAuth.getCurrentUser().getPhoneNumber())){
                admin = u.getRole().equals("admin");
            }
        }

        if (admin){
            mActivityMainBinding.bottomNavView.inflateMenu(R.menu.bottom_menu_admin);
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.profileAuthenticatedAdminFragment);
        }
        else {
            mActivityMainBinding.bottomNavView.inflateMenu(R.menu.bottom_menu_user);
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.profileAuthenticatedUserFragment);
        }

    }
    private void userFromRememberMe(){

        if (mAuth.getCurrentUser() != null){
            boolean admin = false;

            mActivityMainBinding.bottomNavView.getMenu().clear();

            for (User u: mUserList){
                if (u.getEmail().equals(mAuth.getCurrentUser().getEmail()) || u.getPhoneNumber().equals(mAuth.getCurrentUser().getPhoneNumber())){
                    admin = u.getRole().equals("admin");
                }
            }

            if (admin){
                mActivityMainBinding.bottomNavView.inflateMenu(R.menu.bottom_menu_admin);
            }
            else {
                mActivityMainBinding.bottomNavView.inflateMenu(R.menu.bottom_menu_user);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivityMainBinding = ActivityMainBinding.inflate(inflater,container,false);

        mAuth = FirebaseAuth.getInstance();

        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_USER);
        mUserList = new ArrayList<>();

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(mActivityMainBinding.bottomNavView,navController);


        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mUserList.add(snapshot.toObject(User.class));
                }
                userFromRememberMe();
            }
        });
        return mActivityMainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
