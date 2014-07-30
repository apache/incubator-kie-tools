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

package org.kie.workbench.common.widgets.client.discussion;

import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;

public class VersionRecordManager {

    private DropdownButton button = new DropdownButton(ToolsMenuConstants.INSTANCE.LatestVersion());

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Callback<VersionRecord> selectionCallback;
    private List<VersionRecord> versions;
    private ObservablePath pathToLatest;
    private String version;

    public VersionRecordManager() {
        button.setRightDropdown(true);
        button.getTriggerWidget().addStyleName("btn-mini");
    }

    public MenuItem buildMenu() {
        MenuCustom<Widget> version = new MenuCustom<Widget>() {

            @Override public Widget build() {
                return button;
            }

            @Override public boolean isEnabled() {
                return false;
            }

            @Override public void setEnabled(boolean enabled) {

            }

            @Override public String getContributionPoint() {
                return null;
            }

            @Override public String getCaption() {
                return null;
            }

            @Override public MenuPosition getPosition() {
                return null;
            }

            @Override public int getOrder() {
                return 0;
            }

            @Override public void addEnabledStateChangeListener(EnabledStateChangeListener listener) {

            }

            @Override public String getSignatureId() {
                return null;
            }

            @Override public Collection<String> getRoles() {
                return null;
            }

            @Override public Collection<String> getTraits() {
                return null;
            }
        };
        return version;
    }

    public void setVersions(List<VersionRecord> versions) {

        button.clear();

        resolveVersions(versions);

        fillMenu(version);
    }

    private void fillMenu(String version) {
        int versionIndex = 1;

        button.setText(ToolsMenuConstants.INSTANCE.LatestVersion());

        for (final VersionRecord versionRecord : versions) {
            final CommitLabel commitLabel = new CommitLabel(versionRecord);
            commitLabel.addClickHandler(
                    new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            if (selectionCallback != null) {
                                selectionCallback.callback(versionRecord);
                            }
                        }
                    });

            button.add(commitLabel);

            if (versionRecord.id().equals(version) && versionIndex != versions.size()) {
                button.setText(ToolsMenuConstants.INSTANCE.Version(versionIndex));
            }

            versionIndex++;
        }
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

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public ObservablePath getCurrentPath() {
        if (version == null) {
            return getPathToLatest();
        } else {
            return IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(
                    PathFactory.newPathBasedOn(getCurrentPath().getFileName(), getCurrentVersionRecord().uri(), getCurrentPath()));
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
}
