package uk.gov.companieshouse.payments.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.payments.admin.web.Application;
import uk.gov.companieshouse.payments.admin.web.service.navigation.NavigatorService;

public abstract class BaseController {

    @Autowired
    protected NavigatorService navigatorService;

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(Application.APPLICATION_NAME_SPACE);

    protected static final String ERROR_VIEW = "error";

    protected BaseController() {
    }

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    protected void addBackPageAttributeToModel(Model model, String... pathVars) {

        model.addAttribute("backButton", navigatorService.getPreviousControllerPath(this.getClass(), pathVars));
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("headerText", "Payments service refunds");
        model.addAttribute("headerURL", "/admin/payments/refunds");
    }
}
