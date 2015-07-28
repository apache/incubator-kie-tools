/*
 * Copyright 2014 JBoss Inc
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

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public abstract class KieEditor
        extends BaseEditor
        implements KieEditorWrapperView.KieEditorWrapperPresenter {

    protected Menus menus;

    @Inject
    protected KieEditorWrapperView kieView;

    @Inject
    protected OverviewWidgetPresenter overviewWidget;

    @Inject
    @New
    protected FileMenuBuilder menuBuilder;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected ProjectContext workbenchContext;

    protected SaveOperationService saveOperationService = new SaveOperationService();

    protected Metadata metadata;

    private ViewDRLSourceWidget sourceWidget;

    //The default implementation delegates to the HashCode comparison in BaseEditor
    private final MayCloseHandler DEFAULT_MAY_CLOSE_HANDLER   = new MayCloseHandler() {

        @Override
        public boolean mayClose(final Object object) {
            if (object != null) {
                return KieEditor.this.mayClose(object.hashCode());
            } else {
                return true;
            }
        }

    };
    //This implementation always permits closure as something went wrong loading the Editor's content
    private final MayCloseHandler EXCEPTION_MAY_CLOSE_HANDLER = new MayCloseHandler() {
        @Override
        public boolean mayClose(final Object object) {
            return true;
        }
    };

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
        this.init(path, place, type, true, false, menuItems);
    }

    @Override
    protected void init(final ObservablePath path,
                        final PlaceRequest place,
                        final ClientResourceType type,
                        final boolean addFileChangeListeners,
                        final boolean displayShowMoreVersions,
                        final MenuItems... menuItems) {
        kieView.setPresenter(this);
        super.init(path, place, type, addFileChangeListeners, displayShowMoreVersions, menuItems);
    }

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
        sourceWidget = new ViewDRLSourceWidget();
        kieView.addSourcePage(sourceWidget);
    }

    protected void addPage(Page page) {
        kieView.addPage(page);
    }

    protected void resetEditorPages(final Overview overview) {

        this.overviewWidget.setContent(overview, versionRecordManager.getPathToLatest());
        this.metadata = overview.getMetadata();

        kieView.clear();

        kieView.addMainEditorPage(baseView);

        kieView.addOverviewPage(overviewWidget,
                                new com.google.gwt.user.client.Command() {
                                    @Override public void execute() {
                                        overviewWidget.refresh(versionRecordManager.getVersion());
                                    }
                                });

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
    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                }))
                .addCopy(versionRecordManager.getCurrentPath(),
                         fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                           fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addValidate(onValidate())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
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
        saveOperationService.save(versionRecordManager.getCurrentPath(),
                                  new ParameterizedCommand<String>() {
                                      @Override
                                      public void execute(final String commitMessage) {
                                          baseView.showSaving();
                                          save(commitMessage);
                                          concurrentUpdateSessionInfo = null;
                                      }
                                  });
        concurrentUpdateSessionInfo = null;
    }

    protected void save(final String commitMessage) {

    }

    public void onOverviewSelected() {
    }

    public void onSourceTabSelected() {
    }

    /**
     * Overwrite this if you want to do something special when the editor tab is selected.
     */
    public void onEditTabSelected() {
    }

    public void onEditTabUnselected() {
    }

    //Handler for MayClose requests
    private interface MayCloseHandler {

        boolean mayClose(final Object object);

    }

}
