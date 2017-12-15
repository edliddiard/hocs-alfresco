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
}

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
            var password = 'Password1';
        } else {
            var password = users[j].password;
        }

        createPerson(userName, firstName, secondName, email, password, groupNameArray);
    }
}

/* ------------- Permissions ------------- */
function importPermissions() {
    for (var k = 0; k < permissions.length; k++) {
        var folder = permissions[k].folderName,
            groupPermissionsArray = permissions[k].permissions,
            ctsFolder = companyhome.childByNamePath("CTS"),
            casesFolders = ctsFolder.childByNamePath("Cases"),
            correspondenceType = casesFolders.childByNamePath(folder);

        /* If the folder doesn't exist, create it */
        if ((correspondenceType == '' | correspondenceType == null) && folder != 'Cases') {
            correspondenceType = casesFolders.createFolder(folder, "cm:folder");
            reportLog(folder + ' created');
        }

        /* Permissions for correspondence types folders */
        for (var p = 0; p < groupPermissionsArray.length; p++) {

            var nameOfGroup = groupPermissionsArray[p].groupName,
                groupPermissions = groupPermissionsArray[p].groupPermission;

            if (folder == 'Cases') {
                addPermissions(casesFolders, nameOfGroup, groupPermissions, folder);
            } else {
                addPermissions(correspondenceType, nameOfGroup, groupPermissions, folder);
            }



        }
    }
}

/* ----------------------- OTHER FUNCTIONS ------------------------- */

function addPermissions(correspondenceType, nameOfGroup, groupPermissions, folder) {
    /* Set the permissions to the folder */
    if ((nameOfGroup != null || nameOfGroup != '' || nameOfGroup.length > 0) &&
        (groupPermissions != null || groupPermissions != '' || groupPermissions.length > 0)){

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

function createPerson(username, firstName, secondname, email, Password1, groupNameArray) {
    // delete person first
    var deletedExisting = deletePerson(username);
    if (email === '' | email === null){
        email = 'test@test.com';
    }
    var person = people.createPerson(username, firstName, secondname, email, Password1, true);
    if (person == null) {
        person = people.getPerson(username);
    }

    var expireDate = new Date();
    expireDate.setMonth(expireDate.getMonth() + 30);

    for (var g = 0; g < groupNameArray.length; g++) {
        //var groupFullName = 'GROUP_' + groupNameArray[g];
        var groupFullName = groupNameArray[g];
        if (people.getGroup(groupFullName) != null) {
            people.addAuthority(people.getGroup(groupFullName), person);
            person.properties["{http://cts-beta.homeoffice.gov.uk/model/user/1.0}passwordExpiryDate"] = expireDate;
            person.save();
        } else {
            reportLog("Group missing: Cannot add " + username + " to " + groupNameArray[g]);
        }
    }

    if (deletedExisting) {
        reportLog("person replaced: " + username);
    } else {
        reportLog("New Person Created: " + username);
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




