
var scriptLog = [];
function reportLog(log) {
	logger.log(log);
	scriptLog.push(log);
}

	listAllUsersAndExtendPassword();
	model.report = scriptLog;

function listAllGroupNodes() {

	var groupNodes = [];
	var unitsGroup = groups.getGroup('Units');
	var	unitList = unitsGroup.getChildGroups();


	for (var k = 0; k < unitList.length; k++) {
		var unit = unitList[k]

		groupNodes.push(unit);

		//groupShortName = unit.shortName;
		//childGroup = groups.getGroup(groupShortName);

		var teams = unit.getChildGroups();
		for (var i = 0; i < teams.length; i++) {
			groupNodes.push(teams[i])
		}

	}
	return groupNodes;
}

function listAllUsersAndExtendPassword() {

	var expireDate = new Date();
	expireDate.setMonth(expireDate.getMonth() + 3);

	var groupNodes = listAllGroupNodes();

	for (var k = 0; k < groupNodes.length; k++) {

		var groupNode = groupNodes[k];
		var test = groupNode.getGroupNode();
		var userNodes = people.getMembers(test, false);

		if (userNodes !== null) {
			for (i = 0; i < userNodes.length; i++) {
				var userName = null,
						firstName = '',
						lastName = '',
						email = '';

				var userNode = userNodes[i];

				userName = userNode.properties.userName;
				firstName = userNode.properties.firstName;
				lastName = userNode.properties.lastName;
				email = userNode.properties.email;
				reportLog("Password expiry for " + userName +  " to  " + expireDate.toString());
				userNode.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
				userNode.save();
			}
		}

	}

}
