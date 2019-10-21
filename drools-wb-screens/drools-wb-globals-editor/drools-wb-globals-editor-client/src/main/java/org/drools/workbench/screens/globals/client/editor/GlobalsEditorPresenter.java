/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.globals.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.workbench.screens.globals.client.type.GlobalResourceType;
import org.drools.workbench.screens.globals.model.GlobalsEditorContent;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.CommandErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = GlobalsEditorPresenter.EDITOR_ID, supportedTypes = {GlobalResourceType.class}, priority = 101)
public class GlobalsEditorPresenter
        extends KieEditor<GlobalsModel> {

    public static final String EDITOR_ID = "org.kie.guvnor.globals";

    @Inject
    protected Caller<GlobalsEditorService> globalsEditorService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected ValidationPopup validationPopup;

    private GlobalsEditorView view;

    @Inject
    private GlobalResourceType type;

    private GlobalsModel model;

    public GlobalsEditorPresenter() {
    }
    
    @Inject
    public GlobalsEditorPresenter(final GlobalsEditorView baseView) {
        super(baseView);
        this.view = baseView;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    final ParameterizedCommand<Boolean> onSave = withComments -> {
                        saveWithComments = withComments;
                        saveAction();
                    };
                    fileMenuBuilder
                            .addSave(versionRecordManager.newSaveMenuItem(onSave))
                            .addCopy(versionRecordManager.getCurrentPath(),
                                     assetUpdateValidator)
                            .addRename(getSaveAndRename())
                            .addDelete(this::onDelete);
                }

                addDownloadMenuItem(fileMenuBuilder);

                fileMenuBuilder
                        .addValidate(getValidateCommand())
                        .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());

                return promises.resolve();
            });
        }

        return promises.resolve();
    }

    protected void loadContent() {
        view.showLoading();
        globalsEditorService.call(getModelSuccessCallback(),
                                  getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Supplier<GlobalsModel> getContentSupplier() {
        return () -> model;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<GlobalsModel, Metadata>> getSaveAndRenameServiceCaller() {
        return globalsEditorService;
    }

    protected RemoteCallback<GlobalsEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GlobalsEditorContent>() {

            @Override
            public void callback(final GlobalsEditorContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                model = content.getModel();

                resetEditorPages(content.getOverview());
                addSourcePage();

                final List<String> fullyQualifiedClassNames = content.getFullyQualifiedClassNames();

                view.setContent(content.getModel().getGlobals(),
                                fullyQualifiedClassNames,
                                isReadOnly,
                                content.getOverview().getMetadata().isGenerated());

                createOriginalHash(model);
                view.hideBusyIndicator();
            }
        };
    }

    @Override
    protected void onValidate(final Command finished) {
        globalsEditorService.call(
                validationPopup.getValidationCallback(finished),
                new CommandErrorCallback(finished)).validate(versionRecordManager.getCurrentPath(),
                                                             model);
    }

    @Override
    protected void save() {
        ParameterizedCommand<String> doSave = (commitMessage) -> {
            baseView.showSaving();
            globalsEditorService
                .call(getSaveSuccessCallback(model.hashCode()),
                      new HasBusyIndicatorDefaultErrorCallback(view))
                .save(versionRecordManager.getCurrentPath(),
                      model,
                      metadata,
                      commitMessage);
            concurrentUpdateSessionInfo = null;
        };

        Command showPopUp = () -> {
            savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                    doSave);
        };

        Command command = () -> {
            if (saveWithComments) {
                showPopUp.execute();
            } else {
                doSave.execute("");
            }
        };

        validationService.call((obj) -> {
            List<ValidationMessage> validationMessages = (List<ValidationMessage>) obj;
            if (validationMessages.isEmpty()) {
                command.execute();
            } else {
                validationPopup.showSaveValidationMessages(command,
                                                           () -> {},
                                                           validationMessages);
            }
        }).validateForSave(versionRecordManager.getCurrentPath(),
                           model);

        concurrentUpdateSessionInfo = null;
    }

    protected void onDelete() {
        validationService.call((validationMessages) -> {
            if (((List<ValidationMessage>) validationMessages).isEmpty()) {
                showDeletePopup(getVersionRecordManager().getCurrentPath());
            } else {
                validationPopup.showDeleteValidationMessages(() -> showDeletePopup(versionRecordManager.getCurrentPath()),
                                                             () -> {
                                                             },
                                                             (List<ValidationMessage>) validationMessages);
            }
        }).validateForDelete(versionRecordManager.getCurrentPath());
    }

    private void showDeletePopup(final Path path) {
        deletePopUpPresenter.show(assetUpdateValidator,
                                  comment -> {
                                      view.showBusyIndicator(CommonConstants.INSTANCE.Deleting());
                                      globalsEditorService.call(getDeleteSuccessCallback(),
                                                                new HasBusyIndicatorDefaultErrorCallback(view)).delete(path,
                                                                                                                       "delete");
                                  });
    }

    private RemoteCallback<Path> getDeleteSuccessCallback() {
        return response -> {
            view.hideBusyIndicator();
            notification.fire(new NotificationEvent(org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
        };
    }

    @Override
    public void onSourceTabSelected() {
        globalsEditorService.call(new RemoteCallback<String>() {
            @Override
            public void callback(String source) {
                updateSource(source);
            }
        }).toSource(versionRecordManager.getCurrentPath(),
                    model);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        super.onClose();
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }
}
