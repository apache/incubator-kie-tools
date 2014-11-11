package org.uberfire.ext.plugin.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
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
