const DEFAULT_MAX_RESULTS = 500;
const SITES_SPACE_QNAME_PATH = "/app:company_home/app:dictionary/cm:Scheduled_x0020_Actions";
const SCHEDULED_ACTIONS_NODE_PATH = "/Company Home/Data Dictionary/Scheduled Actions";


var referenceType = "path";
// store type, store id, display path
var reference = ["workspace", "SpacesStore", "Company Home/Data Dictionary", "Scheduled Actions", ];
var foundNode = search.findNode(referenceType, reference);
model.nodes = foundNode;



// extract folder listing arguments from URI
var folderpath = SCHEDULED_ACTIONS_NODE_PATH;

// search for folder within Alfresco content repository
var folder = roothome.childByNamePath(folderpath);

// validate that folder has been found
if (folder == undefined || !folder.isContainer) {
    status.code = 404;
    status.message = "Folder " + folderpath + " not found.";
    status.redirect = true;
}

// construct model for response template to render
model.folder = folder;