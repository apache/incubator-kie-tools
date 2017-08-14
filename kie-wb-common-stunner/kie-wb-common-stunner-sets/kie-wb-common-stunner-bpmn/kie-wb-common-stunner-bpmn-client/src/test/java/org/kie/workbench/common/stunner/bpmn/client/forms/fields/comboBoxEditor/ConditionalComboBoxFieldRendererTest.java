/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldType;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindingUtils;
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

import static org.mockito.Mockito.*;

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

  @Spy
  @InjectMocks
  private ConditionalComboBoxFieldRenderer conditionalComboBoxFieldRenderer = new ConditionalComboBoxFieldRenderer(comboBoxFixedValuesWidgetView,
                                                                                                                   adapterManager);

  @Before
  public void setup() {
    when(conditionalComboBoxFieldDefinition.getRelatedField()).thenReturn("onEntryAction;onExitAction");
  }

  @Test
  public void refreshFieldCondition() throws Exception {
    reset(comboBoxFixedValuesWidgetView);
    reset(conditionalComboBoxFieldRenderer);

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
  public void init() throws Exception {
    reset(comboBoxFixedValuesWidgetView);
    reset(conditionalComboBoxFieldRenderer);
    reset(adapterManager);
    reset(renderingContext);

    EmbeddedSubprocess embeddedSubprocess = new EmbeddedSubprocess.EmbeddedSubprocessBuilder().build();
    OnEntryAction onEntryAction = embeddedSubprocess.getOnEntryAction();
    OnExitAction onExitAction = embeddedSubprocess.getOnExitAction();

    when(renderingContext.getModel()).thenReturn(embeddedSubprocess);
    when(adapterManager.forProperty()).thenReturn(Mockito.mock(PropertyAdapter.class));
    when(adapterManager.forProperty().getValue(onEntryAction)).thenReturn("value");
    when(adapterManager.forProperty().getValue(onExitAction)).thenReturn("");

    PowerMockito.mockStatic(ClientBindingUtils.class);
    BDDMockito.given(ClientBindingUtils.getProxiedValue(embeddedSubprocess,
                                                        "onEntryAction")).willReturn(onEntryAction);
    BDDMockito.given(ClientBindingUtils.getProxiedValue(embeddedSubprocess,
                                                        "onExitAction")).willReturn(onExitAction);

    conditionalComboBoxFieldRenderer.init(renderingContext,
                                          conditionalComboBoxFieldDefinition);

    verify(conditionalComboBoxFieldRenderer,
           never()).setReadOnly(true);

    when(adapterManager.forProperty().getValue(onEntryAction)).thenReturn(null);
    when(adapterManager.forProperty().getValue(onExitAction)).thenReturn("");

    verify(conditionalComboBoxFieldRenderer,
           times(1)).setReadOnly(false);
  }

  @Test
  public void getName() throws Exception {
    Assert.assertEquals(conditionalComboBoxFieldRenderer.getName(),
                        ConditionalComboBoxFieldType.NAME);
  }

  @Test
  public void getSupportedCode() throws Exception {
    Assert.assertEquals(conditionalComboBoxFieldRenderer.getSupportedCode(),
                        ConditionalComboBoxFieldType.NAME);
  }

  @Test
  public void getSupportedFieldDefinition() throws Exception {
    Assert.assertEquals(conditionalComboBoxFieldRenderer.getSupportedFieldDefinition(),
                        ConditionalComboBoxFieldDefinition.class);
  }
}