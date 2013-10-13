package org.drools.workbench.screens.globals.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Definition of a Global
 */
@Portable
public class Global {

    private String alias;
    private String className;

    public Global() {
    }

    public Global( final String alias,
                   final String className ) {
        this.alias = PortablePreconditions.checkNotNull( "alias",
                                                         alias );
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias( String alias ) {
        this.alias = alias;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }
}
