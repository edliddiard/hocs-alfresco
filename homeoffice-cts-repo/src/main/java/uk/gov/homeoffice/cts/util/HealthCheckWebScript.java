package uk.gov.homeoffice.cts.util;


import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Retrieves statistics using solr
 * Created by dawud on 31/03/2016.
 */
//public class HealthWebScript extends DeclarativeWebScript {
public class HealthCheckWebScript extends AbstractWebScript {

    private static final String DEFAULT_CTS_SITE = "cts";
    private static final String DEFAULT_TEST_CTS_SITE = "healthcheck001";
    private static final String DEFAULT_CTS_PATH = "/Company Home/CTS";
    private static final String DEFAULT_CTS_LUCENE_SEARCH_QUERY = "PATH:\"/app:company_home/cm:CTS\"";
    public static final String SCHEDULED_ACTIONS_NODE_NAME = "Scheduled Actions";
    public static final String SCHEDULED_ACTIONS_NODE_PATH = "/Company Home/Data Dictionary/Scheduled Actions";
    public static final String DATA_DICTIONARY_NODE_NAME = "Data Dictionary";
    private Boolean verbose;
    private String site = null;

    /**
     * Enum for list of health check services
     */
    enum ServiceEnum {
        FILESYSTEM("filesystem"),
        DATABASE("database"),
        SEARCH("search"),
        PERSON("person"),
        SITE("site"),
        ALFRESCO("alfresco"),
        ALL("all"),
        LIST("list"),
        DEFAULT("default");

        private String serviceName;

        ServiceEnum(String serviceName) {
            this.serviceName = serviceName;
        }

        public String serviceName() {
            return serviceName;
        }
    }

    private SiteService siteService;
    private PersonService personService;
    private NodeService nodeService;
    private SearchService searchService;
    private FileFolderService fileFolderService;
    private DataSource dataSource;
    private TaggingService taggingService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

        AuthenticationUtil.setRunAsUserSystem();

        Map<String, Object> model = new HashMap<String, Object>();

        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();

        if (req.getParameter("verbose") != null && !req.getParameter("verbose").isEmpty()) {
            verbose = Boolean.parseBoolean(req.getParameter("verbose").toLowerCase().trim());
        } else {
            verbose = false;
        }

        if (req.getParameter("site") != null && !req.getParameter("site").isEmpty()) {
            site = req.getParameter("site").toLowerCase().trim();
        } else {
            site = DEFAULT_CTS_SITE;
        }

        ServiceEnum serviceName = ServiceEnum.DEFAULT;
        String service = "";
        if (templateVars != null && templateVars.containsKey("service")) {
            service = templateVars.get("service").toLowerCase().trim();
            serviceName = ServiceEnum.valueOf(service.toUpperCase().trim());
        }

        switch (serviceName) {
            case ALL:
                model.put("alfresco", alfrescoCheck());
                model.put("fileSystem", fileSystemCheck());
                model.put("database", databaseCheck());
                model.put("search-lucene", searchServiceCheck());
                model.put("siteService", siteServiceCheck());
                break;
            case FILESYSTEM:
                model.put("fileSystem", fileSystemCheck());
                break;
            case DATABASE:
                model.put("database", databaseCheck());
                break;
            case SEARCH:
                model.put("search-lucene", searchServiceCheck());
                break;
            case SITE:
                model.put("siteService", siteServiceCheck());
                break;
            case ALFRESCO:
                model.put("alfresco", alfrescoCheck());
                break;
            case PERSON:
                model.put("personService", personServiceCheck());
                break;
            case LIST:
                model.put("services list", serviceName.values());
                break;
            default:
                model.put("alfresco", alfrescoCheck());
                model.put("database", databaseCheck());
                model.put("siteService", siteServiceCheck());
        }


        try {
            // build a json object
            JSONObject obj = new JSONObject();

            // put some data on it
            obj.put("healthcheck", model);

            // build a JSON string and send it back
            String jsonString = obj.toString();

            res.setContentType(MimetypeMap.MIMETYPE_JSON);
            res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            res.getWriter().write(jsonString);
        } catch (JSONException e) {
            throw new WebScriptException("Unable to serialize JSON");
        }
    }


    /**
     * ALFRESCO
     */
    private Map<String, Object> alfrescoCheck() {

        int mb = 1024 * 1024;
        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> alfrescoMap = new HashMap<String, Object>();
        if (verbose) {
            alfrescoMap.put("Used Memory", (runtime.totalMemory() - runtime.freeMemory()) / mb);
            alfrescoMap.put("Free Memory", runtime.freeMemory() / mb);
            alfrescoMap.put("Total Memory Heap (Xms)", runtime.totalMemory() / mb);
            alfrescoMap.put("Max Memory (Xmx)", runtime.maxMemory() / mb);
            alfrescoMap.put("Percentage memory free", ((double) runtime.freeMemory() / (double) runtime.totalMemory()) * (double) 100);
        }
        alfrescoMap.put("status", ((double) runtime.freeMemory() / (double) runtime.totalMemory() * (double) 100 > 10) ? "OK" : "FAIL");
        return alfrescoMap;
    }


    /**
     * FILESYSTEM
     */
    private Map<String, Object> fileSystemCheck() throws IOException {

        int warnLowDiskSpaceThresholdPercent = 10;
        Map<String, Object> filesystemMap = new HashMap<String, Object>();

        NumberFormat nf = NumberFormat.getNumberInstance();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            FileStore store = Files.getFileStore(root);
            if (verbose) {
                filesystemMap.put("Free DiskSpace (GB) - " + root, nf.format(store.getUsableSpace()));
                filesystemMap.put("Total DiskSpace (GB) - " + root, nf.format(store.getTotalSpace()));
            }
            filesystemMap.put("status", ((double) store.getUsableSpace() / (double) store.getTotalSpace() * (double) 100 > warnLowDiskSpaceThresholdPercent) ? "OK" : "FAIL");
        }
        return filesystemMap;
    }

    /**
     * DATABASE
     */
    private Map<String, Object> databaseCheck() {

        Map<String, Object> databaseMap = new HashMap<String, Object>();

        Connection dbConnection = null;
        try {
            dbConnection = dataSource.getConnection();
            if (verbose) {
                DatabaseMetaData dbMetaData = dbConnection.getMetaData();
                databaseMap.put("DB Product Name", dbMetaData.getDatabaseProductName());
                databaseMap.put("DB Product Version", dbMetaData.getDatabaseProductVersion());
                databaseMap.put("DB Driver Name", dbMetaData.getDriverName());
                databaseMap.put("DB Driver Version", dbConnection.getMetaData().getURL());
                databaseMap.put("DB Username", dbMetaData.getUserName());
                databaseMap.put("DB URL", dbMetaData.getURL());
            }
            databaseMap.put("status", (dbConnection != null) ? "OK" : "FAIL");

        } catch (SQLException e) {
            databaseMap.put("status", "FAIL");
            e.printStackTrace();
        } finally {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return databaseMap;
    }

    /**
     * SEARCH SERVICE (LUCENE)
     */
    private Map<String, Object> searchServiceCheck() {
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
        ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, DEFAULT_CTS_LUCENE_SEARCH_QUERY);
        Map<String, Object> searchServiceMap = new HashMap<String, Object>();
        NodeRef nodeRef = null;
        try {
            if (rs.length() == 0) {
                searchServiceMap.put("searchService", "FAIL");
                if (verbose) {
                    searchServiceMap.put("CTS Folder node not found", site);
                    searchServiceMap.put("Node Name", site);
                    searchServiceMap.put("Lucene Search Query", DEFAULT_CTS_LUCENE_SEARCH_QUERY);
                }
            } else {
                nodeRef = rs.getNodeRef(0);
                String nodeName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString().trim().toLowerCase();
                searchServiceMap.put("searchService", (nodeName.toLowerCase().equals(site) ? "OK" : "FAIL"));
                if (verbose) {
                    searchServiceMap.put("Node Name", site);
                    searchServiceMap.put("Lucene Search Query", DEFAULT_CTS_LUCENE_SEARCH_QUERY);
                    searchServiceMap.put(site + " Folder node", nodeRef.toString());
                    searchServiceMap.put("Search results found", rs.length());
                }
            }
        } finally {
            rs.close();
        }
        return searchServiceMap;
    }

    /**
     * PERSON SERVICE test
     */
    private Map<String, Object> personServiceCheck() {

        boolean personServiceTest = personService.personExists("admin");

        Map<String, Object> personServiceMap = new HashMap<String, Object>();
        personServiceMap.put("status", (personServiceTest) ? "OK" : "FAIL");
        if (verbose) {
            personServiceMap.put("Username", "admin");
            personServiceMap.put("Exists", (personServiceTest) ? "OK" : "FAIL");
        }
        return personServiceMap;
    }


    /**
     * SITE SERVICE test readonly
     */
    private Map<String, Object> siteServiceCheck() {

        SiteInfo siteInfo = siteService.getSite(site);
        Map<String, Object> siteServiceMap = new HashMap<String, Object>();
        siteServiceMap.put("status", (siteInfo != null && siteInfo.getTitle().equals(site)) ? "OK" : "FAIL");
        if (verbose) {
            siteServiceMap.put("Site Info Preset", siteInfo.getSitePreset());
            siteServiceMap.put("Site Info Short Name", siteInfo.getShortName());
            siteServiceMap.put("Site Info Title", siteInfo.getTitle());
            siteServiceMap.put("Site Info Description", siteInfo.getDescription());
            siteServiceMap.put("Site Info Visibility", siteInfo.getVisibility());
            siteServiceMap.put("Site Info NodeRef", siteInfo.getNodeRef());
            siteServiceMap.put("Site Info Tag Scope", taggingService.isTagScope(siteInfo.getNodeRef()));
        }

        return siteServiceMap;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setTaggingService(TaggingService taggingService) {
        this.taggingService = taggingService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
