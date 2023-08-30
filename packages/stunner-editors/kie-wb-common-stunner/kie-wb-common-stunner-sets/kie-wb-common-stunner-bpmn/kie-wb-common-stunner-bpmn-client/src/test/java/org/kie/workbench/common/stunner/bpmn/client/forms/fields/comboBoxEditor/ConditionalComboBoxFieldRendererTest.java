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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldType;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindingUtils;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientBindingUtils.class)
public class ConditionalComboBoxFieldRendererTest {

    @Mock
    private ComboBoxFixedValuesWidgetView comboBoxFixedValuesWidgetView;

    @Mock
    private ConditionalComboBoxFieldDefinition conditionalComboBoxFieldDefinition;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private FormRenderingContext renderingContext;

    @Mock
    private FormRenderingContext renderingContextParent;

    @Mock
    private ClientTranslationService translationService;

    @Spy
    @InjectMocks
    private ConditionalComboBoxFieldRenderer conditionalComboBoxFieldRenderer = new ConditionalComboBoxFieldRenderer(comboBoxFixedValuesWidgetView,
                                                                                                                     translationService,
                                                                                                                     adapterManager);

    @Test
    public void refreshFieldCondition() throws Exception {
        resetMocks();

        conditionalComboBoxFieldRenderer.refreshFieldCondition(null);
        conditionalComboBoxFieldRenderer.refreshFieldCondition("");
        conditionalComboBoxFieldRenderer.refreshFieldCondition(" ");
        conditionalComboBoxFieldRenderer.refreshFieldCondition("Value");

        InOrder inOrder = Mockito.inOrder(comboBoxFixedValuesWidgetView);
        inOrder.verify(comboBoxFixedValuesWidgetView,
                       times(3)).setReadOnly(true);
        inOrder.verify(comboBoxFixedValuesWidgetView,
                       times(1)).setReadOnly(false);
    }

    @Test
    public void init() {
        resetMocks();

        EmbeddedSubprocess embeddedSubprocess = new EmbeddedSubprocess();
        OnEntryAction onEntryAction = embeddedSubprocess.getExecutionSet().getOnEntryAction();
        OnExitAction onExitAction = embeddedSubprocess.getExecutionSet().getOnExitAction();

        //static mock
        PowerMockito.mockStatic(ClientBindingUtils.class);
        BDDMockito.given(ClientBindingUtils.getProxiedValue(embeddedSubprocess,
                                                            "onEntryAction")).willReturn(onEntryAction);
        BDDMockito.given(ClientBindingUtils.getProxiedValue(embeddedSubprocess,
                                                            "onExitAction")).willReturn(onExitAction);

        //instances mock
        when(conditionalComboBoxFieldDefinition.getRelatedField()).thenReturn("onEntryAction;onExitAction");
        when(renderingContext.getModel()).thenReturn(embeddedSubprocess);
        when(adapterManager.forProperty()).thenReturn(Mockito.mock(PropertyAdapter.class));
        when(adapterManager.forProperty().getValue(onEntryAction)).thenReturn("value");
        when(adapterManager.forProperty().getValue(onExitAction)).thenReturn("");

        conditionalComboBoxFieldRenderer.init(renderingContext,
                                              conditionalComboBoxFieldDefinition);

        verify(conditionalComboBoxFieldRenderer,
               never()).setReadOnly(true);

        when(adapterManager.forProperty().getValue(onEntryAction)).thenReturn(null);
        when(adapterManager.forProperty().getValue(onExitAction)).thenReturn("");

        conditionalComboBoxFieldRenderer.init(renderingContext,
                                              conditionalComboBoxFieldDefinition);

        verify(conditionalComboBoxFieldRenderer,
               times(1)).setReadOnly(false);
    }

    private void resetMocks() {
        reset(comboBoxFixedValuesWidgetView);
        reset(conditionalComboBoxFieldRenderer);
        reset(adapterManager);
        reset(renderingContext);
    }

    @Test
    public void initWithDefinitionSet() throws Exception {
        resetMocks();

        SequenceFlow sequenceFlow = new SequenceFlow();
        SequenceFlowExecutionSet sequenceFlowExecutionSet = sequenceFlow.getExecutionSet();
        ConditionExpression conditionExpression = sequenceFlowExecutionSet.getConditionExpression();

        //static mock
        PowerMockito.mockStatic(ClientBindingUtils.class);
        BDDMockito.given(ClientBindingUtils.getProxiedValue(sequenceFlow,
                                                            "executionSet")).willReturn(sequenceFlowExecutionSet);
        BDDMockito.given(ClientBindingUtils.getProxiedValue(sequenceFlowExecutionSet,
                                                            "conditionExpression")).willReturn(conditionExpression);

        //instances mock
        when(conditionalComboBoxFieldDefinition.getRelatedField()).thenReturn("executionSet.conditionExpression");
        when(renderingContext.getModel()).thenReturn(null);
        when(renderingContext.getParentContext()).thenReturn(renderingContextParent);
        when(renderingContextParent.getModel()).thenReturn(sequenceFlow);
        when(adapterManager.forProperty()).thenReturn(Mockito.mock(PropertyAdapter.class));

        when(adapterManager.forProperty().getValue(conditionExpression)).thenReturn("value");
        conditionalComboBoxFieldRenderer.init(renderingContext,
                                              conditionalComboBoxFieldDefinition);
        verify(conditionalComboBoxFieldRenderer,
               never()).setReadOnly(true);
        verify(conditionalComboBoxFieldRenderer,
               times(1)).setReadOnly(false);

        when(adapterManager.forProperty().getValue(conditionExpression)).thenReturn("");
        conditionalComboBoxFieldRenderer.init(renderingContext,
                                              conditionalComboBoxFieldDefinition);
        verify(conditionalComboBoxFieldRenderer,
               times(1)).setReadOnly(true);

        when(adapterManager.forProperty().getValue(conditionExpression)).thenReturn(null);
        conditionalComboBoxFieldRenderer.init(renderingContext,
                                              conditionalComboBoxFieldDefinition);
        verify(conditionalComboBoxFieldRenderer,
               atLeastOnce()).setReadOnly(true);
    }

    @Test
    public void getName() throws Exception {
        Assert.assertEquals(conditionalComboBoxFieldRenderer.getName(),
                            ConditionalComboBoxFieldType.NAME);
    }
}
