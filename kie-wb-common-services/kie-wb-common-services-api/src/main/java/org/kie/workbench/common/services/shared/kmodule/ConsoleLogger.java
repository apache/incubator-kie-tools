package org.kie.workbench.common.services.shared.kmodule;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ConsoleLogger
        implements KSessionLogger {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ConsoleLogger that = ( ConsoleLogger ) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
