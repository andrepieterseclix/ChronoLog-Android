
package za.co.vitalsoft.chronolog.services.timesheet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import za.co.vitalsoft.chronolog.models.CapturedTimeItem;

public class GetTimesheetResponse {

    @SerializedName("CapturedTimeItems")
    @Expose
    private List<CapturedTimeItem> capturedTimeItems = null;
    @SerializedName("SessionExpired")
    @Expose
    private Boolean sessionExpired;
    @SerializedName("Errors")
    @Expose
    private List<Error> errors = null;

    public List<CapturedTimeItem> getCapturedTimeItems() {
        return capturedTimeItems;
    }

    public void setCapturedTimeItems(List<CapturedTimeItem> capturedTimeItems) {
        this.capturedTimeItems = capturedTimeItems;
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
