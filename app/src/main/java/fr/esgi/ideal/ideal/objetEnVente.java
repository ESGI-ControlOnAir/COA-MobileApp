package fr.esgi.ideal.ideal;

import android.graphics.Bitmap;

public class objetEnVente {

    String nom;
    String description;
    String prix;
    Bitmap image;
    int liked;
    int unliked;


    public objetEnVente(String name, String description, String prix, Bitmap image, int liked, int unliked ) {
        this.nom=name;
        this.description=description;
        this.prix=prix;
        this.liked=liked;
        this.image=image;
        this.unliked=unliked;
        this.liked=liked;
    }

    public String getName() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getPrix() {
        return prix;
    }

    public int getLike() { return liked; }

    public int getUnlike() { return unliked; }

    public Bitmap getImage() {
        return image;
    }
}