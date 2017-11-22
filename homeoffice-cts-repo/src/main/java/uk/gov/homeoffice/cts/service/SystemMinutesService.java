package uk.gov.homeoffice.cts.service;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.audit.AuditQueryGetProxy;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.QName;
import org.apache.axis.utils.StringUtils;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsMinute;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;

import java.util.*;

/**
 * Service to pull together audit entries into system minutes which will be
 * displayed against a case
 * Created by chris on 28/07/2014.
 */
public class SystemMinutesService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SystemMinutesService.class);
    private AuditQueryGetProxy auditQuery;
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private AuthorityService authorityService;

    private static final List<String> decodeGroupQNames = Arrays.asList(CtsModel.PROP_MARKUP_UNIT.toString(),
            CtsModel.PROP_MARKUP_MINISTER.toString(), CtsModel.PROP_ASSIGNED_UNIT.toString(),
            CtsModel.PROP_ASSIGNED_TEAM.toString());

    public List<CtsMinute> getSystemMinutes(final NodeRef nodeRef){
        @SuppressWarnings("unchecked") List<Map<String, Object>> entries = getAuditEnties(nodeRef);

        List<CtsMinute> systemMinutes = new ArrayList<>();
        for (int i = entries.size() - 1; i >= 0; i--) {
            Map<String, Object> entry = entries.get(i);
            Long dbId = (Long) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_ID);
            String user = (String) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_USER);

            Date time = (Date) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_TIME);

            @SuppressWarnings("unchecked")
            Map<String, String> valueStrings = (Map<String, String>) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_VALUES);
            String text = buildText(valueStrings);
            if(text != null){
                CtsMinute ctsMinute = new CtsMinute(
                    dbId,
                    time,
                    text,
                    user,
                    "system"
                );

                systemMinutes.add(ctsMinute);
            }
        }
        return systemMinutes;
    }

    /*
     * service method to check all audit entries and returns list of signed off dates
     */
    public Map<String,Date> getSignedOffDates(final NodeRef nodeRef){
        @SuppressWarnings("unchecked") List<Map<String, Object>> entries = getAuditEnties(nodeRef);

        Date rejectedOn = getLatestRejectedDate(entries);

        Map<String, Date> signedOffDates = new HashMap<>();

        for (int i = entries.size() - 1; i >= 0; i--) {
            Map<String, Object> entry = entries.get(i);
            Date time = (Date) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_TIME);
            @SuppressWarnings("unchecked")
            Map<String, String> valueStrings = (Map<String, String>) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_VALUES);
            String signedOffFieldsKey = extractSignedOffDateFieldKey(valueStrings);
            if (signedOffFieldsKey != null && (rejectedOn == null || rejectedOn.before(time))) {
                signedOffDates.put(signedOffFieldsKey, time);
            }
        }
        return signedOffDates;
    }

    private Date getLatestRejectedDate(List<Map<String, Object>> entries) {
        Date rejectedOn = null;
        for (int i = entries.size() - 1; i >= 0; i--) {
            Map<String, Object> entry = entries.get(i);
            Date time = (Date) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_TIME);
            @SuppressWarnings("unchecked")
            Map<String, String> valueStrings = (Map<String, String>) entry.get(AuditQueryGetProxy.JSON_KEY_ENTRY_VALUES);
            if (isRejectedCase(valueStrings)) {
                if (rejectedOn == null) {
                    rejectedOn = time;
                } else if (rejectedOn.before(time)) {
                    rejectedOn = time;
                }
            }

        }
        return rejectedOn;
    }

    private String buildText(Map<String, String> valueStrings) {
        String action = valueStrings.get("/alfresco-access/transaction/action");
        StringBuilder sb = new StringBuilder();
        switch (action) {
            case "CREATE":
                sb.append("Case was created");
                return sb.toString();
            case "DELETE":
                break;
            case "UPDATE CONTENT":
                //is adding content to a content node so can ignore
                break;
            case "addNodeAspect":
                //this occurs when adding an initial comment but don't need to display
                //the audit of it as we display the comments as manual minutes
                break;
            case "updateNodeProperties":
                String fromObjects = valueStrings.get("/alfresco-access/transaction/properties/from");
                String addObjects = valueStrings.get("/alfresco-access/transaction/properties/add");
                if(fromObjects!=null) {
                    if(fromObjects.contains(ForumModel.PROP_COMMENT_COUNT.toString())){
                        //ignore this as we see the comments as manual minutes
                        break;
                    }
                    sb.append("Case saved.");
                    sb.append("\n");

                    Map<String,String> fromProps;
                    Map<String,String> toProps;

                    String toObjects = valueStrings.get("/alfresco-access/transaction/properties/to");
                    try {
                        fromProps = parsePropertiesAsAnArray(fromObjects);
                        toProps = parsePropertiesAsAnArray(toObjects);

                        Set<String> keys = fromProps.keySet();
                        for (String fromProp : keys) {
                            String fromValue = fromProps.get(fromProp);
                            String toValue = toProps.get(fromProp);

                            if (fromProp.contains(CtsModel.PROP_DOCUMENT_ADDED.toString())) {
                                sb = generateDocumentAddedMinute(toValue);
                            } else if (fromProp.contains(CtsModel.PROP_DOCUMENT_DELETED.toString())) {
                                sb = generateDocumentDeletedMinute(toValue);
                            } else if (fromProp.contains(CtsModel.PROP_SLAVE_ADDED.toString())) {
                                sb.append(generateGroupedCaseMinute(toValue, "Dependent grouped case added"));
                            } else if (fromProp.contains(CtsModel.PROP_MASTER_ADDED.toString())) {
                                sb.append(generateGroupedCaseMinute(toValue, "Master case associated"));
                            } else if (fromProp.contains(CtsModel.PROP_SLAVE_REMOVED.toString())) {
                                sb.append(generateGroupedCaseMinute(toValue, "Dependent grouped case removed"));
                            } else if (fromProp.contains(CtsModel.PROP_MASTER_REMOVED.toString())) {
                                sb.append(generateGroupedCaseMinute(toValue, "Master case no longer associated"));
                            } else if (fromProp.contains(CtsModel.PROP_LINK_CASE_ADDED.toString())) {
                                sb.append(generateLinkCaseMinute(toValue, "Link case added"));
                            } else if (fromProp.contains(CtsModel.PROP_LINK_CASE_REMOVED.toString())) {
                                sb.append(generateLinkCaseMinute(toValue, "Link case removed"));
                            }else {
                                if (!getPropertyWithoutNamespace(fromProp).equals("modified") &&
                                        !getPropertyWithoutNamespace(fromProp).equals("modifier")) {
                                    sb.append(generateChangedPropertyMinute(fromProp, fromValue, toValue));
                                }
                            }
                        }
                    } catch (InvalidPropertiesFormatException e) {
                        LOGGER.error("Invalid minute properties ",e);
                    }


                }
                if(addObjects!=null) {
                    String[] addProps = getPropertiesAsAnArray(addObjects);
                    if (addProps != null) {
                        for (int i = 0; i < addProps.length; i++) {
                            if (addProps[i].split("=").length == 2) {
                                String addProp = addProps[i].split("=")[0];
                                String addValue = addProps[i].split("=")[1];
                                if (addProp.contains(CtsModel.PROP_DOCUMENT_ADDED.toString())) {
                                    sb = generateDocumentAddedMinute(addValue);
                                } else if (addProp.contains(CtsModel.PROP_DOCUMENT_DELETED.toString())) {
                                    sb = generateDocumentDeletedMinute(addValue);
                                } else if (addProp.contains(CtsModel.PROP_SLAVE_ADDED.toString())) {
                                    sb.append(generateGroupedCaseMinute(addValue, "Dependent grouped case added"));
                                } else if (addProp.contains(CtsModel.PROP_MASTER_ADDED.toString())) {
                                    sb.append(generateGroupedCaseMinute(addValue, "Master case associated"));
                                } else if (addProp.contains(CtsModel.PROP_SLAVE_REMOVED.toString())) {
                                    sb.append(generateGroupedCaseMinute(addValue, "Dependent grouped case removed"));
                                } else if (addProp.contains(CtsModel.PROP_MASTER_REMOVED.toString())) {
                                    sb.append(generateGroupedCaseMinute(addValue, "Master case no longer associated"));
                                } else if (addProp.contains(CtsModel.PROP_LINK_CASE_ADDED.toString())) {
                                    sb.append(generateLinkCaseMinute(addValue, "Link case added"));
                                } else if (addProp.contains(CtsModel.PROP_LINK_CASE_REMOVED.toString())) {
                                    sb.append(generateLinkCaseMinute(addValue, "Link case removed"));
                                }else {
                                    sb.append(generatePropertyAddedMinute(addProp, addValue));
                                }
                            }
                        }
                    }
                }
                return sb.toString();
            case "COPY":
                sb.append("Case was copied from ");
                sb.append(valueStrings.get("/alfresco-access/transaction/move/from/path"));
                sb.append(" to ");
                sb.append(valueStrings.get("/alfresco-access/transaction/path"));
                return sb.toString();
            case "MOVE":
                sb.append("Case was moved from ");
                sb.append(valueStrings.get("/alfresco-access/transaction/move/from/path"));
                sb.append(" to ");
                sb.append(valueStrings.get("/alfresco-access/transaction/path"));
                return sb.toString();
            default:
                return null;
        }
        return null;
    }

    private StringBuilder generatePropertyAddedMinute(String addProp, String addValue) {
        if (decodeGroupQNames.contains(addProp)) {
            addValue = authorityService.getAuthorityDisplayName(addValue);
        }
        StringBuilder sb = new StringBuilder();
        if (addValue.equals("null")) {
            addValue = "";
        }
        sb.append("Property - " + getPropertyWithoutNamespace(addProp)).append("\n").append("Added Value - " + addValue).append("\n");
        return sb;
    }

    private StringBuilder generateChangedPropertyMinute(String fromProp, String fromValue, String toValue) {
        if (decodeGroupQNames.contains(fromProp)) {
            fromValue = authorityService.getAuthorityDisplayName(fromValue);
            toValue = authorityService.getAuthorityDisplayName(toValue);
        }
        StringBuilder sb = new StringBuilder();
        if (fromValue.equals("null")) {
            fromValue = "";
        }
        if (toValue.equals("null")) {
            toValue = "";
        }
        sb.append("Changed Property - " + getPropertyWithoutNamespace(fromProp));
        sb.append("\n");
        sb.append("From Value - " + fromValue);
        sb.append("\n");
        sb.append("To Value - " + toValue);
        sb.append("\n");
        return sb;
    }

    private StringBuilder generateDocumentAddedMinute(String toValue) {
        StringBuilder sb = new StringBuilder();
        NodeRef documentNodeRef = getNodeRefFromComment(toValue);
        if(documentNodeRef == null){
            sb.append(toValue);
        } else if(getNodeService().exists(documentNodeRef)) {
            String documentName = (String) nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME);
            if(toValue.toLowerCase().indexOf("version")>-1){
                String label = toValue.substring(0,toValue.toLowerCase().indexOf("version"));
                sb.append("Added version " + label + " to document - ");
            }else{
                sb.append("Added document - ");
            }

            sb.append(documentName);
        } else {
            sb.append("Added document (Now deleted) - ");
            sb.append(documentNodeRef);
        }
        return sb;
    }

    private StringBuilder generateDocumentDeletedMinute(String toValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(toValue);
//        NodeRef documentNodeRef = getNodeRefFromComment(toValue);


//        if(documentNodeRef == null) {
//            sb.append(toValue);
//        } else if(getNodeService().exists(documentNodeRef)) {
//            String documentName = (String) nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME);
//            sb.append("Deleted document - ");
//
//            sb.append(documentName);
//        } else {
//            sb.append("Added document (Now deleted) - ");
//            sb.append(documentNodeRef);
//        }
        return sb;
    }

    protected void createGroupedCaseMinuteText(StringBuilder sb, String minuteText, String uin, String urn) {
        sb.append(minuteText);
        sb.append(" - UIN: ");
        sb.append(uin);
        sb.append(", HRN: ");
        sb.append(urn);
        sb.append("\n");
    }

    protected void createLinkedCaseMinuteText(StringBuilder sb, String minuteText, String urn) {
        sb.append(minuteText);
        sb.append(" - HRN: ");
        sb.append(urn);
        sb.append("\n");
    }

    private StringBuilder generateGroupedCaseMinute(String toValue, String minuteText) {
        StringBuilder sb = new StringBuilder();
        String[] groupedComments = splitComment(toValue);
        for (String groupedComment : groupedComments) {
            NodeRef groupedNodeRef = getNodeRefFromComment(groupedComment);
            if(groupedNodeRef == null){
                sb.append(groupedComment);
            }else{
                String uin = (String) nodeService.getProperty(groupedNodeRef, CtsModel.PROP_UIN);
                String correspondenceType = (String) nodeService.getProperty(groupedNodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE);
                String urnSuffix = (String) nodeService.getProperty(groupedNodeRef, CtsModel.PROP_URN_SUFFIX);
                String urn = correspondenceType+"/"+urnSuffix;
                createGroupedCaseMinuteText(sb, minuteText, uin, urn);
            }
        }
        return sb;
    }

    private StringBuilder generateLinkCaseMinute(String toValue, String minuteText) {
        StringBuilder sb = new StringBuilder();
        String[] linkedComments = splitComment(toValue);
        for (String linkedComment : linkedComments) {
            NodeRef linkedNodeRef = getNodeRefFromComment(linkedComment);
            if (linkedNodeRef == null) {
                sb.append(toValue);
            } else {
                String correspondenceType = (String) nodeService.getProperty(linkedNodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE);
                String urnSuffix = (String) nodeService.getProperty(linkedNodeRef, CtsModel.PROP_URN_SUFFIX);
                String urn = correspondenceType+"/"+urnSuffix;
                createLinkedCaseMinuteText(sb, minuteText, urn);
            }
        }
        return sb;
    }

    protected String[] splitComment(String comment) {
        return StringUtils.split(comment, ';');
    }

    protected NodeRef getNodeRefFromComment(String comment) {
        if(comment != null) {
            int index = comment.lastIndexOf("workspace");
            if (index >= 0) {
                return new NodeRef(comment.substring(index));
            }
        }
        return null;
    }

    protected String getPropertyWithoutNamespace(String prop) {
        if(prop == null){
            return null;
        }
        if(prop.equals("")){
            return prop;
        }
        if(getDictionaryService().getProperty(QName.createQName(prop)) == null){
            return null;
        }

        return getDictionaryService().getProperty(QName.createQName(prop)).getTitle();
    }


    protected String[] getPropertiesAsAnArray(String fromObjects) {
        //it starts and ends with curly bracket so remove those
        fromObjects = fromObjects.substring(0, fromObjects.length()-1);
        fromObjects = fromObjects.substring(1);
        String[] array = fromObjects.split(",");
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }

    private AuditQueryGetProxy getAuditQuery() {
        return auditQuery;
    }

    public void setAuditQuery(AuditQueryGetProxy auditQuery) {
        this.auditQuery = auditQuery;
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    /**
     * parser for the audit properties
     * Still not perfect but better than before.
     * Wont cope with ,{ appearing in a value but that should be pretty rare.
     * We ignore the workflow config which has JSON in it and does not need displaying.
     * @param properties
     * @return
     * @throws InvalidPropertiesFormatException
     */
    public Map<String,String> parsePropertiesAsAnArray(String properties) throws InvalidPropertiesFormatException {
        Map<String,String> map = new HashMap<>();

        //it starts and ends with curly bracket so remove those
        properties = properties.substring(1, properties.length()-1);

        boolean openQName = false;
        boolean openValue = false;
        char lastChar = ' ',secondToLastChar = ' ';

        StringBuilder qnameBuilder = null,valueBuilder=null;

        for (int i = 0 ; i < properties.length() ; i++){
            char c = properties.charAt(i);
            //checking for the opening bracket
            if(!openQName && c == '{' && ((lastChar == ' ' && secondToLastChar == ',') || qnameBuilder == null)){
                openQName = true;
                if(qnameBuilder!=null){
                    //then we need to store the property we just parsed
                    if(valueBuilder == null){
                        //use an empty string to avoid Null pointers
                        map.put(qnameBuilder.toString(),"");
                    }else{
                        String value = valueBuilder.toString().trim();
                        if(value.endsWith(",")){
                            value = value.substring(0,value.length()-1);
                        }
                        map.put(qnameBuilder.toString(),value);
                    }
                    openValue = false;
                }
                qnameBuilder = new StringBuilder();
                qnameBuilder.append(c);

            }else if (openQName && c == '}'){
                openQName = false;
                i = fastForwardToEquals(properties,i,qnameBuilder);

                valueBuilder = new StringBuilder();
                openValue = true;
                if(qnameBuilder.toString().equals(CtsModel.PROP_CASE_WORKFLOW_STATUS.toString())){
                    //this is some system JSON so ignore and fast forward to next property
                    i = fastForwardThroughJSON(properties, i+1);
                    openValue = false;
                    openQName = false;
                    qnameBuilder = null;
                    lastChar = ',';
                    continue;
                }
            }else if (openQName){
                qnameBuilder.append(c);
            }else if(openValue){
                valueBuilder.append(c);
            }
            secondToLastChar = lastChar;
            lastChar = c;
        }
        if(qnameBuilder != null && !map.containsKey(qnameBuilder.toString())){
            //add the final value
            String value;
            if(valueBuilder!=null) {
                value = valueBuilder.toString().trim();
                if (value.endsWith(",")) {
                    value = value.substring(0, value.length() - 1);
                }
            }else{
                value = "";
            }
            map.put(qnameBuilder.toString(),value);
        }
        return map;
    }

    private int fastForwardToEquals(String properties, int i, StringBuilder qnameBuilder) throws InvalidPropertiesFormatException {
        for( ; i < properties.length() ; i++){
            char c = properties.charAt(i);
            if(c == '='){
                return i;
            }else{
                qnameBuilder.append(c);
            }
        }
        throw new InvalidPropertiesFormatException("Expected to find an equals sign: "+properties);
    }

    protected int fastForwardThroughJSON(String properties, int i) throws InvalidPropertiesFormatException {
        int depth = 0;
        for( ; i < properties.length() ; i++){
            char c = properties.charAt(i);
            if(c == '{'){
                depth++;
            }else if (c == '}'){
                depth--;
            }
            if(depth == 0){
                return ++i;
            }
        }
        throw new InvalidPropertiesFormatException("The JSON was not properly formatted: "+properties);
    }



    private List<Map<String, Object>> getAuditEnties(final NodeRef nodeRef) {
        Map<String, Object> auditEntries = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            @SuppressWarnings("synthetic-access")
            public Map<String, Object> doWork() throws Exception {
                Map<String, Object> auditEntries = getAuditQuery().getAudit(nodeRef);
                return auditEntries;
            }
        }, AuthenticationUtil.getSystemUserName());

        return (List<Map<String, Object>>) auditEntries.get(AuditQueryGetProxy.JSON_KEY_ENTRIES);
    }

    private boolean isRejectedCase(Map<String, String> valueStrings) {
        String action = valueStrings.get("/alfresco-access/transaction/action");
        if (action != null && "updateNodeProperties".equalsIgnoreCase(action)) {
            String fromObjects = valueStrings.get("/alfresco-access/transaction/properties/from");
            String toObjects = valueStrings.get("/alfresco-access/transaction/properties/to");
            if (fromObjects != null) {
                Map<String, String> fromProps;
                Map<String, String> toProps;
                try {
                    fromProps = parsePropertiesAsAnArray(fromObjects);
                    toProps = parsePropertiesAsAnArray(toObjects);
                    for (Map.Entry<String, String> entry : fromProps.entrySet()) {
                        String isTask = getPropertyWithoutNamespace(entry.getKey());
                        String toValue = toProps.get(entry.getKey());
                        if ("Task".equals(isTask) && TaskStatus.AMEND_RESPONSE.getStatus().equals(toValue)) {
                           return true;
                        }
                    }
                } catch (InvalidPropertiesFormatException e) {
                    LOGGER.error("Invalid minute properties ", e);
                }
            }
        }

        return false;
    }

    private String extractSignedOffDateFieldKey(Map<String, String> valueStrings) {
        String action = valueStrings.get("/alfresco-access/transaction/action");
        if (action != null && "updateNodeProperties".equalsIgnoreCase(action)) {
            String fromObjects = valueStrings.get("/alfresco-access/transaction/properties/from");
            if (fromObjects != null) {
                Map<String, String> fromProps;
                try {
                    fromProps = parsePropertiesAsAnArray(fromObjects);
                    for (Map.Entry<String, String> entry : fromProps.entrySet()) {
                        String isTask = getPropertyWithoutNamespace(entry.getKey());
                        if ("Task".equals(isTask) && isSignedOffBy(entry.getValue())) {
                            return entry.getValue();
                        }
                    }
                } catch (InvalidPropertiesFormatException e) {
                    LOGGER.error("Invalid minute properties ", e);
                }
            }
        }

        return null;
    }

    private boolean isSignedOffBy(String prop) {
        return prop!=null && (TaskStatus.MINISTERS_SIGN_OFF.getStatus().equals(prop)
                || TaskStatus.HOME_SECS_SIGN_OFF.getStatus().equals(prop)
                || TaskStatus.LORD_MINISTERS_SIGN_OFF.getStatus().equals(prop)
                || TaskStatus.PARLIAMENTARY_UNDER_SECRETARY_SIGN_OFF.getStatus().equals(prop));

    }
}
