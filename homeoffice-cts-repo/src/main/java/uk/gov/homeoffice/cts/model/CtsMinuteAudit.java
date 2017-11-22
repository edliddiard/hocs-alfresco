package uk.gov.homeoffice.cts.model;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Object to wrap up a minute for it to be sent to the reporting database
 * Created by chris on 14/05/2015.
 */
public class CtsMinuteAudit extends CtsMinute{
    NodeRef nodeRef;
    NodeRef caseNodeRef;
    Date modified;

    public CtsMinuteAudit(NodeRef caseNodeRef, NodeRef nodeRef, Date modified, Long dbid, Date timeStamp, String text, String userName, String minuteType) {
        super(dbid, timeStamp, text, userName, minuteType);
        this.caseNodeRef = caseNodeRef;
        this.nodeRef = nodeRef;
        this.modified = modified;
    }

    @Override
    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("caseNodeRef", getCaseNodeRef());
            jsonObject.put("nodeRef", getNodeRef());
            jsonObject.put("dbid", getDbid());
            jsonObject.put("minuteDateTime", ISO8601DateFormat.format(getTimeStamp()));
            jsonObject.put("lastModified", ISO8601DateFormat.format(getModified()));
            jsonObject.put("minuteContent", getText());
            jsonObject.put("minuteCreatedBy", getUserName());
            jsonObject.put("minuteType", getMinuteType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public NodeRef getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(NodeRef nodeRef) {
        this.nodeRef = nodeRef;
    }

    public NodeRef getCaseNodeRef() {
        return caseNodeRef;
    }

    public void setCaseNodeRef(NodeRef caseNodeRef) {
        this.caseNodeRef = caseNodeRef;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
