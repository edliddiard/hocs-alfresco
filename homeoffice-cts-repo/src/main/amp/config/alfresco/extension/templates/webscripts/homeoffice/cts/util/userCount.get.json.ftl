{
    "Total number of users": ${userCount},
    "Total PQ users" : ${pqUserCount},
    "Total users in Units" : [
        <#list groups as group>
        {
            "${group.groupShortName}" : ${group.usersInGroupCount}
        }
            <#if !(group.groupShortName == groups?last.groupShortName)>,</#if>
        </#list>
    ]
}