package com.greenFood.market.fragments.auth;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentSignInByEmailBinding;
import com.greenFood.market.entities.User;

import java.util.ArrayList;
import java.util.List;

public class SignInByEmailFragment extends Fragment {
    private FragmentSignInByEmailBinding mBinding;
    private CollectionReference mReference;
    private FirebaseAuth mAuth;
    private List<User> mUserList;
    private Callbacks mCallbacks;

    public interface Callbacks{
        public void authenticated();
    }

    public SignInByEmailFragment() {
        // Required empty public constructor
    }

    public static SignInByEmailFragment newInstance(String param1, String param2) {
        SignInByEmailFragment fragment = new SignInByEmailFragment();

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
        // Inflate the layout for this fragment
        mBinding = FragmentSignInByEmailBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_USER);
        mUserList = new ArrayList<>();

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mUserList.add(snapshot.toObject(User.class));
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

        mBinding.signInByPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).popBackStack();
            }
        });


        mBinding.signUpByEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_signInByEmailFragment2_to_signUpByEmailFragment);
            }
        });


        mBinding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        return mBinding.getRoot();
    }

    private void signIn(){
        String email = mBinding.emailEt.getText().toString().trim();
        String password = mBinding.passwordEt.getText().toString().trim();

        boolean success = false;

        if (email.isEmpty()){
            mBinding.emailEt.setError("Заполните E-mail");
            mBinding.emailEt.requestFocus();
            return;
        }
        if (password.isEmpty()){
            mBinding.passwordEt.setError("Заполните E-mail");
            mBinding.passwordEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mBinding.emailEt.setError("Введите правильный E-mail");
            mBinding.emailEt.requestFocus();
            return;
        }

        for (User u: mUserList){
            if (u.getEmail().equals(email) && u.getPassword().equals(password)){
                success = true;
            }
        }

        if (success){
            mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mCallbacks.authenticated();
                }
            });
        }
        else {
            mBinding.emailEt.setText("");
            mBinding.passwordEt.setText("");


            mBinding.emailEt.setError("Неверные данные");
            mBinding.emailEt.requestFocus();

            mBinding.passwordEt.setError("Неверные данные");
            mBinding.passwordEt.requestFocus();
            return;
        }


    }
}