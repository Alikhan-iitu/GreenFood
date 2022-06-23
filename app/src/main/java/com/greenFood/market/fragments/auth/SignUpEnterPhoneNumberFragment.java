package com.greenFood.market.fragments.auth;

import static com.greenFood.market.Const.REQUEST_REGISTER_PHONE_NUMBER;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.greenFood.market.R;
import com.greenFood.market.databinding.FragmentSignUpEnterPhoneNumberBinding;

public class SignUpEnterPhoneNumberFragment extends Fragment {
    private FragmentSignUpEnterPhoneNumberBinding signUpEnterPhoneNumberBinding;
    private String phone_number;
    public static SignUpEnterPhoneNumberFragment newInstance() {
        return new SignUpEnterPhoneNumberFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        signUpEnterPhoneNumberBinding = FragmentSignUpEnterPhoneNumberBinding.inflate(inflater,container,false);
        return signUpEnterPhoneNumberBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpEnterPhoneNumberBinding = FragmentSignUpEnterPhoneNumberBinding.bind(view);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        signUpEnterPhoneNumberBinding.continueSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_number != null){
                    if (phone_number.length()==12){

                        Log.d("TAG", "onClick: Bundle args = new Bundle();");
                        Bundle args = new Bundle();
                        args.putString(REQUEST_REGISTER_PHONE_NUMBER,phone_number);
                        Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                                .navigate(R.id.action_signUpEnterPhoneNumberFragment_to_signUpEnterSmsCode,args);

                    }
                    else{
                        Snackbar.make(signUpEnterPhoneNumberBinding.getRoot(),R.string.enter_phone_number_text_view,Snackbar.LENGTH_SHORT).show();
                    }
                }
                else{
                    Snackbar.make(signUpEnterPhoneNumberBinding.getRoot(),R.string.enter_phone_number_text_view,Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        signUpEnterPhoneNumberBinding.enterPhoneNumberEt.addTextChangedListener(new TextWatcher() {
            int length_before = 0;
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

        signUpEnterPhoneNumberBinding.signInByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_signUpEnterPhoneNumberFragment_to_signInByEmailFragment2);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        signUpEnterPhoneNumberBinding = null;
    }
}
