package uk.gov.companieshouse.payments.admin.web.session.impl;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import java.util.Map;

@Component
public class SessionServiceImpl implements SessionService {
    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getSessionDataFromContext() {
        return SessionHandler.getSessionDataFromContext();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getUserInfo() {
        Map<String, Object> sessionData = getSessionDataFromContext();
        Map<String, Object> signInInfo = (Map<String, Object>) sessionData.get(SIGN_IN_KEY);

        return signInInfo != null ? (Map<String, Object>) signInInfo.get(USER_PROFILE_KEY) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getUserPermissions() {
        Map<String, Object> userInfo = getUserInfo();

        return userInfo != null ? (Map<String, Object>) userInfo.get("permissions") : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getUserToken() {
        Map<String, Object> signInInfo = getUserInfo();
        Map<String,Object> accessToken = (Map<String, Object>) signInInfo.get(ACCESS_TOKEN);


        return accessToken != null ? accessToken.get(ACCESS_TOKEN).toString() : null;
    }
}
