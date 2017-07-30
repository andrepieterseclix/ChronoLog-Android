
package za.co.vitalsoft.chronolog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class CapturedTimeItem implements Serializable {

    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Date")
    @Expose
    private Date date;
    @SerializedName("HoursWorked")
    @Expose
    private Integer hoursWorked;
    @SerializedName("IsLocked")
    @Expose
    private Boolean isLocked;
    @SerializedName("IsPublicHoliday")
    @Expose
    private Boolean isPublicHoliday;
    @SerializedName("IsEnabled")
    @Expose
    private Boolean isEnabled;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Integer hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getIsPublicHoliday() {
        return isPublicHoliday;
    }

    public void setIsPublicHoliday(Boolean isPublicHoliday) {
        this.isPublicHoliday = isPublicHoliday;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
