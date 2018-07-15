package fr.esgi.ideal.ideal;

public class objetEnVente {

    String nom;
    String description;
    String prix;
    String like;
    String image;

    public objetEnVente(String name, String description, String prix, String like, String URLImage ) {
        this.nom=name;
        this.description=description;
        this.prix=prix;
        this.like=like;
        this.image=image;
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

    public String getLike() { return like; }

    public String getImage() {
        return image;
    }
}