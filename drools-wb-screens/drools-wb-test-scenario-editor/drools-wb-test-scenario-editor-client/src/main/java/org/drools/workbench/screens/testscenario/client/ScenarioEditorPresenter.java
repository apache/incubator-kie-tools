/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import javax.enterprise.inject.New;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "ScenarioEditorPresenter", supportedTypes = { TestScenarioResourceType.class })
public class ScenarioEditorPresenter {

    private final FileMenuBuilder menuBuilder;
    private final ScenarioEditorView view;
    private Menus menus;
    protected PackageDataModelOracle dmo;
    private final Caller<ScenarioTestEditorService> service;
    private boolean isReadOnly;

    private Path path;

    private Scenario scenario;

    @Inject
    public ScenarioEditorPresenter( final @New ScenarioEditorView view,
                                    final @New FileMenuBuilder menuBuilder,
                                    final Caller<ScenarioTestEditorService> service ) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.service = service;
    }

    @OnStart
    public void onStart( final Path path,
                         final PlaceRequest place ) {

        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;
        this.path = path;

        if ( !isReadOnly ) {
            view.addMetaDataPage( path, isReadOnly );
        }
        
        view.addBulkRunTestScenarioPanel( path, isReadOnly );        


        service.call( new RemoteCallback<TestScenarioModelContent>() {
            @Override
            public void callback( TestScenarioModelContent modelContent ) {
                scenario = modelContent.getScenario();

                dmo = modelContent.getOracle();
                dmo.filter( scenario.getImports() );

                view.setScenario( modelContent.getPackageName(), scenario, dmo );

                ifFixturesSizeZeroThenAddExecutionTrace();

                if ( !isReadOnly ) {
                    view.addTestRunnerWidget( scenario, service, path );
                }

                view.renderEditor();

                view.initImportsTab( dmo, scenario.getImports(), isReadOnly );
            }
        } ).loadContent( path );

        makeMenuBar();
    }

    private void onSave() {
        if ( isReadOnly ) {
            view.showCanNotSaveReadOnly();
        } else {
            new SaveOperationService().save( path,
                                             new CommandWithCommitMessage() {
                                                 @Override
                                                 public void execute( final String commitMessage ) {
                                                     view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                     service.call( getSaveSuccessCallback(),
                                                                   new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                                                                            scenario,
                                                                                                                            view.getMetadata(),
                                                                                                                            commitMessage );
                                                 }
                                             } );
        }
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                view.hideBusyIndicator();
                view.resetMetadataDirty();
                view.showSaveSuccessful();
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return view.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addCopy( path )
                    .addRename( path )
                    .addDelete( path )
                    .build();
        }
    }

    private void ifFixturesSizeZeroThenAddExecutionTrace() {
        if ( scenario.getFixtures().size() == 0 ) {
            scenario.getFixtures().add( new ExecutionTrace() );
        }
    }
}
