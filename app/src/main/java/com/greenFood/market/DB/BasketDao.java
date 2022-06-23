package com.greenFood.market.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.greenFood.market.entities.Product;

import java.util.List;

@Dao
public interface BasketDao {
    @Query("SELECT * FROM product")
    List<Product> getAllProducts();

    @Query("SELECT * FROM product WHERE name =:name")
    Product getProduct(String name);

    @Update
    void updateProduct(Product product);

    @Insert
    void insertProduct(Product product);

    @Delete
    void deleteProduct(Product product);
}
