package org.uberfire.ext.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Activity {

    private String name;
    private PluginType type;

    public Activity() {
    }

    public Activity( final String name,
                     final PluginType type ) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public PluginType getType() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Activity ) ) {
            return false;
        }

        return name.equals( ( (Activity) o ).name );
    }

    @Override
    public int hashCode() {
        return ~~name.hashCode();
    }
}
