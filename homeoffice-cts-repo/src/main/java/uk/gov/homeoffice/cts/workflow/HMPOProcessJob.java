package uk.gov.homeoffice.cts.workflow;


import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HMPOProcessJob implements Job {

    private HMPOProcessService hmpoProcessService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        JobDataMap jobData = jobExecutionContext.getJobDetail().getJobDataMap();

        this.hmpoProcessService = (HMPOProcessService) jobData.get("hmpoProcessService");

        hmpoProcessService.processHMPOCases();


    }


}
