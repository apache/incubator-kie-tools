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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

@Dependent
@Templated
public class KogitoKieAssetsDropdownView extends KieAssetsDropdownView implements KogitoKieAssetsDropdown.View {

    private String value;

    @Inject
    public KogitoKieAssetsDropdownView(final HTMLSelectElement nativeSelect,
                                       final HTMLOptionElement htmlOptionElement,
                                       final TranslationService translationService) {
        super(nativeSelect, htmlOptionElement, translationService);
    }

    @Override
    public void initialize() {
        dropdown().selectpicker("val", "");
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void enableDropdownMode() {
        nativeSelect.classList.remove(HIDDEN_CSS_CLASS);
        dropdown().selectpicker("show");
    }

    @Override
    protected void onDropdownChangeHandlerMethod(final JQuerySelectPickerEvent event) {
        this.value = event.target.value;
        super.onDropdownChangeHandlerMethod(event);
    }
}
