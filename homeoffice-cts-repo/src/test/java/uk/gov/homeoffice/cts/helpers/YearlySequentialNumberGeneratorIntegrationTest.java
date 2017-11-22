package uk.gov.homeoffice.cts.helpers;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseAlfrescoSpringTest;
import org.junit.Before;

import javax.transaction.*;
import java.util.UUID;

/**
 * Test for yearly sequential number generator running spring integration
 * Created by Chris Davidson on 22/07/2014.
 */
public class YearlySequentialNumberGeneratorIntegrationTest {// extends BaseAlfrescoSpringTest {
    private Repository repository;

//    @Override
//    @Before
//    protected void onSetUpInTransaction() throws Exception {
//        super.onSetUpInTransaction();
//
//        // Get the required services
//        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
//        this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
//        repository = (Repository)this.applicationContext.getBean("repositoryHelper");
//
//    }
    final private static String NUMBER_SEQUENCE = UUID.randomUUID().toString();
//    public void testGetFirstNumber() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
//        YearlySequentialNumberGenerator yearlySequentialNumberGenerator = new YearlySequentialNumberGenerator();
//        yearlySequentialNumberGenerator.setNodeService(this.nodeService);
//        yearlySequentialNumberGenerator.setTransactionService(this.transactionService);
//        yearlySequentialNumberGenerator.setRepository(repository);
//
//        assertNotNull(yearlySequentialNumberGenerator.getNodeService());
//
//        String numberOne = yearlySequentialNumberGenerator.nextNumber(NUMBER_SEQUENCE);
//        assertEquals("0000001", numberOne);
//
//        String numberTwo = yearlySequentialNumberGenerator.nextNumber(NUMBER_SEQUENCE);
//        assertEquals("0000002", numberTwo);
//
//        String numberThree = yearlySequentialNumberGenerator.nextNumber(NUMBER_SEQUENCE);
//        assertEquals("0000003", numberThree);
//    }
}
