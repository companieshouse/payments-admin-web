package uk.gov.companieshouse.payments.admin.web.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NavigationExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Navigation error occurred";
        NavigationException ex = new NavigationException(message);
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Navigation error with cause";
        Throwable cause = new RuntimeException("Root cause");
        NavigationException ex = new NavigationException(message, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

