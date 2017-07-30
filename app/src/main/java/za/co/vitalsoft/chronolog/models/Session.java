package za.co.vitalsoft.chronolog.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("SessionKey")
    @Expose
    private String sessionKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

}
