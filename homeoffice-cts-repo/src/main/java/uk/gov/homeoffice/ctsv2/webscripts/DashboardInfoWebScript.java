package uk.gov.homeoffice.ctsv2.webscripts;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.ctsv2.dashboard.DashboardProcessor;
import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

public class DashboardInfoWebScript extends AbstractWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardInfoWebScript.class);

    private DashboardProcessor dashboardProcessor;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running DashboardInfoWebScript");
        long tResStart = System.currentTimeMillis();

        Map<String, Map<String, SummaryByStatus>> summary = dashboardProcessor.getSummary();

        long tResEnd = System.currentTimeMillis();
        long tResponse = tResEnd - tResStart;
        LOGGER.debug("Retrieving minutes property. Total Time: {}ms", tResponse);

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(generateJsonResponse(summary));
    }

    private String generateJsonResponse(Map<String, Map<String, SummaryByStatus>> summary) throws IOException {
        // Generate JSON response
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // jackson library defaults
        mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.WRAP_EXCEPTIONS);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        // custom options
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

        JsonGenerator jsonGenerator;
        StringWriter writer;
        writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.setCodec(mapper);
        jsonGenerator.writeObject(summary);
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setDashboardProcessor(DashboardProcessor dashboardProcessor) {
        this.dashboardProcessor = dashboardProcessor;
    }
}
