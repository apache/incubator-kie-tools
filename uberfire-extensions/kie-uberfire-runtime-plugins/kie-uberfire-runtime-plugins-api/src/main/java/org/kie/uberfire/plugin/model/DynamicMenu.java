package org.kie.uberfire.plugin.model;

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
}
