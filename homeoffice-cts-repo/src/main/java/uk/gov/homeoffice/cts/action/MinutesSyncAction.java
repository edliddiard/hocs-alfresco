package uk.gov.homeoffice.cts.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.service.ManualMinutesService;
import uk.gov.homeoffice.cts.service.SystemMinutesService;
import uk.gov.homeoffice.ctsv2.model.CtsMinute;
import uk.gov.homeoffice.ctsv2.model.CtsMinuteModel;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by dawud on 21/06/2016.
 * Action will synchronise the Case Node Minutes Collated property with the Auditservice upon update of Manual and System Minutes relating to this Case
 */
public class MinutesSyncAction extends ActionExecuterAbstractBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinutesSyncAction.class);
    public static final String NAME = "MinutesSyncAction";
    public static final String PARAM_CASE_NODE_REF = "caseNodeRef";
    private static final String PARAM_APPLICATION = "alfresco-access";
    private static final String PARAM_PATH = "/alfresco-access/transaction/node";

    // Services
    private SystemMinutesService systemMinutesService;
    private ManualMinutesService manualMinutesService;
    private NodeService nodeService;
    private ContentService contentService;
    private AuditService auditService;
    private BehaviourFilter behaviourFilter;

    @Override
    public void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        LOGGER.info("Running MinutesSyncAction CaseNode[{}]", actionedUponNodeRef);
        // Add minute aspect and update with minutes from Audit service
        synchroniseMinutes(actionedUponNodeRef);
        LOGGER.info("Completed minutes synchronise for CaseNode[{}] ", actionedUponNodeRef.toString());
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        // Add definitions for action parameters
        paramList.add(
                new ParameterDefinitionImpl(                       // Create a new parameter defintion to add to the list
                        PARAM_CASE_NODE_REF,                              // The name used to identify the parameter
                        DataTypeDefinition.NODE_REF,                       // The parameter value type
                        true,                                           // Indicates whether the parameter is mandatory
                        getParamDisplayLabel(PARAM_CASE_NODE_REF)));      // The parameters display label
    }


    public void synchroniseMinutes(final NodeRef caseNode) {
        try {
            AuthenticationUtil.setRunAsUserSystem();
            if(exist(caseNode)) {
                // disable Audit temporarily
                auditService.disableAudit(PARAM_APPLICATION, PARAM_PATH);
                // disable BehaviourPolicies temporarily
                behaviourFilter.disableBehaviour(caseNode);

                // Add minute aspect and update with minutes copy
                updateMinuteProperty(caseNode);

                // enable Audit again
                auditService.enableAudit(PARAM_APPLICATION, PARAM_PATH);
                // enable BehaviourPolicies again
                behaviourFilter.enableBehaviour(caseNode);
            }

        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }

    public void updateMinuteProperty(NodeRef caseNode) {

        try {
            // Add Minute Aspect
            if (!nodeService.hasAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE)) {
                Map<QName, Serializable> minuteProps = new HashMap<>();
                nodeService.addAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE, minuteProps);
                LOGGER.debug("Success - Applied Minutes Aspect for CaseNode[{}] ", caseNode.toString());
            }
            // Writing data to a node's content
            ContentWriter writer = contentService.getWriter(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
//        writer.setMimetype("application/json;charset=UTF-8");
            writer.setEncoding("UTF-8");
            String content = getMinutesCollatedJSONStr(caseNode);
            writer.putContent(content);
            LOGGER.info("Success - Updated Minutes Property CaseNode[{}] ", caseNode.toString());
        }catch (InvalidNodeRefException e) {
            LOGGER.info("ERROR- InvalidNodeRefException - Updated Minutes Property CaseNode[{}] ", caseNode.toString());
        } catch (Exception e) {
            LOGGER.info("ERROR -Exception" +e.getMessage()+" -  while Updating Minutes Property CaseNode[{}] ", caseNode.toString());

        }
    }

    private boolean exist(NodeRef nodeRef) {
        NodeRef.Status status = nodeService.getNodeStatus(nodeRef);
        return (status != null && !status.isDeleted());

    }

    public String getMinutesCollatedJSONStr(NodeRef caseNode) {
        // Generate JSON response
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // jackson library defaults
        mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.WRAP_EXCEPTIONS);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        // custom options
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);


        // converting object to string value
        JsonGenerator jsonGenerator = null;
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            // Add the CtsCase properties to the CtsCase object
            JsonFactory jsonFactory = new JsonFactory();
            jsonGenerator = jsonFactory.createGenerator(writer);
            jsonGenerator.setCodec(mapper);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("minutes");
            jsonGenerator.writeObject(getMinutesCollated(caseNode));
            jsonGenerator.writeEndObject();

        } catch (IOException exception) {
            LOGGER.error("Unable to convert minutes to Json String. Exception[{}]", exception.getStackTrace());
        } finally {
            try {
                jsonGenerator.close();
                writer.close();
            } catch (IOException e) {
            }
        }
        return writer.toString();
    }

    public List<CtsMinute> getMinutesCollated(NodeRef caseNode) {
        // Retrieve Minutes
        List<uk.gov.homeoffice.cts.model.CtsMinute> minutes = getSystemMinutesService().getSystemMinutes(caseNode);

        // Get comments into a List<CtsMinute> as well, combine and sort
        List<uk.gov.homeoffice.cts.model.CtsMinute> manualMinutes = getManualMinutesService().getManualMinutes(caseNode);

        // Get CtsCase minutes
        List<uk.gov.homeoffice.cts.model.CtsMinute> completeList = new ArrayList<>();
        completeList.addAll(minutes);
        completeList.addAll(manualMinutes);
        Collections.sort(completeList, Collections.reverseOrder());
        return toCtsMinuteV2List(completeList);
    }

    public List<CtsMinute> toCtsMinuteV2List(List<uk.gov.homeoffice.cts.model.CtsMinute> ctsMinuteList) {
        // Convert old v1 case minutes to v2 api
        List<CtsMinute> ctsMinuteV2List = new ArrayList<>();
        for (uk.gov.homeoffice.cts.model.CtsMinute ctsMinute : ctsMinuteList) {
            ctsMinuteV2List.add(new CtsMinute(ctsMinute));
        }
        return ctsMinuteV2List;
    }


    public SystemMinutesService getSystemMinutesService() {
        return systemMinutesService;
    }

    public void setSystemMinutesService(SystemMinutesService systemMinutesService) {
        this.systemMinutesService = systemMinutesService;
    }

    public ManualMinutesService getManualMinutesService() {
        return manualMinutesService;
    }

    public void setManualMinutesService(ManualMinutesService manualMinutesService) {
        this.manualMinutesService = manualMinutesService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public AuditService getAuditService() {
        return auditService;
    }

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public BehaviourFilter getBehaviourFilter() {
        return behaviourFilter;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }
}
