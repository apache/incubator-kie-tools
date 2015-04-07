/*
* Copyright 2012 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.uberfire.ext.plugin.client.perspective.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.layout.editor.client.LayoutEditorPluginAPI;
import org.uberfire.ext.plugin.client.perspective.editor.components.popup.AddTag;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorGenerator;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.util.TagButton;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;

@Dependent
@WorkbenchEditor(identifier = "Perspective Editor", supportedTypes = { PerspectiveLayoutPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class PerspectiveEditorPresenter
        extends BaseEditor {

    private final View perspectiveEditorView;

    public interface View extends BaseEditorView {

        void setupLayoutEditor( Widget widget );

    }

    @Inject
    private PerspectiveEditorGenerator perspectiveEditorGenerator;

    @Inject
    private LayoutEditorPluginAPI layoutEditorPluginAPI;

    @Inject
    private Event<NotificationEvent> ufNotification;

    @Inject
    private PerspectiveLayoutPluginResourceType resourceType;

    private Plugin plugin;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private ScreenLayoutDragComponent screenLayoutDragComponent;

    @Inject
    private HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Inject
    public PerspectiveEditorPresenter( final View perspectiveEditorView ) {

        super( perspectiveEditorView );
        this.perspectiveEditorView = perspectiveEditorView;

    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init( path,
              place,
              resourceType,
              true,
              false,
              SAVE,
              COPY,
              RENAME,
              DELETE );

        // This is only used to define the "name" used by @WorkbenchPartTitle which is called by Uberfire after @OnStartup
        // but before the async call in "loadContent()" has returned. When the *real* plugin is loaded this is overwritten
        final String name = place.getParameter( "name",
                                                "" );
        plugin = new Plugin( name,
                             PluginType.PERSPECTIVE_LAYOUT,
                             path );

        this.layoutEditorPluginAPI.init( PluginType.PERSPECTIVE_LAYOUT, name, screenLayoutDragComponent, htmlLayoutDragComponent );

        this.perspectiveEditorView.setupLayoutEditor( layoutEditorPluginAPI.asWidget() );
    }

    @Override
    protected void makeMenuBar() {
        super.makeMenuBar();
        menuBuilder.addNewTopLevelMenu( new TagButton( new Command() {
            @Override
            public void execute() {
                AddTag addTag = new AddTag( PerspectiveEditorPresenter.this );
                addTag.show();
            }
        } ) );
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( layoutEditorPluginAPI.getCurrentModelHash() );
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return "Perspective Editor [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    protected void loadContent() {
        baseView.hideBusyIndicator();
        layoutEditorPluginAPI.load( PluginType.PERSPECTIVE_LAYOUT, versionRecordManager.getCurrentPath(), new ParameterizedCommand<LayoutEditorModel>() {
            @Override
            public void execute( LayoutEditorModel layoutEditorModel ) {
                setOriginalHash( layoutEditorPluginAPI.getCurrentModelHash() );
                plugin = layoutEditorModel;
            }
        } );
    }

    protected void save() {
        layoutEditorPluginAPI.save( versionRecordManager.getCurrentPath(),
                                    getSaveSuccessCallback( layoutEditorPluginAPI.getCurrentModelHash() ) );
        concurrentUpdateSessionInfo = null;
    }

    protected RemoteCallback<Path> getSaveSuccessCallback( final int newHash ) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                RemoteCallback<Path> saveSuccessCallback = PerspectiveEditorPresenter.super.getSaveSuccessCallback( layoutEditorPluginAPI.getCurrentModelHash() );
                saveSuccessCallback.callback( path );
                perspectiveEditorGenerator.generate( layoutEditorPluginAPI.getModel() );
            }
        };
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorPresenter> getWidget() {
        return (UberView<PerspectiveEditorPresenter>) super.baseView;
    }

    protected void onPlugInRenamed( @Observes final PluginRenamed pluginRenamed ) {
        if ( pluginRenamed.getOldPluginName().equals( plugin.getName() ) &&
                pluginRenamed.getPlugin().getType().equals( plugin.getType() ) ) {
            plugin = new Plugin( pluginRenamed.getPlugin().getName(),
                                 PluginType.PERSPECTIVE_LAYOUT,
                                 pluginRenamed.getPlugin().getPath() );
            changeTitleNotification.fire( new ChangeTitleWidgetEvent( place,
                                                                      getTitleText(),
                                                                      getTitle() ) );
        }
    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return pluginServices;
    }

    public void saveProperty( String key,
                              String value ) {
        layoutEditorPluginAPI.addLayoutProperty( key, value );
    }

    public String getLayoutProperty( String key ) {
        return layoutEditorPluginAPI.getLayoutProperty( key );
    }

}

