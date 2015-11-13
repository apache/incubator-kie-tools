/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.views.bs2.listbar;

import java.util.LinkedHashSet;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ListBarWidgetUnitTestWrapper extends ListBarWidgetImpl {

    public ListBarWidgetUnitTestWrapper() {
    }

    ;

    public ListBarWidgetUnitTestWrapper setupMocks( FlowPanel menuArea,
                                                    Button closeButton,
                                                    Pair<PartDefinition, FlowPanel> currentPart,
                                                    WorkbenchPanelPresenter presenter,
                                                    PanelManager panelManager,
                                                    FocusPanel container,
                                                    Button contextDisplay,
                                                    FlowPanel contextMenu,
                                                    SimplePanel title,
                                                    FlowPanel content,
                                                    LinkedHashSet<PartDefinition> parts
                                                  ) {
        this.menuArea = menuArea;
        this.closeButton = closeButton;
        this.currentPart = currentPart;
        this.presenter = presenter;
        this.panelManager = panelManager;
        this.container = container;
        this.contextDisplay = contextDisplay;
        this.contextMenu = contextMenu;
        this.title = title;
        this.content = content;
        this.parts = parts;
        return this;
    }

    public boolean isMultiPart() {
        return isMultiPart;
    }

    public boolean isDndEnabled() {
        return isDndEnabled;
    }

    @Override
    boolean isPropertyListbarContextDisable() {
        return true;
    }

    boolean isCustomListNull(){
        return partChooserList==null;
    }

}
