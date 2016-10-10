
ConnectionRuleImpl ${ruleId} = new ConnectionRuleImpl("${ruleId}", "${ruleDefinitionId}",
    new HashSet<ConnectionRule.PermittedConnection>(${connectionsSize}) {{
    <#list connections as connection>
        add(new PermittedConnectionImpl( "${connection.from}", "${connection.to}" ));
    </#list>
    }}
);