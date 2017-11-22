package uk.gov.homeoffice.ctsv2.dashboard;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class DashboardDataJobScheduler implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardDataJobScheduler.class);

    private DashboardProcessor dashboardProcessor;

    /**
     * Scheduled Action initialisation.
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("Dashboard - Clearing and reloading data at: "+ new Date());

        JobDataMap jobData = jobExecutionContext.getJobDetail().getJobDataMap();
        dashboardProcessor = (DashboardProcessor) jobData.get("dashboardProcessor");
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            @SuppressWarnings("synthetic-access")
            public Map<String, Object> doWork() throws Exception {
                dashboardProcessor.refreshDashBoardData();
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

    }

}