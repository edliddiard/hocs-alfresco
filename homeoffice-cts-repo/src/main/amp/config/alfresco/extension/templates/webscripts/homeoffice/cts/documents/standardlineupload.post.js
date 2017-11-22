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
		name = decodeURIComponent(field.value);
	} else if (field.name == "file" && field.isFile) {
		filename = field.filename;
		content = field.content;
	} else if (field.name == "associatedTopic") {
        associatedTopic = decodeURIComponent(field.value);
    } else if (field.name == "associatedUnit") {
        associatedUnit = decodeURIComponent(field.value);
    } else if (field.name == "reviewDate") {
        validFromDate = decodeURIComponent(field.value);
    } else if (field.name == "updateVersion") {
        updateVersion = field.value
    } else if (field.name == "originalName") {
        originalName = decodeURIComponent(field.value)
    }
}

// ensure mandatory file attributes have been located
if (filename == undefined || content == undefined) {
	status.code = 400;
	status.message = "Uploaded file cannot be located in request";
	status.redirect = true;
} else {

    //delete the file first
    if (updateVersion == 'true') {
        var currNode = companyhome.childByNamePath(String("/CTS/Standard Lines/" + originalName));
        currNode.remove();
    }

    // check if the file already exists, return 409 if so
    var file = null;
    try {
        file = cmisSession.getObjectByPath(String("/CTS/Standard Lines/" + name));
		status.code = 409;
		status.message = "File with name "+ name +" already exists.";
		status.redirect = true;
    } catch (ex) {
        // if we get an error it is because the file does not exist
        // and we can carry on safely
    }

    if (file == null) {
        // check if a standard line for the topic already exists
        var def = {
            query: "select cmis:objectId from cts:standardLine where cts:associatedTopic='" + associatedTopic.replace("'", "\\'") + "'",
            language: "cmis-alfresco"
        };
        var results = search.query(def);
        if (results.length > 0) {
            status.code = 409;
            status.message = 'Standard line for topic "'+associatedTopic+'" already exists.';
            status.redirect = true;
        } else {
            var folder = cmisSession.getObjectByPath(String("/CTS/Standard Lines"));
            if (folder != undefined && folder.baseType.id == "cmis:folder") {
                var properties = cmis.createMap()
                properties["cmis:name"] = String(name);
                properties["cmis:objectTypeId"] = "D:cts:standardLine";

                var contentStream = cmis.createContentStream(filename, content);
                var newDoc = folder.createDocument(properties, contentStream, null);

                //everything before was OpenCmis objects, change to an Alfresco Scriptnode to update properties
                //the id comes with the version number on the end, remove this
                var nodeRef = new String(newDoc.getId());
                nodeRef = nodeRef.replace(/;[0-9.]*/g, '');
                var alfrescoDoc = utils.getNodeFromString(nodeRef);

                alfrescoDoc.properties["cts:associatedTopic"] = String(associatedTopic);
                alfrescoDoc.properties["cts:associatedUnit"] = String(associatedUnit);

                if (validFromDate != '') {
                    alfrescoDoc.properties["cts:reviewDate"] = utils.fromISO8601(validFromDate);
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
}
