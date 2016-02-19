package org.kie.workbench.common.screens.server.management.client.events;

/**
 * TODO: update me
 */
public class ServerTemplateListRefresh {

    private String selectServerTemplateId = null;

    public ServerTemplateListRefresh() {

    }

    public ServerTemplateListRefresh( final String selectServerTemplateId ) {
        this.selectServerTemplateId = selectServerTemplateId;
    }

    public String getSelectServerTemplateId() {
        return selectServerTemplateId;
    }
}
