package fr.esgi.ideal.ideal.oauth2.request;

/**
 * Created by gokhan on 4/8/15.
 */
public class AccessTokenRequest {

    private String username;
    private String password;
    private String client_id;
    private String secret;
    private String grant_type;
    private String scope;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setClient_secret(String secret) {
        this.secret = secret;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
