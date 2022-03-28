package uk.gov.companieshouse.payments.admin.web.api.impl;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.payments.admin.web.api.ApiClientService;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;

@Component
public class ApiClientServiceImpl implements ApiClientService {

    @Override
    public ApiClient getApiClient() {
        return ApiClientManager.getSDK();
    }

    @Override
    public InternalApiClient getInternalApiClient() {
        return ApiClientManager.getPrivateSDK();
    }
}
