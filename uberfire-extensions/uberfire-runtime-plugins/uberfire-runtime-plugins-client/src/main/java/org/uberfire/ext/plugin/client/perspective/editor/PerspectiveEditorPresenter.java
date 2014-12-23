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

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.plugin.client.perspective.editor.api.ExternalPerspectiveEditorComponent;
import org.uberfire.ext.plugin.client.perspective.editor.components.popup.AddTag;
import org.uberfire.ext.plugin.client.perspective.editor.dnd.DragGridElement;
import org.uberfire.ext.plugin.client.perspective.editor.structure.PerspectiveEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.util.DragType;
import org.uberfire.ext.plugin.client.perspective.editor.util.TagButton;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.model.PerspectiveEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
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

        void setupDndMenu( AccordionGroup... accordionsGroup );

        void createDefaultPerspective( String name );

        PerspectiveEditor getModel();

        void loadPerspective( PerspectiveEditor perspectiveEditorJSON );
    }

    @Inject
    private Event<NotificationEvent> ufNotification;

    @Inject
    private PerspectiveLayoutPluginResourceType resourceType;

    @Inject
    private PerspectiveEditorPresenterHelper helper;

    private Plugin plugin;

    @Inject
    private PerspectiveEditorUI perspectiveEditor;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    public PerspectiveEditorPresenter( final View perspectiveEditorView ) {

        super( perspectiveEditorView );
        this.perspectiveEditorView = perspectiveEditorView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        init( path, place, resourceType, true, false, SAVE, COPY, RENAME, DELETE );
        this.plugin = new Plugin( place.getParameter( "name", "" ), PluginType.DYNAMIC_MENU, path );
        setupDndWidget();
        setupPerspectiveBuilder( place.getParameter( "name", "" ));
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
        } ));
    }

    private void setupPerspectiveBuilder( String name ) {
        this.perspectiveEditorView.createDefaultPerspective(name);
    }

    private void setupDndWidget() {
        AccordionGroup gridSystem = generateGridSystem();
        AccordionGroup components = generateComponent();
        perspectiveEditorView.setupDndMenu( gridSystem, components );
    }

    private AccordionGroup generateGridSystem() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Grid System" );
        accordion.setIcon( IconType.TH );
        accordion.setDefaultOpen( true );
        accordion.add( new DragGridElement( DragType.GRID, "12", ufNotification ) );
        accordion.add( new DragGridElement( DragType.GRID, "6 6", ufNotification ) );
        accordion.add( new DragGridElement( DragType.GRID, "4 4 4", ufNotification ) );
        return accordion;
    }

    private AccordionGroup generateComponent() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Components" );
        accordion.setIcon( IconType.FOLDER_OPEN );
        accordion.add( new DragGridElement( DragType.SCREEN, DragType.SCREEN.label(), ufNotification ) );
        accordion.add( new DragGridElement( DragType.HTML, DragType.HTML.label(), ufNotification ) );
        generateExternalComponents( accordion );
        return accordion;
    }

    private void generateExternalComponents( AccordionGroup accordion ) {
        for ( ExternalPerspectiveEditorComponent externalPerspectiveEditorComponent : helper.lookupExternalComponents() ) {
            accordion.add( new DragGridElement( DragType.EXTERNAL, externalPerspectiveEditorComponent.getPlaceName(), externalPerspectiveEditorComponent ) );
        }
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
        pluginServices.call( new RemoteCallback<PerspectiveEditorModel>() {
            @Override
            public void callback( final PerspectiveEditorModel response ) {
                if ( response.getPerspectiveModel() != null ) {
                    perspectiveEditorView.loadPerspective( response.getPerspectiveModel() );
                }
            }
        } ).getPerspectiveEditor( versionRecordManager.getCurrentPath() );
    }

    protected void save() {
        pluginServices.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( getContent() );
    }

    public PerspectiveEditorModel getContent() {
        return new PerspectiveEditorModel( plugin.getName(), PluginType.PERSPECTIVE_LAYOUT, versionRecordManager.getCurrentPath(), perspectiveEditorView.getModel() );
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorPresenter> getWidget() {
        return (UberView<PerspectiveEditorPresenter>) super.baseView;
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


    public List<String> getTags() {
        return perspectiveEditorView.getModel().getTags();
    }

}

