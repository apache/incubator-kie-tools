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

package org.kie.workbench.common.dmn.client.canvas.controls.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsInformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObserverBuilderControlTest {

    @Mock
    private ObserverBuilderControl observerBuilderControl;

    @Before
    public void setup() {
        doCallRealMethod().when(observerBuilderControl).updateElementFromDefinition(anyObject(), anyObject());
    }

    @Test
    public void testUpdateNameFromDefinition() {

        final String expectedName = "expectedName";
        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasName newDefinition = mock(HasName.class);
        final Name newDefinitionName = mock(Name.class);
        final HasName definition = mock(HasName.class);
        final Name definitionName = mock(Name.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(newDefinition.getName()).thenReturn(newDefinitionName);
        when(definition.getName()).thenReturn(definitionName);

        when(definitionName.getValue()).thenReturn(expectedName);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinitionName).setValue(expectedName);
    }

    @Test
    public void testUpdateDynamicReadOnlyTrueFromDefinition() {
        testUpdateDynamicReadOnlyFromDefinition(true);
    }

    @Test
    public void testUpdateDynamicReadOnlyFalseFromDefinition() {
        testUpdateDynamicReadOnlyFromDefinition(false);
    }

    private void testUpdateDynamicReadOnlyFromDefinition(final boolean expectedDynamicReadOnlyValue) {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final DynamicReadOnly newDefinition = mock(DynamicReadOnly.class);
        final DynamicReadOnly definition = mock(DynamicReadOnly.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(definition.isAllowOnlyVisualChange()).thenReturn(expectedDynamicReadOnlyValue);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinition).setAllowOnlyVisualChange(expectedDynamicReadOnlyValue);
    }

    @Test
    public void testUpdateIdFromDefinition() {

        final String expectedId = "happyId";
        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final DMNElement newDefinition = mock(DMNElement.class);
        final Id newDefinitionId = mock(Id.class);
        final DMNElement definition = mock(DMNElement.class);
        final Id definitionId = mock(Id.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(newDefinition.getId()).thenReturn(newDefinitionId);
        when(definition.getId()).thenReturn(definitionId);
        when(definitionId.getValue()).thenReturn(expectedId);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinitionId).setValue(expectedId);
    }

    @Test
    public void testUpdateExpressionFromDefinition() {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasExpression newHasExpression = mock(HasExpression.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Expression expression = mock(Expression.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newHasExpression);
        when(hasExpression.getExpression()).thenReturn(expression);

        observerBuilderControl.updateElementFromDefinition(element, hasExpression);

        verify(newHasExpression).setExpression(expression);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateVariableFromDefinition() {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasVariable newHasVariable = mock(HasVariable.class);
        final HasVariable hasVariable = mock(HasVariable.class);
        final IsInformationItem isInformationItem = mock(IsInformationItem.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newHasVariable);
        when(hasVariable.getVariable()).thenReturn(isInformationItem);

        observerBuilderControl.updateElementFromDefinition(element, hasVariable);

        verify(newHasVariable).setVariable(isInformationItem);
    }
}

