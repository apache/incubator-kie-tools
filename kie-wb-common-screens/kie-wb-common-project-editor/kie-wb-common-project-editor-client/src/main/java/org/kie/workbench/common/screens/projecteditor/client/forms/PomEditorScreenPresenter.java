/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.client.type.POMResourceType;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorView;
import org.kie.workbench.common.screens.projecteditor.service.PomEditorService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "pomScreen", supportedTypes = { POMResourceType.class }, priority = 2)
public class PomEditorScreenPresenter
        extends KieTextEditorPresenter {

    private Caller<PomEditorService> pomEditorService;
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Inject
    public PomEditorScreenPresenter( final KieTextEditorView baseView,
                                     final Caller<PomEditorService> pomEditorService,
                                     final ConflictingRepositoriesPopup conflictingRepositoriesPopup ) {
        super( baseView );
        this.pomEditorService = pomEditorService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.onStartup( path,
                         place );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.XML;
    }

    @Override
    protected void save( final String commitMessage ) {
        doSave( commitMessage,
                DeploymentMode.VALIDATED );
    }

    private void doSave( final String commitMessage,
                         final DeploymentMode mode ) {
        //Instantiate a new instance on each "save" operation to pass in commit message
        final Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onSaveGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put( GAVAlreadyExistsException.class,
                 new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                     @Override
                     public void execute( final Throwable parameter ) {
                         view.hideBusyIndicator();
                         final GAVAlreadyExistsException e = (GAVAlreadyExistsException) parameter;
                         conflictingRepositoriesPopup.setContent( e.getGAV(),
                                                                  e.getRepositories(),
                                                                  new Command() {
                                                                      @Override
                                                                      public void execute() {
                                                                          conflictingRepositoriesPopup.hide();
                                                                          doSave( commitMessage,
                                                                                  DeploymentMode.FORCED );
                                                                      }
                                                                  } );
                         conflictingRepositoriesPopup.show();
                     }
                 } );
        }};

        view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        pomEditorService.call( getSaveSuccessCallback( view.getContent().hashCode() ),
                               new CommandWithThrowableDrivenErrorCallback( busyIndicatorView,
                                                                            onSaveGavExistsHandler ) ).save( versionRecordManager.getCurrentPath(),
                                                                                                             view.getContent(),
                                                                                                             metadata,
                                                                                                             commitMessage,
                                                                                                             mode );
    }

}
