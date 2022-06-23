package com.greenFood.market.fragments.auth;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.greenFood.market.Const;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentSignUpEnterSmsCodeBinding;
import com.greenFood.market.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SignUpEnterSmsCode extends Fragment {
    private String phone_number;
    private FragmentSignUpEnterSmsCodeBinding mFragmentSignUpEnterSmsCodeBinding;

    private FirebaseAuth mAuth;
    private CollectionReference mReference;
    private List<User> mUserList;
    private String verificationId;
    private Callbacks mCallbacks;

    public interface Callbacks{
        public void authenticated();
    }
    public static SignUpEnterSmsCode newInstance(String phone_number) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.REQUEST_REGISTER_PHONE_NUMBER,phone_number);
        SignUpEnterSmsCode fragment = new SignUpEnterSmsCode();
        fragment.setArguments(bundle);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.phone_number = getArguments().getString(Const.REQUEST_REGISTER_PHONE_NUMBER);

        mUserList = new ArrayList<>();

        mReference = FirebaseFirestore.getInstance().collection(Const.NODE_USER);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ru");
        StringBuilder str = new StringBuilder();
        for(int i =0; i<phone_number.length();i++){
            if (phone_number.charAt(i) != '-'){
                str.append(phone_number.charAt(i));
            }
        }

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    mUserList.add(snapshot.toObject(User.class));
                }
            }
        });

        if (str.length()==10)
            sendVerificationCode("+7"+str.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentSignUpEnterSmsCodeBinding = FragmentSignUpEnterSmsCodeBinding.inflate(inflater,container,false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return mFragmentSignUpEnterSmsCodeBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mFragmentSignUpEnterSmsCodeBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        if (phone_number!=null && !phone_number.isEmpty()){
            mFragmentSignUpEnterSmsCodeBinding.yourNumberTv.setText("+7 "+phone_number);
        }

        mFragmentSignUpEnterSmsCodeBinding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: phone_number "+phone_number);
                if (!mFragmentSignUpEnterSmsCodeBinding.smsCodeEt.getText().toString().trim().isEmpty())
                    verifyCode(mFragmentSignUpEnterSmsCodeBinding.smsCodeEt.getText().toString());
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean oldAccount = false;
                            StringBuilder str = new StringBuilder();
                            for(int i =0; i<phone_number.length();i++){
                                if (phone_number.charAt(i) != '-'){
                                    str.append(phone_number.charAt(i));
                                }
                            }


                            for (User u : mUserList){
                                if (u.getPhoneNumber().equals("+7"+str)){
                                    oldAccount = true;
                                    break;
                                }
                            }
                            if (oldAccount){
                                mCallbacks.authenticated();
                            }
                            else{
                                User user = new User("","","+7"+str,
                                        "user","","","","");

                                mReference.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        mCallbacks.authenticated();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(mFragmentSignUpEnterSmsCodeBinding.getRoot(),"FAIl",Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Snackbar.make(mFragmentSignUpEnterSmsCodeBinding.getRoot(),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            mFragmentSignUpEnterSmsCodeBinding.smsCodeEt.setText(phoneAuthCredential.getSmsCode());
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Snackbar.make(mFragmentSignUpEnterSmsCodeBinding.getRoot(),e.getMessage(),Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }
    };
}