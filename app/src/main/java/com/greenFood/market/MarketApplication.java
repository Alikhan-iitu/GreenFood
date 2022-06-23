package com.greenFood.market;

import android.app.Application;

import com.greenFood.market.DB.BasketRepository;

public class MarketApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        BasketRepository.initialize(this);
    }
}
