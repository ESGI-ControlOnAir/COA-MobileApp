package fr.esgi.ideal.ideal.api;

import android.graphics.Bitmap;

/**
 * Created by Kray on 09/04/2018.
 */

public class Article {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int img = 0;
    private int like;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImg(int id) { this.img = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Article(String name, String description, double Price){
        this.name = name;
        this.description = description;
        this.price = Price;
    }
}
