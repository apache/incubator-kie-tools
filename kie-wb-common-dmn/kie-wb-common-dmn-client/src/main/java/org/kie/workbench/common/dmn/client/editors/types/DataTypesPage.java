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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessages;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.uberfire.client.views.pfly.multipage.PageImpl;

import static java.util.Optional.ofNullable;
import static org.jboss.errai.common.client.ui.ElementWrapperWidget.getWidget;

@Dependent
public class DataTypesPage extends PageImpl {

    private final DataTypeList treeList;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final ItemDefinitionStore definitionStore;

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    private final DataTypeManagerStackStore stackIndex;

    private final DataTypeFlashMessages flashMessages;

    private final DMNGraphUtils dmnGraphUtils;

    private final HTMLDivElement pageView;

    private String loadedDMNModelNamespace;

    @Inject
    public DataTypesPage(final DataTypeList treeList,
                         final ItemDefinitionUtils itemDefinitionUtils,
                         final ItemDefinitionStore definitionStore,
                         final DataTypeStore dataTypeStore,
                         final DataTypeManager dataTypeManager,
                         final DataTypeManagerStackStore stackIndex,
                         final DataTypeFlashMessages flashMessages,
                         final DMNGraphUtils dmnGraphUtils,
                         final TranslationService translationService,
                         final HTMLDivElement pageView) {

        super(getWidget(pageView), getPageTitle(translationService));

        this.treeList = treeList;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.definitionStore = definitionStore;
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
        this.stackIndex = stackIndex;
        this.flashMessages = flashMessages;
        this.dmnGraphUtils = dmnGraphUtils;
        this.pageView = pageView;
    }

    private static String getPageTitle(final TranslationService translationService) {
        return translationService.format(DMNEditorConstants.DataTypesPage_Label);
    }

    @Override
    public void onFocus() {
        if (!isLoaded()) {
            reload();
        }
        refreshPageView();
    }

    @Override
    public void onLostFocus() {
        flashMessages.hideMessages();
    }

    public void reload() {

        loadedDMNModelNamespace = currentDMNModelNamespace();

        cleanDataTypeStore();
        loadDataTypes();
    }

    void refreshPageView() {
        pageView.innerHTML = "";
        pageView.appendChild(flashMessages.getElement());
        pageView.appendChild(treeList.getElement());
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
    }

    void loadDataTypes() {
        treeList.setupItems(itemDefinitionUtils
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
        return ofNullable(dmnGraphUtils.getDefinitions());
    }
}
