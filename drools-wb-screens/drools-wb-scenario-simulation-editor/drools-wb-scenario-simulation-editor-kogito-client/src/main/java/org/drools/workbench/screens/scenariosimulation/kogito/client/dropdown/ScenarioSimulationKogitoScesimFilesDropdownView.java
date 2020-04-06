/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dropdown;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;

@Dependent
@Templated
public class ScenarioSimulationKogitoScesimFilesDropdownView extends KieAssetsDropdownView implements ScenarioSimulationKogitoScesimFilesDropdown.View {

    @Inject
    public ScenarioSimulationKogitoScesimFilesDropdownView(HTMLSelectElement nativeSelect, HTMLOptionElement htmlOptionElement, TranslationService translationService) {
        super(nativeSelect, htmlOptionElement, translationService);
    }

    public IsWidget asWidget() {
        return ElementWrapperWidget.getWidget(getElement());
    }
}
