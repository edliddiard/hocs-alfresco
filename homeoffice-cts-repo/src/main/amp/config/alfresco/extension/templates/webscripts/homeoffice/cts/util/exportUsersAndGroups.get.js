var createdGroup;

var scriptLog = [];
function reportLog(log) {
	logger.log(log);
}

site = siteService.getSite("cts");

userList = [];
groupList = [];
permissionList = [];
// list all users
listAllUsers();
listAllGroups();
listPermissions();
listAllGroupNodes();

model.users = userList;
model.units = groupList;
model.perms = permissionList;


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

function listAllUsers() {
	//var users = people.getPeople(null);

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
					email = '',
					groupNameArray = [],
					role = '';

				var userNode = userNodes[i];

				userName = userNode.properties.userName;
				firstName = userNode.properties.firstName;
				lastName = userNode.properties.lastName;
				email = userNode.properties.email;

				var groupArray = people.getContainerGroups(userNode);

				for (var j = 0; j < groupArray.length; j++) {
					var groupName = groupArray[j].properties["authorityName"];
					if (groupName != null) {
						groupNameArray.push(groupName);
					}
				}

				if (typeof role != "string") {
					role = '';
				}
				if (!doesUserExist(userList, userName)) {
					userList.push({
						'userName': userName,
						'firstName': firstName,
						'lastName': lastName,
						'email': email,
						'groupNameArray': groupNameArray
					});
				}
			}
		}
		reportLog(userList);
	}

}

function doesUserExist(users, userName) {
	for (var i = 0; i < users.length; i++) {
		var user = users[i];
		if (user.userName === userName) {
			return true;
		}
	}
	return false;
}

// List all groups in Units
function listAllGroups() {
	var unitsGroup = groups.getGroup('Units'),
		childGroupList = unitsGroup.getChildGroups(),
		childGroup = null,
		groupFullName = null,
		groupShortName = null,
        groupDisplayName = null,
		teams = null;

	for (var i = 0; i < childGroupList.length; i++) {
		groupFullName = childGroupList[i]['fullName'];
		groupShortName = childGroupList[i]['shortName'];
        groupDisplayName = childGroupList[i]['displayName'];

		// get teams of the group
		childGroup = groups.getGroup(groupShortName);
		teams = childGroup.getChildGroups();

		var teamsArray = [];
		if (teams !== null ) {
			for (x = 0; x < teams.length; x++) {
				if (teams[x]['shortName'] != null) {
					teamsArray.push({
					"groupName": teams[x]['shortName'],
					"groupDescription": teams[x]['fullName'],
                    "groupDisplayName": teams[x]['displayName']
					});
				}
			}
		}
		else {
			reportLog("The Group has no teams");
		}
		groupList.push(
			{
				"groupName": groupShortName,
				"groupDescription": groupFullName,
                "groupDisplayName": groupDisplayName,
				"teams" : teamsArray
			}
		);
	}
}

function listPermissions() {
    var ctsNode = companyhome.childByNamePath("CTS"),
        children = ctsNode.children;
    for(var i = 0 ; i < children.length ; i++){
        if(children[i].name == 'Cases'){
            var subChildren = children[i].children,
                casesPermissions = children[i].getPermissions();

            getFolderPermissions(casesPermissions, "Cases");


            /* Children folders of Cases e.g. LPQ, FOI, DCU, etc. */
            for(var j = 0 ; j < subChildren.length ; j++){

                var folderName = subChildren[j].name,
                    folderPermissions = subChildren[j].getPermissions();

                getFolderPermissions(folderPermissions, folderName);


            }
        }
    }
}

function getFolderPermissions(permissions, folderName) {
    var permissionsArray = [];

    /* Gets all permissions for the folder. Looks like: ALLOWED;Parliamentary Questions Team;PQDrafter, */
    permissions = permissions.toString();
    permissions = permissions.replace(/ALLOWED;/g , "").replace(/DENIED;/g , "");

    var permArray = permissions.split(/\,\s*/g);
    for (var z = 0; z < permArray.length; z++) {
        permArray[z] = permArray[z].split(/\;\s*/g);
        permissionsArray.push({
            "groupName": permArray[z][0],
            "groupPerm": permArray[z][1]
        });
        reportLog(folderName + 'permissions = ' + permArray[z][0] + ', ' + permArray[z][1]);
    }
    permissionList.push(
        {
            "folderName": folderName,
            "permissions": permissionsArray
        });
}


