package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.ctsv2.model.CtsMinuteModel;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * WebScript that will return the system and manual minutes for a case
 * Created by jw on 21/03/2017
 */
    public class GetCountQueryWebscript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCountQueryWebscript.class);

    private NodeService nodeService;
    private SearchService searchService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("CMIS Query Count Script");
        String cmisQuery = req.getParameter("q");

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        sp.setQuery(cmisQuery);

        ResultSet rs = searchService.query(sp);
        Integer length = rs.length();

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

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("count");
        jsonGenerator.writeObject(length);
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(writer.toString());

    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
