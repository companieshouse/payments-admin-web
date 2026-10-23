package uk.gov.companieshouse.payments.admin.web.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.payments.admin.web.Application;
import uk.gov.companieshouse.payments.admin.web.service.navigation.NavigatorService;

public abstract class BaseController {

    protected final NavigatorService navigatorService;

    protected final MessageSource messageSource;

    protected BaseController(NavigatorService navigatorService, MessageSource messageSource) {
        this.navigatorService = navigatorService;
        this.messageSource = messageSource;
    }

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(Application.APPLICATION_NAME_SPACE);

    protected static final String ERROR_VIEW = "error";

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    protected void addBackPageAttributeToModel(Model model, String... pathVars) {

        model.addAttribute("backButton", navigatorService.getPreviousControllerPath(this.getClass(), pathVars));
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        String headerText = messageSource.getMessage("refund.header.service-name", null, LocaleContextHolder.getLocale());
        model.addAttribute("headerText", headerText);
        model.addAttribute("headerURL", "/admin/payments/refunds");
    }
}
