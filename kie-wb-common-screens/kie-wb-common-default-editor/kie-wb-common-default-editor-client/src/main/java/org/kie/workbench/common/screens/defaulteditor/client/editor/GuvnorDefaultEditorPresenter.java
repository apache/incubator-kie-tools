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

package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.uberfire.client.common.Page;
import org.kie.uberfire.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A text based editor for Domain Specific Language definitions
 */
@Dependent
@WorkbenchEditor(identifier = "GuvnorDefaultFileEditor", supportedTypes = { AnyResourceType.class }, priority = -1)
public class GuvnorDefaultEditorPresenter
        extends DefaultFileEditorPresenter {

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private Caller<DefaultEditorService> defaultEditorService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    @Inject
    private MetadataWidget metadataWidget;

    private boolean isReadOnly;
    private String version;
    private Path path;

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.onStartup( path );

        this.path = path;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.version = place.getParameter( "version", null );

        makeMenuBar();

        multiPage.addWidget( super.getWidget(),
                             CommonConstants.INSTANCE.EditTabTitle() );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                    metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                    metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                       isReadOnly ),
                                          new HasBusyIndicatorDefaultErrorCallback( metadataWidget )
                                        ).getMetadata( path );
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addCopy( path,
                              fileNameValidator )
                    .addRename( path,
                                fileNameValidator )
                    .addDelete( path )
                    .build();
        }
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @OnClose
    public void onClose() {
        super.onClose();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = path.getFileName();
        if ( version != null ) {
            fileName = fileName + " v" + version;
        }
        return "Default Editor [" + fileName + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

}
