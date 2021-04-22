/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.modal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.DRG_ELEMENT_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.IMPORT_TYPE_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.ITEM_DEFINITION_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PATH_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PMML_MODEL_COUNT_METADATA;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class IncludedModelModal extends Elemental2Modal<IncludedModelModal.View> {

    static final String WIDTH = "600px";

    private final DMNAssetsDropdown dropdown;

    private final ImportRecordEngine recordEngine;

    private final DMNIncludeModelsClient client;

    private final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    private IncludedModelsPagePresenter grid;

    @Inject
    public IncludedModelModal(final View view,
                              final DMNAssetsDropdown dropdown,
                              final ImportRecordEngine recordEngine,
                              final DMNIncludeModelsClient client,
                              final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent,
                              final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent) {
        super(view);
        this.dropdown = dropdown;
        this.recordEngine = recordEngine;
        this.client = client;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
    }

    @PostConstruct
    public void setup() {
        superSetup();
        setWidth(WIDTH);
        getView().init(this);
        getView().setupAssetsDropdown(getInitializedDropdownElement());
    }

    public void init(final IncludedModelsPagePresenter grid) {
        this.grid = grid;
    }

    @Override
    public void show() {
        getDropdown().loadAssets();
        getView().initialize();
        getView().disableIncludeButton();
        superShow();
    }

    void superShow() {
        super.show();
    }

    HTMLElement getInitializedDropdownElement() {
        getDropdown().initialize();
        getDropdown().registerOnChangeHandler(getOnValueChanged());
        return getDropdown().getElement();
    }

    public void include() {
        getDropdown()
                .getValue()
                .ifPresent(value -> {
                    final BaseIncludedModelActiveRecord includedModel = createIncludedModel(value);
                    refreshGrid();
                    refreshDecisionComponents();
                    refreshDataTypesList(includedModel);
                    hide();
                });
    }

    void refreshDataTypesList(final BaseIncludedModelActiveRecord includedModel) {
        client.loadItemDefinitionsByNamespace(includedModel.getName(),
                                              includedModel.getNamespace(),
                                              getItemDefinitionConsumer());
    }

    Consumer<List<ItemDefinition>> getItemDefinitionConsumer() {
        return itemDefinitions -> refreshDataTypesListEvent.fire(new RefreshDataTypesListEvent(itemDefinitions));
    }

    private void refreshDecisionComponents() {
        refreshDecisionComponentsEvent.fire(new RefreshDecisionComponents());
    }

    @Override
    public void hide() {
        superHide();
        getDropdown().clear();
    }

    BaseIncludedModelActiveRecord createIncludedModel(final KieAssetsDropdownItem value) {
        final Map<String, String> metaData = value.getMetaData();
        final BaseIncludedModelActiveRecord includedModel = createIncludedModel(metaData);
        includedModel.setName(NameUtils.normaliseName(getView().getModelNameInput()));
        includedModel.setNamespace(value.getValue());
        includedModel.setImportType(metaData.get(IMPORT_TYPE_METADATA));
        includedModel.setPath(metaData.get(PATH_METADATA));
        includedModel.create();
        return includedModel;
    }

    BaseIncludedModelActiveRecord createIncludedModel(final Map<String, String> metaData) {
        final String importType = metaData.get(IMPORT_TYPE_METADATA);
        if (Objects.equals(DMNImportTypes.DMN, determineImportType(importType))) {
            final DMNIncludedModelActiveRecord dmnIncludedModel = new DMNIncludedModelActiveRecord(recordEngine);
            dmnIncludedModel.setDrgElementsCount(Integer.valueOf(metaData.get(DRG_ELEMENT_COUNT_METADATA)));
            dmnIncludedModel.setDataTypesCount(Integer.valueOf(metaData.get(ITEM_DEFINITION_COUNT_METADATA)));
            return dmnIncludedModel;
        } else if (Objects.equals(DMNImportTypes.PMML, determineImportType(importType))) {
            final PMMLIncludedModelActiveRecord pmmlIncludedModel = new PMMLIncludedModelActiveRecord(recordEngine);
            pmmlIncludedModel.setModelCount(Integer.valueOf(metaData.get(PMML_MODEL_COUNT_METADATA)));
            return pmmlIncludedModel;
        }
        return new DefaultIncludedModelActiveRecord(recordEngine);
    }

    private DMNAssetsDropdown getDropdown() {
        return dropdown;
    }

    void superHide() {
        super.hide();
    }

    private Optional<IncludedModelsPagePresenter> getGrid() {
        return Optional.ofNullable(grid);
    }

    private void refreshGrid() {
        getGrid().ifPresent(IncludedModelsPagePresenter::refresh);
    }

    Command getOnValueChanged() {
        return this::onValueChanged;
    }

    void onValueChanged() {
        if (isValidValues()) {
            getView().enableIncludeButton();
        } else {
            getView().disableIncludeButton();
        }
    }

    boolean isValidValues() {
        return !isEmpty(getView().getModelNameInput()) && getDropdown().getValue().isPresent();
    }

    public interface View extends Elemental2Modal.View<IncludedModelModal> {

        String getModelNameInput();

        void setupAssetsDropdown(final HTMLElement dropdown);

        void initialize();

        void disableIncludeButton();

        void enableIncludeButton();
    }
}
