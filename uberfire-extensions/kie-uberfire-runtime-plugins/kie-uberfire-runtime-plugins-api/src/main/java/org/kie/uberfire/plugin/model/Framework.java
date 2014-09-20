package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum Framework {
    NONE( "" ), ANGULAR( "angularjs" ), KNOCKOUT( "ko" );

    private final String type;

    Framework( String type ) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
