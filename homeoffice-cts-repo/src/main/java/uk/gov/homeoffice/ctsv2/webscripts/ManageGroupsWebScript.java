package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.helpers.CtsGroupHelper;
import uk.gov.homeoffice.cts.model.GroupAction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageGroupsWebScript extends AbstractWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageGroupsWebScript.class);

    private CtsGroupHelper ctsGroupHelper;

    private static final String UNIT_REF_NAME = "unitRefName";
    private static final String TEAM_REF_NAME = "teamRefName";
    private static final String UNIT_DISPLAY_NAME = "unitDisplayName";
    private static final String TEAM_DISPLAY_NAME = "teamDisplayName";

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running ManageGroupsWebScript");

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        List<String> logDetails = new ArrayList<>();

        final List<GroupAction> addUnits = new ArrayList<>();
        final List<GroupAction> addTeams = new ArrayList<>();
        final List<GroupAction> removeUnits = new ArrayList<>();
        final List<GroupAction> removeTeams = new ArrayList<>();

        JSONObject jsonRecord = null;

        try {
            String json = req.getContent().getContent();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonManageGroups = jsonObject.getJSONArray("manageGroups");
            for (int i = 0; i < jsonManageGroups.length(); i++) {
                jsonRecord = jsonManageGroups.getJSONObject(i);
                String action = (String) jsonRecord.get("action");
                switch (action) {
                    case "addUnit": extractAddUnit(addUnits, jsonRecord); break;
                    case "addTeam": extractAddTeam(addTeams, jsonRecord); break;
                    case "removeUnit": extractRemoveUnit(removeUnits, jsonRecord); break;
                    case "removeTeam": extractRemoveTeam(removeTeams, jsonRecord); break;
                    default: {
                        logDetails.add("Invalid action in JSON payload");
                        LOGGER.error("Invalid action in JSON payload");
                    }
                }

            }

            createUnits(logDetails, addUnits);

            createTeams(logDetails, addTeams);

            removeUnits(logDetails, removeUnits);

            removeTeams(logDetails, removeTeams);

            String response = generateJsonResponse(logDetails);
            LOGGER.debug(response);

            res.getWriter().write(response);

        } catch (IOException | JSONException e) {
            handleError(res, jsonRecord, e);
        }
        LOGGER.debug("Completed ManageGroupsWebscript");
    }

    private void handleError(WebScriptResponse res, JSONObject jsonObject1, Exception e) throws IOException {
        LOGGER.error("Invalid payload", e);
        String errorMsg = jsonObject1 != null ? jsonObject1.toString() : "";
        res.getWriter().write(generateJsonResponse(Collections.singletonList("Invalid payload: " + errorMsg + "  Error msg: " + e.getMessage())));
    }

    private void removeTeams(List<String> logDetails, List<GroupAction> removeTeams) {
        if (!removeTeams.isEmpty()) {
            ctsGroupHelper.removeTeams(logDetails, removeTeams);
        }
    }

    private void removeUnits(List<String> logDetails, List<GroupAction> removeUnits) {
        if (!removeUnits.isEmpty()) {
            ctsGroupHelper.removeUnits(logDetails, removeUnits);
        }
    }

    private void createTeams(List<String> logDetails, List<GroupAction> addTeams) {
        if (!addTeams.isEmpty()) {
            ctsGroupHelper.createTeams(logDetails, addTeams);
        }
    }

    private void createUnits(List<String> logDetails, List<GroupAction> addUnits) {
        if (!addUnits.isEmpty()) {
            ctsGroupHelper.createUnits(logDetails, addUnits);
        }
    }

    private void extractRemoveTeam(List<GroupAction> removeTeams, JSONObject jsonObject1) throws JSONException {
        final String groupRefName = getField(jsonObject1, TEAM_REF_NAME);
        removeTeams.add(new GroupAction(null, groupRefName, null, null));
    }

    private void extractRemoveUnit(List<GroupAction> removeUnits, JSONObject jsonObject1) throws JSONException {
        final String unitRefName = getField(jsonObject1, UNIT_REF_NAME);
        removeUnits.add(new GroupAction(unitRefName, null, null, null));
    }

    private void extractAddTeam(List<GroupAction> addTeams, JSONObject jsonObject1) throws JSONException {
        final String unitRefName = getField(jsonObject1, UNIT_REF_NAME);
        final String teamRefName = getField(jsonObject1, TEAM_REF_NAME);
        final String teamDisplayName = getField(jsonObject1, TEAM_DISPLAY_NAME);
        addTeams.add(new GroupAction(unitRefName, teamRefName, null, teamDisplayName));
    }

    private void extractAddUnit(List<GroupAction> addUnits, JSONObject jsonObject1) throws JSONException {
        final String unitRefName = getField(jsonObject1, UNIT_REF_NAME);
        final String unitDisplayName = getField(jsonObject1, UNIT_DISPLAY_NAME);
        addUnits.add(new GroupAction(unitRefName, null, unitDisplayName, null));
    }

    private String getField(JSONObject jsonObject1, String fieldName) throws JSONException {
        return (String) jsonObject1.get(fieldName);
    }

    private String generateJsonResponse(List<String> logDetails) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.writeStartArray();
        for (String log: logDetails) {
            jsonGenerator.writeString(log);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setCtsGroupHelper(CtsGroupHelper ctsGroupHelper) {
        this.ctsGroupHelper = ctsGroupHelper;
    }
}

