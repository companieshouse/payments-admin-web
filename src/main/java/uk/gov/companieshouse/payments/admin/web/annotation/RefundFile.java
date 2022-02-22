package uk.gov.companieshouse.payments.admin.web.annotation;

import uk.gov.companieshouse.payments.admin.web.validation.RefundFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RefundFileValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface RefundFile {

    String message() default "Refund file is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
