
DockingRuleImpl  ${ruleId} = new DockingRuleImpl("${ruleId}", "${ruleDefinitionId}", new HashSet<String>() {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

