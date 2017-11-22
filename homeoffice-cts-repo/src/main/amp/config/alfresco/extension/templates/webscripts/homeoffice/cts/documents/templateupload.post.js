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

// locate file attributes
for each (field in formdata.fields) {
	if (field.name == "name") {
		name = field.value;
	} else if (field.name == "file" && field.isFile) {
		filename = field.filename;
		content = field.content;
	} else if (field.name == "appliesToCorrespondenceType") {
        appliesToCorrespondenceType = field.value;
    } else if (field.name == "templateName") {
        templateName = field.value;
    } else if (field.name == "validFromDate") {
        validFromDate = decodeURIComponent(field.value);
    } else if (field.name == "validToDate") {
        validToDate = decodeURIComponent(field.value);
    }
}

// ensure mandatory file attributes have been located
if (filename == undefined || content == undefined) {
	status.code = 400;
	status.message = "Uploaded file cannot be located in request";
	status.redirect = true;
} else {

    // check if the file already exists, return 409 if so
    var file = null;
    try {
        file = cmisSession.getObjectByPath(String("/CTS/Document Templates/" + name));
		status.code = 409;
		status.message = "Error: File with that name already exists";
		status.redirect = true;
    } catch (ex) {
        // if we get an error it is because the file does not exist
        // and we can carry on safely
    }

    if (file == null) {
        var folder = cmisSession.getObjectByPath(String("/CTS/Document Templates"));
        if (folder != undefined && folder.baseType.id == "cmis:folder") {
            var properties = cmis.createMap()
            properties["cmis:name"] = String(name);
            properties["cmis:objectTypeId"] = "D:cts:caseDocumentTemplate";

            var contentStream = cmis.createContentStream(filename, content);
            var newDoc = folder.createDocument(properties, contentStream, null);

            //everything before was OpenCmis objects, change to an Alfresco Scriptnode to update properties
            //the id comes with the version number on the end, remove this
            var nodeRef = new String(newDoc.getId());
            nodeRef = nodeRef.replace(/;[0-9.]*/g, '');
            var alfrescoDoc = utils.getNodeFromString(nodeRef);

            alfrescoDoc.properties["cts:templateName"] = String(templateName);
            alfrescoDoc.properties["cts:appliesToCorrespondenceType"] = String(appliesToCorrespondenceType);
            if (validFromDate != '') {
                alfrescoDoc.properties["cts:validFromDate"] = utils.fromISO8601(validFromDate);
            }
            if (validToDate != '') {
                alfrescoDoc.properties["cts:validToDate"] = utils.fromISO8601(validToDate);
            }
            alfrescoDoc.save();
            model.doc = newDoc;
        } else {
            status.code = 400;
            status.message = "Error: Upload folder does not exist";
            status.redirect = true;
        }
    }
}
