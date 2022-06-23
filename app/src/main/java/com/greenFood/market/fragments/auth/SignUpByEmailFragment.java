package com.greenFood.market.fragments.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentSignUpByEmailBinding;
import com.greenFood.market.entities.User;

import java.util.ArrayList;
import java.util.List;

public class SignUpByEmailFragment extends Fragment {
    private FragmentSignUpByEmailBinding mBinding;
    private CollectionReference mReference;
    private FirebaseAuth mAuth;
    private List<User> mUserList;

    public SignUpByEmailFragment() {
        // Required empty public constructor
    }

    public static SignUpByEmailFragment newInstance(String param1, String param2) {
        SignUpByEmailFragment fragment = new SignUpByEmailFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSignUpByEmailBinding.inflate(inflater,container,false);
        mAuth = FirebaseAuth.getInstance();

        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_USER);
        mUserList = new ArrayList<>();

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mUserList.add(snapshot.toObject(User.class));

                    Log.d("TAG", "onSuccess: "+snapshot.toObject(User.class).getEmail());
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


        mBinding.phoneNumberEt.addTextChangedListener(new TextWatcher() {
            int length_before = 0;
            String phone_number = mBinding.phoneNumberEt.getText().toString();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                length_before = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone_number = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (length_before < editable.length()) {
                    if (editable.length() == 3 || editable.length() == 7)
                        editable.append("-");
                    if (editable.length() > 3) {
                        if (Character.isDigit(editable.charAt(3)))
                            editable.insert(3, "-");
                    }
                    if (editable.length() > 7) {
                        if (Character.isDigit(editable.charAt(7)))
                            editable.insert(7, "-");
                    }
                }
            }
        });

        mBinding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        return mBinding.getRoot();
    }

    private void registerUser(){
        String email = mBinding.emailEt.getText().toString().trim();
        String username = mBinding.nameEt.getText().toString().trim();
        String phoneNumber = mBinding.phoneNumberEt.getText().toString().trim();
        String password = mBinding.passwordEt.getText().toString().trim();
        String re_password = mBinding.rePasswordEt.getText().toString().trim();

        boolean account_exist = false;


        if (username.isEmpty()){
            mBinding.nameEt.setError("Заполните поле");
            mBinding.nameEt.requestFocus();
            return;
        }
        if (email.isEmpty()){
            mBinding.emailEt.setError("Заполните поле");
            mBinding.emailEt.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()){
            mBinding.phoneNumberEt.setError("Заполните поле");
            mBinding.phoneNumberEt.requestFocus();
            return;
        }
        if (password.isEmpty()){
            mBinding.passwordEt.setError("Заполните поле");
            mBinding.passwordEt.requestFocus();
            return;
        }
        if (re_password.isEmpty()){
            mBinding.rePasswordEt.setError("Заполните поле");
            mBinding.rePasswordEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mBinding.emailEt.setError("Введите правильный E-mail");
            mBinding.emailEt.requestFocus();
            return;
        }


        if (password.length()<8){
            mBinding.passwordEt.setError("Короткий пароль");
            mBinding.passwordEt.requestFocus();
            return;
        }

        if (!password.equals(re_password)){
            mBinding.passwordEt.setError("Пароли не совпадают");
            mBinding.rePasswordEt.setError("Пароли не совпадают");
            mBinding.passwordEt.requestFocus();
            mBinding.rePasswordEt.requestFocus();
            return;
        }

        phoneNumber = "+7"+phoneNumber.replace("-","");

        for (User u: mUserList){
            if (u.getPhoneNumber().equals(phoneNumber) || u.getEmail().equals(email)){
                account_exist = true;
            }
        }

        if (account_exist){
            Snackbar.make(mBinding.getRoot(),"Аккаун с введенными данными уже существует", Snackbar.LENGTH_SHORT).show();
        }
        else {
            User user = new User(username,email,phoneNumber,"user","","",password,"");
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_USER);
                        mReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                mAuth.signInWithEmailAndPassword(email,password);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(mBinding.getRoot(),"FAIl",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }

    }
}