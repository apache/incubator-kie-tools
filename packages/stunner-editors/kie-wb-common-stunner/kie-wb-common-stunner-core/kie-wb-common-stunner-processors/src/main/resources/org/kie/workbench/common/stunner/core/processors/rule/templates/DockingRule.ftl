
CanDock ${ruleId} = new CanDock("${ruleId}", "${ruleDefinitionId}", new HashSet<String>(${rolesCount}) {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

