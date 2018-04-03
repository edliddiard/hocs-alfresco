package uk.gov.homeoffice.cts.helpers;


import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.helpers.CreateCaseWorkflowHelper.UnitAndTeamToAssign;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.FileDetails;

import javax.mail.MessagingException;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class CreateCaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCaseHelper.class);

    private NodeService nodeService;

    private CtsFolderHelper ctsFolderHelper;
    private TransactionService transactionService;

    private ContentService contentService;
    private FileFolderService fileFolderService;

    private CreateCaseWorkflowHelper workflowHelper = new CreateCaseWorkflowHelper();


    public String createCase(List<FileDetails> caseFiles, String caseQueueName) throws Exception {

        String name = "CaseCreatedByEmail-" + UUID.randomUUID().toString();
        Map<QName, Serializable> contentProps = new HashMap<>();

        UnitAndTeamToAssign forWorkflow = workflowHelper.getTeamAndUnitForCaseType(caseQueueName);

        contentProps.put(ContentModel.PROP_NAME, name);
        contentProps.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        contentProps.put(CtsModel.PROP_DATE_RECEIVED, new Date());
        contentProps.put(CtsModel.PROP_CORRESPONDENCE_TYPE, caseQueueName);
        contentProps.put(CtsModel.PROP_CASE_STATUS, "New");
        contentProps.put(CtsModel.PROP_CASE_TASK, "Create case");
        contentProps.put(CtsModel.PROP_ASSIGNED_UNIT, forWorkflow.getUnit());
        contentProps.put(CtsModel.PROP_ASSIGNED_TEAM, forWorkflow.getTeam());
        contentProps.put(CtsModel.PROP_ASSIGNED_USER, null);

        LOGGER.debug("Creating case ::  Name = " + name);
        LOGGER.debug("Creating case ::  Case Queue Name = " + caseQueueName);
        LOGGER.debug("Creating case ::  Unit = " + forWorkflow.getUnit());
        LOGGER.debug("Creating case ::  Team = " + forWorkflow.getTeam());

        UserTransaction txn = transactionService.getUserTransaction();
        try {
            txn.begin();

            // create case node
            ChildAssociationRef childRef = nodeService.createNode(ctsFolderHelper.getOrCreateCtsCasesFolder(),
                    ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                    CtsModel.TYPE_CTS_CASE,
                    contentProps);

            //we have atleast one file i.e. body of email
            for (FileDetails caseFile : caseFiles) {
                addFile(childRef.getChildRef(), caseFile);
            }

            txn.commit();
            LOGGER.debug("Case created:: " + childRef.getChildRef());
            return childRef.getChildRef().toString();
        } catch (Exception e) {
            txn.rollback();
            throw e;

        }
    }


    protected void addFile(NodeRef nodeRef, FileDetails fileDetails) throws MessagingException, IOException {
        QName contentQName = CtsModel.TYPE_CTS_DOCUMENT;

        FileInfo fileInfo = fileFolderService.create(nodeRef, fileDetails.getName(), contentQName);
        NodeRef fileNode = fileInfo.getNodeRef();

        Map<QName, Serializable> pros = new HashMap<>();
        pros.put(CtsModel.PROP_DOCUMENT_TYPE, "Original");
        pros.put(ContentModel.PROP_VERSION_LABEL, "1.0");
        pros.put(ContentModel.PROP_AUTO_VERSION, false);
        pros.put(ContentModel.PROP_INITIAL_VERSION, false);
        pros.put(CtsModel.PROP_DOCUMENT_DESCRIPTION, "Created by email tool");
        nodeService.addProperties(fileNode, pros);

        ContentWriter writer = contentService.getWriter(fileNode, ContentModel.PROP_CONTENT, true);
        writer.setLocale(Locale.UK);
        writer.setMimetype(fileDetails.getMimeType());
        writer.putContent(fileDetails.getFile());
    }


    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }
}
