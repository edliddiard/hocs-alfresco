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
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListResponse;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListService;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by dawud on 01/07/2016.
 */
public final class GetToDoListWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetToDoListWebScript.class);

    private ToDoListService toDoListService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetToDoListWebScript");
        ToDoListResponse toDoListCases = toDoListService.getToDoList(req);
        String response = generateJsonResponse(toDoListCases);

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(response);
    }

    private String generateJsonResponse(ToDoListResponse response) throws IOException {
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
        // Add the CtsCase properties to the CtsCase object
        JsonFactory jsonFactory = new JsonFactory();
        jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.setCodec(mapper);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("totalResults");
        jsonGenerator.writeObject(response.getTotalResults());
        jsonGenerator.writeFieldName("ctsCases");
        jsonGenerator.writeObject(response.getCaseList());
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setToDoListService(ToDoListService toDoListService) {
        this.toDoListService = toDoListService;
    }
}