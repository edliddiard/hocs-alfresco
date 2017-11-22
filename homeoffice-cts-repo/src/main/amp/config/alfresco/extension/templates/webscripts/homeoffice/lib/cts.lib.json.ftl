<#macro allowableactions node>
    <#nested>
    <#assign typedef = cmistype(node)>
    "propertyPermissions" : {
        <#list customPerm.getCustomPermissions(node) as element>
            <#if element.name != "canAssignUser">
                "${element.name}" : "${element.value}"<#if element_has_next>,</#if>
            </#if>
        </#list>
    },
    <#-- doing this to support legacy code in front end -->
    <#list customPerm.getCustomPermissions(node) as element>
        <#if element.name == "canAssignUser">
            "${element.name}" : "${element.value}",
        </#if>
    </#list>
    <#list typedef.actionEvaluators?values as actionevaluator>

            "${actionevaluator.action.label}" : "${actionevaluator.isAllowed(node.nodeRef)?string}"<#if actionevaluator_has_next>,</#if>

    </#list>


</#macro>
