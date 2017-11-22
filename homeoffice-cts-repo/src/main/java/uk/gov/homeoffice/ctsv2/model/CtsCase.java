package uk.gov.homeoffice.ctsv2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.alfresco.model.ContentModel;
import org.alfresco.rest.api.model.Folder;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.joda.time.LocalDate;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CtsCase extends Folder {

    private String id;
    private String correspondenceType;
    private String caseStatus;
    private String caseTask;
    private String caseOwner;
    private String urnSuffix;
    private String markupDecision;
    private String markupUnit;
    private String markupTopic;
    private String markupMinister;
    private String markupTeam;
    private String secondaryTopic;
    private String assignedUnit;
    private String assignedTeam;
    private String assignedUser;
    private String originalDrafterUnit;
    private String originalDrafterTeam;
    private String originalDrafterUser;
    private List<CtsCase> linkedCases;
    private Boolean isLinkedCase;
    private String caseWorkflowStatus;
    private String caseMandatoryFields;
    private String caseMandatoryFieldDependencies;
    private String caseMandatoryFieldStatus;
    private String caseMandatoryFieldTask;
    private String ogdName;
    private Date statusUpdatedDatetime;
    private Date taskUpdatedDatetime;
    private Date ownerUpdatedDatetime;
    private Boolean autoCreatedCase;
    private Integer returnedCount;
    // DCU
    private Date dateReceived;
    private Date dateOfLetter;
    private String channel;
    private Boolean priority;
    private Boolean advice;
    private Date caseResponseDeadline;
    private Date poTarget;
    private Date responderHubTarget;
    private Date allocateToResponderTarget;
    private Boolean homeSecretaryReply;
    private String mpRef;
    private String replyToName;
    private String replyToPostcode;
    private String replyToAddressLine1;
    private String replyToAddressLine2;
    private String replyToAddressLine3;
    private String replyToCountry;
    private String replyToTelephone;
    private String replyToEmail;
    private Boolean replyToNumberTenCopy;
    private String correspondentTitle;
    private String correspondentForename;
    private String correspondentSurname;
    private String correspondentPostcode;
    private String correspondentAddressLine1;
    private String correspondentAddressLine2;
    private String correspondentAddressLine3;
    private String correspondentCountry;
    private String correspondentTelephone;
    private String correspondentEmail;
    // PQ
    private String uin;
    private Date politicianDeadline;
    private Date opDate;
    private Date woDate;
    private String questionNumber;
    private String questionText;
    private String receivedType;
    private String answerText;
    private Date draftDate;
    private String member;
    private String constituency;
    private String party;
    private Boolean signedByHomeSec;
    private Boolean signedByLordsMinister;
    private String  lordsMinister;
    private Boolean reviewedByPermSec;
    private Boolean reviewedBySpads;
    private Boolean parlyDispatch;
    private Boolean roundRobin;
    private String cabinetOfficeGuidance;
    private String transferDepartmentName;
    private List<CtsCase> groupedCases;
    private Boolean isGroupedSlave;
    private Boolean isGroupedMaster;
    private String masterNodeRef;
    private String answeringMinister;
    private String answeringMinisterId;
    // UKVI and HMPO complaints
    private String caseRef;
    private String thirdPartyCorrespondentTitle;
    private String thirdPartyCorrespondentForename;
    private String thirdPartyCorrespondentSurname;
    private String thirdPartyCorrespondentOrganisation;
    private String thirdPartyCorrespondentTelephone;
    private String thirdPartyCorrespondentEmail;
    private String thirdPartyCorrespondentPostcode;
    private String thirdPartyCorrespondentAddressLine1;
    private String thirdPartyCorrespondentAddressLine2;
    private String thirdPartyCorrespondentAddressLine3;
    private String thirdPartyCorrespondentCountry;
    // FOI
    private Date allocateTarget;
    private Date draftResponseTarget;
    private Date scsApprovalTarget;
    private Date finalApprovalTarget;
    private Date dispatchTarget;
    private Boolean foiMinisterSignOff;
    private Boolean foiIsEir;
    private String exemptions;
    private Boolean pitExtension;
    private Date pitLetterSentDate;
    private String pitQualifiedExemptions;
    private Boolean acpoConsultation;
    private Boolean foiDisclosure;
    //FOI Complaints
    private String hoCaseOfficer;
    private Date responseDate;
    private Boolean complex;
    private Boolean newInformationReleased;
    private String icoReference;
    private String icoOutcome;
    private String icoComplaintOfficer;
    private Date icoOutcomeDate;
    private String tsolRep;
    private String appellant;
    private Boolean hoJoined;
    private String tribunalOutcome;
    private Date tribunalOutcomeDate;
    private Boolean enforcementNoticeNeeded;
    private Date enforcementNoticeDeadline;
    private String organisation;
    // HMPO
    private Boolean cabinetOfficeConsultation;
    private Boolean nslgConsultation;
    private Boolean royalsConsultation;
    private Boolean roundRobinAdviceConsultation;
    private String hmpoResponse;
    private String hmpoStage;
    private Boolean replyToCorrespondent;
    private String typeOfCorrespondent;
    private String typeOfComplainant;
    private String typeOfRepresentative;
    private String typeOfThirdParty;
    private Boolean consentAttached;
    private Boolean replyToApplicant;
    private String applicantTitle;
    private String applicantForename;
    private String applicantSurname;
    private String applicantAddressLine1;
    private String applicantAddressLine2;
    private String applicantAddressLine3;
    private String applicantPostcode;
    private String applicantCountry;
    private String applicantEmail;
    private String applicantTelephone;
    private Boolean replyToComplainant;
    private String complainantTitle;
    private String complainantForename;
    private String complainantSurname;
    private String complainantAddressLine1;
    private String complainantAddressLine2;
    private String complainantAddressLine3;
    private String complainantPostcode;
    private String complainantCountry;
    private String complainantEmail;
    private String complainantTelephone;
    private String hmpoRefundDecision;
    private String hmpoRefundAmount;
    private String hmpoComplaintOutcome;
    private String hmpoPassportNumber;
    private String hmpoApplicationNumber;

    //on fly details
    private String displayStatus;
    private String displayTask;
    private Integer caseProgressStatus;
    protected String canonicalCorrespondent;

    //hmpo common fields

    private Date bringUpDate;


    public CtsCase(Map<QName, Serializable> caseProps) {
        this.name = (String)caseProps.get(ContentModel.PROP_NAME);
        this.id = ((String)caseProps.get(ContentModel.PROP_NODE_UUID));
        this.createdAt = ((Date)caseProps.get(ContentModel.PROP_CREATED));
        this.correspondenceType = (String)caseProps.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
        this.caseStatus = (String)caseProps.get(CtsModel.PROP_CASE_STATUS);
        this.caseTask = (String)caseProps.get(CtsModel.PROP_CASE_TASK);
        this.urnSuffix = (String)caseProps.get(CtsModel.PROP_URN_SUFFIX);
        this.markupDecision = (String)caseProps.get(CtsModel.PROP_MARKUP_DECISION);
        this.markupUnit = (String)caseProps.get(CtsModel.PROP_MARKUP_UNIT);
        this.markupTopic = (String)caseProps.get(CtsModel.PROP_MARKUP_TOPIC);
        this.markupMinister = (String)caseProps.get(CtsModel.PROP_MARKUP_MINISTER);
        this.markupTeam = (String)caseProps.get(CtsModel.PROP_MARKUP_TEAM);
        this.secondaryTopic = (String)caseProps.get(CtsModel.PROP_SECONDARY_TOPIC);
        this.assignedUnit = (String)caseProps.get(CtsModel.PROP_ASSIGNED_UNIT);
        this.assignedTeam = (String)caseProps.get(CtsModel.PROP_ASSIGNED_TEAM);
        this.assignedUser = (String)caseProps.get(CtsModel.PROP_ASSIGNED_USER);
        this.originalDrafterUnit = (String)caseProps.get(CtsModel.PROP_ORIGINAL_DRAFTER_UNIT);
        this.originalDrafterTeam = (String)caseProps.get(CtsModel.PROP_ORIGINAL_DRAFTER_TEAM);
        this.originalDrafterUser = (String)caseProps.get(CtsModel.PROP_ORIGINAL_DRAFTER_USER);
        this.isLinkedCase = Boolean.parseBoolean(caseProps.get(CtsModel.PROP_IS_LINKED_CASE).toString());
        this.caseWorkflowStatus = (String)caseProps.get(CtsModel.PROP_CASE_WORKFLOW_STATUS);
        this.caseMandatoryFields = (String)caseProps.get(CtsModel.PROP_CASE_MANDATORY_FIELDS);
        this.caseMandatoryFieldDependencies = (String)caseProps.get(CtsModel.PROP_CASE_MANDATORY_FIELDS_DEPS);
        this.caseMandatoryFieldStatus = (String)caseProps.get(CtsModel.PROP_CASE_MANDATORY_FIELDS_STATUS);
        this.caseMandatoryFieldTask = (String)caseProps.get(CtsModel.PROP_CASE_MANDATORY_FIELDS_TASK);
        this.ogdName = (String)caseProps.get(CtsModel.PROP_OGD_NAME);
        this.statusUpdatedDatetime = (Date)caseProps.get(CtsModel.PROP_STATUS_UPDATED_DATETIME);
        this.taskUpdatedDatetime = (Date)caseProps.get(CtsModel.PROP_TASK_UPDATED_DATETIME);
        this.ownerUpdatedDatetime = (Date)caseProps.get(CtsModel.PROP_OWNER_UPDATED_DATETIME);
        this.autoCreatedCase = (Boolean)caseProps.get(CtsModel.PROP_AUTO_CREATED_CASE);
        this.returnedCount = (Integer)caseProps.get(CtsModel.PROP_RETURNED_COUNT);
        // DCU specific
        this.dateReceived = (Date)caseProps.get(CtsModel.PROP_DATE_RECEIVED);
        this.dateOfLetter = (Date)caseProps.get(CtsModel.PROP_DATE_OF_LETTER);
        this.channel = (String)caseProps.get(CtsModel.PROP_CHANNEL);
        this.priority = (Boolean)caseProps.get(CtsModel.PROP_PRIORITY);
        this.advice = (Boolean)caseProps.get(CtsModel.PROP_ADVICE);
        this.caseResponseDeadline = (Date)caseProps.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);
        this.poTarget = (Date)caseProps.get(CtsModel.PROP_PO_TARGET);
        this.setResponderHubTarget((Date)caseProps.get(CtsModel.PROP_RESPONDER_HUB_TARGET));
        this.setAllocateToResponderTarget((Date)caseProps.get(CtsModel.PROP_ALLOCATE_TO_RESPONDER_TARGET));
        this.dispatchTarget = (Date)caseProps.get(CtsModel.PROP_DISPATCH_TARGET);
        this.homeSecretaryReply = (Boolean)caseProps.get(CtsModel.PROP_HOME_SECRETARY_REPLY);
        this.mpRef = (String)caseProps.get(CtsModel.PROP_MP_REF);
        this.replyToName = (String)caseProps.get(CtsModel.PROP_REPLY_TO_NAME);
        this.replyToPostcode = (String)caseProps.get(CtsModel.PROP_REPLY_TO_POSTCODE);
        this.replyToAddressLine1 = (String)caseProps.get(CtsModel.PROP_REPLY_TO_ADDRESS_LINE1);
        this.replyToAddressLine2 = (String)caseProps.get(CtsModel.PROP_REPLY_TO_ADDRESS_LINE2);
        this.replyToAddressLine3 = (String)caseProps.get(CtsModel.PROP_REPLY_TO_ADDRESS_LINE3);
        this.replyToCountry = (String)caseProps.get(CtsModel.PROP_REPLY_TO_COUNTRY);
        this.replyToTelephone = (String)caseProps.get(CtsModel.PROP_REPLY_TO_TELEPHONE);
        this.replyToEmail = (String)caseProps.get(CtsModel.PROP_REPLY_TO_EMAIL);
        this.replyToNumberTenCopy = (Boolean)caseProps.get(CtsModel.PROP_NO10_COPY);
        this.correspondentTitle = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_TITLE);
        this.correspondentForename = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_FORENAME);
        this.correspondentSurname = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_SURNAME);
        this.correspondentPostcode = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_POSTCODE);
        this.correspondentAddressLine1 = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE1);
        this.correspondentAddressLine2 = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE2);
        this.correspondentAddressLine3 = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE3);
        this.correspondentCountry = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_COUNTRY);
        this.correspondentTelephone = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_TELEPHONE);
        this.correspondentEmail = (String)caseProps.get(CtsModel.PROP_CORRESPONDENT_EMAIL);
        // PQ specific
        this.uin = (String)caseProps.get(CtsModel.PROP_UIN);
        this.politicianDeadline = (Date)caseProps.get(CtsModel.PROP_DATE_RECEIVED);
        this.opDate = (Date)caseProps.get(CtsModel.PROP_OP_DATE);
        this.woDate = (Date)caseProps.get(CtsModel.PROP_WO_DATE);
        this.questionNumber = (String)caseProps.get(CtsModel.PROP_QUESTION_NUMBER);
        this.questionText = (String)caseProps.get(CtsModel.PROP_QUESTION_TEXT);
        this.receivedType = (String)caseProps.get(CtsModel.PROP_RECEIVED_TYPE);
        this.answerText = (String)caseProps.get(CtsModel.PROP_ANSWER_TEXT);
        this.member = (String)caseProps.get(CtsModel.PROP_MEMBER);
        this.constituency = (String)caseProps.get(CtsModel.PROP_CONSTITUENCY);
        this.party = (String)caseProps.get(CtsModel.PROP_PARTY);
        this.signedByHomeSec = (Boolean)caseProps.get(CtsModel.PROP_SIGNED_BY_HOME_SEC);
        this.signedByLordsMinister = (Boolean)caseProps.get(CtsModel.PROP_SIGNED_BY_LORDS_MINISTER);
        this.lordsMinister = (String)caseProps.get(CtsModel.PROP_LORDS_MINISTER);
        this.reviewedByPermSec = (Boolean)caseProps.get(CtsModel.PROP_REVIEWED_BY_PERM_SEC);
        this.reviewedBySpads = (Boolean)caseProps.get(CtsModel.PROP_REVIEWED_BY_SPADS);
        this.parlyDispatch = (Boolean)caseProps.get(CtsModel.PROP_PARLY_DISPATCH);
        this.roundRobin = (Boolean)caseProps.get(CtsModel.PROP_ROUND_ROBIN);
        this.cabinetOfficeGuidance = (String)caseProps.get(CtsModel.PROP_CABINET_OFFICE_GUIDANCE);
        this.transferDepartmentName = (String)caseProps.get(CtsModel.PROP_TRANSFER_DEPARTMENT_NAME);
        this.isGroupedSlave = Boolean.parseBoolean(caseProps.get(CtsModel.PROP_IS_GROUPED_SLAVE).toString());
        this.isGroupedMaster = Boolean.parseBoolean(caseProps.get(CtsModel.PROP_IS_GROUPED_MASTER).toString());
        this.masterNodeRef = (String)caseProps.get(CtsModel.PROP_MASTER_NODE_REF);
        this.answeringMinister = (String)caseProps.get(CtsModel.PROP_ANSWERING_MINISTER);
        this.answeringMinisterId = (String)caseProps.get(CtsModel.PROP_ANSWERING_MINISTER_ID);
        //UKVI
        this.caseRef = (String)caseProps.get(CtsModel.PROP_CASE_REF);
        this.thirdPartyCorrespondentTitle = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_TITLE);
        this.thirdPartyCorrespondentForename = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_FORENAME);
        this.thirdPartyCorrespondentSurname = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_SURNAME);
        this.thirdPartyCorrespondentOrganisation = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ORGANISATION);
        this.thirdPartyCorrespondentTelephone = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_TELEPHONE);
        this.thirdPartyCorrespondentEmail = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_EMAIL);
        this.thirdPartyCorrespondentPostcode = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_POSTCODE);
        this.thirdPartyCorrespondentAddressLine1 = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1);
        this.thirdPartyCorrespondentAddressLine2 = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2);
        this.thirdPartyCorrespondentAddressLine3 = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3);
        this.thirdPartyCorrespondentCountry = (String)caseProps.get(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_COUNTRY);
        //FOI
        this.allocateTarget = (Date)caseProps.get(CtsModel.PROP_ALLOCATE_TARGET);
        this.draftResponseTarget = (Date)caseProps.get(CtsModel.PROP_DRAFT_RESPONSE_TARGET);
        this.scsApprovalTarget = (Date)caseProps.get(CtsModel.PROP_SCS_APPROVAL_TARGET);
        this.finalApprovalTarget = (Date)caseProps.get(CtsModel.PROP_FINAL_APPROVAL_TARGET);
        this.foiMinisterSignOff = (Boolean)caseProps.get(CtsModel.PROP_FOI_MINISTER_SIGN_OFF);
        this.foiIsEir = (Boolean)caseProps.get(CtsModel.PROP_FOI_IS_EIR);
        this.exemptions = (String)caseProps.get(CtsModel.PROP_EXEMPTIONS);
        this.pitExtension = (Boolean)caseProps.get(CtsModel.PROP_PIT_EXTENSION);
        this.pitLetterSentDate = (Date)caseProps.get(CtsModel.PROP_PIT_LETTER_SENT_DATE);
        this.pitQualifiedExemptions = (String)caseProps.get(CtsModel.PROP_PIT_QUALIFIED_EXEMPTIONS);
        this.acpoConsultation = (Boolean)caseProps.get(CtsModel.PROP_ACPO_CONSULTATION);
        this.foiDisclosure = (Boolean)caseProps.get(CtsModel.PROP_FOI_DISCLOSURE);
        //FOI complaints
        this.hoCaseOfficer = (String)caseProps.get(CtsModel.PROP_HO_CASE_OFFICE);
        this.responseDate = (Date)caseProps.get(CtsModel.PROP_RESPONSE_DATE);
        this.complex = (Boolean)caseProps.get(CtsModel.PROP_COMPLEX);
        this.newInformationReleased = (Boolean)caseProps.get(CtsModel.PROP_NEW_INFORMATION_RELEASED);
        this.icoReference = (String)caseProps.get(CtsModel.PROP_ICO_REFERENCE);
        this.icoOutcome = (String)caseProps.get(CtsModel.PROP_ICO_OUTCOME);
        this.icoComplaintOfficer = (String)caseProps.get(CtsModel.PROP_ICO_COMPLAINT_OFFICER);
        this.icoOutcomeDate = (Date)caseProps.get(CtsModel.PROP_OUTCOME_DATE);
        this.tsolRep = (String)caseProps.get(CtsModel.PROP_TSLO_REP);
        this.appellant = (String)caseProps.get(CtsModel.PROP_APPELLANT);
        this.hoJoined = (Boolean)caseProps.get(CtsModel.PROP_HO_JOINED);
        this.tribunalOutcome = (String)caseProps.get(CtsModel.PROP_TRIBUNAL_OUTCOME);
        this.tribunalOutcomeDate = (Date)caseProps.get(CtsModel.PROP_TRIBUNAL_OUTCOME_DATE);
        this.enforcementNoticeNeeded = (Boolean)caseProps.get(CtsModel.PROP_ENFORCEMENT_NOTICE_NEEDED);
        this.enforcementNoticeDeadline = (Date)caseProps.get(CtsModel.PROP_ENFORCEMENT_NOTICE_DEADLINE);
        this.organisation = (String)caseProps.get(CtsModel.PROP_ORGANISATION);
        //HMPO
        this.cabinetOfficeConsultation = (Boolean)caseProps.get(CtsModel.PROP_CABINET_OFFICE_CONSULTATION);
        this.nslgConsultation = (Boolean)caseProps.get(CtsModel.PROP_NSLG_CONSULTATION);
        this.royalsConsultation = (Boolean)caseProps.get(CtsModel.PROP_ROYALS_CONSULTATION);
        this.roundRobinAdviceConsultation = (Boolean)caseProps.get(CtsModel.PROP_ROUND_ROBIN_ADVICE_CONSULTATION);
        this.hmpoResponse = (String)caseProps.get(CtsModel.PROP_HMPO_RESPONSE);
        this.hmpoStage = (String)caseProps.get(CtsModel.PROP_HMPO_STAGE);
        this.replyToCorrespondent = (Boolean)caseProps.get(CtsModel.PROP_REPLY_TO_CORRESPONDENT);
        this.typeOfCorrespondent = (String)caseProps.get(CtsModel.PROP_TYPE_OF_CORRESPONDENT);
        this.typeOfComplainant = (String)caseProps.get(CtsModel.PROP_TYPE_OF_COMPLAINANT);
        this.typeOfRepresentative = (String)caseProps.get(CtsModel.PROP_TYPE_OF_REPRESENTATIVE);
        this.typeOfThirdParty = (String)caseProps.get(CtsModel.PROP_TYPE_OF_THIRD_PARTY);
        this.consentAttached = (Boolean)caseProps.get(CtsModel.PROP_CONSENT_ATTACHED);
        this.replyToApplicant = (Boolean)caseProps.get(CtsModel.PROP_REPLY_TO_APPLICANT);
        this.applicantTitle = (String)caseProps.get(CtsModel.PROP_APPLICANT_TITLE);
        this.applicantForename = (String)caseProps.get(CtsModel.PROP_APPLICANT_FORENAME);
        this.applicantSurname = (String)caseProps.get(CtsModel.PROP_APPLICANT_SURNAME);
        this.applicantAddressLine1 = (String)caseProps.get(CtsModel.PROP_APPLICANT_ADDRESS_LINE1);
        this.applicantAddressLine2 = (String)caseProps.get(CtsModel.PROP_APPLICANT_ADDRESS_LINE2);
        this.applicantAddressLine3 = (String)caseProps.get(CtsModel.PROP_APPLICANT_ADDRESS_LINE3);
        this.applicantPostcode = (String)caseProps.get(CtsModel.PROP_APPLICANT_POSTCODE);
        this.applicantCountry = (String)caseProps.get(CtsModel.PROP_APPLICANT_COUNTRY);
        this.applicantEmail = (String)caseProps.get(CtsModel.PROP_APPLICANT_EMAIL);
        this.applicantTelephone = (String)caseProps.get(CtsModel.PROP_APPLICANT_TELEPHONE);
        this.replyToComplainant = (Boolean)caseProps.get(CtsModel.PROP_REPLY_TO_COMPLAINANT);
        this.complainantTitle = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_TITLE);
        this.complainantForename = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_FORENAME);
        this.complainantSurname = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_SURNAME);
        this.complainantAddressLine1 = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_ADDRESS_LINE1);
        this.complainantAddressLine2 = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_ADDRESS_LINE2);
        this.complainantAddressLine3 = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_ADDRESS_LINE3);
        this.complainantPostcode = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_POSTCODE);
        this.complainantCountry = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_COUNTRY);
        this.complainantEmail = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_EMAIL);
        this.complainantTelephone = (String)caseProps.get(CtsModel.PROP_COMPLAINANT_TELEPHONE);
        this.hmpoRefundDecision = (String)caseProps.get(CtsModel.PROP_HMPO_REFUND_DECISION);
        this.hmpoRefundAmount = (String)caseProps.get(CtsModel.PROP_HMPO_REFUND_AMOUNT);
        this.hmpoComplaintOutcome = (String)caseProps.get(CtsModel.PROP_HMPO_COMPLAINT_OUTCOME);
        this.hmpoPassportNumber = (String)caseProps.get(CtsModel.PROP_HMPO_PASSPORT_NUMBER);
        this.hmpoApplicationNumber = (String)caseProps.get(CtsModel.PROP_HMPO_APPLICATION_NUMBER);
        this.draftDate = (Date)caseProps.get(CtsModel.PROP_DRAFT_DATE);
        this.bringUpDate = (Date) caseProps.get(CtsModel.BRING_UP_DATE);
        populateCanonicalCorrespondent();
    }

    public void populateCanonicalCorrespondent() {
        switch(this.correspondenceType) {
              case "FOI": case "FTC": case "FTCI": case "FSC": case "FSCI": case "FLT": case "FUT":
                   this.canonicalCorrespondent = concatStrings(this.correspondentForename , this.correspondentSurname);
                   break;
              case "UTEN": case "NPQ": case "LPQ": case "OPQ": case "IMCB": case "IMCM":
                   this.canonicalCorrespondent = this.member;
                   break;
              case "TRO": case "DTEN": case "MIN":
                   this.canonicalCorrespondent = this.member;
                   if (this.canonicalCorrespondent == null) {
                       this.canonicalCorrespondent = concatStrings(this.correspondentForename, this.correspondentSurname);
                   }
                   break;
              case "COM" : case "GEN": case "COM1": case "COM2": case "DGEN": case "GNR":
                  this.canonicalCorrespondent = concatStrings(this.applicantForename, this.applicantSurname);
                   if (this.canonicalCorrespondent == null) {
                       this.canonicalCorrespondent = concatStrings(this.correspondentForename, this.correspondentSurname);
                   }
                break;
             default :  this.canonicalCorrespondent = null;
        }
    }



    private String concatStrings(String firstPart, String secondPart) {
        if (firstPart == null && secondPart == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        if (firstPart != null) {
            sb.append(firstPart);
        }
        if (secondPart != null) {
            if (sb.length() != 0) {
                sb.append(" ").append(secondPart);
            } else {
                sb.append(secondPart);
            }
        }
        return sb.toString();
    }

    public CtsCase() {
        //For tests
    }

    //factory method to create cts case
    public static CtsCase getCtsCase(Map<QName, Serializable> caseProps) {
        CtsCase retVal;
        String correspondenceType = (String) caseProps.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
        switch (correspondenceType) {
            case "COL":
                retVal = new HMPOCollectiveCase(caseProps);
                break;
            case "COM1": case "COM2": case "DGEN": case "GNR":
                retVal = new HMPOComplaintCase(caseProps);
                break;
            default:
                retVal = new CtsCase(caseProps);
        }
        return retVal;
    }

    public String getCorrespondenceType() {
        return correspondenceType;
    }

    public void setCorrespondenceType(String correspondenceType) {
        this.correspondenceType = correspondenceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCaseTask() {
        return caseTask;
    }

    public void setCaseTask(String caseTask) {
        this.caseTask = caseTask;
    }

    public String getUrnSuffix() {
        return urnSuffix;
    }

    public void setUrnSuffix(String urnSuffix) {
        this.urnSuffix = urnSuffix;
    }

    public String getMarkupDecision() {
        return markupDecision;
    }

    public void setMarkupDecision(String markupDecision) {
        this.markupDecision = markupDecision;
    }

    public String getMarkupUnit() {
        return markupUnit;
    }

    public void setMarkupUnit(String markupUnit) {
        this.markupUnit = markupUnit;
    }

    public String getMarkupTopic() {
        return markupTopic;
    }

    public void setMarkupTopic(String markupTopic) {
        this.markupTopic = markupTopic;
    }

    public String getSecondaryTopic() {
        return secondaryTopic;
    }

    public void setSecondaryTopic(String secondaryTopic) {
        this.secondaryTopic = secondaryTopic;
    }

    public String getMarkupMinister() {
        return markupMinister;
    }

    public void setMarkupMinister(String markupMinister) {
        this.markupMinister = markupMinister;
    }

    public String getAssignedUnit() {
        return assignedUnit;
    }

    public void setAssignedUnit(String assignedUnit) {
        this.assignedUnit = assignedUnit;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(String assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Date getDateOfLetter() {
        return dateOfLetter;
    }

    public void setDateOfLetter(Date dateOfLetter) {
        this.dateOfLetter = dateOfLetter;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Boolean getAdvice() {
        return advice;
    }

    public void setAdvice(Boolean advice) {
        this.advice = advice;
    }

    public Date getCaseResponseDeadline() {
        return caseResponseDeadline;
    }

    public void setCaseResponseDeadline(Date caseResponseDeadline) {
        this.caseResponseDeadline = caseResponseDeadline;
    }

    public Date getPoTarget() {
        return poTarget;
    }

    public void setPoTarget(Date poTarget) {
        this.poTarget = poTarget;
    }

    public Boolean getHomeSecretaryReply() {
        return homeSecretaryReply;
    }

    public void setHomeSecretaryReply(Boolean homeSecretaryReply) {
        this.homeSecretaryReply = homeSecretaryReply;
    }

    public String getMpRef() {
        return mpRef;
    }

    public void setMpRef(String mpRef) {
        this.mpRef = mpRef;
    }

    public String getReplyToName() {
        return replyToName;
    }

    public void setReplyToName(String replyToName) {
        this.replyToName = replyToName;
    }

    public String getReplyToPostcode() {
        return replyToPostcode;
    }

    public void setReplyToPostcode(String replyToPostcode) {
        this.replyToPostcode = replyToPostcode;
    }

    public String getReplyToAddressLine1() {
        return replyToAddressLine1;
    }

    public void setReplyToAddressLine1(String replyToAddressLine1) {
        this.replyToAddressLine1 = replyToAddressLine1;
    }

    public String getReplyToAddressLine2() {
        return replyToAddressLine2;
    }

    public void setReplyToAddressLine2(String replyToAddressLine2) {
        this.replyToAddressLine2 = replyToAddressLine2;
    }

    public String getReplyToAddressLine3() {
        return replyToAddressLine3;
    }

    public void setReplyToAddressLine3(String replyToAddressLine3) {
        this.replyToAddressLine3 = replyToAddressLine3;
    }

    public String getReplyToCountry() {
        return replyToCountry;
    }

    public void setReplyToCountry(String replyToCountry) {
        this.replyToCountry = replyToCountry;
    }

    public String getReplyToTelephone() {
        return replyToTelephone;
    }

    public void setReplyToTelephone(String replyToTelephone) {
        this.replyToTelephone = replyToTelephone;
    }

    public String getReplyToEmail() {
        return replyToEmail;
    }

    public void setReplyToEmail(String replyToEmail) {
        this.replyToEmail = replyToEmail;
    }

    public Boolean getReplyToNumberTenCopy() {
        return replyToNumberTenCopy;
    }

    public void setReplyToNumberTenCopy(Boolean replyToNumberTenCopy) {
        this.replyToNumberTenCopy = replyToNumberTenCopy;
    }

    public String getCorrespondentTitle() {
        return correspondentTitle;
    }

    public void setCorrespondentTitle(String correspondentTitle) {
        this.correspondentTitle = correspondentTitle;
    }

    public String getCorrespondentForename() {
        return correspondentForename;
    }

    public void setCorrespondentForename(String correspondentForename) {
        this.correspondentForename = correspondentForename;
    }

    public String getCorrespondentSurname() {
        return correspondentSurname;
    }

    public void setCorrespondentSurname(String correspondentSurname) {
        this.correspondentSurname = correspondentSurname;
    }

    public String getCorrespondentPostcode() {
        return correspondentPostcode;
    }

    public void setCorrespondentPostcode(String correspondentPostcode) {
        this.correspondentPostcode = correspondentPostcode;
    }

    public String getCorrespondentAddressLine1() {
        return correspondentAddressLine1;
    }

    public void setCorrespondentAddressLine1(String correspondentAddressLine1) {
        this.correspondentAddressLine1 = correspondentAddressLine1;
    }

    public String getCorrespondentAddressLine2() {
        return correspondentAddressLine2;
    }

    public void setCorrespondentAddressLine2(String correspondentAddressLine2) {
        this.correspondentAddressLine2 = correspondentAddressLine2;
    }

    public String getCorrespondentAddressLine3() {
        return correspondentAddressLine3;
    }

    public void setCorrespondentAddressLine3(String correspondentAddressLine3) {
        this.correspondentAddressLine3 = correspondentAddressLine3;
    }

    public String getCorrespondentCountry() {
        return correspondentCountry;
    }

    public void setCorrespondentCountry(String correspondentCountry) {
        this.correspondentCountry = correspondentCountry;
    }

    public String getCorrespondentTelephone() {
        return correspondentTelephone;
    }

    public void setCorrespondentTelephone(String correspondentTelephone) {
        this.correspondentTelephone = correspondentTelephone;
    }

    public String getCorrespondentEmail() {
        return correspondentEmail;
    }

    public void setCorrespondentEmail(String correspondentEmail) {
        this.correspondentEmail = correspondentEmail;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public Date getPoliticianDeadline() {
        return politicianDeadline;
    }

    public void setPoliticianDeadline(Date politicianDeadline) {
        this.politicianDeadline = politicianDeadline;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    public Date getWoDate() {
        return woDate;
    }

    public void setWoDate(Date woDate) {
        this.woDate = woDate;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getReceivedType() {
        return receivedType;
    }

    public void setReceivedType(String receivedType) {
        this.receivedType = receivedType;
    }

    public Date getDraftDate() {
        return draftDate;
    }

    public void setDraftDate(Date draftDate) {
        this.draftDate = draftDate;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Boolean getSignedByHomeSec() {
        return signedByHomeSec;
    }

    public void setSignedByHomeSec(Boolean signedByHomeSec) {
        this.signedByHomeSec = signedByHomeSec;
    }

    public Boolean getSignedByLordsMinister() {
        return signedByLordsMinister;
    }

    public void setSignedByLordsMinister(Boolean signedByLordsMinister) {
        this.signedByLordsMinister = signedByLordsMinister;
    }

    public String getLordsMinister() {
        return lordsMinister;
    }

    public void setLordsMinister(String lordsMinister) {
        this.lordsMinister = lordsMinister;
    }

    public Boolean getReviewedByPermSec() {
        return reviewedByPermSec;
    }

    public void setReviewedByPermSec(Boolean reviewedByPermSec) {
        this.reviewedByPermSec = reviewedByPermSec;
    }

    public Boolean getReviewedBySpads() {
        return reviewedBySpads;
    }

    public void setReviewedBySpads(Boolean reviewedBySpads) {
        this.reviewedBySpads = reviewedBySpads;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getRoundRobin() {
        return roundRobin;
    }

    public void setRoundRobin(Boolean roundRobin) {
        this.roundRobin = roundRobin;
    }

    public String getCabinetOfficeGuidance() {
        return cabinetOfficeGuidance;
    }

    public void setCabinetOfficeGuidance(String cabinetOfficeGuidance) {
        this.cabinetOfficeGuidance = cabinetOfficeGuidance;
    }

    public String getTransferDepartmentName() {
        return transferDepartmentName;
    }

    public void setTransferDepartmentName(String transferDepartmentName) {
        this.transferDepartmentName = transferDepartmentName;
    }

    public List<CtsCase> getGroupedCases() {
        return groupedCases;
    }

    public void setGroupedCases(List<CtsCase> groupedCases) {
        this.groupedCases = groupedCases;
    }

    public Boolean getIsGroupedSlave() {
        return isGroupedSlave;
    }

    public void setIsGroupedSlave(Boolean isGroupedSlave) {
        this.isGroupedSlave = isGroupedSlave;
    }

    public Boolean getIsGroupedMaster() {
        return isGroupedMaster;
    }

    public void setIsGroupedMaster(Boolean isGroupedMaster) {
        this.isGroupedMaster = isGroupedMaster;
    }

    public String getMasterNodeRef() {
        return masterNodeRef;
    }

    public void setMasterNodeRef(String masterNodeRef) {
        this.masterNodeRef = masterNodeRef;
    }

    public Boolean getIsLinkedCase() {
        return isLinkedCase;
    }

    public void setIsLinkedCase(Boolean isLinkedCase) {
        this.isLinkedCase = isLinkedCase;
    }

    public List<CtsCase> getLinkedCases() {
        return linkedCases;
    }

    public void setLinkedCases(List<CtsCase> linkedCases) {
        this.linkedCases = linkedCases;
    }

    public String getCaseRef() {
        return caseRef;
    }

    public void setCaseRef(String caseRef) {
        this.caseRef = caseRef;
    }

    public String getThirdPartyCorrespondentTitle() {
        return thirdPartyCorrespondentTitle;
    }

    public void setThirdPartyCorrespondentTitle(String thirdPartyCorrespondentTitle) {
        this.thirdPartyCorrespondentTitle = thirdPartyCorrespondentTitle;
    }

    public String getThirdPartyCorrespondentForename() {
        return thirdPartyCorrespondentForename;
    }

    public void setThirdPartyCorrespondentForename(String thirdPartyCorrespondentForename) {
        this.thirdPartyCorrespondentForename = thirdPartyCorrespondentForename;
    }

    public String getThirdPartyCorrespondentSurname() {
        return thirdPartyCorrespondentSurname;
    }

    public void setThirdPartyCorrespondentSurname(String thirdPartyCorrespondentSurname) {
        this.thirdPartyCorrespondentSurname = thirdPartyCorrespondentSurname;
    }

    public String getThirdPartyCorrespondentOrganisation() {
        return thirdPartyCorrespondentOrganisation;
    }

    public void setThirdPartyCorrespondentOrganisation(String thirdPartyCorrespondentOrganisation) {
        this.thirdPartyCorrespondentOrganisation = thirdPartyCorrespondentOrganisation;
    }

    public String getThirdPartyCorrespondentTelephone() {
        return thirdPartyCorrespondentTelephone;
    }

    public void setThirdPartyCorrespondentTelephone(String thirdPartyCorrespondentTelephone) {
        this.thirdPartyCorrespondentTelephone = thirdPartyCorrespondentTelephone;
    }

    public String getThirdPartyCorrespondentEmail() {
        return thirdPartyCorrespondentEmail;
    }

    public void setThirdPartyCorrespondentEmail(String thirdPartyCorrespondentEmail) {
        this.thirdPartyCorrespondentEmail = thirdPartyCorrespondentEmail;
    }

    public String getThirdPartyCorrespondentPostcode() {
        return thirdPartyCorrespondentPostcode;
    }

    public void setThirdPartyCorrespondentPostcode(String thirdPartyCorrespondentPostcode) {
        this.thirdPartyCorrespondentPostcode = thirdPartyCorrespondentPostcode;
    }

    public String getThirdPartyCorrespondentAddressLine1() {
        return thirdPartyCorrespondentAddressLine1;
    }

    public void setThirdPartyCorrespondentAddressLine1(String thirdPartyCorrespondentAddressLine1) {
        this.thirdPartyCorrespondentAddressLine1 = thirdPartyCorrespondentAddressLine1;
    }

    public String getThirdPartyCorrespondentAddressLine2() {
        return thirdPartyCorrespondentAddressLine2;
    }

    public void setThirdPartyCorrespondentAddressLine2(String thirdPartyCorrespondentAddressLine2) {
        this.thirdPartyCorrespondentAddressLine2 = thirdPartyCorrespondentAddressLine2;
    }

    public String getThirdPartyCorrespondentAddressLine3() {
        return thirdPartyCorrespondentAddressLine3;
    }

    public void setThirdPartyCorrespondentAddressLine3(String thirdPartyCorrespondentAddressLine3) {
        this.thirdPartyCorrespondentAddressLine3 = thirdPartyCorrespondentAddressLine3;
    }

    public String getThirdPartyCorrespondentCountry() {
        return thirdPartyCorrespondentCountry;
    }

    public void setThirdPartyCorrespondentCountry(String thirdPartyCorrespondentCountry) {
        this.thirdPartyCorrespondentCountry = thirdPartyCorrespondentCountry;
    }

    public Date getAllocateTarget() {
        return allocateTarget;
    }

    public void setAllocateTarget(Date allocateTarget) {
        this.allocateTarget = allocateTarget;
    }

    public Date getDraftResponseTarget() {
        return draftResponseTarget;
    }

    public void setDraftResponseTarget(Date draftResponseTarget) {
        this.draftResponseTarget = draftResponseTarget;
    }

    public Date getScsApprovalTarget() {
        return scsApprovalTarget;
    }

    public void setScsApprovalTarget(Date scsApprovalTarget) {
        this.scsApprovalTarget = scsApprovalTarget;
    }

    public Date getFinalApprovalTarget() {
        return finalApprovalTarget;
    }

    public void setFinalApprovalTarget(Date finalApprovalTarget) {
        this.finalApprovalTarget = finalApprovalTarget;
    }

    public Boolean getFoiMinisterSignOff() {
        return foiMinisterSignOff;
    }

    public void setFoiMinisterSignOff(Boolean foiMinisterSignOff) {
        this.foiMinisterSignOff = foiMinisterSignOff;
    }

    public Boolean getFoiIsEir() {
        return foiIsEir;
    }

    public void setFoiIsEir(Boolean foiIsEir) {
        this.foiIsEir = foiIsEir;
    }

    public String getExemptions() {
        return exemptions;
    }

    public void setExemptions(String exemptions) {
        this.exemptions = exemptions;
    }

    public String getCaseWorkflowStatus() {
        return caseWorkflowStatus;
    }

    public void setCaseWorkflowStatus(String caseWorkflowStatus) {
        this.caseWorkflowStatus = caseWorkflowStatus;
    }

    public String getCaseMandatoryFields() {
        return caseMandatoryFields;
    }

    public void setCaseMandatoryFields(String mandatoryFields) {
        this.caseMandatoryFields = mandatoryFields;
    }

    public void setcaseMandatoryFieldDependencies(String caseMandatoryFieldDependencies) {
        this.caseMandatoryFieldDependencies = caseMandatoryFieldDependencies;
    }

    public String getcaseMandatoryFieldDependencies() {
        return caseMandatoryFieldDependencies;
    }

    public void setCaseMandatoryFieldStatus(String caseMandatoryFieldStatus) {
        this.caseMandatoryFieldStatus = caseMandatoryFieldStatus;
    }

    public String getCaseMandatoryFieldStatus() { return this.caseMandatoryFieldStatus; }

    public void setCaseMandatoryFieldTask(String caseMandatoryFieldTask) {
        this.caseMandatoryFieldTask = caseMandatoryFieldTask;
    }

    public String getCaseMandatoryFieldTask() { return this.caseMandatoryFieldTask; }


    public String getHmpoResponse() {
        return hmpoResponse;
    }

    public void setHmpoResponse(String hmpoResponse) {
        this.hmpoResponse = hmpoResponse;
    }

    public String getHmpoStage() {
        return hmpoStage;
    }

    public void setHmpoStage(String hmpoStage) {
        this.hmpoStage = hmpoStage;
    }

    public Boolean getReplyToCorrespondent() {
        return replyToCorrespondent;
    }

    public void setReplyToCorrespondent(Boolean replyToCorrespondent) {
        this.replyToCorrespondent = replyToCorrespondent;
    }

    public String getTypeOfCorrespondent() {
        return typeOfCorrespondent;
    }

    public void setTypeOfCorrespondent(String typeOfCorrespondent) {
        this.typeOfCorrespondent = typeOfCorrespondent;
    }

    public String getTypeOfComplainant() {
        return typeOfComplainant;
    }

    public void setTypeOfComplainant(String typeOfComplainant) {
        this.typeOfComplainant = typeOfComplainant;
    }

    public String getTypeOfRepresentative() {
        return typeOfRepresentative;
    }

    public void setTypeOfRepresentative(String typeOfRepresentative) {
        this.typeOfRepresentative = typeOfRepresentative;
    }

    public Boolean getReplyToApplicant() {
        return replyToApplicant;
    }

    public void setReplyToApplicant(Boolean replyToApplicant) {
        this.replyToApplicant = replyToApplicant;
    }

    public String getApplicantTitle() {
        return applicantTitle;
    }

    public void setApplicantTitle(String applicantTitle) {
        this.applicantTitle = applicantTitle;
    }

    public String getApplicantForename() {
        return applicantForename;
    }

    public void setApplicantForename(String applicantForename) {
        this.applicantForename = applicantForename;
    }

    public String getApplicantSurname() {
        return applicantSurname;
    }

    public void setApplicantSurname(String applicantSurname) {
        this.applicantSurname = applicantSurname;
    }

    public String getApplicantAddressLine1() {
        return applicantAddressLine1;
    }

    public void setApplicantAddressLine1(String applicantAddressLine1) {
        this.applicantAddressLine1 = applicantAddressLine1;
    }

    public String getApplicantAddressLine2() {
        return applicantAddressLine2;
    }

    public void setApplicantAddressLine2(String applicantAddressLine2) {
        this.applicantAddressLine2 = applicantAddressLine2;
    }

    public String getApplicantAddressLine3() {
        return applicantAddressLine3;
    }

    public void setApplicantAddressLine3(String applicantAddressLine3) {
        this.applicantAddressLine3 = applicantAddressLine3;
    }

    public String getApplicantPostcode() {
        return applicantPostcode;
    }

    public void setApplicantPostcode(String applicantPostcode) {
        this.applicantPostcode = applicantPostcode;
    }

    public String getApplicantCountry() {
        return applicantCountry;
    }

    public void setApplicantCountry(String applicantCountry) {
        this.applicantCountry = applicantCountry;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantTelephone() {
        return applicantTelephone;
    }

    public void setApplicantTelephone(String applicantTelephone) {
        this.applicantTelephone = applicantTelephone;
    }

    public Boolean getReplyToComplainant() {
        return replyToComplainant;
    }

    public void setReplyToComplainant(Boolean replyToComplainant) {
        this.replyToComplainant = replyToComplainant;
    }

    public String getComplainantTitle() {
        return complainantTitle;
    }

    public void setComplainantTitle(String complainantTitle) {
        this.complainantTitle = complainantTitle;
    }

    public String getComplainantForename() {
        return complainantForename;
    }

    public void setComplainantForename(String complainantForename) {
        this.complainantForename = complainantForename;
    }

    public String getComplainantSurname() {
        return complainantSurname;
    }

    public void setComplainantSurname(String complainantSurname) {
        this.complainantSurname = complainantSurname;
    }

    public String getComplainantAddressLine1() {
        return complainantAddressLine1;
    }

    public void setComplainantAddressLine1(String complainantAddressLine1) {
        this.complainantAddressLine1 = complainantAddressLine1;
    }

    public String getComplainantAddressLine2() {
        return complainantAddressLine2;
    }

    public void setComplainantAddressLine2(String complainantAddressLine2) {
        this.complainantAddressLine2 = complainantAddressLine2;
    }

    public String getComplainantAddressLine3() {
        return complainantAddressLine3;
    }

    public void setComplainantAddressLine3(String complainantAddressLine3) {
        this.complainantAddressLine3 = complainantAddressLine3;
    }

    public String getComplainantPostcode() {
        return complainantPostcode;
    }

    public void setComplainantPostcode(String complainantPostcode) {
        this.complainantPostcode = complainantPostcode;
    }

    public String getComplainantCountry() {
        return complainantCountry;
    }

    public void setComplainantCountry(String complainantCountry) {
        this.complainantCountry = complainantCountry;
    }

    public String getComplainantEmail() {
        return complainantEmail;
    }

    public void setComplainantEmail(String complainantEmail) {
        this.complainantEmail = complainantEmail;
    }

    public String getComplainantTelephone() {
        return complainantTelephone;
    }

    public void setComplainantTelephone(String complainantTelephone) {
        this.complainantTelephone = complainantTelephone;
    }

    public Date getResponderHubTarget() {
        return responderHubTarget;
    }

    public void setResponderHubTarget(Date responderHubTarget) {
        this.responderHubTarget = responderHubTarget;
    }

    public Date getAllocateToResponderTarget() {
        return allocateToResponderTarget;
    }

    public void setAllocateToResponderTarget(Date allocateToResponderTarget) {
        this.allocateToResponderTarget = allocateToResponderTarget;
    }

    public Date getDispatchTarget() {
        return dispatchTarget;
    }

    public void setDispatchTarget(Date dispatchTarget) {
        this.dispatchTarget = dispatchTarget;
    }

    public String getOriginalDrafterUnit() {
        return originalDrafterUnit;
    }

    public void setOriginalDrafterUnit(String originalDrafterUnit) {
        this.originalDrafterUnit = originalDrafterUnit;
    }

    public String getOriginalDrafterTeam() {
        return originalDrafterTeam;
    }

    public void setOriginalDrafterTeam(String originalDrafterTeam) {
        this.originalDrafterTeam = originalDrafterTeam;
    }

    public String getOriginalDrafterUser() {
        return originalDrafterUser;
    }

    public void setOriginalDrafterUser(String originalDrafterUser) {
        this.originalDrafterUser = originalDrafterUser;
    }

    public Boolean getPitExtension() {
        return pitExtension;
    }

    public void setPitExtension(Boolean pitExtension) {
        this.pitExtension = pitExtension;
    }

    public Date getPitLetterSentDate() {
        return pitLetterSentDate;
    }

    public void setPitLetterSentDate(Date pitLetterSentDate) {
        this.pitLetterSentDate = pitLetterSentDate;
    }

    public String getPitQualifiedExemptions() {
        return pitQualifiedExemptions;
    }

    public void setPitQualifiedExemptions(String pitQualifiedExemptions) {
        this.pitQualifiedExemptions = pitQualifiedExemptions;
    }

    public String getTypeOfThirdParty() {
        return typeOfThirdParty;
    }

    public void setTypeOfThirdParty(String typeOfThirdParty) {
        this.typeOfThirdParty = typeOfThirdParty;
    }

    public Boolean getConsentAttached() {
        return consentAttached;
    }

    public void setConsentAttached(Boolean consentAttached) {
        this.consentAttached = consentAttached;
    }

    public Boolean getAcpoConsultation() {
        return acpoConsultation;
    }

    public void setAcpoConsultation(Boolean acpoConsultation) {
        this.acpoConsultation = acpoConsultation;
    }

    public Boolean getCabinetOfficeConsultation() {
        return cabinetOfficeConsultation;
    }

    public void setCabinetOfficeConsultation(Boolean cabinetOfficeConsultation) {
        this.cabinetOfficeConsultation = cabinetOfficeConsultation;
    }

    public Boolean getNslgConsultation() {
        return nslgConsultation;
    }

    public void setNslgConsultation(Boolean nslgConsultation) {
        this.nslgConsultation = nslgConsultation;
    }

    public Boolean getRoyalsConsultation() {
        return royalsConsultation;
    }

    public void setRoyalsConsultation(Boolean royalsConsultation) {
        this.royalsConsultation = royalsConsultation;
    }

    public Boolean getRoundRobinAdviceConsultation() {
        return roundRobinAdviceConsultation;
    }

    public void setRoundRobinAdviceConsultation(Boolean roundRobinAdviceConsultation) {
        this.roundRobinAdviceConsultation = roundRobinAdviceConsultation;
    }

    public String getHmpoRefundDecision() {
        return hmpoRefundDecision;
    }

    public void setHmpoRefundDecision(String hmpoRefundDecision) {
        this.hmpoRefundDecision = hmpoRefundDecision;
    }

    public String getHmpoRefundAmount() {
        return hmpoRefundAmount;
    }

    public void setHmpoRefundAmount(String hmpoRefundAmount) {
        this.hmpoRefundAmount = hmpoRefundAmount;
    }

    public String getHmpoComplaintOutcome() {
        return hmpoComplaintOutcome;
    }

    public void setHmpoComplaintOutcome(String hmpoComplaintOutcome) {
        this.hmpoComplaintOutcome = hmpoComplaintOutcome;
    }

    @JsonProperty("passportNumber")
    public String getHmpoPassportNumber() {
        return hmpoPassportNumber;
    }

    public void setHmpoPassportNumber(String hmpoPassportNumber) {
        this.hmpoPassportNumber = hmpoPassportNumber;
    }

    @JsonProperty("applicationNumber")
    public String getHmpoApplicationNumber() {
        return hmpoApplicationNumber;
    }

    public void setHmpoApplicationNumber(String hmpoApplicationNumber) {
        this.hmpoApplicationNumber = hmpoApplicationNumber;
    }

    public String getOgdName() { return ogdName;  }

    public void setOgdName(String ogdName) { this.ogdName = ogdName; }

    public String getHoCaseOfficer() {
        return hoCaseOfficer;
    }

    public void setHoCaseOfficer(String hoCaseOfficer) {
        this.hoCaseOfficer = hoCaseOfficer;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public Boolean getComplex() {
        return complex;
    }

    public void setComplex(Boolean complex) {
        this.complex = complex;
    }

    public Boolean getNewInformationReleased() {
        return newInformationReleased;
    }

    public void setNewInformationReleased(Boolean newInformationReleased) {
        this.newInformationReleased = newInformationReleased;
    }

    public String getIcoReference() {
        return icoReference;
    }

    public void setIcoReference(String icoReference) {
        this.icoReference = icoReference;
    }

    public String getIcoOutcome() {
        return icoOutcome;
    }

    public void setIcoOutcome(String icoOutcome) {
        this.icoOutcome = icoOutcome;
    }

    public String getIcoComplaintOfficer() {
        return icoComplaintOfficer;
    }

    public void setIcoComplaintOfficer(String icoComplaintOfficer) {
        this.icoComplaintOfficer = icoComplaintOfficer;
    }

    public Date getIcoOutcomeDate() {
        return icoOutcomeDate;
    }

    public void setIcoOutcomeDate(Date icoOutcomeDate) {
        this.icoOutcomeDate = icoOutcomeDate;
    }

    public String getTsolRep() {
        return tsolRep;
    }

    public void setTsolRep(String tsolRep) {
        this.tsolRep = tsolRep;
    }

    public String getAppellant() {
        return appellant;
    }

    public void setAppellant(String appellant) {
        this.appellant = appellant;
    }

    public Boolean getHoJoined() {
        return hoJoined;
    }

    public void setHoJoined(Boolean hoJoined) {
        this.hoJoined = hoJoined;
    }

    public String getTribunalOutcome() {
        return tribunalOutcome;
    }

    public void setTribunalOutcome(String tribunalOutcome) {
        this.tribunalOutcome = tribunalOutcome;
    }

    public Date getTribunalOutcomeDate() {
        return tribunalOutcomeDate;
    }

    public void setTribunalOutcomeDate(Date tribunalOutcomeDate) {
        this.tribunalOutcomeDate = tribunalOutcomeDate;
    }

    public Boolean getEnforcementNoticeNeeded() {
        return enforcementNoticeNeeded;
    }

    public void setEnforcementNoticeNeeded(Boolean enforcementNoticeNeeded) {
        this.enforcementNoticeNeeded = enforcementNoticeNeeded;
    }

    public Date getEnforcementNoticeDeadline() {
        return enforcementNoticeDeadline;
    }

    public void setEnforcementNoticeDeadline(Date enforcementNoticeDeadline) {
        this.enforcementNoticeDeadline = enforcementNoticeDeadline;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getAnsweringMinister() { return answeringMinister; }

    public void setAnsweringMinister(String answeringMinister) { this.answeringMinister = answeringMinister; }

    public String getAnsweringMinisterId() { return answeringMinisterId; }

    public void setAnsweringMinisterId(String answeringMinisterId) { this.answeringMinisterId = answeringMinisterId; }

    public Boolean getFoiDisclosure() {
        return foiDisclosure;
    }

    public void setFoiDisclosure(Boolean foiDisclosure) {
        this.foiDisclosure = foiDisclosure;
    }

    public Date getStatusUpdatedDatetime() {
        return statusUpdatedDatetime;
    }

    public void setStatusUpdatedDatetime(Date statusUpdatedDatetime) {
        this.statusUpdatedDatetime = statusUpdatedDatetime;
    }

    public Date getTaskUpdatedDatetime() {
        return taskUpdatedDatetime;
    }

    public void setTaskUpdatedDatetime(Date taskUpdatedDatetime) {
        this.taskUpdatedDatetime = taskUpdatedDatetime;
    }

    public Date getOwnerUpdatedDatetime() {
        return ownerUpdatedDatetime;
    }

    public void setOwnerUpdatedDatetime(Date ownerUpdatedDatetime) {
        this.ownerUpdatedDatetime = ownerUpdatedDatetime;
    }

    public Boolean getAutoCreatedCase() {
        return autoCreatedCase;
    }

    public void setAutoCreatedCase(Boolean autoCreatedCase) {
        this.autoCreatedCase = autoCreatedCase;
    }

    public Integer getReturnedCount() {
        return returnedCount;
    }

    public void setReturnedCount(Integer returnedCount) {
        this.returnedCount = returnedCount;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getDisplayTask() {
        return displayTask;
    }

    public void setDisplayTask(String displayTask) { this.displayTask = displayTask; }

    public void setCaseProgressStatus(Integer caseProgressStatus) { this.caseProgressStatus = caseProgressStatus; }

    public Integer getCaseProgressStatus() { return caseProgressStatus; }

    public void setMarkupTeam(String markupTeam) { this.markupTeam = markupTeam; }

    public String getMarkupTeam() { return markupTeam; }

    public Boolean getParlyDispatch() { return parlyDispatch; }

    public void setParlyDispatch(Boolean parlyDispatch) { this.parlyDispatch = parlyDispatch; }

    public Date getBringUpDate() {
        return bringUpDate;
    }

    @JsonProperty("caseOverdueFlag")
    public Boolean isCaseOverdue() { return this.caseResponseDeadline != null && new LocalDate().isAfter(new LocalDate(caseResponseDeadline)); }

    public String getCanonicalCorrespondent() { return canonicalCorrespondent; }

    /**
     * Add Custom JSON properties for de-serialising
     */
    @Override
    @JsonProperty("folderName")
    public String getName() {
        return super.getName();
    }

    @Override
    @JsonProperty("dateCreated")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
