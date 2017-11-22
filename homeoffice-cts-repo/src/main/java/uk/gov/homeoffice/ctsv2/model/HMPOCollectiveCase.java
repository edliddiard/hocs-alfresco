package uk.gov.homeoffice.ctsv2.model;


import org.alfresco.service.namespace.QName;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class HMPOCollectiveCase extends CtsCase {

    private Date hardCopyReceived;
    private String correspondingName;
    private String numberOfChildren;
    private String countryOfDestination;
    private String otherCountriesToBeVisited;
    private String countriesToBeTravelledThrough;
    private Date departureDateFromUK;
    private Date arrivingDateInUK;
    private Boolean individualHousehold;
    private String leadersAddressAboard;
    private String partyLeaderLastName;
    private String partyLeaderOtherNames;
    private String partyLeaderPassportNumber;
    private String partyLeaderPassportIssuedAt;
    private Date partyLeaderPassportIssuedOn;
    private String partyLeaderDeputyLastName;
    private String partyLeaderDeputyOtherNames;
    private String partyLeaderDeputyPassportNumber;
    private String partyLeaderDeputyPassportIssuedAt;
    private Date partyLeaderDeputyPassportIssuedOn;
    private Boolean feeIncluded;
    private String deliveryType;
    private Boolean examinerSecurityCheck;
    private String passportStatus;
    private Boolean deferDispatch;
    private Date dispatchedDate;
    private String deliveryNumber;


    public HMPOCollectiveCase(Map<QName, Serializable> caseProps) {
        super(caseProps);
        this.hardCopyReceived= (Date)caseProps.get(CtsModel.HARD_COPY_RECEIVED);
        this.correspondingName = (String)caseProps.get(CtsModel.CORRESPONDING_NAME);
        this.numberOfChildren = (String)caseProps.get(CtsModel.NUMBER_OF_CHILDREN);
        this.countryOfDestination = (String)caseProps.get(CtsModel.COUNTRY_OF_DESTINATION);
        this.otherCountriesToBeVisited = (String)caseProps.get(CtsModel.OTHER_COUNTRIES_TO_BE_VISITED);
        this.countriesToBeTravelledThrough = (String)caseProps.get(CtsModel.COUNTRIES_TO_BE_TRAVELLED_THROUGH);
        this.departureDateFromUK = (Date)caseProps.get(CtsModel.DEPARTURE_DATE_FROM_UK);
        this.arrivingDateInUK = (Date)caseProps.get(CtsModel.ARRIVING_DATE_IN_UK);
        this.individualHousehold = (Boolean) caseProps.get(CtsModel.INDIVIDUAL_HOUSEHOLD);
        this.leadersAddressAboard = (String)caseProps.get(CtsModel.LEADERS_ADDRESS_ABOARD);
        this.partyLeaderLastName = (String)caseProps.get(CtsModel.PARTY_LEADER_LAST_NAME);
        this.partyLeaderOtherNames = (String)caseProps.get(CtsModel.PARTY_LEADER_OTHER_NAMES);
        this.partyLeaderPassportNumber = (String)caseProps.get(CtsModel.PARTY_LEADER_PASSPORT_NUMBER);
        this.partyLeaderPassportIssuedAt = (String)caseProps.get(CtsModel.PARTY_LEADER_PASSPORT_ISSUED_AT);
        this.partyLeaderPassportIssuedOn = (Date)caseProps.get(CtsModel.PARTY_LEADER_PASSPORT_ISSUED_ON);
        this.partyLeaderDeputyLastName = (String)caseProps.get(CtsModel.PARTY_LEADER_DEPUTY_LAST_NAME);
        this.partyLeaderDeputyOtherNames = (String)caseProps.get(CtsModel.PARTY_LEADER_DEPUTY_OTHER_NAMES);
        this.partyLeaderDeputyPassportNumber = (String)caseProps.get(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_NUMBER);
        this.partyLeaderDeputyPassportIssuedAt = (String)caseProps.get(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT);
        this.partyLeaderDeputyPassportIssuedOn = (Date)caseProps.get(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON);
        this.feeIncluded = (Boolean) caseProps.get(CtsModel.FEE_INCLUDED);
        this.deliveryType = (String)caseProps.get(CtsModel.DELIVERY_TYPE);
        this.examinerSecurityCheck = (Boolean) caseProps.get(CtsModel.EXAMINER_SECURITY_CHECK);
        this.passportStatus = (String)caseProps.get(CtsModel.PASSPORT_STATUS);
        this.deferDispatch = (Boolean) caseProps.get(CtsModel.DEFER_DISPATCH);
        this.dispatchedDate = (Date)caseProps.get(CtsModel.DISPATCHED_DATE);
        this.deliveryNumber = (String)caseProps.get(CtsModel.DELIVERY_NUMBER);
    }

    @Override
    public void populateCanonicalCorrespondent() {
        super.canonicalCorrespondent = this.correspondingName;
    }

    public Date getHardCopyReceived() {
        return hardCopyReceived;
    }

    public String getCorrespondingName() {
        return correspondingName;
    }

    public String getNumberOfChildren() {
        return numberOfChildren;
    }

    public String getCountryOfDestination() {
        return countryOfDestination;
    }

    public String getOtherCountriesToBeVisited() {
        return otherCountriesToBeVisited;
    }

    public String getCountriesToBeTravelledThrough() {
        return countriesToBeTravelledThrough;
    }

    public Date getDepartureDateFromUK() {
        return departureDateFromUK;
    }

    public Date getArrivingDateInUK() {
        return arrivingDateInUK;
    }

    public Boolean getIndividualHousehold() {
        return individualHousehold;
    }

    public String getLeadersAddressAboard() {
        return leadersAddressAboard;
    }

    public String getPartyLeaderLastName() {
        return partyLeaderLastName;
    }

    public String getPartyLeaderOtherNames() {
        return partyLeaderOtherNames;
    }

    public String getPartyLeaderPassportNumber() {
        return partyLeaderPassportNumber;
    }

    public String getPartyLeaderPassportIssuedAt() {
        return partyLeaderPassportIssuedAt;
    }

    public Date getPartyLeaderPassportIssuedOn() {
        return partyLeaderPassportIssuedOn;
    }

    public String getPartyLeaderDeputyLastName() {
        return partyLeaderDeputyLastName;
    }

    public String getPartyLeaderDeputyOtherNames() {
        return partyLeaderDeputyOtherNames;
    }

    public String getPartyLeaderDeputyPassportNumber() {
        return partyLeaderDeputyPassportNumber;
    }

    public String getPartyLeaderDeputyPassportIssuedAt() {
        return partyLeaderDeputyPassportIssuedAt;
    }

    public Date getPartyLeaderDeputyPassportIssuedOn() {
        return partyLeaderDeputyPassportIssuedOn;
    }

    public Boolean getFeeIncluded() { return feeIncluded; }

    public String getDeliveryType() {
        return deliveryType;
    }

    public Boolean getExaminerSecurityCheck() {
        return examinerSecurityCheck;
    }

    public String getPassportStatus() {
        return passportStatus;
    }

    public Boolean getDeferDispatch() {
        return deferDispatch;
    }

    public Date getDispatchedDate() {
        return dispatchedDate;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }
}
