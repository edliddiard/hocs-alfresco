var scriptLog = [];
function reportLog(log) {
    logger.log(log);
    scriptLog.push(log);
}

var requestJson = requestbody.content,
    exportJson = eval('(' + requestJson + ')'),
    users = exportJson.users,
    units = exportJson.units,
    permissions = exportJson.permissions,
    userName = '',
    createdGroup;

//importUnitsAndGroups();
importUsers();
importPermissions();
//extendPasswordExpiry();

model.report = scriptLog;


/* ------------- Units and Groups ------------- */
function importUnitsAndGroups() {
    for (var i = 0; i < units.length; i++){
        /* Add the Units first */
        var parentGroup = 'Units',
            unitGroupName = units[i].groupName,
            unitGroupDisplayName = units[i].groupDisplayName,
            teams = units[i].teams;

        createGroup(parentGroup, unitGroupName, unitGroupDisplayName);

        /* Add the teams linked to the units */
        if (teams.length > 0){
            for (var x = 0; x < teams.length; x++){
                var teamParentGroup = unitGroupName,
                    teamGroupName = teams[x].groupName,
                    teamGroupDisplayName = teams[x].groupDisplayName;

                createGroup(teamParentGroup, teamGroupName, teamGroupDisplayName);
            }
        }
    }
}

/* ------------- Users ------------- */
function importUsers(){
    for (var j = 0; j < users.length; j++){
        var userName = users[j].userName,
            firstName = users[j].firstName,
            secondName = users[j].lastName,
            email = users[j].email,
            groupNameArray = users[j].groupNameArray;
        if (!users[j].password) {
            var password = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
        } else {
            var password = users[j].password;
        }

        createPerson(userName, firstName, secondName, email, password, groupNameArray);
    }
}

/* ------------- Permissions ------------- */
function importPermissions() {

    reportLog('Applying permission sets');

    var ctsFolder = companyhome.childByNamePath('CTS'),
        casesFolder = ctsFolder.childByNamePath('Cases'),
        autoCreateFolder = ctsFolder.childByNamePath('Auto Create'),
        standardLinesFolder = ctsFolder.childByNamePath('Standard Lines'),
        documentTemplatesFolder = ctsFolder.childByNamePath('Document Templates');

    for (var i = 0; i < permissions.length; i ++) {
        var folderName = permissions[i].folderName,
            folder = null,
            permissionsArray = permissions[i].permissions;

        switch(folderName) {
            case 'Cases':
                if(casesFolder == null)
                    casesFolder = ctsFolder.createFolder(folderName, "cm:folder");
                folder = casesFolder;
                break;
            case 'Standard Lines':
                if(standardLinesFolder == null)
                    standardLinesFolder = ctsFolder.createFolder(folderName, "cm:folder");
                folder = standardLinesFolder;
                break;
            case 'Document Templates':
                if(documentTemplatesFolder == null)
                    documentTemplatesFolder = ctsFolder.createFolder(folderName, "cm:folder");
                folder = documentTemplatesFolder;
                break;
            case 'Auto Create':
                if(autoCreateFolder == null)
                    autoCreateFolder = ctsFolder.createFolder(folderName, "cm:folder");
                folder = autoCreateFolder;
                break;
            default:
                reportLog(folderName + ' not created, unsupported folder')
        }

        if (folder !== null) {
            for (var j = 0; j < permissionsArray.length; j++) {
                var nameOfGroup = permissionsArray[j].groupName,
                    permission = permissionsArray[j].groupPermission;
                addPermissions(folder, nameOfGroup, permission, folderName);
            }
        } else {
            reportLog('Permissions will not be set for ' + folderName + ' unsupported folder')
        }

    }
}

/* ----------------------- OTHER FUNCTIONS ------------------------- */

function addPermissions(correspondenceType, nameOfGroup, groupPermissions, folder) {
    /* Set the permissions to the folder */
    if ((nameOfGroup != null || nameOfGroup != '' || nameOfGroup.length > 0) &&
        (groupPermissions != null || groupPermissions != '' || groupPermissions.length > 0)){

        reportLog(groupPermissions + ' adding permissions to ' + nameOfGroup + ' in ' + folder + ' folder.');
        correspondenceType.setPermission(groupPermissions, nameOfGroup);
        reportLog(groupPermissions + ' permissions added to ' + nameOfGroup + ' in ' + folder + ' folder.');

    } else {
        reportLog('No groups or permissions set for folder: ' + folder);
    }
}

function createGroup(parentGroup, groupName, groupDisplayName) {
    var groupExists = doesGroupExist(groupName);

    if (groupExists) {
        reportLog("Group: " + groupName + " already exists. Skipping create Group.")
        return;
    }

    if (arguments.length == 2) {
        groupDisplayName = groupName;
    }

    var parent = groups.getGroup(parentGroup);
    if (parent !== null) {
        reportLog(groupName + " " + groupDisplayName)
        parent.createGroup(groupName, groupDisplayName);
    }

    return createdGroup;
}

function doesGroupExist(groupName) {
    var group = people.getGroup("GROUP_" + groupName);
    if (group != null) {
        return true;
    }
    return false;
}

function createPerson(username, firstName, secondname, email, password, groupNameArray) {
    var person = people.getPerson(username);
    if(person == null) {
        if (email === '' | email === null) {
            email = 'test@test.com';
        }
        var person = people.createPerson(username, firstName, secondname, email, password, true);
        if (person == null) {
            person = people.getPerson(username);
        }

        var expireDate = new Date();
        expireDate.setMonth(expireDate.getMonth() + 30);

        person.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;

        reportLog("New Person Created: " + username);
    }
    else {
        reportLog("Person already exists: " + username);
    }

    var associatedGroups = people.getContainerGroups(person);
    var associatedGroupNames = [];

    for (var k = 0; k < associatedGroups.length; k++) {
        var group = associatedGroups[k].getQnamePath();
        var p = group.split(":");
        associatedGroupNames.push(p[p.length-1]);
    }

    for (var i = 0; i < groupNameArray.length; i++) {
        var fullGroupName = 'GROUP_' + groupNameArray[i];
        if(associatedGroupNames.indexOf(fullGroupName) === -1) {
            // Create group
            reportLog("Associated user with group");
            people.addAuthority(people.getGroup(fullGroupName), person);
            person.save();
        } else {
            // Group already exists
            reportLog("User already associated with group");
        }
    }

    for (var j = 0; j < associatedGroupNames.length; j++) {
        var groupName = associatedGroupNames[j].substring(6, associatedGroupNames[j].length);
        if(groupNameArray.indexOf(groupName) === -1) {
            // User has been removed from group
            reportLog("Removing user from group");
            people.removeAuthority(associatedGroups[j], person);
            person.save();
        }2
    }

}

function extendPasswordExpiry() {
    var expireDate = new Date();
    expireDate.setMonth(expireDate.getMonth() + 3);

    var users = search.luceneSearch('TYPE:"cm:person"');

    for(var i = 0 ; i < users.length ; i++) {
        reportLog("Password expiry" + expireDate.toString());
        var user = users[i];
        user.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
        user.save();
    }
}

function deletePerson(username) {
    var person = people.getPerson(username);
    // if person exists, delete them
    if (person != null && username !== 'admin' && username !== 'guest') {
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




