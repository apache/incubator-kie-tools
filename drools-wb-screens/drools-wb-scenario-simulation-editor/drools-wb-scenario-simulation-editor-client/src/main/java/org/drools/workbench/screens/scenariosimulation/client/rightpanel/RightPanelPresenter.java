/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Map;
import java.util.SortedMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.events.SetColumnValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter.IDENTIFIER;

@Dependent
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class RightPanelPresenter implements RightPanelView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    public static final String IDENTIFIER = "org.drools.scenariosimulation.RightPanel";

    private RightPanelView view;

    private ListGroupItemPresenter listGroupItemPresenter;

    Map<String, FactModelTree> factTypeFieldsMap;

    EventBus eventBus;

    boolean editingColumnEnabled = false;

    public RightPanelPresenter() {
        //Zero argument constructor for CDI
    }

    @Inject
    public RightPanelPresenter(RightPanelView view, ListGroupItemPresenter listGroupItemPresenter) {
        this.view = view;
        this.listGroupItemPresenter = listGroupItemPresenter;
        this.listGroupItemPresenter.init(this);
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ScenarioSimulationEditorConstants.INSTANCE.testTools();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onClearSearch() {
        view.clearInputSearch();
        view.hideClearButton();
        onSearchedEvent("");
    }

    @Override
    public void onClearNameField() {
        view.clearNameField();
    }

    @Override
    public void onClearStatus() {
        onClearSearch();
        onClearNameField();
    }

    @Override
    public void clearList() {
        view.getListContainer().removeAllChildren();
    }

    @Override
    public FactModelTree getFactModelTree(String factName) {
        return factTypeFieldsMap.get(factName);
    }

    @Override
    public void setFactTypeFieldsMap(SortedMap<String, FactModelTree> factTypeFieldsMap) {
        clearList();
        this.factTypeFieldsMap = factTypeFieldsMap;
        this.factTypeFieldsMap.forEach(this::addListGroupItemView);
    }

    @Override
    public void onShowClearButton() {
        view.showClearButton();
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onSearchedEvent(String search) {
        clearList();
        if (factTypeFieldsMap.isEmpty()) {
            return;
        }
        factTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
    }

    @Override
    public void addListGroupItemView(String factName, FactModelTree factModelTree) {
        DivElement toAdd = listGroupItemPresenter.getDivElement(factName, factModelTree);
        view.getListContainer().appendChild(toAdd);
    }

    @Override
    public void onEnableEditorTab() {
        listGroupItemPresenter.setDisabled(false);
        editingColumnEnabled = true;
        view.enableEditorTab();
    }

    @Override
    public void onDisableEditorTab() {
        listGroupItemPresenter.setDisabled(true);
        editingColumnEnabled = false;
        view.disableEditorTab();
    }

    @Override
    public void onModifyColumn(String factName, String fieldName, String valueClassName) {
        if (editingColumnEnabled) {
            String value = factName + "." + fieldName;
            String baseClass = factName.split("\\.")[0];
            String fullPackage = getFactModelTree(baseClass).getFullPackage();
            eventBus.fireEvent(new SetColumnValueEvent(fullPackage, value, valueClassName));
        }
    }
}
