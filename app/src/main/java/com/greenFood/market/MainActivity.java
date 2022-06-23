package com.greenFood.market;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.greenFood.market.fragments.MainFragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public void authenticated() {

        FragmentManager fm =getSupportFragmentManager();
        MainFragment fragment = (MainFragment)fm.findFragmentById(R.id.fragment_container);

        fragment.authenticated();
    }
}