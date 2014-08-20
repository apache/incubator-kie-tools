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

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

public class VersionMenuDropDownButton
        implements IsWidget {

    private DropdownButton button = new DropdownButton(ToolsMenuConstants.INSTANCE.LatestVersion());

    public VersionMenuDropDownButton() {
        button.setRightDropdown(true);
        button.getTriggerWidget().addStyleName("btn-mini");
    }

    @Override
    public Widget asWidget() {
        return button;
    }

    public void clear() {
        button.clear();
    }

    public void setTextToLatest() {
        button.setText(ToolsMenuConstants.INSTANCE.LatestVersion());
    }

    public void addLabel(VersionRecord versionRecord, int versionIndex, boolean isSelected, Command selectionCommand) {
        VersionMenuItemLabel widget = new VersionMenuItemLabel(
                versionRecord,
                versionIndex,
                isSelected,
                selectionCommand);
        widget.setWidth("400px");
        button.add(widget);
    }

    public void addViewAllLabel(int index, Command command) {
        button.add(
                new ViewAllLabel(
                        index,
                        command));
    }

    public void setTextToVersion(int versionIndex) {
        button.setText(ToolsMenuConstants.INSTANCE.Version(versionIndex));
    }
}
