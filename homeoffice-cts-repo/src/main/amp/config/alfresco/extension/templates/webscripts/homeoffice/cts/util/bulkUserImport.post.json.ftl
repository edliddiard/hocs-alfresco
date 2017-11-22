{
    <#if createdUsers??>
        "createdUsers": [
            <#assign keys = createdUsers?keys>
            <#assign i = 0>
            <#list keys as email>
              <#assign i = i+1>
              {
                "username" : "${email}",
                "password" : "${createdUsers[email]}"
              }<#if i < createdUsers?size>,</#if>
            </#list>
        ]
    </#if>
    <#if errors??>
        , "errors": [
            <#list errors as error>
                "${error}"<#if (error_has_next)>,</#if>
            </#list>
        ]
    </#if>
}