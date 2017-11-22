deleteAllUsers();
deleteAllGroups();


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
                continue;
            } else {
                people.deletePerson(username);

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

            }
        }

        groupNode = people.getGroup(groupFullName);
        people.deleteGroup(groupNode);

    }
}

