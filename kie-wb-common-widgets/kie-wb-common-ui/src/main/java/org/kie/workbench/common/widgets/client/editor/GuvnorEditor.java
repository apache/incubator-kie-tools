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

package org.kie.workbench.common.widgets.client.editor;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.guvnor.common.services.shared.version.events.RestoreEvent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.discussion.VersionRecordManager;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.uberfire.client.common.ConcurrentChangePopup.*;

public abstract class GuvnorEditor {

    protected boolean isReadOnly;

    private GuvnorEditorView baseView;

    protected ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    protected Menus menus;

    @Inject
    private PlaceManager placeManager;

    @Inject protected DefaultFileNameValidator fileNameValidator;

    @Inject
    protected Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    @New protected VersionRecordManager versionRecordManager;

    @Inject
    @New protected FileMenuBuilder menuBuilder;

    @Inject
    protected Event<NotificationEvent> notification;

    private PlaceRequest place;

    protected GuvnorEditor() {
    }

    protected GuvnorEditor(
            GuvnorEditorView baseView) {
        this.baseView = baseView;
    }

    protected void init(ObservablePath path, PlaceRequest place) {
        this.place = place;

        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());

        this.isReadOnly = this.place.getParameter("readOnly", null) == null ? false : true;
        versionRecordManager.setVersion(this.place.getParameter("version", null));
        versionRecordManager.setPathToLatest(path);
        addFileChangeListeners(path);

        versionRecordManager.addVersionSelectionCallback(
                new Callback<VersionRecord>() {
                    @Override
                    public void callback(VersionRecord versionRecord) {

                        baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());

                        if (versionRecordManager.isLatest(versionRecord)) {
                            isReadOnly = false;
                            versionRecordManager.setVersion(null);
                        } else {
                            isReadOnly = true;
                            versionRecordManager.setVersion(versionRecord.id());
                        }

                        loadContent();
                    }
                });

        makeMenuBar();

        loadContent();

    }

    private void addFileChangeListeners(final ObservablePath path) {
        path.onRename(new Command() {
            @Override
            public void execute() {
                //Effectively the same as reload() but don't reset concurrentUpdateSessionInfo
                changeTitleNotification.fire(new ChangeTitleWidgetEvent(place, getTitle(), null));
                baseView.showBusyIndicator(CommonConstants.INSTANCE.Loading());
                loadContent();
            }
        });
        path.onDelete(new Command() {
            @Override
            public void execute() {
                placeManager.forceClosePlace(place);
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

    protected void onSave() {

        if (isReadOnly && versionRecordManager.getVersion() == null) {
            baseView.alertReadOnly();
            return;
        } else if (isReadOnly && versionRecordManager.getVersion() != null) {
            versionRecordManager.restoreToCurrentVersion();
            return;
        }

        if (concurrentUpdateSessionInfo != null) {
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
        } else {
            save();
        }
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                })
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
        if (versionRecordManager.getPathToLatest().equals(restore.getPath())) {
            init(restore.getPath(), place);
            loadContent();
            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRestored()));
        }
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        changeTitleNotification.fire(new ChangeTitleWidgetEvent(place, getTitle(), null));
        loadContent();
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

    protected abstract Command onValidate();

    protected abstract String getTitle();

    protected abstract void loadContent();

    protected abstract void save();

}

