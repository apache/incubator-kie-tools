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

import java.util.Collections;
import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dropdown.ScenarioSimulationKogitoCreationAssetsDropdown;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoCreationPopupViewTest {

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
    private ScenarioSimulationKogitoCreationAssetsDropdown scenarioSimulationKogitoCreationAssetsDropdownMock;
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
    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    private ScenarioSimulationKogitoCreationPopupView scenarioSimulationCreationPopupViewSpy;

    @Before
    public void setup() {
        scenarioSimulationCreationPopupViewSpy = spy(new ScenarioSimulationKogitoCreationPopupView() {
            {
                scenarioSimulationKogitoCreationAssetsDropdown = scenarioSimulationKogitoCreationAssetsDropdownMock;
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
        when(scenarioSimulationKogitoCreationAssetsDropdownMock.getElement()).thenReturn(htmlElementMock);
    }

    @Test
    public void show() {
        scenarioSimulationCreationPopupViewSpy.show(ScenarioSimulationEditorConstants.INSTANCE.createButton(), okCommandMock);
        verify(scenarioSimulationCreationPopupViewSpy, times(1)).initialize();
    }

    @Test
    public void initialize() {
        scenarioSimulationCreationPopupViewSpy.initialize();
        verify(okButtonMock, times(1)).setEnabled(eq(false));
        verify(cancelButtonMock, times(1)).setText(eq(ScenarioSimulationEditorConstants.INSTANCE.cancelButton()));
        verify(dmnAssetsDivElementMock, times(1)).setAttribute(eq(ConstantHolder.HIDDEN), eq(""));
        verify(dmnAssetsDivElementMock, times(1)).appendChild(eq(dmnAssetsLabelElementMock));
        verify(infoRuleIconDivElementMock, times(1)).setAttribute(eq("title"), eq(ScenarioSimulationEditorConstants.INSTANCE.currentlyNotAvailable()));
        verify(scenarioSimulationCreationPopupViewSpy, times(1)).initializeDropdown();
    }

    @Test
    public void initializeDropdown() {
        when(scenarioSimulationKogitoCreationAssetsDropdownMock.getValue()).thenReturn(Optional.of(new KieAssetsDropdownItem("text", "subtext", "path/file.dmn", Collections.emptyMap())));
        scenarioSimulationCreationPopupViewSpy.initializeDropdown();
        verify(dmnAssetsDivElementMock, times(1)).appendChild(eq(htmlElementMock));
        verify(scenarioSimulationKogitoCreationAssetsDropdownMock, times(1)).clear();
        verify(scenarioSimulationKogitoCreationAssetsDropdownMock, times(1)).init();
        verify(scenarioSimulationKogitoCreationAssetsDropdownMock, times(1)).initializeDropdown();
        verify(scenarioSimulationKogitoCreationAssetsDropdownMock, times(1)).registerOnChangeHandler(commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();
        verify(scenarioSimulationCreationPopupViewSpy, times(1)).enableCreateButtonForDMNScenario();
        assertEquals("path/file.dmn", scenarioSimulationCreationPopupViewSpy.selectedPath);
    }

    @Test
    public void onDmnClick() {
        dmnButtonMock.checked = true;
        scenarioSimulationCreationPopupViewSpy.onDmnClick(clickEventMock);
        verify(dmnAssetsDivElementMock, times(1)).removeAttribute(eq(ConstantHolder.HIDDEN));
        verify(scenarioSimulationCreationPopupViewSpy, times(1)).enableCreateButtonForDMNScenario();
        assertEquals(ScenarioSimulationModel.Type.DMN, scenarioSimulationCreationPopupViewSpy.getSelectedType());
    }

    @Test
    public void onRuleClick() {
        ruleButtonMock.checked = true;
        scenarioSimulationCreationPopupViewSpy.onRuleClick(clickEventMock);
        verify(dmnAssetsDivElementMock, times(1)).setAttribute(eq(ConstantHolder.HIDDEN), eq(""));
        verify(okButtonMock, times(1)).setEnabled(eq(true));
        assertEquals(ScenarioSimulationModel.Type.RULE, scenarioSimulationCreationPopupViewSpy.getSelectedType());
    }

    @Test
    public void enableCreateButtonForDMNScenarioNullPath() {
        scenarioSimulationCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, never()).setEnabled(anyBoolean());
    }

    @Test
    public void enableCreateButtonForDMNScenarioEmptyPath() {
        scenarioSimulationCreationPopupViewSpy.selectedPath = "";
        scenarioSimulationCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, never()).setEnabled(anyBoolean());
    }

    @Test
    public void enableCreateButtonForDMNScenarioWithPath() {
        scenarioSimulationCreationPopupViewSpy.selectedPath = "Path";
        scenarioSimulationCreationPopupViewSpy.enableCreateButtonForDMNScenario();
        verify(okButtonMock, times(1)).setEnabled(eq(true));
    }
}
