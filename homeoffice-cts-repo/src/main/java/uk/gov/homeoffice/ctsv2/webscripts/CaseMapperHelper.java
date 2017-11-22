package uk.gov.homeoffice.ctsv2.webscripts;

import java.util.Map;

/**
 * class to map alfresco case properties to custom values.
 */
public class CaseMapperHelper {

    private Map<String, String> caseStatusMapper;

    private Map<String, String> caseTaskMapper;

    private Map<String, Integer> caseProgressMapper;

    public String getCaseDisplayStatus(final String currentStatus) {
        String displayStatus = caseStatusMapper.get(currentStatus);
        if (displayStatus == null) {
            displayStatus = currentStatus;
        }
        return displayStatus;
    }

    public String getCaseDisplayTask(final String currentTask) {
        String displayTask = caseTaskMapper.get(currentTask);
        if (displayTask == null) {
            displayTask = currentTask;
        }
        return displayTask;
    }

    public Integer getCaseProgressStatus(final String caseStatus) {
        Integer progressStatus = caseProgressMapper.get(caseStatus);
        if (progressStatus == null) {
            progressStatus = -1;
        }
        return progressStatus;
    }

    public void setCaseStatusMapper(Map<String, String> caseStatusMapper) {
        this.caseStatusMapper = caseStatusMapper;
    }

    public void setCaseTaskMapper(Map<String, String> caseTaskMapper) {
        this.caseTaskMapper = caseTaskMapper;
    }

    public void setCaseProgressMapper(Map<String, Integer> caseProgressMapper) { this.caseProgressMapper = caseProgressMapper; }
}
