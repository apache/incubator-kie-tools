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

package org.kie.workbench.common.screens.socialscreen.client.discussion;

import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

public class VersionMenuBuilder {

    private DropdownButton button = new DropdownButton("Latest version");

    @Inject
    @New
    private FileMenuBuilder menuBuilder;

    public VersionMenuBuilder() {
        button.setRightDropdown(true);
        button.getTriggerWidget().addStyleName("btn-mini");
    }

    private MenuItem createVersionMenu() {
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

    public void setVersions(String version, List<VersionRecord> versions) {
        int versionIndex = versions.size();

        for (VersionRecord versionRecord : versions) {
            button.add(new CommitLabel(versionRecord));

            if (versionRecord.id().equals(version) && versionIndex != versions.size()) {
                button.setText("Version " + versionIndex);
            }
        }
    }

    public MenuFactory.MenuBuilder<MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder>> newSaveMenuItem() {
        return MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Save());
    }

    public Menus buildBasic(Command command) {
        return newSaveMenuItem()
                .respondsWith(command)
                .endMenu()
                .newTopLevelMenu(createVersionMenu())
                .endMenu()
                .build();
    }

    public Menus buildRestoreMenu(ObservablePath path) {
        return menuBuilder.addRestoreVersion(path).build();
    }
}
