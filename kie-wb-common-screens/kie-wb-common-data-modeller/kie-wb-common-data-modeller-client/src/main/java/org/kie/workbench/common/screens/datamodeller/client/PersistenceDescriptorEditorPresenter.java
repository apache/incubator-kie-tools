/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.type.PersistenceDescriptorType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.Page;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor( identifier = "PersistenceDescriptorEditor",
        supportedTypes = { PersistenceDescriptorType.class },
        priority = Integer.MAX_VALUE )
public class PersistenceDescriptorEditorPresenter
        extends KieEditor
        implements PersistenceDescriptorEditorView.Presenter {

    private PersistenceDescriptorEditorView view;

    @Inject
    private PersistenceDescriptorType type;

    @Inject
    private Caller<PersistenceDescriptorEditorService> editorService;

    @Inject
    private Caller<DataModelerService> dataModelerService;

    private boolean createIfNotExists = false;

    @Inject
    public PersistenceDescriptorEditorPresenter( PersistenceDescriptorEditorView baseView ) {
        super( baseView );
        this.view = baseView;
        baseView.setPresenter( this );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
            final PlaceRequest place ) {
        this.createIfNotExists = "true".equals( place.getParameter( "createIfNotExists", null ) );
        init( path, place, type );
    }

    @OnFocus
    public void onFocus() {
        //for some reason the tables needs to be repainted when the view is restored.
        view.redraw();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        if ( versionRecordManager.getCurrentPath() != null ) {
            super.getTitleText();
        } else {
            //TODO check why I'm getting this case and remove after checked
            return type.getDescription();
        }
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        makeMenuBar();
        return super.menus;
    }

    @Override
    protected void loadContent() {
        editorService.call( getLoadModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).loadContent(
                versionRecordManager.getCurrentPath(),
                createIfNotExists );

    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty( getContent().hashCode() ) ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void OnClose() {
        super.OnClose();
    }

    private RemoteCallback<PersistenceDescriptorEditorContent> getLoadModelSuccessCallback() {

        return new RemoteCallback<PersistenceDescriptorEditorContent>() {
            @Override
            public void callback( PersistenceDescriptorEditorContent content ) {

                if ( versionRecordManager.getCurrentPath() == null ) {
                    return;
                }

                resetEditorPages( content.getOverview() );
                addSourcePage();

                createOriginalHash( content );
                view.setContent( content, isReadOnly );
                updateSource( content.getSource() );

                view.hideBusyIndicator();
            }
        };
    }

    protected void addSourcePage() {

        addPage( new Page( view.getSourceEditor(),
                CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
                onSourceTabSelected();
            }

            @Override
            public void onLostFocus() {

            }

        });
    }

    protected void updateSource(String source) {
        view.setSource( source );
    }

    protected void save() {
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String commitMessage ) {
                        editorService.call( getSaveSuccessCallback( getContent().hashCode() ) ).save( versionRecordManager.getCurrentPath(),
                                getContent(),
                                metadata,
                                commitMessage );
                    }
                }
        );
        concurrentUpdateSessionInfo = null;
    }

    private PersistenceDescriptorEditorContent getContent() {
        return view.getContent();
    }

    //Presenter methods
    @Override
    public void onPersistenceUnitNameChanged( String persistenceUnitName ) {
        getContent().getDescriptorModel().getPersistenceUnit().setName( persistenceUnitName );
    }

    @Override
    public void onPersistenceProviderChanged( String provider ) {
        getContent().getDescriptorModel().getPersistenceUnit().setProvider( provider );
    }

    @Override
    public void onJTADataSourceChanged( String jtaDataSource ) {
        getContent().getDescriptorModel().getPersistenceUnit().setJtaDataSource( jtaDataSource );
    }

    @Override
    public void onLoadClasses() {
        view.showBusyIndicator( Constants.INSTANCE.persistence_descriptor_editor_loading_classes_message() );
        dataModelerService.call( getLoadClassesSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).findPersistableClasses( versionRecordManager.getCurrentPath() );
    }

    private RemoteCallback<List<String>> getLoadClassesSuccessCallback() {
        return new RemoteCallback<List<String>>() {
            @Override
            public void callback( List<String> classes ) {
                view.hideBusyIndicator();
                view.loadClasses( classes );
            }
        };
    }

}

