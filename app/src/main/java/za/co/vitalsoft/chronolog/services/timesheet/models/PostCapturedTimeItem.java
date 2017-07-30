
package za.co.vitalsoft.chronolog.services.timesheet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PostCapturedTimeItem {

    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Date")
    @Expose
    private Date date;
    @SerializedName("HoursWorked")
    @Expose
    private Integer hoursWorked;

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

}
