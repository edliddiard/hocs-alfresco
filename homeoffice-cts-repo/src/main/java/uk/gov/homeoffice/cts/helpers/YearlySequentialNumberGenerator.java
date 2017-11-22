package uk.gov.homeoffice.cts.helpers;

import org.alfresco.cmis.CMISQueryOptions;
import org.alfresco.cmis.CMISQueryService;
import org.alfresco.cmis.CMISResultSet;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate a unique number for a year, the numbers are sequential
 * from 1 and should be 7 digits long.
 * This implementation uses relies on the Alfresco constraint that
 * a folder can't have children with the same name.
 * Created by chris davidson on 22/07/2014.
 */
public class YearlySequentialNumberGenerator implements NumberGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(YearlySequentialNumberGenerator.class);

    private NodeService nodeService;
    private TransactionService transactionService;
    private Repository repository;
    private CMISQueryService cmisQueryService;
    final private static String counterRootFolderName = "ctsCounter";

    public String nextNumber(final String year) {
        getTransactionService().getNonPropagatingUserTransaction();

        return getTransactionService().getRetryingTransactionHelper().doInTransaction(
            new RetryingTransactionHelper.RetryingTransactionCallback<String>()
            {
                public String execute() throws SQLException {
                    try {
                        String newName = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<String>() {
                            @SuppressWarnings("synthetic-access")
                            public String doWork() throws Exception {

                                LOGGER.debug("Getting number");
                                NodeRef counterNodeRef = getCounterRoot();

                                NodeRef yearNodeRef = getNodeService().getChildByName(counterNodeRef, ContentModel.ASSOC_CONTAINS, year);

                                //create the year node
                                if (yearNodeRef == null) {

                                    Map<QName, Serializable> contentProps = new HashMap<>();
                                    contentProps.put(ContentModel.PROP_NAME, year);

                                    yearNodeRef = getNodeService().createNode(counterNodeRef, ContentModel.ASSOC_CONTAINS,
                                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, year),
                                            ContentModel.TYPE_FOLDER, contentProps).getChildRef();
                                }

                                //find the latest folder under year
                                String sql = "select cmis:name from cmis:folder F WHERE IN_FOLDER('"+yearNodeRef.getId()+"') ORDER BY F.cmis:name DESC";
                                CMISQueryOptions cmisQueryOptions = new CMISQueryOptions(sql,getRepository().getCompanyHome().getStoreRef());
                                cmisQueryOptions.setMaxItems(1);
                                cmisQueryOptions.setQueryConsistency(QueryConsistency.TRANSACTIONAL);
                                CMISResultSet cmisResultSet = getCmisQueryService().query(cmisQueryOptions);

                                int thisNumber = 0;
                                if(cmisResultSet.length() > 0 ){
                                    NodeRef numberNodeRef = cmisResultSet.getNodeRef(0);
                                    String name = (String) getNodeService().getProperty(numberNodeRef, ContentModel.PROP_NAME);
                                    thisNumber = Integer.parseInt(name);
                                }

                                int nextNumber = thisNumber + 1;
                                //formatting should make sure there are 7 digits in number including leading zeroes
                                String newName = String.format("%07d", nextNumber);

                                LOGGER.debug("Trying name "+newName);

                                Map<QName, Serializable> contentProps = new HashMap<>();
                                contentProps.put(ContentModel.PROP_NAME, newName);
                                getNodeService().createNode(yearNodeRef, ContentModel.ASSOC_CONTAINS,
                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, newName),
                                        ContentModel.TYPE_FOLDER, contentProps).getChildRef();

                                LOGGER.debug("Created counter node");

                                return newName;
                            }
                        }, AuthenticationUtil.getSystemUserName());
                        return newName;
                    }catch(DuplicateChildNodeNameException e){
                        //Retrying transaction only retries on a specific list of exceptions,
                        //duplicate child name is one it should not capture, but for this
                        //case where we update the name it is OK. Throwing SQLException as it
                        //does trigger the retrying transaction
                        LOGGER.debug("Catching and hiding Duplicate child name exception ");
                        throw new SQLException();
                    }
                }
            },false,true);
    }

    @Override
    public String nextNumber(String year, String nodeRef) {
        return nextNumber(year);
    }

    private NodeRef ctsCounterNodeRef;

    /**
     * Get the root folder for all counters
     * @return NodeRef
     */
    public NodeRef getCounterRoot()
    {
        if (this.ctsCounterNodeRef == null) {
            NodeRef rootNodeRef = getRepository().getCompanyHome();

            this.ctsCounterNodeRef = getNodeService().getChildByName(rootNodeRef, ContentModel.ASSOC_CONTAINS, counterRootFolderName);

            if (this.ctsCounterNodeRef == null) {
                //then need to create it
                Map<QName, Serializable> contentProps = new HashMap<>();
                contentProps.put(ContentModel.PROP_NAME, counterRootFolderName);
                contentProps.put(ContentModel.PROP_DESCRIPTION, "System file do not change");

                ChildAssociationRef ctsCounterChildRef = getNodeService().createNode(rootNodeRef, ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, counterRootFolderName),
                        ContentModel.TYPE_FOLDER,contentProps);
                this.ctsCounterNodeRef = ctsCounterChildRef.getChildRef();
            }
        }
        return ctsCounterNodeRef;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public CMISQueryService getCmisQueryService() {
        return cmisQueryService;
    }

    public void setCmisQueryService(CMISQueryService cmisQueryService) {
        this.cmisQueryService = cmisQueryService;
    }
}
