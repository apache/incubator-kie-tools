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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.tooltip.StructureTypesTooltip;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_CustomTitle;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_DefaultTitle;
import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Dependent
@Templated
public class DataTypeSelectView implements DataTypeSelect.View {

    static final String IS_BUILT_IN_TYPE_ATTR = "data-is-built-in-type";

    @DataField("type-text")
    private final HTMLDivElement typeText;

    @DataField("type-select")
    private final HTMLSelectElement typeSelect;

    @DataField("type-select-optgroup")
    private final HTMLOptGroupElement typeSelectOptGroup;

    @DataField("type-select-structure-optgroup")
    private final HTMLOptGroupElement typeSelectStructureOptGroup;

    @DataField("type-select-option")
    private final HTMLOptionElement typeSelectOption;

    private final TranslationService translationService;

    private final StructureTypesTooltip structureTypesTooltip;

    private DataTypeSelect presenter;

    private String value;

    @Inject
    public DataTypeSelectView(final HTMLDivElement typeText,
                              final HTMLSelectElement typeSelect,
                              final HTMLOptGroupElement typeSelectOptGroup,
                              final HTMLOptionElement typeSelectOption,
                              final HTMLOptGroupElement typeSelectStructureOptGroup,
                              final TranslationService translationService,
                              final StructureTypesTooltip structureTypesTooltip) {
        this.typeText = typeText;
        this.typeSelect = typeSelect;
        this.typeSelectOptGroup = typeSelectOptGroup;
        this.typeSelectOption = typeSelectOption;
        this.typeSelectStructureOptGroup = typeSelectStructureOptGroup;
        this.translationService = translationService;
        this.structureTypesTooltip = structureTypesTooltip;
    }

    @Override
    public void init(final DataTypeSelect presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupDropdown() {
        setupDropdownItems();
        setupSelectPicker();
        hideSelectPicker();
        setupSelectPickerOnChangeHandler();
    }

    void setupDropdownItems() {
        RemoveHelper.removeChildren(typeSelect);
        typeSelect.appendChild(makeOptionStructureGroup());
        typeSelect.appendChild(makeOptionGroup(defaultTitle(), presenter.getDefaultDataTypes(), DataType::getType));
        typeSelect.appendChild(makeOptionGroup(customTitle(), presenter.getCustomDataTypes(), DataType::getName));
    }

    private String defaultTitle() {
        return translationService.format(DataTypeSelectView_DefaultTitle);
    }

    private String customTitle() {
        return translationService.format(DataTypeSelectView_CustomTitle);
    }

    HTMLOptGroupElement makeOptionGroup(final String groupTitle,
                                        final List<DataType> dataTypes,
                                        final Function<DataType, String> dataTypeConsumer) {

        final HTMLOptGroupElement optionGroup = makeHTMLOptGroupElement();

        optionGroup.label = groupTitle;

        dataTypes.stream()
                .forEach(dataType -> {
                    final HTMLOptionElement option = makeOption(dataType, dataTypeConsumer);
                    optionGroup.appendChild(option);
                });

        return optionGroup;
    }

    HTMLOptGroupElement makeOptionStructureGroup() {
        return typeSelectStructureOptGroup;
    }

    HTMLOptionElement makeOption(final DataType dataType,
                                 final Function<DataType, String> dataTypeConsumer) {
        final String optionValue = dataTypeConsumer.apply(dataType);

        final HTMLOptionElement option = makeHTMLOptionElement();
        option.text = optionValue;
        option.value = optionValue;
        return option;
    }

    @Override
    public void enableEditMode() {
        hide(typeText);
        showSelectPicker();
        setPickerValue(presenter.getDataType().getType());
    }

    @Override
    public void disableEditMode() {
        typeText.textContent = presenter.getDataType().getType();
        hideSelectPicker();
        show(typeText);
    }

    @Override
    public void setDataType(final DataType dataType) {
        final String type = dataType.getType();
        typeText.setAttribute(IS_BUILT_IN_TYPE_ATTR, isBuiltInType(type));
        typeText.textContent = type;
        value = type;
    }

    public void onSelectChange(final JQuerySelectPickerEvent event) {

        final String newValue = event.target.value;

        if (!Objects.equals(newValue, getValue())) {
            setPickerValue(newValue);
            presenter.clearDataTypesList();
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @EventHandler("type-text")
    public void onTypeTextClick(final ClickEvent event) {
        final String type = presenter.getDataType().getType();
        final HTMLElement element = getElement();
        if (!isBuiltInType(type)) {
            structureTypesTooltip.show(element, type);
        }
        skipClickListeners(event);
    }

    private void skipClickListeners(final ClickEvent event) {
        // Skip click listeners
        event.preventDefault();
        event.stopPropagation();
    }

    void setPickerValue(final String value) {
        setPickerValue(getSelectPicker(), value);
        this.value = value;
    }

    HTMLOptionElement makeHTMLOptionElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLOptionElement'.
        return (HTMLOptionElement) typeSelectOption.cloneNode(false);
    }

    HTMLOptGroupElement makeHTMLOptGroupElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLOptGroupElement'.
        return (HTMLOptGroupElement) typeSelectOptGroup.cloneNode(false);
    }

    void setupSelectPickerOnChangeHandler() {
        setupOnChangeHandler(getSelectPicker());
    }

    void hideSelectPicker() {
        triggerPickerAction(getSelectPicker(), "destroy");
        triggerPickerAction(getSelectPicker(), "hide");
    }

    void showSelectPicker() {
        triggerPickerAction(getSelectPicker(), "show");
    }

    void setupSelectPicker() {
        triggerPickerAction(getSelectPicker(), "refresh");
    }

    Element getSelectPicker() {
        return getElement().querySelector("[data-field='type-select']");
    }

    void triggerPickerAction(final Element element,
                             final String method) {
        $(element).selectpicker(method);
    }

    void setPickerValue(final Element element,
                        final String value) {
        $(element).selectpicker("val", value);
    }

    void setupOnChangeHandler(final Element element) {
        $(element).on("hidden.bs.select", this::onSelectChange);
    }

    private boolean isBuiltInType(final String type) {
        return BuiltInTypeUtils.isBuiltInType(type) || Objects.equals(presenter.structure(), type);
    }
}
