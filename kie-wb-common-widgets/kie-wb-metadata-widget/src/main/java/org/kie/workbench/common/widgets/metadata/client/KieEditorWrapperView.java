/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.Page;

public interface KieEditorWrapperView
        extends IsWidget {

    interface KieEditorWrapperPresenter {

        void onSourceTabSelected();

        void onEditTabSelected();

        void onEditTabUnselected();

        void onOverviewSelected();
    }

    void setPresenter(KieEditorWrapperPresenter presenter);

    void addMainEditorPage(IsWidget baseView);

    MultiPageEditor getMultiPage();

    void addPage(Page page);

    void clear();

    void addImportsTab(IsWidget importsWidget);

    boolean isEditorTabSelected();

    boolean isOverviewTabSelected();

    int getSelectedTabIndex();

    void selectOverviewTab();

    void selectEditorTab();

    void setSelectedTab(int tabIndex);

    void addOverviewPage(OverviewWidgetPresenter overviewWidget,
                         Command onFocus);

    void addSourcePage(ViewDRLSourceWidget sourceWidget);

    void addTabBarWidget(IsWidget customWidget);

    String getNotAllowedSavingMessage();

    String getUnexpectedErrorWhileSavingMessage();
}
