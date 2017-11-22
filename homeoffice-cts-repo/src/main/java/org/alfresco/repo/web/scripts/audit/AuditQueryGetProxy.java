package org.alfresco.repo.web.scripts.audit;

import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.Runtime;
import org.alfresco.repo.web.scripts.audit.AuditQueryGet;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple proxy class that will get us access to the Alfresco
 * webscript and get the data back without having to copy paste code
 * Created by chris on 27/07/2014.
 */
public class AuditQueryGetProxy extends org.alfresco.repo.web.scripts.audit.AuditQueryGet {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditQueryGetProxy.class);

    public Map<String, Object> getAudit(NodeRef nodeRef){

        Status status = new Status();
        Cache cache = null;
        WebScriptRequest webScriptRequest = getWebScriptRequest(nodeRef);

        Map<String, Object> map = this.executeImpl(webScriptRequest, status, cache);

        return map;
    }

    /**
     * This creates a mock WebRequest so we can reuse the webscript that gets audits.
     * @param nodeRef
     * @return
     */
    public WebScriptRequest getWebScriptRequest(final NodeRef nodeRef) {
        WebScriptRequest webScriptRequest = new WebScriptRequest() {
            String nodeRef;
            @Override
            public Match getServiceMatch() {
                String templatePath = "";
                Map<String, String> templateVars = new HashMap<>();
                templateVars.put(PARAM_APPLICATION, "alfresco-access");
                templateVars.put(PARAM_PATH,"/alfresco-access/transaction/node");//is this a parameter or part of the url, is probably
                //the path for the query

                String matchPath = "";//?
                Match match = new Match(templatePath, templateVars, matchPath);

                LOGGER.debug("PARAM_APPLICATION"+match.getTemplateVars().get(PARAM_APPLICATION));
                LOGGER.debug("PARAM_PATH"+match.getTemplateVars().get(PARAM_PATH));
                return match;
            }

            @Override
            public String getServerPath() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getServiceContextPath() {
                return null;
            }

            @Override
            public String getServicePath() {
                return null;
            }

            @Override
            public String getURL() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String[] getParameterNames() {
                return new String[0];
            }

            @Override
            public String getParameter(String s) {
                if (s.equals(PARAM_VALUE)) {
                    LOGGER.debug(nodeRef);
                    return nodeRef;
                } else if (s.equals(PARAM_VALUE_TYPE)) {
                    LOGGER.debug("org.alfresco.service.cmr.repository.NodeRef");
                    return "org.alfresco.service.cmr.repository.NodeRef";
                } else if (s.equals(PARAM_FORWARD)) {
                    LOGGER.debug(PARAM_FORWARD+"true");
                    return "true";
                } else if (s.equals(PARAM_LIMIT)) {
                    LOGGER.debug(PARAM_LIMIT+"10000");
                    return "10000";//this should be enough to cover every case
                } else if (s.equals(PARAM_VERBOSE)) {
                    LOGGER.debug(PARAM_VERBOSE+"true");
                    return "true";
                }
                return null;
            }

            @Override
            public String[] getParameterValues(String s) {
                return new String[0];
            }

            @Override
            public String[] getHeaderNames() {
                return new String[0];
            }

            @Override
            public String getHeader(String s) {
                return null;
            }

            @Override
            public String[] getHeaderValues(String s) {
                return new String[0];
            }

            @Override
            public String getExtensionPath() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public Content getContent() {
                return null;
            }

            @Override
            public Object parseContent() {
                return null;
            }

            @Override
            public boolean isGuest() {
                return false;
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public Description.FormatStyle getFormatStyle() {
                return null;
            }

            @Override
            public String getAgent() {
                return null;
            }

            @Override
            public String getJSONCallback() {
                return null;
            }

            @Override
            public boolean forceSuccessStatus() {
                return false;
            }

            @Override
            public Runtime getRuntime() {
                return null;
            }
            private WebScriptRequest init(String nodeRef){
                this.nodeRef = nodeRef;
                return this;
            }
        }.init(nodeRef.toString());
        return webScriptRequest;
    }
    public void setAuditService(AuditService auditService){
        super.setAuditService(auditService);
    }
}
