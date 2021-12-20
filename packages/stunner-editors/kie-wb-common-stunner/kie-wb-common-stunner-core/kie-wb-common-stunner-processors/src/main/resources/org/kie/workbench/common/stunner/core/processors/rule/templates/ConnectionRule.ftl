
CanConnect ${ruleId} = new CanConnect("${ruleId}", "${ruleDefinitionId}",
    new ArrayList<CanConnect.PermittedConnection>(${connectionsSize}) {{
    <#list connections as connection>
        add(new CanConnect.PermittedConnection( "${connection.from}", "${connection.to}" ));
    </#list>
    }}
);