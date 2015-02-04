package org.uberfire.ext.plugin.model;

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class DynamicMenu extends Plugin {

    private Collection<DynamicMenuItem> menuItems;

    public DynamicMenu() {
    }

    public DynamicMenu( final String name,
                        final PluginType type,
                        final Path path,
                        final Collection<DynamicMenuItem> menuItems ) {
        super( name, type, path );
        this.menuItems = menuItems;
    }

    public Collection<DynamicMenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DynamicMenu ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        DynamicMenu that = (DynamicMenu) o;

        if ( menuItems != null ? !menuItems.equals( that.menuItems ) : that.menuItems != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( menuItems != null ? menuItems.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
