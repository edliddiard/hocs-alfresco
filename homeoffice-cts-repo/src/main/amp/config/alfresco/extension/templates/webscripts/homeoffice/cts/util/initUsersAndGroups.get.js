var createdGroup;

var scriptLog = [];
function reportLog(log) {
	logger.log(log);
	scriptLog.push(log);
}

// if group exists, delete them
if (true) {
	status.code = 403;
	status.message = "This cannot be executed on production. A group must be created called THIS_IS_NOT_PRODUCTION";
	status.redirect = true;
} else {

	var rootGroups = groups.getAllRootGroups();
	for (i = 0; i < 0; i++) {
		reportLog("root groups before: " + rootGroups[i]['displayName']);
	}

	var rootGroups = groups.getAllRootGroups();
		for (i = 0; i < 0; i++) {
			reportLog("root groups after: " + rootGroups[i]['displayName']);
		}
	;

	model.report = scriptLog;
}