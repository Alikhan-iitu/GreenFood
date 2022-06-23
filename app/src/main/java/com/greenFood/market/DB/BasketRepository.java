package com.greenFood.market.DB;

import android.content.Context;

import androidx.room.Room;

import com.greenFood.market.entities.Product;

import java.util.List;

public class BasketRepository {
    private static final String DATABASE_NAME = "market_database";
    private AppDatabase db;
    private BasketDao mBasketDao;
    private static BasketRepository mBasketRepository;

    private BasketRepository(Context context) {
        this.db  = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,DATABASE_NAME).allowMainThreadQueries().build();
        mBasketDao = db.mBasketDao();
    }

    public void insertProduct(Product product){
        mBasketDao.insertProduct(product);
    }
    public void updateProduct(Product product){
        mBasketDao.updateProduct(product);
    }
    public void deleteProduct(Product product){
        mBasketDao.deleteProduct(product);
    }

    public List<Product> getAllProduct(){
        return mBasketDao.getAllProducts();
    }

    public Product getProduct(String name){
        return mBasketDao.getProduct(name);
    }

    public static void initialize(Context context){
        if (mBasketRepository == null){
            mBasketRepository = new BasketRepository(context);
        }
    }
    public static BasketRepository getBasketRepository(){
        return mBasketRepository;
    }
}
