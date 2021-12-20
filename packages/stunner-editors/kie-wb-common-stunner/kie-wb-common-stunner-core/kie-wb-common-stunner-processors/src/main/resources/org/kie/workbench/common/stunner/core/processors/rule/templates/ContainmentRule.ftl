
CanContain  ${ruleId} = new CanContain("${ruleId}", "${ruleDefinitionId}", new HashSet<String>(${rolesCount}) {{
    <#list roles as role>
        add( ${role} );
    </#list>
}});

