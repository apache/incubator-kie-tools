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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter.IDENTIFIER;

@ApplicationScoped
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class TestToolsPresenter extends AbstractSubDockPresenter<TestToolsView> implements TestToolsView.Presenter {

    public static final String IDENTIFIER = "org.drools.scenariosimulation.TestTools";
    protected Map<String, FactModelTree> dataObjectFieldsMap = new TreeMap<>();
    protected Map<String, FactModelTree> simpleJavaTypeFieldsMap = new TreeMap<>();
    protected Map<String, FactModelTree> instanceFieldsMap = new TreeMap<>();
    protected Map<String, FactModelTree> simpleJavaInstanceFieldsMap = new TreeMap<>();
    protected Map<String, FactModelTree> hiddenFieldsMap = new TreeMap<>();
    protected EventBus eventBus;
    protected GridWidget gridWidget;
    protected boolean editingColumnEnabled = false;
    protected ListGroupItemView selectedListGroupItemView;
    protected FieldItemView selectedFieldItemView;
    private ListGroupItemPresenter listGroupItemPresenter;

    public TestToolsPresenter() {
        //Zero argument constructor for CDI
        title = ScenarioSimulationEditorConstants.INSTANCE.testTools();
    }

    @Inject
    public TestToolsPresenter(TestToolsView view, ListGroupItemPresenter listGroupItemPresenter) {
        super(view);
        this.listGroupItemPresenter = listGroupItemPresenter;
        this.listGroupItemPresenter.init(this);
        title = ScenarioSimulationEditorConstants.INSTANCE.testTools();
    }

    @Override
    public void onClearSearch() {
        view.clearInputSearch();
        view.hideClearButton();
        onSearchedEvent("");
    }

    @Override
    public void onUndoSearch() {
        view.clearInputSearch();
        view.hideClearButton();
        onPerfectMatchSearchedEvent(listGroupItemPresenter.getFilterTerm(), true);
    }

    @Override
    public void onClearStatus() {
        onClearSearch();
    }

    @Override
    public void clearDataObjectList() {
        view.clearDataObjectList();
    }

    @Override
    public void clearSimpleJavaTypeList() {
        view.clearSimpleJavaTypeList();
    }

    @Override
    public void clearInstanceList() {
        view.clearInstanceList();
    }

    @Override
    public void clearSimpleJavaInstanceFieldList() {
        view.clearSimpleJavaInstanceFieldList();
    }

    @Override
    public void updateInstanceListSeparator() {
        view.updateInstanceListSeparator(GridWidget.SIMULATION.equals(gridWidget));
    }

    @Override
    public void showInstanceListContainerSeparator(boolean show) {
        view.showInstanceListContainerSeparator(show);
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromFactTypeMap(String factName) {
        return Optional.ofNullable(dataObjectFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromSimpleJavaTypeMap(String factName) {
        return Optional.ofNullable(simpleJavaTypeFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromInstanceMap(String factName) {
        return Optional.ofNullable(instanceFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromSimpleJavaInstanceMap(String factName) {
        return Optional.ofNullable(simpleJavaInstanceFieldsMap.get(factName));
    }

    @Override
    public FactModelTree getFactModelTreeFromHiddenMap(String factName) {
        return hiddenFieldsMap.get(factName);
    }

    @Override
    public void setDataObjectFieldsMap(SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        clearDataObjectList();
        this.dataObjectFieldsMap = dataObjectFieldsMap;
        this.dataObjectFieldsMap.forEach(this::addDataObjectListGroupItemView);
    }

    @Override
    public void setSimpleJavaTypeFieldsMap(SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap) {
        clearSimpleJavaTypeList();
        this.simpleJavaTypeFieldsMap = simpleJavaTypeFieldsMap;
        this.simpleJavaTypeFieldsMap.forEach(this::addSimpleJavaTypeListGroupItemView);
    }

    @Override
    public void setInstanceFieldsMap(SortedMap<String, FactModelTree> instanceFieldsMap) {
        clearInstanceList();
        this.instanceFieldsMap = instanceFieldsMap;
        this.instanceFieldsMap.forEach(this::addInstanceListGroupItemView);
        updateInstanceListSeparator();
    }

    @Override
    public void setSimpleJavaInstanceFieldsMap(SortedMap<String, FactModelTree> simpleJavaInstanceFieldsMap) {
        clearSimpleJavaInstanceFieldList();
        this.simpleJavaInstanceFieldsMap = simpleJavaInstanceFieldsMap;
        this.simpleJavaInstanceFieldsMap.forEach(this::addSimpleJavaInstanceListGroupItemView);
        updateInstanceListSeparator();
    }

    @Override
    public void setHiddenFieldsMap(SortedMap<String, FactModelTree> hiddenFieldsMap) {
        this.hiddenFieldsMap = hiddenFieldsMap;
    }

    @Override
    public void hideProperties(Map<String, List<List<String>>> propertiesToHide) {
        listGroupItemPresenter.showAll();
        propertiesToHide.entrySet().stream().forEach(
                stringListEntry -> stringListEntry.getValue()
                        .forEach(propertyParts ->
                                         listGroupItemPresenter.hideProperty(stringListEntry.getKey(), propertyParts))
        );
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
    public void setGridWidget(GridWidget gridWidget) {
        this.gridWidget = gridWidget;
        onDisableEditorTab();
        if (GridWidget.BACKGROUND.equals(gridWidget)) {
            hideInstances();
        }
    }

    @Override
    public void onSearchedEvent(String search) {
        clearLists();
        dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addDataObjectListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addSimpleJavaTypeListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addSimpleJavaInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        updateInstanceListSeparator();
    }

    @Override
    public void onPerfectMatchSearchedEvent(String search, boolean notEqualsSearch) {
        clearLists();
        dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addDataObjectListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addSimpleJavaTypeListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addSimpleJavaInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        updateInstanceListSeparator();
    }

    @Override
    public void addDataObjectListGroupItemView(String factName, FactModelTree factModelTree) {
        view.addDataObjectListGroupItem(listGroupItemPresenter.getDivElement(factName, factModelTree));
    }

    @Override
    public void addSimpleJavaTypeListGroupItemView(String factName, FactModelTree factModelTree) {
        view.addSimpleJavaTypeListGroupItem(listGroupItemPresenter.getDivElement(factName, factModelTree));
    }

    @Override
    public void addInstanceListGroupItemView(String instanceName, FactModelTree factModelTree) {
        view.addInstanceListGroupItem(listGroupItemPresenter.getDivElement(instanceName, factModelTree));
    }

    @Override
    public void addSimpleJavaInstanceListGroupItemView(String instanceName, FactModelTree factModelTree) {
        view.addSimpleJavaInstanceListGroupItem(listGroupItemPresenter.getDivElement(instanceName, factModelTree));
    }

    @Override
    public void onEnableEditorTab() {
        onDisableEditorTab();
        listGroupItemPresenter.enable();
        editingColumnEnabled = true;
        view.enableEditorTab();
        view.enableSearch();
    }

    @Override
    public void onEnableEditorTab(String filterTerm, List<String> propertyNameElements, boolean notEqualsSearch) {
        onDisableEditorTab();
        onPerfectMatchSearchedEvent(filterTerm, notEqualsSearch);
        listGroupItemPresenter.enable(filterTerm);
        editingColumnEnabled = true;
        view.enableEditorTab();
        /* If notEqualsSearch is TRUE, then the instance is not assigned for the selected column.
         * Therefore, it isn't necessary to search through the maps to check it. In that case, the search is activated.
         */
        if (!notEqualsSearch) {
            updateInstanceIsAssignedStatus(filterTerm);
        } else {
            view.enableSearch();
        }
        if (propertyNameElements != null && !notEqualsSearch) {
            listGroupItemPresenter.selectProperty(filterTerm, propertyNameElements);
        }
    }

    @Override
    public void onDisableEditorTab() {
        onSearchedEvent("");
        listGroupItemPresenter.disable();
        editingColumnEnabled = false;
        view.disableEditorTab();
        selectedFieldItemView = null;
        selectedListGroupItemView = null;
    }

    @Override
    public void setSelectedElement(ListGroupItemView selected) {
        selectedListGroupItemView = selected;
        selectedFieldItemView = null;
        if (filterTerm(selected.getFactName(), listGroupItemPresenter.getFilterTerm(), selected.isInstanceAssigned())) {
            view.disableAddButton();
        } else {
            view.enableAddButton();
        }
    }

    @Override
    public void setSelectedElement(FieldItemView selected) {
        selectedFieldItemView = selected;
        selectedListGroupItemView = null;
        String factName = selectedFieldItemView.getFullPath().split("\\.")[0];
        boolean isFactNameAssigned = listGroupItemPresenter.isInstanceAssigned(factName);
        /* If the check is not shown, the item was not selected by an user but automatically. If it's shown,
           then it checks if the related instance is already assigned or not. */
        if (!selectedFieldItemView.isCheckShown() ||
                filterTerm(factName, listGroupItemPresenter.getFilterTerm(), isFactNameAssigned)) {
            view.disableAddButton();
        } else {
            view.enableAddButton();
        }
    }

    @Override
    public void clearSelection() {
        if (selectedFieldItemView != null) {
            selectedFieldItemView.showCheck(false);
        } else if (selectedListGroupItemView != null) {
            selectedListGroupItemView.showCheck(false);
        }
        view.disableAddButton();
    }

    @Override
    public void onModifyColumn() {
        if (editingColumnEnabled) {
            if (selectedListGroupItemView != null) {
                String className = selectedListGroupItemView.getActualClassName();
                getFullPackage(className).ifPresent(fullPackage -> eventBus.fireEvent(
                            new SetPropertyHeaderEvent(gridWidget,
                                                       fullPackage,
                                                       Arrays.asList(className),
                                                       fullPackage + "." + className,
                                                       FactMappingValueType.EXPRESSION)));
            } else if (selectedFieldItemView != null) {
                String baseClass = selectedFieldItemView.getFullPath().split("\\.")[0];
                String value = isSimple(baseClass) ?
                        selectedFieldItemView.getFullPath() :
                        selectedFieldItemView.getFullPath() + "." + selectedFieldItemView.getFieldName();
                List<String> propertyNameElements = Collections.unmodifiableList(Arrays.asList(value.split("\\.")));
                getFullPackage(baseClass).ifPresent(fullPackage -> eventBus.fireEvent(new SetPropertyHeaderEvent(gridWidget, fullPackage,
                                                                                                                 propertyNameElements,
                                                                                                                 selectedFieldItemView.getClassName(),
                                                                                                                 FactMappingValueType.NOT_EXPRESSION)));
            }
        }
    }

    @Override
    public void reset() {
        listGroupItemPresenter.reset();
        view.reset();
    }

    /**
     * Method to hide all the <b>instance-related</b> html
     */
    @Override
    public void hideInstances() {
        clearInstanceList();
        clearSimpleJavaInstanceFieldList();
        showInstanceListContainerSeparator(false);
    }

    /**
     * It navigates through the maps, to check if the given key is present or not in the keySet of these maps.
     * If present, then a INSTANCE is already assigned to the selected column. Then, it assigns the search result to
     * its related view.
     * @param key
     */
    protected void updateInstanceIsAssignedStatus(String key) {
        if (key != null && !key.isEmpty()) {
            boolean assigned = dataObjectFieldsMap.keySet().contains(key) ||
                    simpleJavaTypeFieldsMap.keySet().contains(key) ||
                    instanceFieldsMap.keySet().contains(key) ||
                    simpleJavaInstanceFieldsMap.keySet().contains(key);
            listGroupItemPresenter.setInstanceAssigned(key, assigned);
        }
    }

    protected Optional<String> getFullPackage(String className) {
        return getFactModelTreeFromMaps(className).map(FactModelTree::getFullPackage);
    }

    protected Optional<FactModelTree> getFactModelTreeFromMaps(String key) {
        return Optional.ofNullable(getFactModelTreeFromFactTypeMap(key)
                                           .orElseGet(() -> getFactModelTreeFromSimpleJavaTypeMap(key)
                                                   .orElseGet(() -> getFactModelTreeFromInstanceMap(key)
                                                           .orElseGet(() -> getFactModelTreeFromSimpleJavaInstanceMap(key).orElse(null)))));
    }

    protected boolean isSimple(String key) {
        return Optional.ofNullable(getFactModelTreeFromSimpleJavaTypeMap(key))
                .orElseGet(() -> getFactModelTreeFromSimpleJavaInstanceMap(key))
                .isPresent();
    }

    protected void clearLists() {
        clearDataObjectList();
        clearSimpleJavaTypeList();
        clearInstanceList();
        clearSimpleJavaInstanceFieldList();
    }

    /**
     * It determines if a key (factTitle) is present or not in the search variable.
     * @param key It's the title of the fact to search
     * @param search It represents a concatenation of titles, with ";" as separator
     * @param notEqualsSearch It establishes the method logic: to check if a key is present or not in search string
     * @return
     */
    protected boolean filterTerm(String key, String search, boolean notEqualsSearch) {
        List<String> terms = Arrays.asList(search.split(";"));
        if (notEqualsSearch) {
            return !terms.contains(key);
        } else {
            return terms.contains(key);
        }
    }
}
