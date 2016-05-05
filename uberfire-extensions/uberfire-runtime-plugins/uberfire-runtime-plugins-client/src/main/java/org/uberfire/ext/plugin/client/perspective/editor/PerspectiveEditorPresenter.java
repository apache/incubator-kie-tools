/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
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
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.layout.editor.client.LayoutEditorPlugin;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.components.popup.AddTag;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorGenerator;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
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
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
@WorkbenchEditor(identifier = "Perspective Editor", supportedTypes = { PerspectiveLayoutPluginResourceType.class }, priority = Integer.MAX_VALUE)
public class PerspectiveEditorPresenter extends BaseEditor {

    private final View perspectiveEditorView;

    public interface View extends BaseEditorView {

        void setupLayoutEditor( Widget widget );

    }

    @Inject
    private PerspectiveEditorGenerator perspectiveEditorGenerator;

    @Inject
    private LayoutEditorPlugin layoutEditorPlugin;

    @Inject
    private Event<NotificationEvent> ufNotification;

    @Inject
    private PerspectiveLayoutPluginResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PluginNameValidator pluginNameValidator;

    private Plugin plugin;

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
        final String name = place.getParameter( "name", "" );
        plugin = new Plugin( name, PluginType.PERSPECTIVE_LAYOUT, path );
        this.layoutEditorPlugin.init( name, lookupPerspectiveDragComponents() );
        this.perspectiveEditorView.setupLayoutEditor( layoutEditorPlugin.asWidget() );
    }

    protected List<LayoutDragComponent> lookupPerspectiveDragComponents() {
        List<LayoutDragComponent> result = new ArrayList<LayoutDragComponent>();
        Collection<SyncBeanDef<PerspectiveEditorDragComponent>> beanDefs = IOC.getBeanManager().lookupBeans( PerspectiveEditorDragComponent.class );
        for ( SyncBeanDef<PerspectiveEditorDragComponent> beanDef : beanDefs ) {
            PerspectiveEditorDragComponent dragComponent = beanDef.getInstance();
            result.add( dragComponent );
        }
        return result;
    }

    @Override
    protected void makeMenuBar() {
        super.makeMenuBar();

        menuBuilder.addNewTopLevelMenu( MenuFactory.newTopLevelMenu( CommonConstants.INSTANCE.Tags() )
                                                .respondsWith( new Command() {
                                                    @Override
                                                    public void execute() {
                                                        AddTag addTag = new AddTag( PerspectiveEditorPresenter.this );
                                                        addTag.show();
                                                    }
                                                } )
                                                .endMenu()
                                                .build().getItems().get( 0 ) );
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getCurrentModelHash() );
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return "Perspective Editor [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorPresenter> getWidget() {
        return (UberView<PerspectiveEditorPresenter>) super.baseView;
    }

    @Override
    protected void loadContent() {
        baseView.hideBusyIndicator();
        layoutEditorPlugin.load( PluginType.PERSPECTIVE_LAYOUT, versionRecordManager.getCurrentPath(), new ParameterizedCommand<LayoutEditorModel>() {
            @Override
            public void execute( LayoutEditorModel layoutEditorModel ) {
                setOriginalHash( getCurrentModelHash() );
                plugin = layoutEditorModel;
            }
        } );
    }

    @Override
    protected void save() {
        layoutEditorPlugin.save( versionRecordManager.getCurrentPath(),
                                 getSaveSuccessCallback( getCurrentModelHash() ) );
        concurrentUpdateSessionInfo = null;
    }

    @Override
    protected RemoteCallback<Path> getSaveSuccessCallback( final int newHash ) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                RemoteCallback<Path> saveSuccessCallback = PerspectiveEditorPresenter.super.getSaveSuccessCallback( getCurrentModelHash() );
                saveSuccessCallback.callback( path );
                perspectiveEditorGenerator.generate( layoutEditorPlugin.getLayout() );
            }
        };
    }

    public int getCurrentModelHash() {
        return layoutEditorPlugin.getLayout().hashCode();
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

    @Override
    public Validator getRenameValidator() {
        return pluginNameValidator;
    }

    @Override
    public Validator getCopyValidator() {
        return pluginNameValidator;
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return pluginServices;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return pluginServices;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return pluginServices;
    }

    public void saveProperty( String key,
                              String value ) {
        layoutEditorPlugin.addLayoutProperty( key, value );
    }

    public String getLayoutProperty( String key ) {
        return layoutEditorPlugin.getLayoutProperty( key );
    }
}

