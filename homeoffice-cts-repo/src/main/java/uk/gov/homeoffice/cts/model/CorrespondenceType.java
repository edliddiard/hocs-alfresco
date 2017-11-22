package uk.gov.homeoffice.cts.model;

import java.util.*;

public enum CorrespondenceType {
    ORDINARY_WRITTEN_NEW("OPQN"),NAMED_DAY_NEW("NPQN"),LORDS_WRITTEN_NEW("LPQN"),
    LORDS_WRITTEN("LPQ"), ORDINARY_WRITTEN("OPQ"), NAMED_DAY("NPQ"),
    DCU_MINISTERIAL("MIN"), DCU_TREAT_OFFICIAL("TRO"), DCU_NUMBER_10("DTEN"),
    UKVI_M_REF("IMCM"), UKVI_B_REF("IMCB"), UKVI_NUMBER_10("UTEN"),
    HMPO_COMPLAINT("COM"), HMPO_GENERAL("GEN"), HMPO_COLLECTIVES("COL"),
    HMPO_STAGE_1("COM1"), HMPO_STAGE_2("COM2"), HMPO_DIRECT_GENRAL("DGEN"),HMPO_GNR("GNR"),
    FOI("FOI"), FOI_FTC("FTC"), FOI_FTCI("FTCI"), FOI_FSC("FSC"), FOI_FSCI("FSCI"), FOI_FLT("FLT"), FOI_FUT("FUT");

    public static final Set<CorrespondenceType> PQ_TYPES = EnumSet.of(NAMED_DAY, ORDINARY_WRITTEN, LORDS_WRITTEN);

    public static final Set<CorrespondenceType> HMPO_COMPLAINTS = EnumSet.of(HMPO_STAGE_1, HMPO_STAGE_2, HMPO_DIRECT_GENRAL, HMPO_GNR);

    private String code;

    CorrespondenceType(String code){
       this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static List<String> getAllCaseTypes() {
        List<String> caseTypes = new ArrayList<>();
        caseTypes.add(DCU_MINISTERIAL.getCode());
        caseTypes.add(DCU_TREAT_OFFICIAL.getCode());
        caseTypes.add(LORDS_WRITTEN.getCode());
        caseTypes.add(ORDINARY_WRITTEN.getCode());
        caseTypes.add(NAMED_DAY.getCode());
        caseTypes.add(UKVI_M_REF.getCode());
        caseTypes.add(UKVI_B_REF.getCode());
        caseTypes.add(HMPO_COMPLAINT.getCode());
        caseTypes.add(HMPO_GENERAL.getCode());
        caseTypes.add(HMPO_COLLECTIVES.getCode());
        caseTypes.add(DCU_NUMBER_10.getCode());
        caseTypes.add(UKVI_NUMBER_10.getCode());
        caseTypes.add(FOI.getCode());
        caseTypes.add(FOI_FTC.getCode());
        caseTypes.add(FOI_FTCI.getCode());
        caseTypes.add(FOI_FSC.getCode());
        caseTypes.add(FOI_FSCI.getCode());
        caseTypes.add(FOI_FLT.getCode());
        caseTypes.add(FOI_FUT.getCode());
        caseTypes.add(ORDINARY_WRITTEN_NEW.getCode());
        caseTypes.add(NAMED_DAY_NEW.getCode());
        caseTypes.add(LORDS_WRITTEN_NEW.getCode());
        return caseTypes;
    }

    public static List<String> getUnitCaseTypes(String unit) {
        List<String> caseTypes = new ArrayList<>();
        switch (unit) {
            case "DCU":
                caseTypes.add(DCU_MINISTERIAL.getCode());
                caseTypes.add(DCU_TREAT_OFFICIAL.getCode());
                break;
            case "PQ":
                caseTypes.add(LORDS_WRITTEN.getCode());
                caseTypes.add(ORDINARY_WRITTEN.getCode());
                caseTypes.add(NAMED_DAY.getCode());
                caseTypes.add(ORDINARY_WRITTEN_NEW.getCode());
                caseTypes.add(NAMED_DAY_NEW.getCode());
                caseTypes.add(LORDS_WRITTEN_NEW.getCode());
                break;
            case "UKVI":
                caseTypes.add(UKVI_M_REF.getCode());
                caseTypes.add(UKVI_B_REF.getCode());
                break;
            case "HMPO":
                caseTypes.add(HMPO_COMPLAINT.getCode());
                caseTypes.add(HMPO_GENERAL.getCode());
                caseTypes.add(HMPO_COLLECTIVES.getCode());
                caseTypes.add(HMPO_STAGE_1.getCode());
                caseTypes.add(HMPO_STAGE_2.getCode());
                caseTypes.add(HMPO_DIRECT_GENRAL.getCode());
                caseTypes.add(HMPO_GNR.getCode());
                break;
            case "NO10":
                caseTypes.add(DCU_NUMBER_10.getCode());
                caseTypes.add(UKVI_NUMBER_10.getCode());
                break;
            case "FOI":
                caseTypes.add(FOI.getCode());
                caseTypes.add(FOI_FTC.getCode());
                caseTypes.add(FOI_FTCI.getCode());
                caseTypes.add(FOI_FSC.getCode());
                caseTypes.add(FOI_FSCI.getCode());
                caseTypes.add(FOI_FLT.getCode());
                caseTypes.add(FOI_FUT.getCode());
                break;
        }
        return caseTypes;
    }

    public static boolean isPQType(String correspondenceType) {
        for (CorrespondenceType pqType : PQ_TYPES) {
            if(pqType.getCode().equals(correspondenceType)){
                return true;
            }
        }
        return false;
    }

    public static boolean isHMPOComplaints(String correspondenceType) {
        for (CorrespondenceType hmpoComplaint : HMPO_COMPLAINTS) {
            if(hmpoComplaint.getCode().equals(correspondenceType)){
                return true;
            }
        }
        return false;
    }

    public static CorrespondenceType getByCode(String code){
        for (CorrespondenceType type : CorrespondenceType.values()) {
            if(type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}