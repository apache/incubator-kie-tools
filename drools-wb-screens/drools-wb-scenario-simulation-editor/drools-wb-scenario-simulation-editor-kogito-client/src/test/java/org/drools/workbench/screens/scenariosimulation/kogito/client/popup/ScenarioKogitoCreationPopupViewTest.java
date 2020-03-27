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
package org.drools.workbench.screens.scenariosimulation.kogito.client.popup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dropdown.ScenarioKogitoCreationAssetsDropdown;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioKogitoCreationPopupViewTest {

    @Mock
    private HTMLInputElement dmnButtonMock;
    @Mock
    private HTMLInputElement ruleButtonMock;
    @Mock
    private HTMLDivElement dmnAssetsDivElementMock;
    @Mock
    private Button okButtonMock;
    @Mock
    private Button cancelButtonMock;
    @Mock
    private Command okCommandMock;
    @Mock
    private ClickEvent clickEventMock;
    @Mock
    private ScenarioKogitoCreationAssetsDropdown scenarioKogitoCreationAssetsDropdownMock;
    @Mock
    private Modal modalMock;
    @Mock
    private HTMLElement htmlElementMock;
    @Mock
    private HTMLDivElement infoRuleIconDivElementMock;
    @Mock
    private HTMLLabelElement sourceTypeLabelElementMock;
    @Mock
    private HTMLLabelElement dmnAssetsLabelElementMock;

    private ScenarioKogitoCreationPopupView scenarioKogitoCreationPopupViewSpy;

    @Before
    public void setup() {
        scenarioKogitoCreationPopupViewSpy = spy(new ScenarioKogitoCreationPopupView() {
            {
                scenarioKogitoCreationAssetsDropdown = scenarioKogitoCreationAssetsDropdownMock;
                dmnButton = dmnButtonMock;
                ruleButton = ruleButtonMock;
                okButton = okButtonMock;
                cancelButton = cancelButtonMock;
                dmnAssetsDivElement = dmnAssetsDivElementMock;
                dmnAssetsLabelElement = dmnAssetsLabelElementMock;
                sourceTypeLabelElement = sourceTypeLabelElementMock;
                modal = modalMock;
                infoRuleIconDivElement = infoRuleIconDivElementMock;
                selectedPath = "";
            }
        });
        when(scenarioKogitoCreationAssetsDropdownMock.getElement()).thenReturn(htmlElementMock);
    }

    @Test
    public void show() {
        scenarioKogitoCreationPopupViewSpy.show(ScenarioSimulationEditorConstants.INSTANCE.createButton(), okCommandMock);
        verify(scenarioKogitoCreationPopupViewSpy, times(1)).initialize();
    }

    @Test
    public void initialize() {
        scenarioKogitoCreationPopupViewSpy.initialize();
        verify(okButtonMock, times(1)).setEnabled(eq(false));
        verify(cancelButtonMock, times(1)).setText(eq(ScenarioSimulationEditorConstants.INSTANCE.cancelButton()));
        verify(dmnAssetsDivElementMock, times(1)).setAttribute(eq(ConstantHolder.HIDDEN), eq(""));
        verify(dmnAssetsDivElementMock, times(1)).appendChild(eq(dmnAssetsLabelElementMock));
        verify(infoRuleIconDivElementMock, times(1)).setAttribute(eq("title"), eq(ScenarioSimulationEditorConstants.INSTANCE.currentlyNotAvailable()));
        verify(scenarioKogitoCreationPopupViewSpy, times(1)).initializeDropdown();
    }

    @Test
    public void initializeDropdown() {
        scenarioKogitoCreationPopupViewSpy.initializeDropdown();
        verify(dmnAssetsDivElementMock, times(1)).appendChild(eq(htmlElementMock));
        verify(scenarioKogitoCreationAssetsDropdownMock, times(1)).clear();
        verify(scenarioKogitoCreationAssetsDropdownMock, times(1)).init();
        verify(scenarioKogitoCreationAssetsDropdownMock, times(1)).initializeDropdown();
        verify(scenarioKogitoCreationAssetsDropdownMock, times(1)).registerOnChangeHandler(isA(Command.class));

    }

    @Test
    public void onDmnClick() {
        dmnButtonMock.checked = true;
        scenarioKogitoCreationPopupViewSpy.onDmnClick(clickEventMock);
        verify(dmnAssetsDivElementMock, times(1)).removeAttribute(eq(ConstantHolder.HIDDEN));
        verify(scenarioKogitoCreationPopupViewSpy, times(1)).enableCreateButtonForDMNScenario();
        assertEquals(ScenarioSimulationModel.Type.DMN, scenarioKogitoCreationPopupViewSpy.getSelectedType());
    }

    @Test
    public void onRuleClick() {
        ruleButtonMock.checked = true;
        scenarioKogitoCreationPopupViewSpy.onRuleClick(clickEventMock);
        verify(dmnAssetsDivElementMock, times(1)).setAttribute(eq(ConstantHolder.HIDDEN), eq(""));
        verify(okButtonMock, times(1)).setEnabled(eq(true));
        assertEquals(ScenarioSimulationModel.Type.RULE, scenarioKogitoCreationPopupViewSpy.getSelectedType());
    }

    @Test
    public void enableCreateButtonForDMNScenarioNullPath() {
        scenarioKogitoCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, never()).setEnabled(anyBoolean());
    }

    @Test
    public void enableCreateButtonForDMNScenarioEmptyPath() {
        scenarioKogitoCreationPopupViewSpy.selectedPath = "";
        scenarioKogitoCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, never()).setEnabled(anyBoolean());
    }

    @Test
    public void enableCreateButtonForDMNScenarioWithPath() {
        scenarioKogitoCreationPopupViewSpy.selectedPath = "Path";
        scenarioKogitoCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, times(1)).setEnabled(eq(true));
    }
}
