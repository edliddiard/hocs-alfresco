package uk.gov.homeoffice.cts.model;

import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

/**
 * Domain bean that will represent a generic minute
 * Created by chris on 28/07/2014.
 */
public class CtsMinute implements Comparable<CtsMinute>{
    private Long dbid;
    private Date timeStamp;
    private String text;
    private String qaOutcomes;
    private String qaTask;
    private String userName;
    private String minuteType;

    public CtsMinute(Long dbid, Date timeStamp, String text, String userName, String minuteType) {
        this.dbid = dbid;
        this.timeStamp = timeStamp;
        this.text = text;
        this.userName = userName;
        this.minuteType = minuteType;
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dbid", getDbid());
            jsonObject.put("minuteDateTime", ISO8601DateFormat.format(getTimeStamp()));
            jsonObject.put("minuteContent", getText());
            jsonObject.put("minuteUpdatedBy", getUserName());
            jsonObject.put("minuteType", getMinuteType());
            jsonObject.put("minuteQaReviewOutcomes", getMinuteQaReviewOutcomes());
            jsonObject.put("task", getQaTask());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMinuteType() { return this.minuteType; }

    public void setMinuteType(String minuteType) { this.minuteType = minuteType; }

    public String getMinuteQaReviewOutcomes() { return this.qaOutcomes; }

    public void setMinuteQaReviewOutcomes(String qaOutcome) { this.qaOutcomes = qaOutcome; }

    public String getQaTask() {
        return qaTask;
    }

    public void setQaTask(String qaTask) {
        this.qaTask = qaTask;
    }

    @Override
    public int compareTo(CtsMinute o) {
        return this.getTimeStamp().before(o.getTimeStamp()) ? -1
                :this.getTimeStamp().after(o.getTimeStamp()) ? 1
                : 0;
    }
}
