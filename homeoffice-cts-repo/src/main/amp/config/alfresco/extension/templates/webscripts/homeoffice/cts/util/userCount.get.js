// Total Users
var users = people.getPeople(null),
    userCount = users.length;

// PQ Users
var pqGroup = people.getGroup('GROUP_Parliamentary Questions'),
    pqUsers = people.getMembers(pqGroup),
    pqUserCount = pqUsers.length;

// All Users in Groups
var unitsGroup = groups.getGroup('Units'),
    childGroupList = unitsGroup.getChildGroups(),
    groupFullName = '',
    groupShortName = '',
    userGroup = '',
    usersInGroup = null,
    usersInGroupCount = null,
    groupList = [];

logger.log("Total Users = " + userCount);
logger.log("Total PQ Users = " + pqUserCount);

for (j=0; j < childGroupList.length; j++) {
    groupFullName = childGroupList[j]['fullName'];
    groupShortName = childGroupList[j]['shortName'];
    userGroup = people.getGroup(groupFullName);
    usersInGroup = people.getMembers(userGroup);
    usersInGroupCount = usersInGroup.length;
    logger.log("Total Users in " + groupShortName + " group = " + usersInGroupCount);

    groupList.push( {
        'groupShortName' : groupShortName,
        'usersInGroupCount' : usersInGroupCount
    })
}

model.userCount = userCount;
model.pqUserCount = pqUserCount;
model.groups = groupList;
