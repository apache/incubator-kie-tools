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

package org.kie.workbench.common.widgets.client.versionhistory;

import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import org.guvnor.common.services.shared.version.VersionService;
import org.guvnor.common.services.shared.version.events.RestoreEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SavePopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.kie.workbench.common.widgets.client.versionhistory.event.VersionSelectedEvent;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

public class VersionRecordManager {

    private DropdownButton button = new DropdownButton(ToolsMenuConstants.INSTANCE.LatestVersion());

    @Inject
    @New
    private FileMenuBuilder menuBuilder;

    @Inject
    private Caller<VersionService> versionService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Event<RestoreEvent> restoreEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<VersionSelectedEvent> versionSelectedEvent;

    private Callback<VersionRecord> selectionCallback;
    private List<VersionRecord> versions;
    private ObservablePath pathToLatest;
    private String version;

    public VersionRecordManager() {
        button.setRightDropdown(true);
        button.getTriggerWidget().addStyleName("btn-mini");
    }

    public MenuItem buildMenu() {
        return new VersionMenuItem(button);
    }

    public void setVersions(List<VersionRecord> versions) {

        button.clear();

        if (version == null) {
            version = versions.get(versions.size() - 1).id();
        }

        resolveVersions(versions);

        fillMenu();
    }

    private void fillMenu() {

        button.setText(ToolsMenuConstants.INSTANCE.LatestVersion());

        fillVersions();

    }

    private void fillVersions() {
        int versionIndex = 1;
        boolean currentHasBeenAdded = false;

        for (final VersionRecord versionRecord : versions) {

            boolean isSelected = isSelected(versionRecord);

            if (isSelected) {
                currentHasBeenAdded = true;
            }

            if (versionIndex < 7 || versions.size() <= 7) {

                addVersionMenuItemLabel(versionIndex, isSelected, versionRecord);
                changeMenuLabelIfNotLatest(versionIndex, versionRecord);

            } else {

                if (!currentHasBeenAdded) {
                    addVersionMenuItemLabel(getCurrentVersionIndex(), true, getCurrentVersionRecord());
                }

                addShowMoreLabel(versionIndex);

                break;

            }

            versionIndex++;
        }
    }

    private int getCurrentVersionIndex() {
        for (int i = 0; i < versions.size(); i++) {
            if (versions.get(i).id().equals(version)) {
                return i + 1;
            }
        }
        return -1;
    }

    private void addVersionMenuItemLabel(int versionIndex, boolean isSelected, VersionRecord versionRecord) {
        VersionMenuItemLabel widget = new VersionMenuItemLabel(
                versionRecord,
                versionIndex,
                isSelected,
                getSelectionCommand(versionRecord));
        widget.setWidth("400px");
        button.add(
                widget);
    }

    private void addShowMoreLabel(int versionIndex) {
        button.add(
                new ViewAllLabel(
                        versions.size() - versionIndex,
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo(
                                        new VersionHistoryScreenPlace(
                                                getPathToLatest(),
                                                getPathToLatest().getFileName(),
                                                getCurrentVersionRecord().id()));
                            }
                        }));
    }

    private boolean isSelected(VersionRecord versionRecord) {
        return versionRecord.id().equals(version);
    }

    private void changeMenuLabelIfNotLatest(int versionIndex, VersionRecord versionRecord) {
        if (versionRecord.id().equals(version) && versionIndex != versions.size()) {
            button.setText(ToolsMenuConstants.INSTANCE.Version(versionIndex));
        }
    }

    private Command getSelectionCommand(final VersionRecord versionRecord) {
        return new Command() {
            @Override
            public void execute() {
                versionSelectedEvent.fire(new VersionSelectedEvent(
                        getPathToLatest(),
                        versionRecord
                ));
            }
        };
    }

    private void resolveVersions(List<VersionRecord> versions) {
        if (this.versions == null || versions.size() > this.versions.size()) {
            this.versions = versions;
        }
    }

    public MenuFactory.MenuBuilder<MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder>> newSaveMenuItem() {
        return MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Save());
    }

    public void addVersionSelectionCallback(Callback<VersionRecord> selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    public boolean isLatest(VersionRecord versionRecord) {
        return versions.get(versions.size() - 1).equals(versionRecord);
    }

    public void setPathToLatest(ObservablePath pathToLatest) {
        this.pathToLatest = pathToLatest;
    }

    public ObservablePath getPathToLatest() {
        return pathToLatest;
    }

    /**
     * It is also possible to change the version with an event.
     * @param event
     */
    public void onVersionSelectedEvent(@Observes VersionSelectedEvent event) {
        if (getPathToLatest().equals(event.getPathToFile()) && selectionCallback != null) {
            selectionCallback.callback(event.getVersionRecord());
        }
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public ObservablePath getCurrentPath() {
        if (isCurrentLatest()) {
            return getPathToLatest();
        } else {
            return createObservablePath(getPathToLatest());
        }
    }

    public boolean isCurrentLatest() {
        return versions == null || getLatestVersionRecord().id().equals(version);
    }

    private VersionRecord getLatestVersionRecord() {
        return versions.get(versions.size()-1);
    }

    private ObservablePath createObservablePath(Path path) {
        return IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(
                PathFactory.newPathBasedOn(path.getFileName(), getCurrentVersionRecordUri(), path));
    }

    private String getCurrentVersionRecordUri() {
        VersionRecord record = getCurrentVersionRecord();
        if (record == null) {
            return getPathToLatest().toURI();
        } else {
            return record.uri();
        }
    }

    private VersionRecord getCurrentVersionRecord() {
        for (VersionRecord versionRecord : versions) {
            if (versionRecord.id().equals(version)) {
                return versionRecord;
            }
        }
        return null;
    }

    public void restoreToCurrentVersion() {
        new SavePopup(new CommandWithCommitMessage() {
            @Override
            public void execute(final String comment) {
                busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Restoring());
                versionService.call(getRestorationSuccessCallback(),
                        new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).restore(getCurrentPath(), comment);
            }
        }).show();
    }

    private RemoteCallback<Path> getRestorationSuccessCallback() {
        return new RemoteCallback<Path>() {
            @Override
            public void callback(final Path restored) {
                busyIndicatorView.hideBusyIndicator();
                version = null;
                restoreEvent.fire(new RestoreEvent(createObservablePath(restored)));
            }
        };
    }

    public void reloadVersions(Path path) {
        versionService.call(new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(List<VersionRecord> records) {
                setVersion(records.get(records.size() - 1).id());
                setVersions(records);
            }
        }).getVersion(path);
    }

    public void clear() {
        selectionCallback = null;
        versions = null;
        pathToLatest = null;
        version = null;
    }
}
