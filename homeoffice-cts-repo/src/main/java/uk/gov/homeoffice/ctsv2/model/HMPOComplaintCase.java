package uk.gov.homeoffice.ctsv2.model;


import org.alfresco.service.namespace.QName;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Map;

public class HMPOComplaintCase extends CtsCase {

    private String secondaryTypeOfCorrespondent;
    private Boolean secondaryCorrespondentReplyTo;
    private String secondaryCorrespondentTitle;
    private String secondaryCorrespondentForename;
    private String secondaryCorrespondentSurname;
    private String secondaryCorrespondentAddressLine1;
    private String secondaryCorrespondentAddressLine2;
    private String secondaryCorrespondentAddressLine3;
    private String secondaryCorrespondentPostcode;
    private String secondaryCorrespondentCountry;
    private String secondaryCorrespondentEmail;
    private String secondaryCorrespondentTelephone;

    private String secCorrespondentTypeOfRepresentative;
    private Boolean secCorrespondentConsentAttached;

    private String thirdPartyTypeOfCorrespondent;
    private Boolean thirdPartyCorrespondentReplyTo;

    private String thirdPartyTypeOfRepresentative;
    private Boolean thirdPartyConsentAttached;

    private String hmpoRefundType;
    private String deferDueTo;
    private String officeOfOrigin;

    public HMPOComplaintCase(Map<QName, Serializable> caseProps) {
        super(caseProps);

        this.secondaryTypeOfCorrespondent = (String)caseProps.get(CtsModel.SECONDARY_TYPE_OF_CORRESPONDENT);
        this.secondaryCorrespondentReplyTo = (Boolean) caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_REPLY_TO);
        this.secondaryCorrespondentTitle = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_TITLE);
        this.secondaryCorrespondentForename = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_FORE_NAME);
        this.secondaryCorrespondentSurname = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_SURNAME);
        this.secondaryCorrespondentAddressLine1 = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_ADDRESS_LINE_1);
        this.secondaryCorrespondentAddressLine2 = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_ADDRESS_LINE_2);
        this.secondaryCorrespondentAddressLine3 = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_ADDRESS_LINE_3);
        this.secondaryCorrespondentPostcode = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_POST_CODE);
        this.secondaryCorrespondentCountry = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_COUNTRY);
        this.secondaryCorrespondentEmail = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_EMAIL);
        this.secondaryCorrespondentTelephone = (String)caseProps.get(CtsModel.SECONDARY_CORRESPONDENT_TELEPHONE);

        this.thirdPartyTypeOfCorrespondent = (String)caseProps.get(CtsModel.THIRD_PARTY_TYPE_OF_CORRESPONDENT);
        this.thirdPartyCorrespondentReplyTo = (Boolean)caseProps.get(CtsModel.THIRD_PARTY_CORRESPONDENT_REPLY_TO);

        this.hmpoRefundType = (String)caseProps.get(CtsModel.HMPO_REFUND_TYPE);
        this.deferDueTo = (String)caseProps.get(CtsModel.DEFER_DUE_TO);
        this.officeOfOrigin = (String)caseProps.get(CtsModel.OFFICE_OF_ORIGIN);

        this.secCorrespondentTypeOfRepresentative = (String)caseProps.get(CtsModel.SEC_CORRESPONDENT_TYPE_OF_REPRESENTATIVE);
        this.secCorrespondentConsentAttached = (Boolean)caseProps.get(CtsModel.SEC_CORRESPONDENT_CONSENT_ATTACHED);

        this.thirdPartyTypeOfRepresentative = (String)caseProps.get(CtsModel.THIRD_PARTY_TYPE_OF_REPRESENTATIVE);
        this.thirdPartyConsentAttached = (Boolean)caseProps.get(CtsModel.THIRD_PARTY_CONSENT_ATTACHED);

    }

    public Boolean getSecondaryCorrespondentReplyTo() {
        return secondaryCorrespondentReplyTo;
    }

    public String getSecondaryCorrespondentTitle() {
        return secondaryCorrespondentTitle;
    }

    public String getSecondaryCorrespondentForename() {
        return secondaryCorrespondentForename;
    }

    public String getSecondaryCorrespondentSurname() {
        return secondaryCorrespondentSurname;
    }

    public String getSecondaryCorrespondentAddressLine1() {
        return secondaryCorrespondentAddressLine1;
    }

    public String getSecondaryCorrespondentAddressLine2() {
        return secondaryCorrespondentAddressLine2;
    }

    public String getSecondaryCorrespondentAddressLine3() {
        return secondaryCorrespondentAddressLine3;
    }

    public String getSecondaryCorrespondentPostcode() {
        return secondaryCorrespondentPostcode;
    }

    public String getSecondaryCorrespondentCountry() {
        return secondaryCorrespondentCountry;
    }

    public String getSecondaryCorrespondentEmail() {
        return secondaryCorrespondentEmail;
    }

    public String getSecondaryCorrespondentTelephone() {
        return secondaryCorrespondentTelephone;
    }

    public Boolean getThirdPartyCorrespondentReplyTo() {
        return thirdPartyCorrespondentReplyTo;
    }

    public String getHmpoRefundType() {
        return hmpoRefundType;
    }

    public String getDeferDueTo() {
        return deferDueTo;
    }

    public String getSecondaryTypeOfCorrespondent() {
        return secondaryTypeOfCorrespondent;
    }

    public String getThirdPartyTypeOfCorrespondent() {
        return thirdPartyTypeOfCorrespondent;
    }

    public String getOfficeOfOrigin() {
        return officeOfOrigin;
    }

    public String getSecCorrespondentTypeOfRepresentative() {
        return secCorrespondentTypeOfRepresentative;
    }

    public Boolean getSecCorrespondentConsentAttached() {
        return secCorrespondentConsentAttached;
    }

    public String getThirdPartyTypeOfRepresentative() {
        return thirdPartyTypeOfRepresentative;
    }

    public Boolean getThirdPartyConsentAttached() {
        return thirdPartyConsentAttached;
    }
}
