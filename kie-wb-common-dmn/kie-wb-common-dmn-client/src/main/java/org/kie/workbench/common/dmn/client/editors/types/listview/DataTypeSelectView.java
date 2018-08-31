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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.List;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.DataType;

import static org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQuerySelectPicker.$;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_CustomTitle;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSelectView_DefaultTitle;

@Dependent
@Templated
public class DataTypeSelectView implements DataTypeSelect.View {

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

    private DataTypeSelect presenter;

    @Inject
    public DataTypeSelectView(final HTMLDivElement typeText,
                              final HTMLSelectElement typeSelect,
                              final HTMLOptGroupElement typeSelectOptGroup,
                              final HTMLOptionElement typeSelectOption,
                              final HTMLOptGroupElement typeSelectStructureOptGroup,
                              final TranslationService translationService) {
        this.typeText = typeText;
        this.typeSelect = typeSelect;
        this.typeSelectOptGroup = typeSelectOptGroup;
        this.typeSelectOption = typeSelectOption;
        this.typeSelectStructureOptGroup = typeSelectStructureOptGroup;
        this.translationService = translationService;
    }

    @Override
    public void init(final DataTypeSelect presenter) {
        this.presenter = presenter;
        setupDropdown();
    }

    void setupDropdown() {
        setupDropdownItems();
        setupSelectPicker();
        hideSelectPicker();
        setupSelectPickerOnChangeHandler();
    }

    void setupDropdownItems() {
        typeSelect.innerHTML = "";
        typeSelect.appendChild(makeOptionGroup(defaultTitle(), presenter.getDefaultDataTypes(), DataType::getType));
        typeSelect.appendChild(makeOptionGroup(customTitle(), presenter.getCustomDataTypes(), DataType::getName));
        typeSelect.appendChild(makeOptionStructureGroup());
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

        dataTypes.forEach(dataType -> {
            final String optionValue = dataTypeConsumer.apply(dataType);
            final HTMLOptionElement option = makeOption(optionValue);
            optionGroup.appendChild(option);
        });

        return optionGroup;
    }

    private HTMLOptGroupElement makeOptionStructureGroup() {
        return typeSelectStructureOptGroup;
    }

    HTMLOptionElement makeOption(final String value) {
        final HTMLOptionElement option = makeHTMLOptionElement();
        option.text = value;
        option.value = value;
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
        typeText.textContent = "(" + typeSelect.value + ")";
        hideSelectPicker();
        show(typeText);
    }

    @Override
    public void setDataType(final DataType dataType) {
        typeText.textContent = "(" + dataType.getType() + ")";
    }

    public void onSelectChange() {
        presenter.refreshView(typeSelect.value);
    }

    @Override
    public String getValue() {
        return typeSelect.value;
    }

    void setPickerValue(final String value) {
        setPickerValue(getSelectPicker(), value);
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
        triggerPickerAction(getSelectPicker(), "hide");
    }

    void showSelectPicker() {
        triggerPickerAction(getSelectPicker(), "show");
    }

    void setupSelectPicker() {
        triggerPickerAction(getSelectPicker(), "refresh");
    }

    void openSelectPicker() {
        triggerPickerAction(getSelectPicker(), "toggle");
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
}
