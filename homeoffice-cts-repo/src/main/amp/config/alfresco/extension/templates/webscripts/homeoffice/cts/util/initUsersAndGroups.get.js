var createdGroup;

var scriptLog = [];
function reportLog(log) {
	logger.log(log);
	scriptLog.push(log);
}

var production_check = people.getGroup("GROUP_THIS_IS_NOT_PRODUCTION");
// if group exists, delete them
if (production_check == null) {
	status.code = 403;
	status.message = "This cannot be executed on production. A group must be created called THIS_IS_NOT_PRODUCTION";
	status.redirect = true;
} else {

	var rootGroups = groups.getAllRootGroups();
	for (i = 0; i < rootGroups.length; i++) {
		reportLog("root groups before: " + rootGroups[i]['displayName']);
	}
	;

	deleteAllTopics();


	/* ----------- GROUPS ----------- */
	// DCU
	createGroup('Units', 'DCU', 'DCU');
	createGroup('DCU', 'CDT', 'Central Drafting Team');
	createGroup('DCU', 'PPT', 'Performance and Processes Team');
	createGroup('DCU', 'TNT', 'Transfers and No10 Team');

	// UKVI
	createGroup('Units', 'UKVI', 'UKVI');
	createGroup('UKVI', 'CPR', 'Central Point of Receipt');
	createGroup('UKVI', 'CQT', 'CQT Team');
	createGroup('UKVI', 'UKVI Drafters');

	// HMPO
	createGroup('Units', 'HMPO');
	createGroup('Units', 'HMPO CCC');
	createGroup('Units', 'HMPO PCU');

	createGroup('HMPO CCC', 'CCC Dispatch Team');
	createGroup('HMPO CCC', 'CCC QA');
	createGroup('HMPO CCC', 'CCC Drafters');

	createGroup('HMPO PCU', 'PCU Dispatch');
	createGroup('HMPO PCU', 'PCU Dispatch Team');
	createGroup('HMPO PCU', 'PCU Drafters');
	createGroup('HMPO PCU', 'PCU QA Team');

	// FOI
	createGroup('Units', 'FOI');
	createGroup('FOI', 'FOI Drafters');
	createGroup('FOI', 'IAT', 'FOI Information Access Team');
	createGroup('FOI', 'FOI QA');
	createGroup('FOI', 'FOI SCS');

	// GLOBAL
	createGroup('Units', 'FOI Ministers Private Office');
	createGroup('Units', 'Home Secretarys Private Office', "Home Secretary's Private Office");
	createGroup('Units', 'James Brokenshires Private Office');
	createGroup('Units', 'Lord Ahmads Private Office');
	createGroup('Units', 'Lord Bates Private Office');
	createGroup('Units', 'Parliamentary Under Secretary Private Office');
	createGroup('Units', 'Permanent Secretary Private Office');
	createGroup('Units', 'Press Office');
	createGroup('Units', 'SPADs', 'Special Advisors');

	// PQ
	createGroup('Units', 'Parliamentary Questions');
	createGroup('Parliamentary Questions', 'Parliamentary Questions Team');
	createGroup('Parliamentary Questions', 'Parly Team Drafters', 'Parly Team Drafters', 'PQDrafter');

	/* ----------- USERS ----------- */

	// DCU
	createPerson('dcuadmin', 'dcu', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('DCU QA Reviewer', 'DCU', 'QA Reviewer', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CDT', 'GROUP_Manager'], "Drafter");
	createPerson('DCU Drafter', 'DCU', 'Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CDT'], "Drafter");
	createPerson('DCU PPT User', 'DCU', 'PPT User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PPT', 'GROUP_Manager'], "CaseViewer");
	createPerson('DCU PPT User2', 'DCU', 'PPT User2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PPT', 'GROUP_Manager'], "CaseViewer");
	createPerson('DCU SPADS Approver', 'DCU', ' SPADS Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_SPADs', 'GROUP_Manager'], "CaseViewer");
	createPerson('TNT User', 'TNT', 'User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_TNT', 'GROUP_Manager'], "CaseViewer");

	// UKVI
	createPerson('ukviadmin', 'ukvi', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('CPR User', 'CPR', 'User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CPR', 'GROUP_Manager'], "CaseViewer");
	createPerson('CQT Approver', 'CQT', 'Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CQT', 'GROUP_Manager'], "CaseViewer");
	createPerson('UKVI Drafter', 'UKVI', 'Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_UKVI Drafters'], "Drafter");
	createPerson('UKVI Home sec approver', 'UKVI', 'Home sec approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Home Secretarys Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('UKVI QA Reviewer', 'UKVI', 'QA Reviewer', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_UKVI Drafters'], "Drafter");
	createPerson('Official Approver', 'Official', 'Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_UKVI Drafters'], "Drafter");

	// HMPO
	createPerson('hmpoadmin', 'hmpo', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('HMPO Dispatcher', 'HMPO', 'Dispatcher', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC Dispatch Team', 'GROUP_Manager'], "CaseViewer");
	createPerson('HMPO Drafter', 'HMPO', 'Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC Drafters', 'GROUP_Manager'], "Drafter");
	createPerson('HMPO QA Approver', 'HMPO', 'QA Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC QA', 'GROUP_Manager'], "CaseViewer");
	createPerson('HMPO SCS Approver', 'HMPO', 'SCS Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC QA'], "Drafter");
	createPerson('PCU Drafter', 'PCU', 'Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PCU Drafters'], "Drafter");
	createPerson('PCU QA User', 'PCU', 'QA User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PCU QA Team'], "CaseViewer");
	createPerson('PCU SCS Approver', 'PCU', ' SCS Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PCU QA Team'], "CaseViewer");
	createPerson('PCU Dispatch User', 'PCU', 'Dispatcher User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_PCU Dispatch'], "Drafter");

	// FOI
	createPerson('FOI Drafter2', 'FOI', 'Drafter2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI Drafters', 'GROUP_Manager'], "Drafter");
	createPerson('FOI Internal Drafter', 'FOI', 'Internal Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI Drafters'], "Drafter");
	createPerson('FOI Internal Head of FOI', 'FOI Internal', 'Head of FOI', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI'], "CaseViewer");
	createPerson('FOI Internal Reviewer', 'FOI Internal', 'Reviewer', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI QA'], "CaseViewer");
	createPerson('FOI Internal SCS Approver', 'FOI Internal', 'SCS Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI SCS', 'GROUP_Manager'], "CaseViewer");
	createPerson('FOI SCS 2', 'FOI', 'SCS 2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI SCS', 'GROUP_Manager'], "CaseViewer");
	createPerson('FOI SPADS Approver', 'FOI', ' SPADS Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_SPADs', 'GROUP_Manager'], "CaseViewer");
	createPerson('IAT User', 'IAT', 'User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_IAT', 'GROUP_Manager'], "Drafter");
	createPerson('IAT User 2', 'IAT', 'User 2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_IAT', 'GROUP_Manager'], "Drafter");
	createPerson('IAT User2', 'IAT', 'User 2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_IAT', 'GROUP_Manager'], "Drafter");
	createPerson('IAT User 3', 'IAT', 'User 3', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_IAT', 'GROUP_Manager'], "Drafter");
	createPerson('IAT User3', 'IAT', 'User 3', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_IAT', 'GROUP_Manager'], "Drafter");
	createPerson('Press Office User', 'Press Office', 'User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Press Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('FOI QA Reviewer', 'FOI', 'QA Reviewer', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI QA', 'GROUP_Manager'], "CaseViewer");
	createPerson('FOI SCS', 'FOI', 'SCS', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI SCS', 'GROUP_Manager'], "CaseViewer");
	createPerson('foiadmin', 'foi', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('foiadmin2', 'foi', 'admin2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('foisubstantiveadmin', 'foi', 'substantiveadmin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");
	createPerson('HeadofFOI', 'Headof', 'FOI', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Manager', 'GROUP_FOI'], "CaseViewer");
	createPerson('foi.drafter@hercule.com', 'FOI', 'Drafter1', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI Drafters', 'GROUP_Manager'], "Drafter");

	// GLOBAL
	createPerson('Home Sec Approver', 'Home', 'Sec Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Home Secretarys Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Home Sec Approver2', 'Home', 'Sec Approver2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Home Secretarys Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Home Sec Approver3', 'Home', 'Sec Approver3', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Home Secretarys Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Perm Sec Approver', 'Perm', 'Sec Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Permanent Secretary Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Perm Sec Approver2', 'Perm', 'Sec Approver2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Permanent Secretary Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User1', 'Private', 'Office User1', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Lord Bates Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User2', 'Private', 'Office User2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User3', 'Private', 'Office User3', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_James Brokenshires Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User4', 'Private', 'Office User4', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Under Secretary Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User5', 'Private', 'Office User5', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_FOI Ministers Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User6', 'Private', 'Office User6', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_James Brokenshires Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User7', 'Private', 'Office User7', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_James Brokenshires Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User8', 'Private', 'Office User8', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_James Brokenshires Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User9', 'Private', 'Office User9', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Lord Bates Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('Private Office User10', 'Private', 'Office User10', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Lord Ahmads Private Office', 'GROUP_Manager'], "CaseViewer");
	createPerson('SPADS Approver', 'SPADS', 'Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_SPADs', 'GROUP_Manager'], "CaseViewer");
	createPerson('SPADS Approver2', 'SPADS', 'Approver2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_SPADs', 'GROUP_Manager'], "CaseViewer");
	createPerson('testadmin', 'test', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_ALFRESCO_ADMINISTRATORS'], "Drafter");

	// PQ
	createPerson('Parly Team Drafter', 'Parly', 'Team Drafter', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parly Team Drafters'], "PQDrafter");
	createPerson('Parly Team Manager', 'Parly', 'Team Manager', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "Drafter");
	createPerson('Parly Team Manager2', 'Parly', ' Team Manager2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "Drafter");
	createPerson('Parly Team User', 'Parly', 'Team User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "PQDrafter");
	createPerson('Parly Team User2', 'Parly', 'Team User2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "PQDrafter");
	createPerson('SCS Approver', 'SCS', 'Approver', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "PQDrafter");
	createPerson('SCS Approver2', 'SCS', 'Approver2', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "PQDrafter");
	createPerson('lpqadmin', 'lpq', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions', 'GROUP_Parliamentary Questions Team',  'GROUP_ALFRESCO_ADMINISTRATORS', 'GROUP_Manager'], "Drafter");
	createPerson('pqadmin', 'pq', 'admin', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Parliamentary Questions', 'GROUP_ALFRESCO_ADMINISTRATORS', 'GROUP_Manager'], "Drafter");
	createPerson('Head of PQs', 'Head', 'of PQs', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_Units', 'GROUP_Parliamentary Questions', 'GROUP_Parliamentary Questions Team', 'GROUP_Manager'], "Drafter");

	// Other
	createPerson('CCC QA User', 'CCC', 'QA User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC QA', 'GROUP_HMPO', 'GROUP_HMPO CCC'], "CaseViewer");
	createPerson('CCC Dispatch User', 'CCC', 'Dispatch User', 'hercule.user@hercule.homeoffice.gov.uk', 'Password1', ['GROUP_CCC Dispatch Team', 'GROUP_HMPO', 'GROUP_HMPO CCC'], "CaseViewer");

	extendPasswordExpiry();


	var rootGroups = groups.getAllRootGroups();
		for (i = 0; i < rootGroups.length; i++) {
			reportLog("root groups after: " + rootGroups[i]['displayName']);
		}
	;

	model.report = scriptLog;
}

	function createPerson(username, firstName, secondname, email, Password1, groupNameArray, role) {
		// delete person first
		var deletedExisting = deletePerson(username);
		var person = people.createPerson(username, firstName, secondname, email, Password1, true);
		if (person == null) {
			person = people.getPerson(username);
		}

		var expireDate = new Date();
		expireDate.setMonth(expireDate.getMonth() + 30);

		for (var g = 0; g < groupNameArray.length; g++) {
			if (people.getGroup(groupNameArray[g]) != null) {
				if (!isInGroup(groupNameArray[g], username)) {
					people.addAuthority(people.getGroup(groupNameArray[g]), person);
					person.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
					person.save();
				}
			} else {
				reportLog("Group missing: Cannot add " + username + " to " + groupNameArray[g]);
			}
		}
		//var casesQueue = companyhome.childByNamePath("CTS/Cases");
		//casesQueue.setPermission(role, username);

		if (deletedExisting) {
			reportLog("person replaced: " + username);
		} else {
			reportLog("New Person Created: " + username);
		}

	}

	function createGroup(parentGroup, groupName, groupDisplayName, groupPermissions) {
		var groupExists = doesGroupExist(groupName);

		if (groupExists) {
			reportLog("Group: " + groupName + " already exists. Skipping create Group.")
			return;
		}

		if (arguments.length == 2) {
			groupDisplayName = groupName;
		}

		if (arguments.length < 4) {
			groupPermissions = "Drafter";
		}

		var parent = groups.getGroup(parentGroup);
		if (parent !== null) {
			reportLog(groupName + " " + groupDisplayName)
			parent.createGroup(groupName, groupDisplayName);
		}

		reportLog("groupname" + groupName);
		reportLog("groupPermissions" + groupPermissions);

		var casesQueue = companyhome.childByNamePath("CTS/Cases");
		casesQueue.setPermission(groupPermissions, 'GROUP_' + groupName);

		return createdGroup;
	}

	function deletePerson(username) {
		var person = people.getPerson(username);
		// if person exists, delete them
		if (person != null) {
			people.deletePerson(username);
			return true;
		}
		return false;
	}

	function isInGroup(group, username) {
		var userArray = people.getMembers(people.getGroup(group));
		var user = people.getPerson(username);
		for (var i = 0; i < userArray.length; i++) {
			if (userArray[i].properties['cm:userName'] == user.properties['cm:userName']) {
				return true;
			}
		}
		return false;
	}

	function deleteAllTopics() {
		var site = companyhome.childByNamePath("Sites/cts");
		var dataLists = site.childByNamePath("dataLists");
		var topicList = dataLists.childByNamePath("hierarchicalTopics");
		topicListItems = topicList.children;
		for (var g = 0; g < topicListItems.length; g++) {
			topicListItems[g].remove();
		}
	}


	function extendPasswordExpiry() {
		var expireDate = new Date();
		expireDate.setMonth(expireDate.getMonth() + 3);

		var users = search.luceneSearch('TYPE:"cm:person"');

		for(var i = 0 ; i < users.length ; i++) {
			reportLog("Password expiry" + expireDate.toString())
			var user = users[i]
			user.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
			user.save();
		}
	}


	function doesGroupExist(groupName) {
		var group = people.getGroup("GROUP_" + groupName);
		if (group != null) {
			return true;
		}
		return false;
	}
