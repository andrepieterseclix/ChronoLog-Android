
package za.co.vitalsoft.chronolog.services.access.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LogoutResponse {

    @SerializedName("SessionExpired")
    @Expose
    private Boolean sessionExpired;
    @SerializedName("Errors")
    @Expose
    private List<Error> errors = null;

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
