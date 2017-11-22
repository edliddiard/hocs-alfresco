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
    deleteAllUsers();
    deleteAllGroups();
    model.report = scriptLog;
}

// delete all users
function deleteAllUsers() {
    var users = people.getPeople(null),
        username = null,
        person = null,
        guest = false,
        admin = false;

    if (users !== null){
        for (i = 0; i < users.length; i++) {
            username = search.findNode(users[i]).properties.userName;
            person = people.getPerson(username);
            guest = people.isGuest(person);
            admin = people.isAdmin(person);
            if (username === 'admin' || guest !== false) {
                reportLog(username + " cannot be deleted");
            } else {
                people.deletePerson(username);
                reportLog("User " + username + " has been deleted.");
            }
        }
    }
}

// delete all groups in Units
function deleteAllGroups() {
    var unitsGroup = groups.getGroup('Units'),
        childGroupList = unitsGroup.getChildGroups(),
        childGroup = null,
        groupFullName = null,
        groupShortName = null,
        groupNode = null,
        teams = null,
        teamFullName,
        teamNode = null;

    for (i = 0; i < childGroupList.length; i++) {
        groupFullName = childGroupList[i]['fullName'];
        groupShortName = childGroupList[i]['shortName'];

        // get teams of the group
        childGroup = groups.getGroup(groupShortName);
        teams = childGroup.getChildGroups();

        // if a child group has any teams then delete them
        if (teams !== null ) {
            for (x = 0; x < teams.length; x++) {
                teamFullName = teams[x]['fullName'];
                teamNode = people.getGroup(teamFullName);
                people.deleteGroup(teamNode);
                reportLog("Team " + teams[x]['shortName'] + " has been deleted.");
            }
        }
        else {
            reportLog("The Group has no teams");
        }

        groupNode = people.getGroup(groupFullName);
        people.deleteGroup(groupNode);
        reportLog("Group " + groupShortName + " has been deleted.");
    }
}

