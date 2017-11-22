package uk.gov.homeoffice.ctsv2.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect
public class SummaryByStatus implements Serializable {
    @JsonProperty
    private Integer open = 0;

    @JsonProperty
    private Integer openAndOverdue = 0;

    @JsonProperty
    private Integer returned = 0;

    public SummaryByStatus(Integer open, Integer openAndOverdue, Integer returned) {
        this.open = open;
        this.openAndOverdue = openAndOverdue;
        this.returned = returned;
    }

    public Integer getOpen() {
        return open;
    }

    public Integer getOpenAndOverdue() {
        return openAndOverdue;
    }

    public Integer getReturned() {
        return returned;
    }

    @Override
    public String toString() {
        return "SummaryByStatus{" +
                "open=" + open +
                ", openAndOverdue=" + openAndOverdue +
                ", returned=" + returned +
                '}';
    }
}