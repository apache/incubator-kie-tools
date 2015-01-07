package org.drools.workbench.screens.globals.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Definition of a Global
 */
@Portable
public class Global {

    private String alias = "";
    private String className = "";

    public Global() {
    }

    public Global( final String alias,
                   final String className ) {
        setAlias( alias );
        setClassName( className );
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias( String alias ) {
        this.alias = PortablePreconditions.checkNotNull( "alias",
                                                         alias );
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Global ) ) {
            return false;
        }

        Global global = (Global) o;

        if ( !alias.equals( global.alias ) ) {
            return false;
        }
        if ( !className.equals( global.className ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias.hashCode();
        result = 31 * result + className.hashCode();
        return result;
    }
}
