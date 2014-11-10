package org.uberfire.plugin.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.plugin.model.DynamicMenu;
import org.uberfire.plugin.model.Media;
import org.uberfire.plugin.model.Plugin;
import org.uberfire.plugin.model.PluginContent;
import org.uberfire.plugin.model.PluginSimpleContent;
import org.uberfire.plugin.model.PluginType;
import org.uberfire.plugin.model.RuntimePlugin;
import org.uberfire.backend.vfs.Path;

@Remote
public interface PluginServices {

    String getMediaServletURI();

    Collection<RuntimePlugin> listRuntimePlugins();

    Collection<Plugin> listPlugins();

    Plugin createNewPlugin( final String name,
                            final PluginType type );

    void save( final PluginSimpleContent plugin );

    void delete( final Plugin plugin );

    PluginContent getPluginContent( final Path path );

    void deleteMedia( final Media media );

    DynamicMenu getDynamicMenuContent( final Path path );

    void save( final DynamicMenu menu );

    Collection<DynamicMenu> listDynamicMenus();
}
