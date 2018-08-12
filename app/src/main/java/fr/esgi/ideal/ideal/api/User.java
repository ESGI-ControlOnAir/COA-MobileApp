package fr.esgi.ideal.ideal.api;

/**
 * Created by Kray on 09/04/2018.
 */

class User {
    private Long id;
    private String name;
    private String email;
    private int img;
    private boolean isAdmin;

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

    public User(String email, int img, boolean isAdmin){
        this.email = email;
        this.img = img;
        this.isAdmin = isAdmin;
    }
}
