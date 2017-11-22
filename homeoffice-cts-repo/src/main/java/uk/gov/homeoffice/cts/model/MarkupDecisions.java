package uk.gov.homeoffice.cts.model;

/**
 * Created by jackm on 26/11/2014.
 */

public enum MarkupDecisions {
    FAQ("FAQ"), REFER_TO_OGD("Refer to OGD"), POLICY_RESPONSE("Policy response"), NO_REPLY_NEEDED("No reply needed"), REQUEST_UNCLEAR("Request unclear"),
    REFER_TO_DCU("Refer to DCU"), PHONE_CALL_RESOLUTION("Phone call resolution"), WITHDRAW_QUESTION("Withdraw question"), INFORMALLY_RESOLVED("Informally resolved");

    private String decision;

    private MarkupDecisions(String decision){
        this.decision = decision;
    }

    public String getDecision() {
        return decision;
    }
}
