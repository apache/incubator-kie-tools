/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.client.editor;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

/**
 * Implementation of the main tab view for the ScenarioSimulation editor.
 * <p>
 * This class acts as a wrapper class which holds the main <code>ScenarioGridWidget</code>
 */
@ApplicationScoped
public class ScenarioSimulationViewImpl extends KieEditorViewImpl implements ScenarioSimulationView {

    protected ScenarioGridWidget scenarioGridWidget;

    private SimplePanel editorPanel = new SimplePanel();

    @Override
    public void init() {
        setContentWidget(scenarioGridWidget);
        initWidget(editorPanel);
    }

    @Override
    public void setContentWidget(final Widget widget) {
        editorPanel.setWidget(widget);
    }

    @Override
    public void setScenarioGridWidgetAsContent() {
        editorPanel.setWidget(scenarioGridWidget);
    }

    /**
     * It manages the TabBar visibility of Test Scenario.
     * For some unknown reasons, using the TabPanel API (i.e. in ScenarioSimulationEditorKogitoWrapper using this call:
     * getWidget().getMultiPage().setTabBarVisible() ) doesn't work. To manage the Widget visibility, we use this
     * workaround, directing accessing the DOM and modifying the resulting Element. This because this widget is outside
     * ScenarioSimulationView. The className used to find the Element must be synchronized!
     * @param visible
     */
    @Override
    public void setScenarioTabBarVisibility(boolean visible) {
        /* The element className is bound with "TabPanelWithDropdowns.ui.xml" */
        HTMLCollection<Element> elements = DomGlobal.document.getElementsByClassName("uf-tabbar-panel-nav-tabs");
        if (elements.length == 1) {
            HTMLDivElement element = (HTMLDivElement) elements.getAt(0);
            element.style.display = visible ? "" : "none";
        }
    }

    @Override
    public ScenarioGridWidget getScenarioGridWidget() {
        return scenarioGridWidget;
    }

    @Override
    public void setScenarioGridWidget(ScenarioGridWidget scenarioGridWidget) {
        this.scenarioGridWidget = scenarioGridWidget;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if (parent != null) {
            final double w = parent.getOffsetWidth();
            final double h = parent.getOffsetHeight();
            setPixelSize((int) w, (int) h);
        }
        scenarioGridWidget.onResize();
    }


}