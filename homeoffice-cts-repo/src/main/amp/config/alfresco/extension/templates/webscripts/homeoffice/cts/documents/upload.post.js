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
	} else if (field.name == "destination") {
        destination = field.value;
    } else if (field.name == "documenttype") {
        documenttype = field.value;
    } else if (field.name == "documentdescription") {
        documentdescription = field.value;
    }
}

// ensure mandatory file attributes have been located
if (filename == undefined || content == undefined) {
	status.code = 400;
	status.message = "Uploaded file cannot be located in request";
	status.redirect = true;
} else {
	var folder = cmisSession.getObject(String(destination));

	if (folder != undefined && folder.baseType.id == "cmis:folder") {
		var properties = cmis.createMap()
		properties["cmis:name"] = String(name);
		properties["cmis:objectTypeId"] = "D:cts:caseDocument";
		properties["cts:documentType"] = String(documenttype);
		properties["cts:documentDescription"] = String(documentdescription);

		var contentStream = cmis.createContentStream(filename, content);
        try {
		    model.doc = folder.createDocument(properties, contentStream, null);
        } catch (e) {
            var message = e.message;
            if (message.match(/Existing file or folder [\s\S]* already exists/i)) {
                message = "File " + name + " already exists";
                status.code = 400;
                status.message = message;
                status.redirect = true;
            } else {
                throw e;
            }
        }
	} else {
		status.code = 400;
		status.message = "Error: Upload folder does not exist";
		status.redirect = true;
	}
}
