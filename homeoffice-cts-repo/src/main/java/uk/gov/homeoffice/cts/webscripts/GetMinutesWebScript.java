package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.model.CtsMinute;
import uk.gov.homeoffice.cts.service.ManualMinutesService;
import uk.gov.homeoffice.cts.service.SystemMinutesService;
import java.util.*;

/**
 * WebScript that will return the system and manual minutes for a case
 * Created by chris on 28/07/2014.
 */
public class GetMinutesWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCaseWebScript.class);

    private SystemMinutesService systemMinutesService;
    private ManualMinutesService manualMinutes;


    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        String n = req.getParameter("nodeRef");

        long tStart = System.currentTimeMillis();
        List<CtsMinute> minutes = getSystemMinutesService().getSystemMinutes(new NodeRef(n));
        long tEnd = System.currentTimeMillis();

        long tDelta = tEnd - tStart;

        LOGGER.debug("CASEMIN Retrieving system minute times: " + tDelta);

        List<CtsMinute> completeList = new ArrayList<>();
        completeList.addAll(minutes);

        //get comments into a List<CtsMinute> as well, combine and sort
        tStart = System.currentTimeMillis();
        List<CtsMinute> manualMinutes = getManualMinutes().getManualMinutes(new NodeRef(n));
        for(CtsMinute min : manualMinutes)
        {
            if(min.getText().startsWith("Viewed Case"))
            {
                min.setMinuteType("system");
            }
            completeList.add(min);
        }
        tEnd = System.currentTimeMillis();

        long tDelta_two = tEnd - tStart;

        LOGGER.debug("CASEMIN Retrieving manual minute times: " + tDelta_two);

        Collections.sort(completeList,Collections.reverseOrder());

        JSONArray jsonArray = new JSONArray();
        for (CtsMinute ctsMinute : completeList) {
            jsonArray.put(ctsMinute.getJsonObject());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("minutes",jsonArray.toString());

        return model;
    }

    private SystemMinutesService getSystemMinutesService() {
        return systemMinutesService;
    }

    public void setSystemMinutesService(SystemMinutesService systemMinutesService) {
        this.systemMinutesService = systemMinutesService;
    }

    private ManualMinutesService getManualMinutes() {
        return manualMinutes;
    }

    public void setManualMinutes(ManualMinutesService manualMinutes) {
        this.manualMinutes = manualMinutes;
    }
}
