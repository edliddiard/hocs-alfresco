package uk.gov.homeoffice.cts.helpers;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.*;
import java.util.UUID;

/**
 * Class to create a unique number direct in the database
 * Created by chris on 29/10/2014.
 */
public class DatabaseSequentialNumberGenerator implements NumberGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSequentialNumberGenerator.class);

    private DataSource dataSource;
    private TransactionService transactionService;
    private String env;

    @Override
    public String nextNumber(String year) {
        return nextNumber(year, UUID.randomUUID().toString());
    }

    /**
     * Get the next sequence number within the year, record this against the node ref supplied.
     * @param year String
     * @param nodeRef String
     * @return String
     */
    @Override
    public String nextNumber(String year, String nodeRef) {
        UserTransaction transaction = getTransactionService().getUserTransaction(false);
        Connection conn = null;
        Statement stmt = null;
        long counter = 0;
        try {
            transaction.begin();
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            checkAndCreateCounterTable(stmt, year);

            lockTable(stmt, year);
            counter = getQueryNextCounterValue(stmt, year);
            insertNextCounterValue(stmt, year, counter, nodeRef);
            unlockTables(stmt);
        } catch (Throwable e) {
            LOGGER.error("Unable to generate URN number: " + e.getMessage());
            if (transaction != null) {
                try {
                    LOGGER.debug("rolling back due to exception", e);
                    transaction.rollback();
                } catch (Exception ex) {
                    LOGGER.debug("Exception during rollback", ex);
                }
            }
            if (conn != null) {
                try {
                    conn.commit();
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.error("Error trying to commit or close connection: " + ex.getMessage());
                }
            }
            throw new AlfrescoRuntimeException("Unable to allocate unique number");
        } finally {
            // finally block used to close resources and unlock tables
            // first unlock tables and close statement
            if (stmt != null) {
                try {
                    unlockTables(stmt);
                    stmt.close();
                } catch (SQLException e) {
                    LOGGER.error("Error trying to unlock tables: " + e.getMessage());
                }
            }
            // then commit the transaction
            if (transaction != null) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    LOGGER.error("Error commit transaction in finally block: " + e.getMessage());
                }
            }
            // finally commit the connection
            if (conn != null) {
                try {
                    conn.commit();
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error trying to commit or close connection: " + e.getMessage());
                }
            }
        }

        return String.format("%07d", counter);
    }

    /**
     * Lock the counter table.
     * @param stmt Statement
     * @param year String
     * @throws SQLException
     */
    private void lockTable(Statement stmt, String year) throws SQLException {
        if (!env.equals("dev")) {
            String lockTable = "LOCK TABLES counter" + year + " WRITE;";
            stmt.execute(lockTable);
        }
    }

    /**
     * Unlock all tables.
     * @param stmt Statement
     * @throws SQLException
     */
    private void unlockTables(Statement stmt) throws SQLException {
        if (!env.equals("dev")) {
            String unlockTable = "UNLOCK TABLES;";
            stmt.execute(unlockTable);
        }
    }

    /**
     * Query the next counter value from the counter table.
     * @param stmt Statement
     * @param year String
     * @return long
     * @throws SQLException
     */
    private long getQueryNextCounterValue(Statement stmt, String year) throws SQLException {
        String nextCounterValueSql = "SELECT IFNULL(MAX(counter), 0)+1 nextCounter FROM counter" + year;
        ResultSet counterResultSet = null;
        long counter = 0;
        try {
            counterResultSet = stmt.executeQuery(nextCounterValueSql);
            if(counterResultSet.next()) {
                counter = counterResultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            counterResultSet.close();
        }
        if (counter != 0) {
            return counter;
        }
        return Long.parseLong(null);
    }

    /**
     * Insert the counter value into the counter table, along with the node ref.
     * @param stmt Statement
     * @param year String
     * @param counterValue long
     * @param nodeRef String
     * @throws SQLException
     */
    private void insertNextCounterValue(Statement stmt, String year, long counterValue, String nodeRef) throws SQLException {
        LOGGER.debug("Creating counter " + counterValue);
        String insertRow = "INSERT INTO counter" + year + " (counter, nodeRef) VALUES (" + counterValue + ", '" + nodeRef + "') ";
        stmt.execute(insertRow);
    }

    /**
     * Check if the counter table for the year exists, create it if not.
     * @param stmt Statement
     * @param year String
     * @throws SQLException
     */
    private void checkAndCreateCounterTable(Statement stmt, String year) throws SQLException {
        ResultSet resultSet = null;
        //always check if the table is there as we need to create them automatically when year changes
        String checkForTableSql = "SELECT COUNT(*)\n" +
                "FROM information_schema.tables \n" +
                "WHERE table_schema = 'alfresco' \n" +
                "AND table_name = 'counter"+year+"';";

        try {
            resultSet = stmt.executeQuery(checkForTableSql);
            if (resultSet.next()) {
                long tableExists = resultSet.getLong(1);
                LOGGER.debug("Does table exist " + tableExists);
                if (tableExists == 0) {
                    String createTableSql = "CREATE TABLE if not EXISTS counter" + year + "\n" +
                                            "(\n" +
                                            "id int not null auto_increment,\n" +
                                            "counter int not null,\n" +
                                            "noderef varchar(255) not null, \n" +
                                            "primary key (id)\n" +
                                            ") engine=innodb;";
                    try {
                        stmt.execute(createTableSql);
                    } catch(SQLException e) {
                        // cover it as the check does not work in h2 dev db
                        LOGGER.debug("Error creating table for counter", e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            resultSet.close();
        }
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    private Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
