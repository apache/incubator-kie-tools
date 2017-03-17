
CanDock ${ruleId} = new CanDock("${ruleId}", "${ruleDefinitionId}", new HashSet<String>() {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

