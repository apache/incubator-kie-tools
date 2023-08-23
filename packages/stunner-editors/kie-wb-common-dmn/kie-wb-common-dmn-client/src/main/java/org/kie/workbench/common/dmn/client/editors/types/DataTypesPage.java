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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

import static java.util.Optional.ofNullable;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypesPage_Title;

@Dependent
public class DataTypesPage extends DMNPage {

    private final DataTypeList dataTypeList;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final ItemDefinitionStore definitionStore;

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    private final DataTypeManagerStackStore stackIndex;

    private final FlashMessages flashMessages;

    private final DataTypeSearchBar searchBar;

    private final DMNGraphUtils dmnGraphUtils;

    private final DataTypeShortcuts dataTypeShortcuts;

    private String loadedDMNModelNamespace;

    @Inject
    public DataTypesPage(final DataTypeList dataTypeList,
                         final ItemDefinitionUtils itemDefinitionUtils,
                         final ItemDefinitionStore definitionStore,
                         final DataTypeStore dataTypeStore,
                         final DataTypeManager dataTypeManager,
                         final DataTypeManagerStackStore stackIndex,
                         final FlashMessages flashMessages,
                         final DataTypeSearchBar searchBar,
                         final DMNGraphUtils dmnGraphUtils,
                         final TranslationService translationService,
                         final DataTypeShortcuts dataTypeShortcuts,
                         final HTMLDivElement pageView) {

        super(DataTypesPage_Title, pageView, translationService);

        this.dataTypeList = dataTypeList;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.definitionStore = definitionStore;
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
        this.stackIndex = stackIndex;
        this.flashMessages = flashMessages;
        this.searchBar = searchBar;
        this.dmnGraphUtils = dmnGraphUtils;
        this.dataTypeShortcuts = dataTypeShortcuts;
    }

    @PostConstruct
    public void init() {
        dataTypeShortcuts.init(dataTypeList);
    }

    @Override
    public void onFocus() {
        if (!isLoaded()) {
            reload();
        }
        refreshPageView();

        dataTypeList.activateReactComponents();
    }

    @Override
    public void onLostFocus() {
        flashMessages.hideMessages();
    }

    public void onRefreshDataTypesListWithNewItemDefinitions(final @Observes RefreshDataTypesListEvent refresh) {
        if (isLoaded()) {
            refreshItemDefinitions(refresh.getNewItemDefinitions());
            reload();
        }
    }

    void refreshItemDefinitions(final List<ItemDefinition> newItemDefinitions) {
        itemDefinitionUtils.addItemDefinitions(newItemDefinitions);
    }

    public void reload() {
        loadedDMNModelNamespace = currentDMNModelNamespace();

        cleanDataTypeStore();
        loadDataTypes();
        enableShortcuts();
    }

    void refreshPageView() {
        final HTMLDivElement pageView = getPageView();
        RemoveHelper.removeChildren(pageView);
        pageView.appendChild(flashMessages.getElement());
        pageView.appendChild(dataTypeList.getElement());
    }

    boolean isLoaded() {
        return Objects.equals(getLoadedDMNModelNamespace(), currentDMNModelNamespace());
    }

    String currentDMNModelNamespace() {
        return getNamespace().map(Text::getValue).orElse("");
    }

    void cleanDataTypeStore() {
        definitionStore.clear();
        dataTypeStore.clear();
        stackIndex.clear();
        searchBar.reset();
    }

    void loadDataTypes() {
        dataTypeList.setupItems(itemDefinitionUtils
                                        .all()
                                        .stream()
                                        .map(this::makeDataType)
                                        .collect(Collectors.toList()));
    }

    DataType makeDataType(final ItemDefinition itemDefinition) {
        return dataTypeManager.from(itemDefinition).get();
    }

    String getLoadedDMNModelNamespace() {
        return loadedDMNModelNamespace;
    }

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        onFocus();
    }

    private Optional<Text> getNamespace() {
        return getDefinitions().map(Definitions::getNamespace);
    }

    private Optional<Definitions> getDefinitions() {
        return ofNullable(dmnGraphUtils.getModelDefinitions());
    }

    public void enableShortcuts() {
        dataTypeShortcuts.setup();
    }

    public void disableShortcuts() {
        dataTypeShortcuts.teardown();
    }
}
