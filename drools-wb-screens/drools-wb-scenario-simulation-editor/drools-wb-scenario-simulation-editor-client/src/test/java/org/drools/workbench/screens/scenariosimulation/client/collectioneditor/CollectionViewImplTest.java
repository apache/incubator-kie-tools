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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.Collections;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.events.CloseCompositeEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SaveEditorEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CollectionViewImplTest extends AbstractCollectionEditorTest {

    private CollectionViewImpl collectionEditorViewImplSpy;

    @Mock
    private DivElement collectionEditorModalBodyMock;
    @Mock
    private LabelElement createLabelMock;
    @Mock
    private LabelElement collectionCreationModeLabelMock;
    @Mock
    private SpanElement collectionCreationCreateLabelMock;
    @Mock
    private SpanElement collectionCreationCreateSpanMock;
    @Mock
    private SpanElement collectionCreationDefineLabelMock;
    @Mock
    private SpanElement collectionCreationDefineSpanMock;
    @Mock
    private ButtonElement saveButtonMock;
    @Mock
    private ButtonElement cancelButtonMock;
    @Mock
    private ButtonElement removeButtonMock;
    @Mock
    private ButtonElement addItemButtonMock;
    @Mock
    private InputElement createCollectionRadioMock;
    @Mock
    private InputElement defineCollectionRadioMock;
    @Mock
    private SpanElement addItemButtonLabelMock;
    @Mock
    private DivElement createCollectionContainerMock;
    @Mock
    private Style createCollectionContainerStyleMock;
    @Mock
    private DivElement defineCollectionContainerMock;
    @Mock
    private Style defineCollectionContainerStyleMock;
    @Mock
    private DivElement addItemButtonContainerMock;
    @Mock
    private Style addItemButtonContainerStyleMock;
    @Mock
    private TextAreaElement expressionElementMock;

    @Before
    public void setup() {
        when(collectionEditorModalBodyMock.getStyle()).thenReturn(styleMock);
        when(createCollectionContainerMock.getStyle()).thenReturn(createCollectionContainerStyleMock);
        when(defineCollectionContainerMock.getStyle()).thenReturn(defineCollectionContainerStyleMock);
        when(addItemButtonContainerMock.getStyle()).thenReturn(addItemButtonContainerStyleMock);
        this.collectionEditorViewImplSpy = spy(new CollectionViewImpl() {
            {
                this.presenter = collectionPresenterMock;
                this.collectionEditorModalBody = collectionEditorModalBodyMock;
                this.createLabel = createLabelMock;
                this.collectionCreationModeLabel = collectionCreationModeLabelMock;
                this.collectionCreationCreateLabel = collectionCreationCreateLabelMock;
                this.collectionCreationCreateSpan = collectionCreationCreateSpanMock;
                this.collectionCreationDefineLabel = collectionCreationDefineLabelMock;
                this.collectionCreationDefineSpan = collectionCreationDefineSpanMock;
                this.createCollectionContainer = createCollectionContainerMock;
                this.defineCollectionContainer = defineCollectionContainerMock;
                this.addItemButtonContainer = addItemButtonContainerMock;
                this.expressionElement = expressionElementMock;
                this.saveButton = saveButtonMock;
                this.cancelButton = cancelButtonMock;
                this.removeButton = removeButtonMock;
                this.addItemButtonLabel = addItemButtonLabelMock;
                this.addItemButton = addItemButtonMock;
                this.createCollectionRadio = createCollectionRadioMock;
                this.defineCollectionRadio = defineCollectionRadioMock;
            }
        });
    }

    @Test
    public void initListStructure() {
        collectionEditorViewImplSpy.initListStructure("key", Collections.EMPTY_MAP, Collections.EMPTY_MAP, ScenarioSimulationModel.Type.DMN);
        verify(collectionEditorViewImplSpy, times(1)).commonInit(eq(ScenarioSimulationModel.Type.DMN));
        verify(createLabelMock, atLeast(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        verify(collectionCreationModeLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionListCreation());
        verify(collectionCreationCreateLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelList());
        verify(collectionCreationCreateSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelListDescription());
        verify(collectionCreationDefineLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelList());
        verify(collectionCreationDefineSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelListDescription());
        verify(collectionPresenterMock, times(1)).initListStructure(eq("key"), eq(Collections.EMPTY_MAP), eq(Collections.EMPTY_MAP), eq(collectionEditorViewImplSpy));
        assertTrue(collectionEditorViewImplSpy.listWidget);
    }

    @Test
    public void initMapStructure() {
        collectionEditorViewImplSpy.initMapStructure("key", Collections.EMPTY_MAP, Collections.EMPTY_MAP, ScenarioSimulationModel.Type.DMN);
        verify(collectionEditorViewImplSpy, times(1)).commonInit(eq(ScenarioSimulationModel.Type.DMN));
        verify(createLabelMock, atLeast(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        verify(collectionCreationModeLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionMapCreation());
        verify(collectionCreationCreateLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap());
        verify(collectionCreationCreateSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.createLabelMapDescription());
        verify(collectionCreationDefineLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMap());
        verify(collectionCreationDefineSpanMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.defineLabelMapDescription());
        verify(collectionPresenterMock, times(1)).initMapStructure(eq("key"), eq(Collections.EMPTY_MAP), eq(Collections.EMPTY_MAP), eq(collectionEditorViewImplSpy));
        assertFalse(collectionEditorViewImplSpy.listWidget);
    }

    @Test
    public void commonInit_RuleScenario() {
        collectionEditorViewImplSpy.commonInit(ScenarioSimulationModel.Type.RULE);
        assertEquals(ScenarioSimulationModel.Type.RULE, collectionEditorViewImplSpy.scenarioType);
        verify(saveButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.saveButton());
        verify(cancelButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
        verify(removeButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.removeButton());
        verify(addItemButtonLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionEditorAddNewItem());
        verify(collectionEditorViewImplSpy, times(1)).enableCreateCollectionContainer(eq(true));
        verify(collectionEditorViewImplSpy, times(1)).initAndRegisterHandlerForExpressionTextArea();
    }

    @Test
    public void commonInit_DMNScenario() {
        collectionEditorViewImplSpy.commonInit(ScenarioSimulationModel.Type.DMN);
        assertEquals(ScenarioSimulationModel.Type.DMN, collectionEditorViewImplSpy.scenarioType);
        verify(saveButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.saveButton());
        verify(cancelButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
        verify(removeButtonMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.removeButton());
        verify(addItemButtonLabelMock, times(1)).setInnerText(ScenarioSimulationEditorConstants.INSTANCE.collectionEditorAddNewItem());
        verify(collectionEditorViewImplSpy, times(1)).enableCreateCollectionContainer(eq(true));
        verify(collectionEditorViewImplSpy, never()).initAndRegisterHandlerForExpressionTextArea();
    }

    @Test
    public void setValue() {
        String testValue = "TEST-JSON";
        collectionEditorViewImplSpy.setValue(testValue);
        verify(collectionPresenterMock, times(1)).setValue(eq(testValue));
    }

    @Test
    public void onCloseCollectionEditorButtonClick() {
        collectionEditorViewImplSpy.onCloseCollectionEditorButtonClick(clickEventMock);
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(CloseCompositeEvent.class));
    }

    @Test
    public void onCancelButtonClick() {
        collectionEditorViewImplSpy.onCancelButtonClick(clickEventMock);
        verify(collectionEditorViewImplSpy, times(1)).close();
    }

    @Test
    public void onRemoveButtonClick() {
        collectionEditorViewImplSpy.onRemoveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).remove();
    }

    @Test
    public void onSaveButtonClick() {
        collectionEditorViewImplSpy.onSaveButtonClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).save();
    }

    @Test
    public void onAddItemButton() {
        collectionEditorViewImplSpy.onAddItemButton(clickEventMock);
        verify(collectionPresenterMock, times(1)).showEditingBox();
    }

    @Test
    public void onFaAngleRightClick() {
        doReturn(true).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(true));
        reset(collectionPresenterMock);
        reset(clickEventMock);
        doReturn(false).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.onFaAngleRightClick(clickEventMock);
        verify(collectionPresenterMock, times(1)).onToggleRowExpansion(eq(false));
    }

    @Test
    public void toggleRowExpansion() {
        doReturn(true).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.toggleRowExpansion();
        verify(collectionEditorViewImplSpy, times(1)).toggleRowExpansion(false);
        reset(collectionEditorViewImplSpy);
        doReturn(false).when(collectionEditorViewImplSpy).isShown();
        collectionEditorViewImplSpy.toggleRowExpansion();
        verify(collectionEditorViewImplSpy, times(1)).toggleRowExpansion(true);
    }

    @Test
    public void updateValue() {
        collectionEditorViewImplSpy.updateValue("TEST_VALUE");
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(SaveEditorEvent.class));
    }

    @Test
    public void close() {
        collectionEditorViewImplSpy.close();
        verify(collectionEditorViewImplSpy, times(1)).fireEvent(isA(CloseCompositeEvent.class));
    }

    @Test
    public void setFixedHeight() {
        double value = 23.0;
        Style.Unit unit = Style.Unit.PX;
        collectionEditorViewImplSpy.setFixedHeight(value, unit);
        verify(styleMock, times(1)).setHeight(eq(value), eq(unit));
    }

    @Test
    public void enableCreateCollectionContainer_CreateList() {
        enableCreateCollectionContainer(true, true);
    }

    @Test
    public void enableCreateCollectionContainer_CreateMap() {
        enableCreateCollectionContainer(true, false);
    }

    @Test
    public void enableCreateCollectionContainer_DefineList() {
        enableCreateCollectionContainer(false, true);
    }

    @Test
    public void enableCreateCollectionContainer_DefineMap() {
        enableCreateCollectionContainer(false, false);
    }

    private void enableCreateCollectionContainer(boolean toEnable, boolean isList) {
        collectionEditorViewImplSpy.listWidget = isList;
        collectionEditorViewImplSpy.enableCreateCollectionContainer(toEnable);
        verify(collectionEditorViewImplSpy, times(1)).showCreateCollectionContainer(eq(toEnable));
        verify(collectionEditorViewImplSpy, times(1)).showDefineCollectionContainer(eq(!toEnable));
        verify(collectionEditorViewImplSpy, times(1)).showAddItemButtonContainer(eq(toEnable));
        if (isList) {
            verify(createLabelMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.createLabelList()));
        } else {
            verify(createLabelMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.createLabelMap()));
        }
    }

    @Test
    public void checkExpressionSyntax_Rule() {
        collectionEditorViewImplSpy.scenarioType = ScenarioSimulationModel.Type.RULE;
        collectionEditorViewImplSpy.ensureExpressionSyntax();
        verify(expressionElementMock, times(1)).setValue(anyString());
    }

    @Test
    public void checkExpressionSyntax_DMN() {
        collectionEditorViewImplSpy.scenarioType = ScenarioSimulationModel.Type.DMN;
        collectionEditorViewImplSpy.ensureExpressionSyntax();
        verify(expressionElementMock, never()).setValue(anyString());
    }

    @Test
    public void setExpression() {
        collectionEditorViewImplSpy.setExpression("test");
        verify(collectionEditorViewImplSpy, times(1)).enableCreateCollectionContainer(eq(false));
        verify(expressionElementMock, times(1)).setValue(eq("test"));
    }

    @Test
    public void enableEditingMode_False() {
        collectionEditorViewImplSpy.enableEditingMode(false);
        verify(createCollectionRadioMock).setDisabled(eq(false));
        verify(defineCollectionRadioMock).setDisabled(eq(false));
        verify(addItemButtonMock).setDisabled(eq(false));
        verify(cancelButtonMock).setDisabled(eq(false));
        verify(removeButtonMock).setDisabled(eq(false));
        verify(saveButtonMock).setDisabled(eq(false));
    }

    @Test
    public void enableEditingMode_True() {
        collectionEditorViewImplSpy.enableEditingMode(true);
        verify(createCollectionRadioMock).setDisabled(eq(true));
        verify(defineCollectionRadioMock).setDisabled(eq(true));
        verify(addItemButtonMock).setDisabled(eq(true));
        verify(cancelButtonMock).setDisabled(eq(true));
        verify(removeButtonMock).setDisabled(eq(true));
        verify(saveButtonMock).setDisabled(eq(true));
    }

    @Test
    public void showCreateCollectionContainer() {
        collectionEditorViewImplSpy.showCreateCollectionContainer(false);
        checkStyleDisplay(createCollectionContainerStyleMock, false);
        collectionEditorViewImplSpy.showCreateCollectionContainer(true);
        checkStyleDisplay(createCollectionContainerStyleMock, true);
    }

    @Test
    public void showDefineCollectionContainer() {
        collectionEditorViewImplSpy.showDefineCollectionContainer(false);
        checkStyleDisplay(defineCollectionContainerStyleMock, false);
        collectionEditorViewImplSpy.showDefineCollectionContainer(true);
        checkStyleDisplay(defineCollectionContainerStyleMock, true);
    }

    @Test
    public void showAddItemButtonContainer() {
        collectionEditorViewImplSpy.showAddItemButtonContainer(false);
        checkStyleDisplay(addItemButtonContainerStyleMock, false);
        collectionEditorViewImplSpy.showAddItemButtonContainer(true);
        checkStyleDisplay(addItemButtonContainerStyleMock, true);
    }

    private void checkStyleDisplay(Style styleElement, boolean show) {
        if (show) {
            verify(styleElement, times(1)).setDisplay(Style.Display.BLOCK);
        } else {
            verify(styleElement, times(1)).setDisplay(Style.Display.NONE);
        }
    }
}
