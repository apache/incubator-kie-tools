package org.uberfire.ext.plugin.client.editor;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.enterprise.event.Observes;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;


public abstract class RuntimePluginBaseEditor extends BaseEditor {

    protected Plugin plugin;

    protected RuntimePluginBaseEditor( final BaseEditorView baseView ) {
        this.baseView = baseView;
    }

    protected abstract PluginType getPluginType();

    protected abstract ClientResourceType getResourceType();

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init( path, place, getResourceType(), true, false, SAVE, COPY, RENAME, DELETE );
        this.plugin = new Plugin( place.getParameter( "name", "" ), getPluginType(), path );
        this.place = place;
    }

    protected void onPlugInRenamed( @Observes final PluginRenamed pluginRenamed ) {
        if ( pluginRenamed.getOldPluginName().equals( plugin.getName() ) &&
                pluginRenamed.getPlugin().getType().equals( plugin.getType() ) ) {
            this.plugin = new Plugin( pluginRenamed.getPlugin().getName(), getPluginType(), pluginRenamed.getPlugin().getPath() );
            changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitleText(), getTitle() ) );
        }
    }


}
