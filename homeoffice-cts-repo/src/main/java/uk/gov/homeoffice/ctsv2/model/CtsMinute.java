package uk.gov.homeoffice.ctsv2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Domain bean that will represent a generic minute
 * Created by dawudr on 05/06/2016.
 */
public class CtsMinute {
    private Long dbid;
    private Date timeStamp;
    private String text;
    private String qaOutcomes;
    private String qaTask;
    private String userName;
    private String minuteType;

    public CtsMinute(uk.gov.homeoffice.cts.model.CtsMinute ctsMinute) {
        this.dbid = ctsMinute.getDbid();
        this.timeStamp = ctsMinute.getTimeStamp();
        this.text = ctsMinute.getText();
        this.userName = ctsMinute.getUserName();
        this.minuteType = ctsMinute.getMinuteType();
    }

    public CtsMinute(Long dbid, Date timeStamp, String text, String userName, String minuteType) {
        this.dbid = dbid;
        this.timeStamp = timeStamp;
        this.text = text;
        this.userName = userName;
        this.minuteType = minuteType;
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    @JsonProperty("minuteDateTime")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone="UTC")
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JsonProperty("minuteContent")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("minuteUpdatedBy")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("minuteType")
    public String getMinuteType() { return this.minuteType; }

    public void setMinuteType(String minuteType) { this.minuteType = minuteType; }

    @JsonProperty("minuteQaReviewOutcomes")
    public String getMinuteQaReviewOutcomes() { return this.qaOutcomes; }

    public void setMinuteQaReviewOutcomes(String qaOutcome) { this.qaOutcomes = qaOutcome; }

    @JsonProperty("task")
    public String getQaTask() {
        return qaTask;
    }

    public void setQaTask(String qaTask) {
        this.qaTask = qaTask;
    }

    public int compareTo(CtsMinute o) {
        return this.getTimeStamp().before(o.getTimeStamp()) ? -1
                :this.getTimeStamp().after(o.getTimeStamp()) ? 1
                : 0;
    }
}
