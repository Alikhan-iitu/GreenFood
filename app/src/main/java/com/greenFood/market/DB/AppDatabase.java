package com.greenFood.market.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.greenFood.market.entities.Product;

@Database(entities = {Product.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BasketDao mBasketDao();
}
