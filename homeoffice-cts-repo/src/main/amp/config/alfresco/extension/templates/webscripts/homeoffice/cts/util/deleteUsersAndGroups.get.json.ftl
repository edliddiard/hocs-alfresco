{
    "report": [
    <#list report as message>
        "${message}"
        <#if !(message == report?last)>,</#if>
    </#list>
    ]
}