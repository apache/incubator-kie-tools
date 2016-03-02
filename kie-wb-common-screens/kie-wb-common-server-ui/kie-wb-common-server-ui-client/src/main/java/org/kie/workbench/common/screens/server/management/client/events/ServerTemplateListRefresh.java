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

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ServerTemplateListRefresh ) ) {
            return false;
        }

        final ServerTemplateListRefresh that = (ServerTemplateListRefresh) o;

        return selectServerTemplateId != null ? selectServerTemplateId.equals( that.selectServerTemplateId ) : that.selectServerTemplateId == null;

    }

    @Override
    public int hashCode() {
        return selectServerTemplateId != null ? selectServerTemplateId.hashCode() : 0;
    }
}
