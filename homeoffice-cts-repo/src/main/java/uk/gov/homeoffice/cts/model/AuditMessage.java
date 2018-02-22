package uk.gov.homeoffice.cts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditMessage {

    @JsonProperty(value="uuid")
    private final UUID uuid;

    @JsonProperty(value="timestamp")
    @JsonFormat(pattern = "YYYY-MM-dd'T'HH:mm:ss")
    private final Date timestamp;

    @JsonProperty(value="caseReference")
    private final String caseReference;

    @JsonProperty(value="data")
    private final Map<String, String> afterMap;

    public AuditMessage(Map<QName, Serializable> after) throws UnsupportedEncodingException {
        uuid = UUID.randomUUID();
        timestamp = new Date();
        afterMap = transformMap(after);
        caseReference = afterMap.get("urnSuffix");
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        sb.append("\"uuid\": \"");
        sb.append(uuid);
        sb.append("\", ");

        sb.append("\"timestamp\": \"");
        sb.append(timestamp);
        sb.append("\", ");

        sb.append("\"caseReference\":\"");
        sb.append(caseReference);
        sb.append("\", ");

        sb.append("\"data\": ");
        sb.append(printMap(afterMap));

        sb.append(" }");

        return sb.toString();
    }

    private static String printMap(Map<String,String> map){
        StringBuilder sb = new StringBuilder();

        sb.append("{ ");
        for(Map.Entry<String, String> entry : map.entrySet()) {
           sb.append("\"" + entry.getKey() + "\": \"");
           sb.append(entry.getValue());
           sb.append("\",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" }");

        return sb.toString();
    }

    private static Map<String, String> transformMap(Map<QName, Serializable> map) throws UnsupportedEncodingException {
        Map<String, String> retMap = new HashMap<>();

        for(Map.Entry<QName, Serializable> entry : map.entrySet()) {
            System.out.print(entry);
            String value = "null";
            if(entry.getValue() != null) {
                byte[] utf8data = entry.getValue().toString().getBytes("UTF-8");
                value = new String(utf8data, "UTF-8");
            }

            retMap.put(entry.getKey().getLocalName(), value );
        }

        return retMap;
    }
}
