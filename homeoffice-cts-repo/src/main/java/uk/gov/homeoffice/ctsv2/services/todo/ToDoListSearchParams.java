package uk.gov.homeoffice.ctsv2.services.todo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Arrays;
import java.util.List;

public final class ToDoListSearchParams {
    private List<String> assignedUserNames;
    private int pageSize;
    private int skipCount;
    private boolean includeAllowableActions;
    private boolean filterByPriority;
    private String sortingOrder;
    private List<String> caseStatus;
    private List<String> assignedTeams;
    private List<String> caseTasks;
    private List<String> caseTypes;
    private List<String> assignedUnits;
    private static final String COMMA = ",";

    public List<String> getAssignedUserNames() {
        return assignedUserNames;
    }

    public void setAssignedUserNames(String assignedUsers) {
        if (assignedUsers != null) {
            this.assignedUserNames = Arrays.asList(assignedUsers.split(COMMA));
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        if (pageSize == null) {
            this.pageSize = -1;
        } else {
            try {
                this.pageSize = Integer.parseInt(pageSize);
            } catch (NumberFormatException e) {
                this.pageSize = -1;
            }
        }
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(String skipCount) {

        if (skipCount == null) {
            this.skipCount = -1;
        } else {
            try {
                this.skipCount = Integer.parseInt(skipCount);
            } catch (NumberFormatException e) {
                this.skipCount = -1;
            }
        }
    }
    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;

    }

    public boolean isIncludeAllowableActions() {
        return includeAllowableActions;
    }

    public void setIncludeAllowableActions(String includeAllowableActions) {
        this.includeAllowableActions = Boolean.valueOf(includeAllowableActions);
    }
    public void setIncludeAllowableActions(Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }

    public String getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(String sortingOrder) {
        if (sortingOrder == null) {
            sortingOrder = "ASC";
        } else if (!"ASC".equalsIgnoreCase(sortingOrder) && (!"DESC".equalsIgnoreCase(sortingOrder))) {
            sortingOrder = "ASC";
        }
        this.sortingOrder = sortingOrder;
    }

    public List<String> getCaseStatus() {
        return caseStatus;
    }


    public void setCaseStatus(String caseStatuses) {
        if (caseStatuses != null) {
            this.caseStatus = Arrays.asList(caseStatuses.split(COMMA));
        }
    }

    public List<String> getAssignedTeams() {
        return assignedTeams;
    }

    public void setAssignedTeams(String assignedTeams) {
        if (assignedTeams != null) {
            this.assignedTeams = Arrays.asList(assignedTeams.split(COMMA));
        }
    }

    public List<String> getCaseTasks() {
        return caseTasks;
    }

    public void setCaseTasks(String caseTasks) {
        if (caseTasks != null) {
            this.caseTasks = Arrays.asList(caseTasks.split(COMMA));
        }
    }

    public List<String> getCaseTypes() {
        return caseTypes;
    }

    public void setCaseTypes(String caseTypes) {
        if (caseTypes != null) {
            this.caseTypes = Arrays.asList(caseTypes.split(COMMA));
        }
    }

    public List<String> getAssignedUnits() {
        return assignedUnits;
    }

    public void setAssignedUnits(String assignedUnits) {
        if (assignedUnits != null) {
            this.assignedUnits = Arrays.asList(assignedUnits.split(COMMA));
        }
    }

    public boolean isFilterByPriority() {
        return filterByPriority;
    }

    public void setFilterByPriority(boolean filterByPriority) {
        this.filterByPriority = filterByPriority;
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

