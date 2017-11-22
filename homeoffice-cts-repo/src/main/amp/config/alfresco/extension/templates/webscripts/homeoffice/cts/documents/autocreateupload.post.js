//get the sample user connection
var connectionId = "cmis-sample-connection"
var cmisConnection = cmis.getConnection(connectionId)
if (cmisConnection == null) {
	// if no connection exists, talk to the local server
	cmisConnection = cmis.getConnection()
}

// get CMIS session
var cmisSession = cmisConnection.getSession();
model.cmisSession = cmisSession;
var assignedUnit = '';
var assignedTeam = '';
var assignedUser = '';

// locate file attributes
for each (field in formdata.fields) {
	if (field.name == "name") {
		name = field.value;
	} else if (field.name == "file" && field.isFile) {
		filename = field.filename;
		content = field.content;
	} else if (field.name == "caseType") {
         caseType = field.value;
    } else if (field.name == "assignedUnit") {
         assignedUnit = field.value;
    } else if (field.name == "assignedTeam") {
         assignedTeam = field.value;
    } else if (field.name == "assignedUser") {
         assignedUser = field.value;
    }
}

// ensure mandatory file attributes have been located
if (filename == undefined || content == undefined) {
	status.code = 400;
	status.message = "Uploaded file cannot be located in request";
	status.redirect = true;
} else {

    var autoCreateDir = "/CTS/Auto Create/" + caseType;
    // check if the file already exists, return 409 if so
    var file = null;
    try {
        file = cmisSession.getObjectByPath(String(autoCreateDir + "/" + name));
		status.code = 409;
		status.message = "Error: File with that name already exists";
		status.redirect = true;
    } catch (ex) {
        // if we get an error it is because the file does not exist
        // and we can carry on safely
    }

    if (file == null) {
        try {
            var folder = cmisSession.getObjectByPath(String(autoCreateDir));
        } catch (ex) {}

        if (folder != undefined && folder.baseType.id == "cmis:folder") {
            var properties = cmis.createMap()
            properties["cmis:name"] = String(name);
            properties["cmis:objectTypeId"] = "D:cts:caseDocument";
            properties["cts:documentType"] = "Original";
            properties["cts:documentDescription"] = "Auto create case document.";
            logger.debug("assignedUnit: " + assignedUnit);
            logger.debug("assignedTeam: " + assignedTeam);
            logger.debug("assignedUser: " + assignedUser);
            properties["cts:documentUnit"] = '' + assignedUnit;
            properties["cts:documentTeam"] = '' + assignedTeam;
            properties["cts:documentUser"] = '' + assignedUser;

            var contentStream = cmis.createContentStream(filename, content);
            var newDoc = folder.createDocument(properties, contentStream, null);

            //everything before was OpenCmis objects, change to an Alfresco Scriptnode to update properties
            //the id comes with the version number on the end, remove this
            var nodeRef = new String(newDoc.getId());
            nodeRef = nodeRef.replace(/;[0-9.]*/g, '');
            var alfrescoDoc = utils.getNodeFromString(nodeRef);

            alfrescoDoc.save();
            model.doc = newDoc;
        } else {
            status.code = 400;
            status.message = "Error: Upload folder does not exist";
            status.redirect = true;
        }
    }
}
