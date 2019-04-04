/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.assets.dropdown;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;

import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.KieAssetsDropdownView_Select;

@Dependent
@Templated
public class KieAssetsDropdownView implements KieAssetsDropdown.View {

    static final String HIDDEN_CSS_CLASS = "hidden";

    static final String SELECT_PICKER_SUBTEXT_ATTRIBUTE = "data-subtext";

    @DataField("native-select")
    private final HTMLSelectElement nativeSelect;

    @DataField("fallback-input")
    private final HTMLInputElement fallbackInput;

    private final HTMLOptionElement htmlOptionElement;

    private final TranslationService translationService;

    private KieAssetsDropdown presenter;

    @Inject
    public KieAssetsDropdownView(final HTMLSelectElement nativeSelect,
                                 final HTMLInputElement fallbackInput,
                                 final HTMLOptionElement htmlOptionElement,
                                 final TranslationService translationService) {
        this.nativeSelect = nativeSelect;
        this.fallbackInput = fallbackInput;
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
        fallbackInput.hidden = true;
        dropdown().on("hidden.bs.select", getOnDropdownChangeHandler());
    }

    CallbackFunction getOnDropdownChangeHandler() {
        return event -> {
            fallbackInput.value = event.target.value;
            presenter.onValueChanged();
        };
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
        fallbackInput.value = "";
        dropdown().selectpicker("val", "");
    }

    @Override
    public void refreshSelectPicker() {
        dropdown().selectpicker("refresh");
    }

    @Override
    public String getValue() {
        return fallbackInput.value;
    }

    @Override
    public void enableInputMode() {
        nativeSelect.classList.add(HIDDEN_CSS_CLASS);
        fallbackInput.classList.remove(HIDDEN_CSS_CLASS);
        dropdown().selectpicker("hide");
    }

    @Override
    public void enableDropdownMode() {
        fallbackInput.classList.add(HIDDEN_CSS_CLASS);
        nativeSelect.classList.remove(HIDDEN_CSS_CLASS);
        dropdown().selectpicker("show");
    }

    @EventHandler("fallback-input")
    public void onFallbackInputChange(final KeyUpEvent e) {
        presenter.onValueChanged();
    }

    HTMLOptionElement selectOption() {
        final HTMLOptionElement option = makeHTMLOptionElement();
        option.text = translationService.format(KieAssetsDropdownView_Select);
        option.value = "";
        return option;
    }

    private HTMLOptionElement entryOption(final KieAssetsDropdownItem entry) {

        final HTMLOptionElement option = makeHTMLOptionElement();

        option.text = entry.getText();
        option.value = entry.getValue();
        option.setAttribute(SELECT_PICKER_SUBTEXT_ATTRIBUTE, entry.getSubText());

        return option;
    }

    HTMLOptionElement makeHTMLOptionElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLOptionElement'.
        return (HTMLOptionElement) htmlOptionElement.cloneNode(false);
    }

    JQuerySelectPicker dropdown() {
        return JQuerySelectPicker.$(nativeSelect);
    }

    private void removeChildren(final Element element) {
        while (element.firstChild != null) {
            element.removeChild(element.firstChild);
        }
    }
}
