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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.version.events.RestoreEvent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.uberfire.client.common.Page;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.versionhistory.VersionRecordManager;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.widgets.viewsource.client.widget.ViewDRLSourceWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import static org.kie.uberfire.client.common.ConcurrentChangePopup.*;

public abstract class KieEditor {

    protected static final int EDITOR_TAB_INDEX = 0;

    protected static final int OVERVIEW_TAB_INDEX = 1;

    protected boolean isReadOnly;

    private KieEditorView baseView;

    protected ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    protected Menus menus;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private OverviewWidgetPresenter overviewWidget;

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected DefaultFileNameValidator fileNameValidator;

    @Inject
    protected Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    @New
    protected VersionRecordManager versionRecordManager;

    @Inject
    @New
    protected FileMenuBuilder menuBuilder;

    @Inject
    protected Event<NotificationEvent> notification;

    protected Metadata metadata;

    protected PlaceRequest place;
    private ClientResourceType type;
    private ViewDRLSourceWidget sourceWidget;

    protected KieEditor() {
    }

    protected KieEditor(
            KieEditorView baseView) {
        this.baseView = baseView;

    }

    private void showVersions() {
        selectOverviewTab();
        overviewWidget.showVersionsTab();
    }

    protected void init(ObservablePath path, PlaceRequest place, ClientResourceType type) {
        init( path, place, type, true );
    }

    protected void init(ObservablePath path, PlaceRequest place, ClientResourceType type, boolean addFileChangeListeners) {
        this.place = place;
        this.type = type;

        baseView.showLoading();

        this.isReadOnly = this.place.getParameter("readOnly", null) == null ? false : true;

        versionRecordManager.init(
                this.place.getParameter("version", null),
                path,
                new Callback<VersionRecord>() {
                    @Override
                    public void callback(VersionRecord versionRecord) {
                        selectVersion(versionRecord);
                    }
                });

        versionRecordManager.setShowMoreCommand(
                new Command() {
                    @Override
                    public void execute() {
                        showVersions();
                    }
                });

        if ( addFileChangeListeners ) {
            addFileChangeListeners(path);
        }

        makeMenuBar();

        loadContent();

    }

    private void selectVersion(VersionRecord versionRecord) {
        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());

        if (versionRecordManager.isLatest(versionRecord)) {
            isReadOnly = false;
        } else {
            isReadOnly = true;
        }

        versionRecordManager.setVersion(versionRecord.id());

        loadContent();
    }

    protected CommandDrivenErrorCallback getNoSuchFileExceptionErrorCallback() {
        return new CommandDrivenErrorCallback(baseView,
                new CommandBuilder().addNoSuchFileException(
                        baseView,
                        multiPage,
                        menus).build()
        );
    }

    protected void addSourcePage() {
        sourceWidget = new ViewDRLSourceWidget();
        addPage(
                new Page(sourceWidget,
                        CommonConstants.INSTANCE.SourceTabTitle()) {
                    @Override
                    public void onFocus() {
                        onSourceTabSelected();
                    }

                    @Override
                    public void onLostFocus() {

                    }
                });
    }

    protected void addPage(Page page) {
        multiPage.addPage(page);
    }

    protected void resetEditorPages(final Overview overview) {

        versionRecordManager.setVersions(overview.getMetadata().getVersion());        
        this.overviewWidget.setContent(overview, versionRecordManager.getPathToLatest(), versionRecordManager.getVersion());
        this.metadata = overview.getMetadata();

        multiPage.clear();

        addPage(
                new Page(baseView,
                        CommonConstants.INSTANCE.EditTabTitle()) {
                    @Override
                    public void onFocus() {
                        onEditTabSelected();
                    }

                    @Override
                    public void onLostFocus() {

                    }
                });

        addPage(
                new Page(this.overviewWidget,
                        CommonConstants.INSTANCE.Overview()) {
                    @Override
                    public void onFocus() {
                        overviewWidget.refresh(versionRecordManager.getVersion());
                        onOverviewSelected();
                    }

                    @Override
                    public void onLostFocus() {

                    }
                }
        );
    }

    protected void OnClose() {
        multiPage.clear();
    }

    protected void addImportsTab(IsWidget importsWidget) {
        multiPage.addWidget(importsWidget,
                CommonConstants.INSTANCE.ConfigTabTitle());

    }

    private void addFileChangeListeners(final ObservablePath path) {
        path.onRename(new Command() {
            @Override
            public void execute() {
                onRename();

            }
        });
        path.onDelete(new Command() {
            @Override
            public void execute() {
                onDelete();
            }
        });

        path.onConcurrentUpdate(new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentUpdateEvent eventInfo) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        });

        path.onConcurrentRename(new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentRenameEvent info) {
                newConcurrentRename(info.getSource(),
                        info.getTarget(),
                        info.getIdentity(),
                        new Command() {
                            @Override
                            public void execute() {
                                disableMenus();
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                reload();
                            }
                        }
                ).show();
            }
        });

        path.onConcurrentDelete(new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentDelete info) {
                newConcurrentDelete(info.getPath(),
                        info.getIdentity(),
                        new Command() {
                            @Override
                            public void execute() {
                                disableMenus();
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.closePlace(place);
                            }
                        }
                ).show();
            }
        });
    }

    private void onDelete() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override public void execute() {
                placeManager.forceClosePlace( place );
            }
        } );
    }

    /**
     * Effectively the same as reload() but don't reset concurrentUpdateSessionInfo
     */
    protected void onRename() {
        refreshTitle();
        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        loadContent();
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitleText(), getTitle() ) );
    }

    /**
     * Override this method and use @WorkbenchPartTitleDecoration
     *
     * @return The widget for the title
     */
    protected IsWidget getTitle() {
        refreshTitle();
        return baseView.getTitleWidget();
    }

    public String getTitleText() {
        return versionRecordManager.getCurrentPath().getFileName() + " - " + type.getDescription();
    }

    private void refreshTitle() {
        baseView.refreshTitle(versionRecordManager.getCurrentPath().getFileName(), type.getDescription());
    }

    protected void onSave() {

        if (isReadOnly && versionRecordManager.isCurrentLatest()) {
            baseView.alertReadOnly();
            return;
        } else if (isReadOnly && !versionRecordManager.isCurrentLatest()) {
            versionRecordManager.restoreToCurrentVersion();
            return;
        }

        if (concurrentUpdateSessionInfo != null) {
            showConcurrentUpdatePopup();
        } else {
            save();
        }
    }

    protected void showConcurrentUpdatePopup() {
        newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                concurrentUpdateSessionInfo.getIdentity(),
                new Command() {
                    @Override
                    public void execute() {
                        save();
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        //cancel?
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        reload();
                    }
                }
        ).show();
    }

    /**
     * If you want to customize the menu override this method.
     */
    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave( versionRecordManager.newSaveMenuItem(new Command() {
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

    protected RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                baseView.setNotDirty();
                baseView.hideBusyIndicator();
                versionRecordManager.reloadVersions(path);
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }

    public void onRestore(@Observes RestoreEvent restore) {
        if (versionRecordManager.getCurrentPath() == null || restore == null || restore.getPath() == null) {
            return;
        }
        if (versionRecordManager.getCurrentPath().equals(restore.getPath())) {
            //when a version is restored we don't want to add the concurrency listeners again -> false
            init(versionRecordManager.getPathToLatest(), place, type, false);
            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRestored()));
        }
    }

    public void reload() {
        concurrentUpdateSessionInfo = null;
        refreshTitle();
        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        loadContent();
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitleText(), getTitle() ) );
    }

    private void disableMenus() {
        disableMenuItem(FileMenuBuilder.MenuItems.COPY);
        disableMenuItem(FileMenuBuilder.MenuItems.RENAME);
        disableMenuItem(FileMenuBuilder.MenuItems.DELETE);
        disableMenuItem(FileMenuBuilder.MenuItems.VALIDATE);
    }

    private void disableMenuItem(FileMenuBuilder.MenuItems menuItem) {
        if (menus.getItemsMap().containsKey(menuItem)) {
            menus.getItemsMap().get(menuItem).setEnabled(false);
        }
    }

    protected boolean isEditorTabSelected() {
        return this.multiPage.selectedPage() == EDITOR_TAB_INDEX;
    }

    protected boolean isOverviewTabSelected() {
        return this.multiPage.selectedPage() == OVERVIEW_TAB_INDEX;
    }

    protected int getSelectedTabIndex() {
        return this.multiPage.selectedPage();
    }

    protected void selectOverviewTab() {
        setSelectedTab(OVERVIEW_TAB_INDEX);
    }

    protected void selectEditorTab() {
        setSelectedTab(EDITOR_TAB_INDEX);
    }

    protected void setSelectedTab(int tabIndex) {
        multiPage.selectPage(tabIndex);
    }

    protected void updateSource(String source) {
        sourceWidget.setContent(source);
    }

    public IsWidget getWidget() {
        return multiPage;
    }

    /**
     * If your editor has validation, overwrite this.
     *
     * @return The validation command
     */
    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                // Default is that nothing happens.
            }
        };
    }

    protected abstract void loadContent();

    /**
     * Needs to be overwritten for save to work
     */
    protected void save() {

    }

    protected void onSourceTabSelected(){};

    protected void onOverviewSelected(){};

    /**
     * Overwrite this if you want to do something special when the editor tab is selected.
     */
    protected void onEditTabSelected() {

    }

}

