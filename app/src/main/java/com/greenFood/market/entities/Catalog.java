package com.greenFood.market.entities;

public class Catalog {
    private String name;
    private String categoryImage;

    public Catalog() {
    }

    public Catalog(String name, String categoryImage) {
        this.name = name;
        this.categoryImage = categoryImage;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}
