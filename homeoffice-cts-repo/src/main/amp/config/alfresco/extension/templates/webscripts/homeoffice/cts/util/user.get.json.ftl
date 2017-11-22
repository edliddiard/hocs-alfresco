<#import "/homeoffice/lib/cts.lib.json.ftl" as ctsLib>
{
    "userName": "${user.userName}",
    "firstName": "${user.firstName}",
    "lastName": "${user.lastName}",
    "email": "${user.email}",
    <#if user.passwordExpiryDate??>
        "passwordExpiryDate": "${user.passwordExpiryDate?datetime?iso("UTC")}",
    </#if>
    "groups": [
        <#list groups as group>
            {
                ${group.json}

            } <#if (group_has_next)>,</#if>
        </#list>
    ],
    "manager": "${manager?string("true", "false")}",
    "casesPermissions": {
        <@ctsLib.allowableactions node=nodeCasesFolder/>
    },
    "documentTemplatesPermissions": {
        <@ctsLib.allowableactions node=nodeDocumentTemplatesFolder/>
    },
    "standardLinesPermissions": {
        <@ctsLib.allowableactions node=nodeStandardLinesFolder/>
    },
    "autoCreatePermissions": {
        <@ctsLib.allowableactions node=nodeAutoCreateFolder/>
    }
}