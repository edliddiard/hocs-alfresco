package uk.gov.homeoffice.cts.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackm on 22/09/2014.
 */
public enum TaskStatus {

    //Create case
    CREATE_CASE("Create case"), MISALLOCATED("Misallocated"), QA_CASE("QA case"),MARKUP_CASE("Mark up"),
    //Draft
    DRAFT_RESPONSE("Draft response"), AMEND_RESPONSE("Amend response"), QA_REVIEW("QA review"),
    DRAFT_AND_CLEAR("Draft and Clear"), DRAFT_DEFER("Defer Draft"), BRING_UP("Bring Up"),
    //Approvals
    SCS_APPROVAL("SCS approval"), PERM_SEC_APPROVAL("Perm Sec approval"), SPADS_APPROVAL("SpAds approval"),
    CHECK_AND_PRINT("Check and print"), CHECK_AND_BUFF_PRINT("Check and buff print"), PARLY_APPROVAL("Parly approval"), PRIVATE_OFFICE_APPROVAL("Private Office approval"),
    PRESS_OFFICE_REVIEW("Press Office review"), HS_PRIVATE_OFFICE_APPROVAL("HS Private Office approval"),
    //Signoff
    FOI_MINISTERS_SIGN_OFF("FOI Minister's sign-off"),
    MINISTERS_SIGN_OFF("Minister's sign-off"), PRINT_RUN("Print run"), HOME_SECS_SIGN_OFF("Home Sec's sign-off"),
    BUFF_PRINT_RUN("Buff print run"), LORD_MINISTERS_SIGN_OFF("Lords Minister's sign-off"), PARLIAMENTARY_UNDER_SECRETARY_SIGN_OFF("Parliamentary Under Secretary sign-off"),
    //Hold
    ICO_OR_TRIBUNAL_OUTCOME("ICO or tribunal outcome"),
    //OGD
    TRANSFER("Transfer"),
    //Dispatch
    DISPATCH_RESPONSE("Dispatch response"), DISPATCH_PARLY("Parly"),  ANSWERED("Answered"), DISPATCH_DEFER("Defer Dispatch"),  DISPATCHED("Dispatched");

    private String status;

    private TaskStatus(String status){
        this.status = status;

    }

    public static List<TaskStatus> getNewCaseTasks() {
        List<TaskStatus> newCaseTasks = new ArrayList<TaskStatus>();
        newCaseTasks.add(CREATE_CASE);
        newCaseTasks.add(QA_CASE);
        newCaseTasks.add(MISALLOCATED);
        newCaseTasks.add(MARKUP_CASE);

        return newCaseTasks;
    }

    public static List<TaskStatus> getDraftCaseTasks() {
        List<TaskStatus> draftCaseTasks = new ArrayList<TaskStatus>();
        draftCaseTasks.add(DRAFT_RESPONSE);
        draftCaseTasks.add(AMEND_RESPONSE);
        draftCaseTasks.add(QA_REVIEW);
        draftCaseTasks.add(DRAFT_DEFER);
        draftCaseTasks.add(BRING_UP);

        return draftCaseTasks;
    }

    public static List<TaskStatus> getApprovalsCaseTasks() {
        List<TaskStatus> approvalCaseTasks = new ArrayList<TaskStatus>();
        approvalCaseTasks.add(SCS_APPROVAL);
        approvalCaseTasks.add(PERM_SEC_APPROVAL);
        approvalCaseTasks.add(SPADS_APPROVAL);
        approvalCaseTasks.add(CHECK_AND_PRINT);
        approvalCaseTasks.add(CHECK_AND_BUFF_PRINT);
        approvalCaseTasks.add(PARLY_APPROVAL);
        approvalCaseTasks.add(PRIVATE_OFFICE_APPROVAL);
        approvalCaseTasks.add(HS_PRIVATE_OFFICE_APPROVAL);
        approvalCaseTasks.add(PRESS_OFFICE_REVIEW);

        return approvalCaseTasks;
    }

    public static List<TaskStatus> getSignoffCaseTasks() {
        List<TaskStatus> signoffCaseTasks = new ArrayList<TaskStatus>();
        signoffCaseTasks.add(MINISTERS_SIGN_OFF);
        signoffCaseTasks.add(PRINT_RUN);
        signoffCaseTasks.add(HOME_SECS_SIGN_OFF);
        signoffCaseTasks.add(BUFF_PRINT_RUN);
        signoffCaseTasks.add(LORD_MINISTERS_SIGN_OFF);
        signoffCaseTasks.add(PARLIAMENTARY_UNDER_SECRETARY_SIGN_OFF);
        signoffCaseTasks.add(FOI_MINISTERS_SIGN_OFF);
        return signoffCaseTasks;
    }

    public static List<TaskStatus> getOgdCaseTasks() {
        List<TaskStatus> ogdCaseTasks = new ArrayList<TaskStatus>();
        ogdCaseTasks.add(TRANSFER);
        return ogdCaseTasks;
    }

    public static List<TaskStatus> getHoldCaseTasks() {
        List<TaskStatus> holdCaseTasks = new ArrayList<TaskStatus>();
        holdCaseTasks.add(ICO_OR_TRIBUNAL_OUTCOME);
        return holdCaseTasks;
    }

    public static List<TaskStatus> getDispatchCaseTasks() {
        List<TaskStatus> dispatchCaseTasks = new ArrayList<TaskStatus>();
        dispatchCaseTasks.add(DISPATCH_RESPONSE);
        dispatchCaseTasks.add(DISPATCH_PARLY);
        dispatchCaseTasks.add(ANSWERED);
        dispatchCaseTasks.add(DISPATCH_DEFER);
        dispatchCaseTasks.add(DISPATCHED);
        return dispatchCaseTasks;
    }

    public String getStatus() {
        return status;
    }
}
