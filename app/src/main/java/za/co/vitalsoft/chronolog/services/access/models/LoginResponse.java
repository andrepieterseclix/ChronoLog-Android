package za.co.vitalsoft.chronolog.services.access.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import za.co.vitalsoft.chronolog.models.Session;
import za.co.vitalsoft.chronolog.models.User;

public class LoginResponse {

    @SerializedName("IsLoggedIn")
    @Expose
    private Boolean isLoggedIn;
    @SerializedName("Session")
    @Expose
    private Session session;
    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("SessionExpired")
    @Expose
    private Boolean sessionExpired;
    @SerializedName("Errors")
    @Expose
    private List<Error> errors = null;

    public Boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(Boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSessionExpired() {
        return sessionExpired;
    }

    public void setSessionExpired(Boolean sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

}
