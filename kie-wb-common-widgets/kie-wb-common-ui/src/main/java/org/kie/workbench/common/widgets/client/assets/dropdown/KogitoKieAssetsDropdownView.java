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
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

@Dependent
@Templated
public class KogitoKieAssetsDropdownView extends KieAssetsDropdownView implements KogitoKieAssetsDropdown.View {

    @DataField("fallback-input")
    protected final HTMLInputElement fallbackInput;

    @Inject
    public KogitoKieAssetsDropdownView(final HTMLSelectElement nativeSelect,
                                       final HTMLInputElement fallbackInput,
                                       final HTMLOptionElement htmlOptionElement,
                                       final TranslationService translationService) {
        super(nativeSelect, htmlOptionElement, translationService);
        this.fallbackInput = fallbackInput;
    }

    @PostConstruct
    public void init() {
        super.init();
        fallbackInput.hidden = true;
    }

    @Override
    public void initialize() {
        fallbackInput.value = "";
        dropdown().selectpicker("val", "");
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

    @Override
    protected void onDropdownChangeHandlerMethod(JQuerySelectPickerEvent event) {
        fallbackInput.value = event.target.value;
        super.onDropdownChangeHandlerMethod(event);
    }
}
