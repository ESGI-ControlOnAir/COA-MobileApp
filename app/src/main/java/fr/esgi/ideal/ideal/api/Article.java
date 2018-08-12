package fr.esgi.ideal.ideal.api;

/**
 * Created by Kray on 09/04/2018.
 */

public class Article {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int like;
    private int img;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Article(String name, int img, String description, double Price){
        this.name = name;
        this.img = img;
        this.description = description;
        this.price = Price;
    }
}
