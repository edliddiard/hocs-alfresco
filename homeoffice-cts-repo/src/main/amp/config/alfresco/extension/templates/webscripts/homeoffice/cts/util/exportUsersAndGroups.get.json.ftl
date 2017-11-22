{
	"users": [
	<#list users as user>
		{
    "userName" : "${user.userName}",
    "firstName" : "${user.firstName}",
    "lastName" : "${user.lastName}",
    "email" : "${user.email}",
    "groupNameArray" : [
		<#list user.groupNameArray as groupName>
			<#if groupName??>
				"${groupName}"
				<#if !(groupName == user.groupNameArray?last)>,</#if>
			</#if>
		</#list>
		]
		}
		<#if !(user.userName == users?last.userName)>,</#if>
	</#list>
	],
	"units": [
	<#list units as unit>
	{
	"groupName" : "${unit.groupName}",
	"groupDescription" : "${unit.groupDescription}",
    "groupDisplayName": "${unit.groupDisplayName}",
	"teams" : [
		<#list unit.teams as team>
        {
        "groupName" : "${team.groupName}",
        "groupDescription" : "${team.groupDescription}",
        "groupDisplayName": "${team.groupDisplayName}"
		}
		<#if !(team.groupName == unit.teams?last.groupName)>,</#if>
		</#list>
    	]
	}
		<#if !(unit.groupName == units?last.groupName)>,</#if>
	</#list>
	],
    "permissions": [
    <#list perms as perm>
    {
    "folderName" : "${perm.folderName}",
    "permissions" : [
        <#list perm.permissions as permission>
            {
            "groupName" : "${permission.groupName}",
            "groupPermission" : "${permission.groupPerm}"
            }
            <#if !(permission.groupName == perm.permissions?last.groupName)>,</#if>
        </#list>
        ]
    }
        <#if !(perm.folderName == perms?last.folderName)>,</#if>
    </#list>
    ]
}
