
CanContain  ${ruleId} = new CanContain("${ruleId}", "${ruleDefinitionId}", new HashSet<String>() {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

