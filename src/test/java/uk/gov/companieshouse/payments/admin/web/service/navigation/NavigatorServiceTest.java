package uk.gov.companieshouse.payments.admin.web.service.navigation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.payments.admin.web.annotation.NextController;
import uk.gov.companieshouse.payments.admin.web.annotation.PreviousController;
import uk.gov.companieshouse.payments.admin.web.controller.BaseController;
import uk.gov.companieshouse.payments.admin.web.controller.BranchController;
import uk.gov.companieshouse.payments.admin.web.controller.ConditionalController;
import uk.gov.companieshouse.payments.admin.web.exception.MissingAnnotationException;
import uk.gov.companieshouse.payments.admin.web.exception.NavigationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class NavigatorServiceTest {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private NavigatorService navigatorService;

    @RequestMapping("/test1/{id}")
    @NextController({TestController2.class})
    static class TestController1 extends BaseController {
        @Override
        protected String getTemplateName() { return "test1"; }
    }

    @RequestMapping("/test2/{id}")
    @PreviousController({TestController1.class})
    static class TestController2 extends BaseController {
        @Override
        protected String getTemplateName() { return "test2"; }
    }

    @RequestMapping("/branch/{id}")
    @NextController({TestController2.class})
    static class BranchTestController extends BaseController implements BranchController {
        @Override
        public boolean shouldBranch(String... pathVars) { return true; }
        @Override
        protected String getTemplateName() { return "branch"; }
    }

    @RequestMapping("/conditional/{a}/{b}/{c}")
    @NextController({TestController2.class})
    static class ConditionalTestController extends BaseController implements ConditionalController {
        @Override
        public boolean willRender(String a, String b, String c) { return true; }
        @Override
        protected String getTemplateName() { return "conditional"; }
    }

    @Test
    void testGetNextControllerRedirect_basic() {
        // No stubbing needed
        String redirect = navigatorService.getNextControllerRedirect(TestController1.class, "123");
        assertTrue(redirect.contains("/test2/123"));
    }

    @Test
    void testGetPreviousControllerPath_basic() {
        // No stubbing needed
        String path = navigatorService.getPreviousControllerPath(TestController2.class, "123");
        assertEquals("/test1/123", path);
    }

    @Test
    void testMissingNextControllerAnnotation_throws() {
        // No stubbing needed
        class NoNext extends BaseController {
            @Override protected String getTemplateName() { return "none"; }
        }
        Exception ex = assertThrows(MissingAnnotationException.class, () ->
            navigatorService.getNextControllerRedirect(NoNext.class, "1")
        );
        assertTrue(ex.getMessage().contains("Missing @NextController annotation"));
    }

    @Test
    void testMissingPreviousControllerAnnotation_throws() {
        // No stubbing needed
        class NoPrev extends BaseController {
            @Override protected String getTemplateName() { return "none"; }
        }
        Exception ex = assertThrows(MissingAnnotationException.class, () ->
            navigatorService.getPreviousControllerPath(NoPrev.class, "1")
        );
        assertTrue(ex.getMessage().contains("Missing @PreviousController annotation"));
    }

    @Test
    void testBranchControllerLogic() {
        // No stubbing needed, as shouldBranch is not called in this scenario
        String redirect = navigatorService.getNextControllerRedirect(BranchTestController.class, "456");
        assertTrue(redirect.contains("/test2/456"));
    }

    @Test
    void testConditionalControllerLogic() {
        // The next controller expects only one path variable, so only pass one
        String redirect = navigatorService.getNextControllerRedirect(ConditionalTestController.class, "789");
        assertTrue(redirect.contains("/test2/789"));
    }

    @Test
    void testNoMatchingPathVars_throws() {
        // No stubbing needed
        Exception ex = assertThrows(NavigationException.class, () ->
            navigatorService.getNextControllerRedirect(TestController1.class)
        );
        assertTrue(ex.getMessage().contains("No mapping found that matches the number of path variables provided"));
    }
}
