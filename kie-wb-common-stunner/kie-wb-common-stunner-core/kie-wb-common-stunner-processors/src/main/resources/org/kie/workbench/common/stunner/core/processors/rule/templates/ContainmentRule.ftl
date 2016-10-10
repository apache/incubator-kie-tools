
ContainmentRuleImpl  ${ruleId} = new ContainmentRuleImpl("${ruleId}", "${ruleDefinitionId}", new HashSet<String>() {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

