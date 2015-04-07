package org.uberfire.ext.plugin.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.plugin.model.DynamicMenu;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;

@Remote
public interface PluginServices
        extends SupportsDelete,
                SupportsCopy,
                SupportsRename {

    String getMediaServletURI();

    Collection<RuntimePlugin> listRuntimePlugins();

    Collection<Plugin> listPlugins();

    Plugin createNewPlugin( final String name,
                            final PluginType type );

    PluginContent getPluginContent( final Path path );

    void deleteMedia( final Media media );

    DynamicMenu getDynamicMenuContent( final Path path );

    Path save( final PluginSimpleContent plugin,
               final String commitMessage );

    LayoutEditorModel getLayoutEditor( Path path,
                                       PluginType pluginType );

    Path saveMenu( final DynamicMenu menu,
                   final String commitMessage );



    Path saveLayout( LayoutEditorModel layoutContent,
                     String commitMessage );

    Collection<DynamicMenu> listDynamicMenus();

    Collection<LayoutEditorModel> listLayoutEditor( PluginType pluginType );
}
