package uk.gov.homeoffice.cts.model;

import org.alfresco.service.namespace.QName;

import java.util.Date;

/**
 * Created by davidt on 30/06/2014.
 */
public interface CtsModel {


    static final String CTS_NAMESPACE = "http://cts-beta.homeoffice.gov.uk/model/content/1.0";
    static final String CTSDL_NAMESPACE = "http://cts-beta.homeoffice.gov.uk/model/content/datalists/1.0";
    static final QName CTS_MODEL_NAME = QName.createQName(CTS_NAMESPACE,"ctsmodel");

    static final QName TYPE_CTS_CASE = QName.createQName(CTS_NAMESPACE, "case");
    static final QName TYPE_CTS_DATA_LIST = QName.createQName(CTSDL_NAMESPACE, "ctsDataList");
    static final QName TYPE_USER_GROUPS_NO_EMAILS = QName.createQName(CTSDL_NAMESPACE, "userGroupNoEmailList");
    static final QName TYPE_CTS_DOCUMENT = QName.createQName(CTS_NAMESPACE, "caseDocument");

    static final QName ASPECT_GROUPED_MASTER = QName.createQName(CTS_NAMESPACE, "groupedMaster");
    static final QName ASPECT_GROUPED_SLAVE = QName.createQName(CTS_NAMESPACE, "groupedSlave");
    static final QName ASPECT_MANAGER = QName.createQName(CTS_NAMESPACE, "managerGroup");
    static final QName ASPECT_AUTO_CREATE_FAILURE = QName.createQName(CTS_NAMESPACE, "autoCreateFailure");
    static final QName PROP_AUTO_CREATE_FAILURE_MESSAGE = QName.createQName(CTS_NAMESPACE, "autoCreateFailureMessage");
    static final QName PROP_AUTO_CREATE_FAILURE_DATETIME = QName.createQName(CTS_NAMESPACE, "autoCreateFailureDateTime");

    static final QName PROP_CORRESPONDENCE_TYPE = QName.createQName(CTS_NAMESPACE, "correspondenceType");
    static final QName PROP_DATE_RECEIVED = QName.createQName(CTS_NAMESPACE, "dateReceived");
    static final QName PROP_DATE_OF_LETTER = QName.createQName(CTS_NAMESPACE, "dateOfLetter");
    static final QName PROP_CHANNEL = QName.createQName(CTS_NAMESPACE, "channel");
    static final QName PROP_CASE_RESPONSE_DEADLINE = QName.createQName(CTS_NAMESPACE, "caseResponseDeadline");
    static final QName PROP_PRIORITY = QName.createQName(CTS_NAMESPACE, "priority");
    static final QName PROP_ADVICE = QName.createQName(CTS_NAMESPACE, "advice");

    static final QName PROP_CASE_STATUS = QName.createQName(CTS_NAMESPACE, "caseStatus");
    static final QName PROP_CASE_TASK = QName.createQName(CTS_NAMESPACE, "caseTask");
    static final QName PROP_URN_SUFFIX = QName.createQName(CTS_NAMESPACE, "urnSuffix");
    static final QName PROP_PO_TARGET = QName.createQName(CTS_NAMESPACE, "poTarget");
    static final QName PROP_CABINET_OFFICE_TARGET = QName.createQName(CTS_NAMESPACE, "cabinetOfficeTarget");
    static final QName PROP_UNIT_TARGET = QName.createQName(CTS_NAMESPACE, "unitTarget");

    static final QName PROP_RESPONDER_HUB_TARGET = QName.createQName(CTS_NAMESPACE, "responderHubTarget");
    static final QName PROP_ALLOCATE_TO_RESPONDER_TARGET = QName.createQName(CTS_NAMESPACE, "allocateToResponderTarget");

    static final QName PROP_HOME_SECRETARY_REPLY = QName.createQName(CTS_NAMESPACE, "homeSecretaryReply");
    static final QName PROP_MP_REF = QName.createQName(CTS_NAMESPACE, "mpRef");
    static final QName PROP_REPLY_TO_NAME = QName.createQName(CTS_NAMESPACE, "replyToName");
    static final QName PROP_REPLY_TO_POSTCODE = QName.createQName(CTS_NAMESPACE, "replyToPostcode");
    static final QName PROP_REPLY_TO_ADDRESS_LINE1 = QName.createQName(CTS_NAMESPACE, "replyToAddressLine1");
    static final QName PROP_REPLY_TO_ADDRESS_LINE2 = QName.createQName(CTS_NAMESPACE, "replyToAddressLine2");
    static final QName PROP_REPLY_TO_ADDRESS_LINE3 = QName.createQName(CTS_NAMESPACE, "replyToAddressLine3");
    static final QName PROP_REPLY_TO_COUNTRY = QName.createQName(CTS_NAMESPACE, "replyToCountry");
    static final QName PROP_REPLY_TO_TELEPHONE = QName.createQName(CTS_NAMESPACE, "replyToTelephone");
    static final QName PROP_REPLY_TO_EMAIL = QName.createQName(CTS_NAMESPACE, "replyToEmail");
    static final QName PROP_NO10_COPY = QName.createQName(CTS_NAMESPACE, "replyToNumberTenCopy");
    static final QName PROP_CORRESPONDENT_TITLE = QName.createQName(CTS_NAMESPACE, "correspondentTitle");
    static final QName PROP_CORRESPONDENT_FORENAME = QName.createQName(CTS_NAMESPACE, "correspondentForename");
    static final QName PROP_CORRESPONDENT_SURNAME = QName.createQName(CTS_NAMESPACE, "correspondentSurname");
    static final QName PROP_CORRESPONDENT_POSTCODE = QName.createQName(CTS_NAMESPACE, "correspondentPostcode");
    static final QName PROP_CORRESPONDENT_ADDRESS_LINE1 = QName.createQName(CTS_NAMESPACE, "correspondentAddressLine1");
    static final QName PROP_CORRESPONDENT_ADDRESS_LINE2 = QName.createQName(CTS_NAMESPACE, "correspondentAddressLine2");
    static final QName PROP_CORRESPONDENT_ADDRESS_LINE3 = QName.createQName(CTS_NAMESPACE, "correspondentAddressLine3");
    static final QName PROP_CORRESPONDENT_COUNTRY = QName.createQName(CTS_NAMESPACE, "correspondentCountry");
    static final QName PROP_CORRESPONDENT_TELEPHONE = QName.createQName(CTS_NAMESPACE, "correspondentTelephone");
    static final QName PROP_CORRESPONDENT_EMAIL = QName.createQName(CTS_NAMESPACE, "correspondentEmail");
    static final QName PROP_MARKUP_DECISION = QName.createQName(CTS_NAMESPACE, "markupDecision");
    static final QName PROP_MARKUP_UNIT = QName.createQName(CTS_NAMESPACE, "markupUnit");
    static final QName PROP_MARKUP_TOPIC = QName.createQName(CTS_NAMESPACE, "markupTopic");
    static final QName PROP_MARKUP_MINISTER = QName.createQName(CTS_NAMESPACE, "markupMinister");
    static final QName PROP_MARKUP_TEAM = QName.createQName(CTS_NAMESPACE, "markupTeam");
    static final QName PROP_SECONDARY_TOPIC = QName.createQName(CTS_NAMESPACE, "secondaryTopic");
    static final QName PROP_ASSIGNED_USER = QName.createQName(CTS_NAMESPACE, "assignedUser");
    static final QName PROP_ASSIGNED_TEAM = QName.createQName(CTS_NAMESPACE, "assignedTeam");
    static final QName PROP_ASSIGNED_UNIT = QName.createQName(CTS_NAMESPACE, "assignedUnit");
    static final QName PROP_ORIGINAL_DRAFTER_USER = QName.createQName(CTS_NAMESPACE, "originalDrafterUser");
    static final QName PROP_ORIGINAL_DRAFTER_TEAM = QName.createQName(CTS_NAMESPACE, "originalDrafterTeam");
    static final QName PROP_ORIGINAL_DRAFTER_UNIT = QName.createQName(CTS_NAMESPACE, "originalDrafterUnit");
    static final QName PROP_DOCUMENT_ADDED = QName.createQName(CTS_NAMESPACE, "documentAdded");
    static final QName PROP_DOCUMENT_DELETED = QName.createQName(CTS_NAMESPACE, "documentDeleted");
    static final QName PROP_SLAVE_ADDED = QName.createQName(CTS_NAMESPACE, "slaveAdded");
    static final QName PROP_MASTER_ADDED = QName.createQName(CTS_NAMESPACE, "masterAdded");
    static final QName PROP_SLAVE_REMOVED = QName.createQName(CTS_NAMESPACE, "slaveRemoved");
    static final QName PROP_MASTER_REMOVED = QName.createQName(CTS_NAMESPACE, "masterRemoved");
    static final QName PROP_LINK_CASE_ADDED = QName.createQName(CTS_NAMESPACE, "linkCaseAdded");
    static final QName PROP_LINK_CASE_REMOVED = QName.createQName(CTS_NAMESPACE, "linkCaseRemoved");
    static final QName PROP_STATUS_UPDATED_DATETIME = QName.createQName(CTS_NAMESPACE, "statusUpdatedDatetime");
    static final QName PROP_TASK_UPDATED_DATETIME = QName.createQName(CTS_NAMESPACE, "taskUpdatedDatetime");
    static final QName PROP_OWNER_UPDATED_DATETIME = QName.createQName(CTS_NAMESPACE, "ownerUpdatedDatetime");

    static final QName PROP_UIN = QName.createQName(CTS_NAMESPACE, "uin");
    static final QName PROP_POLITICIAN_DEADLINE = QName.createQName(CTS_NAMESPACE, "politicianDeadline");
    static final QName PROP_OP_DATE = QName.createQName(CTS_NAMESPACE, "opDate");
    static final QName PROP_WO_DATE = QName.createQName(CTS_NAMESPACE, "woDate");
    static final QName PROP_QUESTION_NUMBER = QName.createQName(CTS_NAMESPACE, "questionNumber");
    static final QName PROP_QUESTION_TEXT = QName.createQName(CTS_NAMESPACE, "questionText");
    static final QName PROP_RECEIVED_TYPE = QName.createQName(CTS_NAMESPACE, "receivedType");
    static final QName PROP_ANSWER_TEXT = QName.createQName(CTS_NAMESPACE, "answerText");

    static final QName PROP_MEMBER = QName.createQName(CTS_NAMESPACE, "member");
    static final QName PROP_CONSTITUENCY = QName.createQName(CTS_NAMESPACE, "constituency");
    static final QName PROP_PARTY = QName.createQName(CTS_NAMESPACE, "party");
    static final QName PROP_SIGNED_BY_HOME_SEC = QName.createQName(CTS_NAMESPACE, "signedByHomeSec");
    static final QName PROP_SIGNED_BY_LORDS_MINISTER = QName.createQName(CTS_NAMESPACE, "signedByLordsMinister");
    static final QName PROP_LORDS_MINISTER = QName.createQName(CTS_NAMESPACE, "lordsMinister");
    static final QName PROP_REVIEWED_BY_PERM_SEC = QName.createQName(CTS_NAMESPACE, "reviewedByPermSec");
    static final QName PROP_REVIEWED_BY_SPADS = QName.createQName(CTS_NAMESPACE, "reviewedBySpads");
    static final QName PROP_PARLY_DISPATCH = QName.createQName(CTS_NAMESPACE, "parlyDispatch");
    static final QName PROP_ROUND_ROBIN = QName.createQName(CTS_NAMESPACE, "roundRobin");
    static final QName PROP_CABINET_OFFICE_GUIDANCE = QName.createQName(CTS_NAMESPACE, "cabinetOfficeGuidance");
    static final QName PROP_TRANSFER_DEPARTMENT_NAME = QName.createQName(CTS_NAMESPACE, "transferDepartmentName");

    static final QName PROP_CASE_REF = QName.createQName(CTS_NAMESPACE, "caseRef");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_TITLE = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentTitle");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_FORENAME = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentForename");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_SURNAME = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentSurname");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_ORGANISATION = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentOrganisation");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_TELEPHONE = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentTelephone");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_EMAIL = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentEmail");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_POSTCODE = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentPostcode");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1 = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentAddressLine1");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2 = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentAddressLine2");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3 = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentAddressLine3");
    static final QName PROP_THIRD_PARTY_CORRESPONDENT_COUNTRY = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentCountry");
    static final QName PROP_ALLOCATE_TARGET = QName.createQName(CTS_NAMESPACE, "allocateTarget");
    static final QName PROP_DRAFT_RESPONSE_TARGET = QName.createQName(CTS_NAMESPACE, "draftResponseTarget");
    static final QName PROP_SCS_APPROVAL_TARGET = QName.createQName(CTS_NAMESPACE, "scsApprovalTarget");
    static final QName PROP_FINAL_APPROVAL_TARGET = QName.createQName(CTS_NAMESPACE, "finalApprovalTarget");
    static final QName PROP_DISPATCH_TARGET = QName.createQName(CTS_NAMESPACE, "dispatchTarget");
    static final QName PROP_FOI_MINISTER_SIGN_OFF = QName.createQName(CTS_NAMESPACE, "foiMinisterSignOff");
    static final QName PROP_FOI_IS_EIR = QName.createQName(CTS_NAMESPACE, "foiIsEir");
    static final QName PROP_EXEMPTIONS = QName.createQName(CTS_NAMESPACE, "exemptions");
    static final QName PROP_PIT_EXTENSION = QName.createQName(CTS_NAMESPACE, "pitExtension");
    static final QName PROP_PIT_LETTER_SENT_DATE = QName.createQName(CTS_NAMESPACE, "pitLetterSentDate");
    static final QName PROP_PIT_QUALIFIED_EXEMPTIONS = QName.createQName(CTS_NAMESPACE, "pitQualifiedExemptions");

    static final QName PROP_HO_CASE_OFFICE = QName.createQName(CTS_NAMESPACE, "hoCaseOfficer");
    static final QName PROP_RESPONSE_DATE = QName.createQName(CTS_NAMESPACE, "responseDate");
    static final QName PROP_COMPLEX = QName.createQName(CTS_NAMESPACE, "complex");
    static final QName PROP_NEW_INFORMATION_RELEASED = QName.createQName(CTS_NAMESPACE, "newInformationReleased");
    static final QName PROP_ICO_REFERENCE = QName.createQName(CTS_NAMESPACE, "icoReference");
    static final QName PROP_ICO_OUTCOME = QName.createQName(CTS_NAMESPACE, "icoOutcome");
    static final QName PROP_ICO_COMPLAINT_OFFICER = QName.createQName(CTS_NAMESPACE, "icoComplaintOfficer");
    static final QName PROP_OUTCOME_DATE = QName.createQName(CTS_NAMESPACE, "icoOutcomeDate");
    static final QName PROP_TSLO_REP = QName.createQName(CTS_NAMESPACE, "tsolRep");
    static final QName PROP_APPELLANT = QName.createQName(CTS_NAMESPACE, "appellant");
    static final QName PROP_HO_JOINED = QName.createQName(CTS_NAMESPACE, "hoJoined");
    static final QName PROP_TRIBUNAL_OUTCOME = QName.createQName(CTS_NAMESPACE, "tribunalOutcome");
    static final QName PROP_TRIBUNAL_OUTCOME_DATE = QName.createQName(CTS_NAMESPACE, "tribunalOutcomeDate");
    static final QName PROP_ENFORCEMENT_NOTICE_NEEDED = QName.createQName(CTS_NAMESPACE, "enforcementNoticeNeeded");
    static final QName PROP_ENFORCEMENT_NOTICE_DEADLINE = QName.createQName(CTS_NAMESPACE, "enforcementNoticeDeadline");
    static final QName PROP_ORGANISATION = QName.createQName(CTS_NAMESPACE, "organisation");

    static final QName PROP_FOI_DISCLOSURE = QName.createQName(CTS_NAMESPACE, "foiDisclosure");
    static final QName PROP_ACPO_CONSULTATION = QName.createQName(CTS_NAMESPACE, "acpoConsultation");
    static final QName PROP_CABINET_OFFICE_CONSULTATION = QName.createQName(CTS_NAMESPACE, "cabinetOfficeConsultation");
    static final QName PROP_NSLG_CONSULTATION = QName.createQName(CTS_NAMESPACE, "nslgConsultation");
    static final QName PROP_ROYALS_CONSULTATION = QName.createQName(CTS_NAMESPACE, "royalsConsultation");
    static final QName PROP_ROUND_ROBIN_ADVICE_CONSULTATION = QName.createQName(CTS_NAMESPACE, "roundRobinAdviceConsultation");

    static final QName PROP_IS_GROUPED_MASTER = QName.createQName(CTS_NAMESPACE, "isGroupedMaster");
    static final QName PROP_IS_GROUPED_SLAVE = QName.createQName(CTS_NAMESPACE, "isGroupedSlave");
    static final QName PROP_MASTER_NODE_REF = QName.createQName(CTS_NAMESPACE, "masterNodeRef");
    static final QName PROP_IS_LINKED_CASE = QName.createQName(CTS_NAMESPACE, "isLinkedCase");
    static final QName PROP_ANSWERING_MINISTER = QName.createQName(CTS_NAMESPACE, "answeringMinister");
    static final QName PROP_ANSWERING_MINISTER_ID = QName.createQName(CTS_NAMESPACE, "answeringMinisterId");

    static final QName PROP_CASE_WORKFLOW_STATUS            = QName.createQName(CTS_NAMESPACE, "caseWorkflowStatus");
    static final QName PROP_CASE_MANDATORY_FIELDS           = QName.createQName(CTS_NAMESPACE, "caseMandatoryFields");
    static final QName PROP_CASE_MANDATORY_FIELDS_DEPS      = QName.createQName(CTS_NAMESPACE, "caseMandatoryFieldDependencies");
    static final QName PROP_CASE_MANDATORY_FIELDS_STATUS    = QName.createQName(CTS_NAMESPACE, "caseMandatoryFieldStatus");
    static final QName PROP_CASE_MANDATORY_FIELDS_TASK      = QName.createQName(CTS_NAMESPACE, "caseMandatoryFieldTask");
    static final QName PROP_OGD_NAME                        = QName.createQName(CTS_NAMESPACE, "ogdName");

    static final QName PROP_HMPO_RESPONSE = QName.createQName(CTS_NAMESPACE, "hmpoResponse");
    static final QName PROP_HMPO_STAGE = QName.createQName(CTS_NAMESPACE, "hmpoStage");
    static final QName PROP_REPLY_TO_CORRESPONDENT = QName.createQName(CTS_NAMESPACE, "replyToCorrespondent");
    static final QName PROP_TYPE_OF_CORRESPONDENT = QName.createQName(CTS_NAMESPACE, "typeOfCorrespondent");
    static final QName PROP_TYPE_OF_COMPLAINANT = QName.createQName(CTS_NAMESPACE, "typeOfComplainant");
    static final QName PROP_TYPE_OF_REPRESENTATIVE = QName.createQName(CTS_NAMESPACE, "typeOfRepresentative");
    static final QName PROP_TYPE_OF_THIRD_PARTY = QName.createQName(CTS_NAMESPACE, "typeOfThirdParty");
    static final QName PROP_CONSENT_ATTACHED = QName.createQName(CTS_NAMESPACE, "consentAttached");
    static final QName PROP_REPLY_TO_APPLICANT = QName.createQName(CTS_NAMESPACE, "replyToApplicant");
    static final QName PROP_APPLICANT_TITLE = QName.createQName(CTS_NAMESPACE, "applicantTitle");
    static final QName PROP_APPLICANT_FORENAME = QName.createQName(CTS_NAMESPACE, "applicantForename");
    static final QName PROP_APPLICANT_SURNAME = QName.createQName(CTS_NAMESPACE, "applicantSurname");
    static final QName PROP_APPLICANT_ADDRESS_LINE1 = QName.createQName(CTS_NAMESPACE, "applicantAddressLine1");
    static final QName PROP_APPLICANT_ADDRESS_LINE2 = QName.createQName(CTS_NAMESPACE, "applicantAddressLine2");
    static final QName PROP_APPLICANT_ADDRESS_LINE3 = QName.createQName(CTS_NAMESPACE, "applicantAddressLine3");
    static final QName PROP_APPLICANT_POSTCODE = QName.createQName(CTS_NAMESPACE, "applicantPostcode");
    static final QName PROP_APPLICANT_COUNTRY = QName.createQName(CTS_NAMESPACE, "applicantCountry");
    static final QName PROP_APPLICANT_EMAIL = QName.createQName(CTS_NAMESPACE, "applicantEmail");
    static final QName PROP_APPLICANT_TELEPHONE = QName.createQName(CTS_NAMESPACE, "applicantTelephone");

    static final QName PROP_REPLY_TO_COMPLAINANT = QName.createQName(CTS_NAMESPACE, "replyToComplainant");
    static final QName PROP_COMPLAINANT_TITLE = QName.createQName(CTS_NAMESPACE, "complainantTitle");
    static final QName PROP_COMPLAINANT_FORENAME = QName.createQName(CTS_NAMESPACE, "complainantForename");
    static final QName PROP_COMPLAINANT_SURNAME = QName.createQName(CTS_NAMESPACE, "complainantSurname");
    static final QName PROP_COMPLAINANT_ADDRESS_LINE1 = QName.createQName(CTS_NAMESPACE, "complainantAddressLine1");
    static final QName PROP_COMPLAINANT_ADDRESS_LINE2 = QName.createQName(CTS_NAMESPACE, "complainantAddressLine2");
    static final QName PROP_COMPLAINANT_ADDRESS_LINE3 = QName.createQName(CTS_NAMESPACE, "complainantAddressLine3");
    static final QName PROP_COMPLAINANT_POSTCODE = QName.createQName(CTS_NAMESPACE, "complainantPostcode");
    static final QName PROP_COMPLAINANT_COUNTRY = QName.createQName(CTS_NAMESPACE, "complainantCountry");
    static final QName PROP_COMPLAINANT_EMAIL = QName.createQName(CTS_NAMESPACE, "complainantEmail");
    static final QName PROP_COMPLAINANT_TELEPHONE = QName.createQName(CTS_NAMESPACE, "complainantTelephone");
    static final QName PROP_HMPO_REFUND_DECISION = QName.createQName(CTS_NAMESPACE, "hmpoRefundDecision");
    static final QName PROP_HMPO_REFUND_AMOUNT = QName.createQName(CTS_NAMESPACE, "hmpoRefundAmount");
    static final QName PROP_HMPO_COMPLAINT_OUTCOME = QName.createQName(CTS_NAMESPACE, "hmpoComplaintOutcome");
    static final QName PROP_HMPO_PASSPORT_NUMBER = QName.createQName(CTS_NAMESPACE, "hmpoPassportNumber");
    static final QName PROP_HMPO_APPLICATION_NUMBER = QName.createQName(CTS_NAMESPACE, "hmpoApplicationNumber");
    static final QName PROP_AUTO_CREATED_CASE = QName.createQName(CTS_NAMESPACE, "autoCreatedCase");
    static final QName PROP_RETURNED_COUNT = QName.createQName(CTS_NAMESPACE, "returnedCount");
    static final QName PROP_PQ_API_CREATED_CASE = QName.createQName(CTS_NAMESPACE, "pqApiCreatedCase");
    static final QName PROP_DRAFT_DATE = QName.createQName(CTS_NAMESPACE, "draftDate");

    static final QName PROP_TOPIC_NAME = QName.createQName(CTSDL_NAMESPACE, "topicName");
    static final QName PROP_PARENT_TOPIC_NAME = QName.createQName(CTSDL_NAMESPACE, "topicListName");
    static final QName PROP_TOPIC_CASE_TYPE = QName.createQName(CTSDL_NAMESPACE, "caseType");

    static final QName ASSOC_GROUPED_CASES = QName.createQName(CTS_NAMESPACE, "groupedPqCases");
    static final QName ASSOC_LINKED_CASES = QName.createQName(CTS_NAMESPACE, "linkedCases");
    static final QName ASSOC_TOPIC_UNITS = QName.createQName(CTSDL_NAMESPACE, "topicUnits");
    static final QName ASSOC_TOPIC_UNIT = QName.createQName(CTSDL_NAMESPACE, "topicUnit");
    static final QName ASSOC_TOPIC_TEAM = QName.createQName(CTSDL_NAMESPACE, "topicTeam");
    static final QName ASSOC_CHILD_TOPIC = QName.createQName(CTSDL_NAMESPACE, "childTopic");
    static final QName ASSOC_PARENT_TOPIC = QName.createQName(CTSDL_NAMESPACE, "parentTopic");
    static final QName ASSOC_GROUPS = QName.createQName(CTSDL_NAMESPACE, "groups");
    static final QName ASSOC_USER = QName.createQName(CTSDL_NAMESPACE, "user");
    static final QName ASSOC_USERS_GROUPS = QName.createQName(CTSDL_NAMESPACE, "usersGroups");

    //case documents
    
    static final QName PROP_DOCUMENT_TYPE = QName.createQName(CTS_NAMESPACE, "documentType");
    static final QName PROP_DOCUMENT_DESCRIPTION = QName.createQName(CTS_NAMESPACE, "documentDescription");

    static final QName PROP_DOCUMENT_UNIT = QName.createQName(CTS_NAMESPACE, "documentUnit");
    static final QName PROP_DOCUMENT_TEAM = QName.createQName(CTS_NAMESPACE, "documentTeam");
    static final QName PROP_DOCUMENT_USER = QName.createQName(CTS_NAMESPACE, "documentUser");

    //case minutes (which ase actually comments)
    static final QName ASPECT_MINUTE_QA_REVIEW = QName.createQName(CTS_NAMESPACE, "qualityReview");
    static final QName PROP_MINUTE_QA_REVIEW_OUTCOMES = QName.createQName(CTS_NAMESPACE, "minuteQaReviewOutcomes");
    static final QName PROP_MINUTE_QA_REVIEW_TASK = QName.createQName(CTS_NAMESPACE, "minuteQaReviewTask");

    static final QName PROP_RETURN_CASE_AT = QName.createQName(CTS_NAMESPACE, "returnCaseAt");
    
    //HMPO collectives props

    static final QName HARD_COPY_RECEIVED = QName.createQName(CTS_NAMESPACE, "hardCopyReceived");

    static final QName CORRESPONDING_NAME = QName.createQName(CTS_NAMESPACE, "correspondingName");
    static final QName NUMBER_OF_CHILDREN= QName.createQName(CTS_NAMESPACE, "numberOfChildren");
    static final QName COUNTRY_OF_DESTINATION= QName.createQName(CTS_NAMESPACE, "countryOfDestination");
    static final QName OTHER_COUNTRIES_TO_BE_VISITED= QName.createQName(CTS_NAMESPACE, "otherCountriesToBeVisited");
    static final QName COUNTRIES_TO_BE_TRAVELLED_THROUGH= QName.createQName(CTS_NAMESPACE, "countriesToBeTravelledThrough");
    static final QName DEPARTURE_DATE_FROM_UK= QName.createQName(CTS_NAMESPACE, "departureDateFromUK");
    static final QName ARRIVING_DATE_IN_UK= QName.createQName(CTS_NAMESPACE, "arrivingDateInUK");
    static final QName INDIVIDUAL_HOUSEHOLD= QName.createQName(CTS_NAMESPACE, "individualHousehold");
    static final QName LEADERS_ADDRESS_ABOARD= QName.createQName(CTS_NAMESPACE, "leadersAddressAboard");

    static final QName PARTY_LEADER_LAST_NAME = QName.createQName(CTS_NAMESPACE, "partyLeaderLastName");
    static final QName PARTY_LEADER_OTHER_NAMES = QName.createQName(CTS_NAMESPACE, "partyLeaderOtherNames");
    static final QName PARTY_LEADER_PASSPORT_NUMBER = QName.createQName(CTS_NAMESPACE, "partyLeaderPassportNumber");
    static final QName PARTY_LEADER_PASSPORT_ISSUED_AT = QName.createQName(CTS_NAMESPACE, "partyLeaderPassportIssuedAt");
    static final QName PARTY_LEADER_PASSPORT_ISSUED_ON = QName.createQName(CTS_NAMESPACE, "partyLeaderPassportIssuedOn");
    static final QName PARTY_LEADER_DEPUTY_LAST_NAME = QName.createQName(CTS_NAMESPACE, "partyLeaderDeputyLastName");
    static final QName PARTY_LEADER_DEPUTY_OTHER_NAMES = QName.createQName(CTS_NAMESPACE, "partyLeaderDeputyOtherNames");
    static final QName PARTY_LEADER_DEPUTY_PASSPORT_NUMBER = QName.createQName(CTS_NAMESPACE, "partyLeaderDeputyPassportNumber");
    static final QName PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT = QName.createQName(CTS_NAMESPACE, "partyLeaderDeputyPassportIssuedAt");
    static final QName PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON = QName.createQName(CTS_NAMESPACE, "partyLeaderDeputyPassportIssuedOn");
    static final QName FEE_INCLUDED = QName.createQName(CTS_NAMESPACE, "feeIncluded");
    static final QName DELIVERY_TYPE = QName.createQName(CTS_NAMESPACE, "deliveryType");
    static final QName EXAMINER_SECURITY_CHECK = QName.createQName(CTS_NAMESPACE, "examinerSecurityCheck");
    static final QName PASSPORT_STATUS = QName.createQName(CTS_NAMESPACE, "passportStatus");
    static final QName BRING_UP_DATE = QName.createQName(CTS_NAMESPACE, "bringUpDate");
    static final QName DEFER_DISPATCH = QName.createQName(CTS_NAMESPACE, "deferDispatch");
    static final QName DISPATCHED_DATE = QName.createQName(CTS_NAMESPACE, "dispatchedDate");
    static final QName DELIVERY_NUMBER = QName.createQName(CTS_NAMESPACE, "deliveryNumber");

    //HMPO complaint

    static final QName SECONDARY_TYPE_OF_CORRESPONDENT = QName.createQName(CTS_NAMESPACE, "secondaryTypeOfCorrespondent");
    static final QName SECONDARY_CORRESPONDENT_REPLY_TO = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentReplyTo");
    static final QName SECONDARY_CORRESPONDENT_TITLE = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentTitle");
    static final QName SECONDARY_CORRESPONDENT_FORE_NAME = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentForename");
    static final QName SECONDARY_CORRESPONDENT_SURNAME = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentSurname");
    static final QName SECONDARY_CORRESPONDENT_ADDRESS_LINE_1 = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentAddressLine1");
    static final QName SECONDARY_CORRESPONDENT_ADDRESS_LINE_2 = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentAddressLine2");
    static final QName SECONDARY_CORRESPONDENT_ADDRESS_LINE_3 = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentAddressLine3");
    static final QName SECONDARY_CORRESPONDENT_POST_CODE = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentPostcode");
    static final QName SECONDARY_CORRESPONDENT_COUNTRY = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentCountry");
    static final QName SECONDARY_CORRESPONDENT_EMAIL = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentEmail");
    static final QName SECONDARY_CORRESPONDENT_TELEPHONE = QName.createQName(CTS_NAMESPACE, "secondaryCorrespondentTelephone");


    static final QName THIRD_PARTY_TYPE_OF_CORRESPONDENT = QName.createQName(CTS_NAMESPACE, "thirdPartyTypeOfCorrespondent");
    static final QName THIRD_PARTY_CORRESPONDENT_REPLY_TO = QName.createQName(CTS_NAMESPACE, "thirdPartyCorrespondentReplyTo");

    static final QName HMPO_REFUND_TYPE = QName.createQName(CTS_NAMESPACE, "hmpoRefundType");
    static final QName DEFER_DUE_TO = QName.createQName(CTS_NAMESPACE, "deferDueTo");
    static final QName OFFICE_OF_ORIGIN = QName.createQName(CTS_NAMESPACE, "officeOfOrigin");

    static final QName SEC_CORRESPONDENT_TYPE_OF_REPRESENTATIVE = QName.createQName(CTS_NAMESPACE, "secCorrespondentTypeOfRepresentative");
    static final QName SEC_CORRESPONDENT_CONSENT_ATTACHED = QName.createQName(CTS_NAMESPACE, "secCorrespondentConsentAttached");
    static final QName THIRD_PARTY_TYPE_OF_REPRESENTATIVE = QName.createQName(CTS_NAMESPACE, "thirdPartyTypeOfRepresentative");
    static final QName THIRD_PARTY_CONSENT_ATTACHED = QName.createQName(CTS_NAMESPACE, "thirdPartyConsentAttached");


}

