
package za.co.vitalsoft.chronolog.services.timesheet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostTimesheetResponse {

    @SerializedName("SessionExpired")
    @Expose
    private Boolean sessionExpired;
    @SerializedName("Errors")
    @Expose
    private List<java.lang.Error> errors = null;

    public Boolean getSessionExpired() {
        return sessionExpired;
    }

    public void setSessionExpired(Boolean sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public List<java.lang.Error> getErrors() {
        return errors;
    }

    public void setErrors(List<java.lang.Error> errors) {
        this.errors = errors;
    }

}
