package uk.gov.companieshouse.payments.admin.web.session.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;
import uk.gov.companieshouse.session.handler.SessionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceImplTest {
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl();
    }

    @Test
    void getSessionDataFromContext_returnsSessionData() {
        Map<String, Object> sessionData = new HashMap<>();
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertSame(sessionData, sessionService.getSessionDataFromContext());
        }
    }

    @Test
    void getUserInfo_returnsUserProfile_whenPresent() {
        Map<String, Object> userProfile = new HashMap<>();
        Map<String, Object> signInInfo = new HashMap<>();
        signInInfo.put("user_profile", userProfile);
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("signin_info", signInInfo);
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertSame(userProfile, sessionService.getUserInfo());
        }
    }

    @Test
    void getUserInfo_returnsNull_whenSignInInfoMissing() {
        Map<String, Object> sessionData = new HashMap<>();
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertNull(sessionService.getUserInfo());
        }
    }

    @Test
    void getUserPermissions_returnsPermissions_whenPresent() {
        Map<String, Object> permissions = new HashMap<>();
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("permissions", permissions);
        Map<String, Object> signInInfo = new HashMap<>();
        signInInfo.put("user_profile", userProfile);
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("signin_info", signInInfo);
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertSame(permissions, sessionService.getUserPermissions());
        }
    }

    @Test
    void getUserPermissions_returnsEmptyMap_whenUserInfoMissing() {
        Map<String, Object> sessionData = new HashMap<>();
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertEquals(Collections.emptyMap(), sessionService.getUserPermissions());
        }
    }

    @Test
    void getUserToken_returnsToken_whenPresent() {
        Map<String, Object> accessToken = new HashMap<>();
        accessToken.put("access_token", "token123");
        Map<String, Object> signInInfo = new HashMap<>();
        signInInfo.put("access_token", accessToken);
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("signin_info", signInInfo);
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertEquals("token123", sessionService.getUserToken());
        }
    }

    @Test
    void getUserToken_returnsNull_whenAccessTokenMissing() {
        Map<String, Object> signInInfo = new HashMap<>();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("signin_info", signInInfo);
        try (MockedStatic<SessionHandler> mocked = Mockito.mockStatic(SessionHandler.class)) {
            mocked.when(SessionHandler::getSessionDataFromContext).thenReturn(sessionData);
            assertNull(sessionService.getUserToken());
        }
    }
}

