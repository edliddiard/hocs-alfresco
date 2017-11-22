package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.model.CtsMinuteAudit;
import uk.gov.homeoffice.cts.service.ManualMinutesService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebScript that will return the manual minutes from a given date
 * This is for the MI
 * Created by chris on 28/07/2014.
 */
public class GetLatestUserMinutesWebScript extends DeclarativeWebScript {
    private ManualMinutesService manualMinutes;


    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        String date = req.getParameter("dateFrom");

        Date dateFrom = ISO8601DateFormat.parse(date);

        //get comments into a List<CtsMinute> as well, combine and sort
        List<CtsMinuteAudit> manualMinutes = getManualMinutes().getLatestMinutes(dateFrom);


        JSONArray jsonArray = new JSONArray();
        for (CtsMinuteAudit ctsMinuteAudit : manualMinutes) {
            jsonArray.put(ctsMinuteAudit.getJsonObject());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("minutes",jsonArray.toString());

        return model;
    }

    private ManualMinutesService getManualMinutes() {
        return manualMinutes;
    }

    public void setManualMinutes(ManualMinutesService manualMinutes) {
        this.manualMinutes = manualMinutes;
    }
}
