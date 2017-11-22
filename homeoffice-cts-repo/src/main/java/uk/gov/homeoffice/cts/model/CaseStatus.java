package uk.gov.homeoffice.cts.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jackm on 22/09/2014.
 */
public enum CaseStatus {


    NEW("New", TaskStatus.getNewCaseTasks()), DRAFT("Draft", TaskStatus.getDraftCaseTasks()), OGD("OGD", TaskStatus.getOgdCaseTasks()),
    APPROVALS("Approvals", TaskStatus.getApprovalsCaseTasks()), OBTAIN_SIGNOFF("Obtain sign-off", TaskStatus.getSignoffCaseTasks()),
    DISPATCH("Dispatch", TaskStatus.getDispatchCaseTasks()), HOLD("Hold", TaskStatus.getHoldCaseTasks()), COMPLETED("Completed", null),
    DELETED("Deleted", null);

    private String status;
    private List<TaskStatus> taskStatuses;

    CaseStatus(String status, List<TaskStatus> taskStatuses){
        this.status = status;
        this.taskStatuses = taskStatuses;
    }

    public String getStatus(){
        return status;
    }

    public List<TaskStatus> getTaskStatuses(){
        return taskStatuses;
    }
}
