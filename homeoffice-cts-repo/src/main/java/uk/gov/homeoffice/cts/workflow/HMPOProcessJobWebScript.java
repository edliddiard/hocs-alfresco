package uk.gov.homeoffice.cts.workflow;


import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.Collections;
import java.util.Map;

public class HMPOProcessJobWebScript extends DeclarativeWebScript {

    private HMPOProcessService hmpoProcessService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);
        final String log = hmpoProcessService.processHMPOCases();
        return Collections.<String, Object>singletonMap("log", log);
    }

    public void setHmpoProcessService(HMPOProcessService hmpoProcessService) {
        this.hmpoProcessService = hmpoProcessService;
    }
}
