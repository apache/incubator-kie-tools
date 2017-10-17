/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

public abstract class KieEditor
        extends BaseEditor
        implements KieEditorWrapperView.KieEditorWrapperPresenter {

    @Inject
    protected KieEditorWrapperView kieView;

    @Inject
    protected OverviewWidgetPresenter overviewWidget;

    @Inject
    protected FileMenuBuilder fileMenuBuilder;

    @Inject
    protected ProjectContext workbenchContext;

    @Inject
    protected SavePopUpPresenter savePopUpPresenter;

    @Inject
    protected DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    protected RenamePopUpPresenter renamePopUpPresenter;

    @Inject
    protected CopyPopUpPresenter copyPopUpPresenter;

    @Inject
    protected ProjectController projectController;

    @Inject
    protected AssetUpdateValidator assetUpdateValidator;

    protected Metadata metadata;

    private ViewDRLSourceWidget sourceWidget;

    //The default implementation delegates to the HashCode comparison in BaseEditor
    private final MayCloseHandler DEFAULT_MAY_CLOSE_HANDLER = (object) -> {
        if (object != null) {
            return KieEditor.this.mayClose(object.hashCode());
        } else {
            return true;
        }
    };
    //This implementation always permits closure as something went wrong loading the Editor's content
    private final MayCloseHandler EXCEPTION_MAY_CLOSE_HANDLER = (object) -> true;

    private MayCloseHandler mayCloseHandler = DEFAULT_MAY_CLOSE_HANDLER;

    protected KieEditor() {
    }

    protected KieEditor(final KieEditorView baseView) {
        super(baseView);
    }

    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type) {
        this.init(path,
                  place,
                  type,
                  true);
    }

    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final boolean addFileChangeListeners) {
        this.init(path,
                  place,
                  type,
                  addFileChangeListeners,
                  true);
    }

    @Override
    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final MenuItems... menuItems) {
        this.init(path,
                  place,
                  type,
                  true,
                  false,
                  menuItems);
    }

    @Override
    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final boolean addFileChangeListeners,
                        final boolean displayShowMoreVersions,
                        final MenuItems... menuItems) {
        kieView.setPresenter(this);
        super.init(path,
                   place,
                   type,
                   addFileChangeListeners,
                   displayShowMoreVersions,
                   menuItems);
    }

    @Override
    protected void showVersions() {
        selectOverviewTab();
        overviewWidget.showVersionsTab();
    }

    protected void createOriginalHash(Object object) {
        if (object != null) {
            setOriginalHash(object.hashCode());
        }
    }

    @Override
    public void setOriginalHash(Integer originalHash) {
        super.setOriginalHash(originalHash);
        overviewWidget.resetDirty();
    }

    public boolean mayClose(Object object) {
        return mayCloseHandler.mayClose(object);
    }

    protected CommandDrivenErrorCallback getNoSuchFileExceptionErrorCallback() {
        return new CommandDrivenErrorCallback(baseView,
                                              new CommandBuilder()
                                                      .addNoSuchFileException(baseView,
                                                                              kieView.getMultiPage(),
                                                                              menus)
                                                      .addFileSystemNotFoundException(baseView,
                                                                                      kieView.getMultiPage(),
                                                                                      menus)
                                                      .build()
        ) {
            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                mayCloseHandler = EXCEPTION_MAY_CLOSE_HANDLER;
                return super.error(message,
                                   throwable);
            }
        };
    }

    protected CommandDrivenErrorCallback getCouldNotGenerateSourceErrorCallback() {
        return new CommandDrivenErrorCallback(baseView,
                                              new CommandBuilder()
                                                      .addSourceCodeGenerationFailedException(baseView,
                                                                                              sourceWidget)
                                                      .build()
        );
    }

    protected void addSourcePage() {
        sourceWidget = GWT.create(ViewDRLSourceWidget.class);
        kieView.addSourcePage(sourceWidget);
    }

    protected void addPage(Page page) {
        kieView.addPage(page);
    }

    protected void resetEditorPages(final Overview overview) {

        this.overviewWidget.setContent(overview,
                                       versionRecordManager.getPathToLatest());
        this.metadata = overview.getMetadata();

        kieView.clear();

        kieView.addMainEditorPage(baseView);

        kieView.addOverviewPage(overviewWidget,
                                () -> overviewWidget.refresh(versionRecordManager.getVersion()));
    }

    protected void OnClose() {
        kieView.clear();
    }

    protected void addImportsTab(IsWidget importsWidget) {
        kieView.addImportsTab(importsWidget);
    }

    /**
     * If you want to customize the menu override this method.
     */
    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator)
                    .addDelete(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator);
        }

        fileMenuBuilder
                .addValidate(onValidate())
                .addNewTopLevelMenu(versionRecordManager.buildMenu());
    }

    protected void saveAction() {
        assetUpdateValidator.validate(null,
                                      new ValidatorWithReasonCallback() {
                                          @Override
                                          public void onFailure(final String reason) {
                                              if (ValidationErrorReason.NOT_ALLOWED.name().equals(reason)) {
                                                  showError(kieView.getNotAllowedSavingMessage());
                                              } else {
                                                  showError(kieView.getUnexpectedErrorWhileSavingMessage());
                                              }
                                          }

                                          @Override
                                          public void onSuccess() {
                                              onSave();
                                          }

                                          @Override
                                          public void onFailure() {
                                              showError(kieView.getUnexpectedErrorWhileSavingMessage());
                                          }
                                      });
    }

    @Override
    protected void onSave() {
        super.onSave();
    }

    private void showError(final String error) {
        notification.fire(new NotificationEvent(error,
                                                NotificationEvent.NotificationType.ERROR));
    }

    protected boolean canUpdateProject() {
        final Project activeProject = workbenchContext.getActiveProject();
        return activeProject == null || projectController.canUpdateProject(activeProject);
    }

    @Override
    protected void buildMenuBar() {
        if (fileMenuBuilder != null) {
            menus = fileMenuBuilder.build();
        }
    }

    protected boolean isEditorTabSelected() {
        return kieView.isEditorTabSelected();
    }

    protected boolean isOverviewTabSelected() {
        return kieView.isOverviewTabSelected();
    }

    protected int getSelectedTabIndex() {
        return kieView.getSelectedTabIndex();
    }

    public void setSelectedTab(int index) {
        kieView.setSelectedTab(index);
    }

    protected void selectOverviewTab() {
        kieView.selectOverviewTab();
    }

    protected void selectEditorTab() {
        kieView.selectEditorTab();
    }

    protected void updateSource(String source) {
        sourceWidget.setContent(source);
    }

    public IsWidget getWidget() {
        return kieView.asWidget();
    }

    public void onRepositoryRemoved(final @Observes RepositoryRemovedEvent event) {
        if (event.getRepository() == null) {
            return;
        }
        if (workbenchContext == null) {
            return;
        }
        if (workbenchContext.getActiveRepository() == null) {
            return;
        }
        if (workbenchContext.getActiveRepository().equals(event.getRepository())) {
            for (MenuItem mi : menus.getItemsMap().values()) {
                mi.setEnabled(false);
            }
        }
    }

    @Override
    public boolean mayClose(Integer currentHash) {
        if (this.isDirty(currentHash) || overviewWidget.isDirty()) {
            return this.baseView.confirmClose();
        } else {
            return true;
        }
    }

    @Override
    protected void save() {
        savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                (commitMessage) -> {
                                    baseView.showSaving();
                                    save(commitMessage);
                                    concurrentUpdateSessionInfo = null;
                                });
        concurrentUpdateSessionInfo = null;
    }

    protected void save(final String commitMessage) {

    }

    @Override
    public void onOverviewSelected() {
    }

    @Override
    public void onSourceTabSelected() {
    }

    /**
     * Overwrite this if you want to do something special when the editor tab is selected.
     */
    @Override
    public void onEditTabSelected() {
    }

    @Override
    public void onEditTabUnselected() {
    }

    //Handler for MayClose requests
    private interface MayCloseHandler {

        boolean mayClose(final Object object);
    }
}
