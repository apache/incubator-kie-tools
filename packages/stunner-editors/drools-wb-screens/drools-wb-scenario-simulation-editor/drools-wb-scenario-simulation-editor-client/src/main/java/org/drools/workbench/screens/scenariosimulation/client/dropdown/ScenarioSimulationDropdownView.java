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
package org.drools.workbench.screens.scenariosimulation.client.dropdown;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;

@Dependent
@Templated(value = "/org/kie/workbench/common/widgets/client/assets/dropdown/KieAssetsDropdownView.html")
@Named(ScenarioSimulationDropdownView.BEAN_NAME)
public class ScenarioSimulationDropdownView extends KieAssetsDropdownView implements ScenarioSimulationDropdown.View {

    final public static String BEAN_NAME = "ScenarioDropdownView";

    @Inject
    public ScenarioSimulationDropdownView(HTMLSelectElement nativeSelect,
                                          HTMLOptionElement htmlOptionElement,
                                          TranslationService translationService) {
        super(nativeSelect, htmlOptionElement, translationService);
    }

    public IsWidget asWidget() {
        return ElementWrapperWidget.getWidget(getElement());
    }
}