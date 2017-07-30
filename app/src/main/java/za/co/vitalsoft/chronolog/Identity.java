package za.co.vitalsoft.chronolog;

import za.co.vitalsoft.chronolog.models.User;

/**
 * Created by Andre on 2017-07-23.
 */

public class Identity {

    private static final Identity ourInstance = new Identity();
    private String mUserId;
    private String mSessionId;
    private String mSessionKey;
    private User mUser;

    private Identity() {
    }

    public static Identity getInstance() {
        return ourInstance;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getSessionKey() {
        return mSessionKey;
    }

    public void setSessionKey(String sessionKey) {
        mSessionKey = sessionKey;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
