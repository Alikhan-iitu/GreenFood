package com.greenFood.market;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.greenFood.market.fragments.ProfileAuthenticatedAdminFragment;
import com.greenFood.market.fragments.ProfileAuthenticatedUserFragment;
import com.greenFood.market.fragments.auth.SignInByEmailFragment;
import com.greenFood.market.fragments.auth.SignUpEnterSmsCode;

public abstract class SingleFragmentActivity extends AppCompatActivity implements SignInByEmailFragment.Callbacks, SignUpEnterSmsCode.Callbacks,
        ProfileAuthenticatedUserFragment.Callbacks, ProfileAuthenticatedAdminFragment.Callbacks {
    protected abstract Fragment createFragment();


    @LayoutRes  
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm =getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);


        if (fragment ==null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}
