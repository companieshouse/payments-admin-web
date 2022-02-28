package uk.gov.companieshouse.payments.admin.web.api.impl;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;
import uk.gov.companieshouse.payments.admin.web.api.ApiClientService;

@Component
public class ApiClientServiceImpl implements ApiClientService {

    @Override
    public ApiClient getPublicApiClient() {
        return ApiClientManager.getSDK();
    }


    @Override
    public InternalApiClient getPrivateApiClient() {
        return ApiClientManager.getPrivateSDK();
    }

}