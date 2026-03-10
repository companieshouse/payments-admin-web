package uk.gov.companieshouse.payments.admin.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefundFileValidatorTest {
    private RefundFileValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new RefundFileValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testNullFile() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testZeroSizeFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(0L);
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testXmlExtensionTooLarge() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(600 * 1024L);
        when(file.getOriginalFilename()).thenReturn("file.xml");
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void testXmlExtensionValidSize() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(100 * 1024L);
        when(file.getOriginalFilename()).thenReturn("file.xml");
        assertTrue(validator.isValid(file, context));
    }

    @ParameterizedTest
    @CsvSource({
        "null, false", // null original filename
        "filename, false", // no extension
        "file.txt, false" // wrong extension
    })
    void testInvalidFilenames(String filename, boolean expected) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(100L);
        when(file.getOriginalFilename()).thenReturn("null".equals(filename) ? null : filename);
        assertEquals(expected, validator.isValid(file, context));
    }
}
