package com.greenFood.market.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {
    private int price;
    private String image;
    private String description;
    @PrimaryKey
    @NonNull
    private String name;
    private String country;
    private int count;
    private Boolean pesticide;
    private String catalogId;


    public Product() {
    }

    public Product( int price, String image, String description, String name, String country, int count,Boolean pesticide,String catalogId) {
        this.price = price;
        this.image = image;
        this.description = description;
        this.name = name;
        this.country = country;
        this.count = count;
        this.pesticide = pesticide;
        this.catalogId = catalogId;
    }

    public Boolean getPesticide() {
        return pesticide;
    }

    public void setPesticide(Boolean pesticide) {
        this.pesticide = pesticide;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
