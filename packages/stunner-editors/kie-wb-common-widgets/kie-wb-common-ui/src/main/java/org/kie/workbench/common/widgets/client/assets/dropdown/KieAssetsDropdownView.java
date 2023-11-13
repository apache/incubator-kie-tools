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


package org.kie.workbench.common.widgets.client.assets.dropdown;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.KieAssetsDropdownView_Select;

@Dependent
@Templated
public class KieAssetsDropdownView implements KieAssetsDropdown.View {

    public static final String HIDDEN_CSS_CLASS = "hidden";

    public static final String SELECT_PICKER_SUBTEXT_ATTRIBUTE = "data-subtext";
    @DataField("native-select")
    protected final HTMLSelectElement nativeSelect;
    protected final HTMLOptionElement htmlOptionElement;
    protected final TranslationService translationService;
    protected KieAssetsDropdown presenter;

    @Inject
    public KieAssetsDropdownView(final HTMLSelectElement nativeSelect,
                                 final HTMLOptionElement htmlOptionElement,
                                 final TranslationService translationService) {
        this.nativeSelect = nativeSelect;
        this.htmlOptionElement = htmlOptionElement;
        this.translationService = translationService;
    }

    @Override
    public void init(final KieAssetsDropdown presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        nativeSelect.hidden = false;
        dropdown().on("hidden.bs.select", getOnDropdownChangeHandler());
    }

    @Override
    public void addValue(final KieAssetsDropdownItem entry) {
        nativeSelect.appendChild(entryOption(entry));
    }

    @Override
    public void clear() {
        removeChildren(nativeSelect);
        nativeSelect.appendChild(selectOption());
        refreshSelectPicker();
    }

    @Override
    public void initialize() {
        dropdown().selectpicker("val", "");
        dropdown().selectpicker("show");
    }

    @Override
    public void refreshSelectPicker() {
        dropdown().selectpicker("refresh");
    }

    @Override
    public String getValue() {
        return dropdown().val();
    }

    protected JQuerySelectPicker.CallbackFunction getOnDropdownChangeHandler() {
        return this::onDropdownChangeHandlerMethod;
    }

    protected void onDropdownChangeHandlerMethod(JQuerySelectPickerEvent event) {
        presenter.onValueChanged();
    }

    protected HTMLOptionElement selectOption() {
        final HTMLOptionElement option = makeHTMLOptionElement();
        option.text = translationService.format(KieAssetsDropdownView_Select);
        option.value = "";
        return option;
    }

    protected HTMLOptionElement makeHTMLOptionElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLOptionElement'.
        return (HTMLOptionElement) htmlOptionElement.cloneNode(false);
    }

    protected JQuerySelectPicker dropdown() {
        return JQuerySelectPicker.$(nativeSelect);
    }

    protected HTMLOptionElement entryOption(final KieAssetsDropdownItem entry) {
        final HTMLOptionElement option = makeHTMLOptionElement();
        option.text = entry.getText();
        option.value = entry.getValue();
        option.setAttribute(SELECT_PICKER_SUBTEXT_ATTRIBUTE, entry.getSubText());
        return option;
    }

    protected void removeChildren(final Element element) {
        while (element.firstChild != null) {
            element.removeChild(element.firstChild);
        }
    }
}
