/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor.factPattern;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.HasConstraints;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@WithClassesToStub({Button.class})
@RunWith(GwtMockitoTestRunner.class)
public class PopupCreatorTest {

    private PopupCreator popupCreator;

    @Before
    public void setUp() {
        popupCreator = spy(new PopupCreator());
    }

    @Test
    public void testMakeExpressionEditorButton() {

        final HasConstraints hasConstraints = mock(HasConstraints.class);
        final FormStylePopup popup = mock(FormStylePopup.class);
        final Button button = mock(Button.class);
        final ClickHandler clickHandler = mock(ClickHandler.class);

        doReturn(button).when(popupCreator).makeExpressionEditorButton();
        doReturn(clickHandler).when(popupCreator).onExpressionEditorButtonClick(hasConstraints, popup);

        popupCreator.makeExpressionEditorButton(hasConstraints, popup);

        verify(button).addClickHandler(clickHandler);
    }

    @Test
    public void testOnExpressionEditorButtonClick() {

        final String factType = "factType";
        final FactPattern factPattern = mock(FactPattern.class);
        final HasConstraints hasConstraints = mock(HasConstraints.class);
        final FormStylePopup popup = mock(FormStylePopup.class);
        final SingleFieldConstraintEBLeftSide constraint = mock(SingleFieldConstraintEBLeftSide.class);
        final RuleModeller ruleModeller = mock(RuleModeller.class);
        final ClickEvent clickEvent = mock(ClickEvent.class);

        doReturn(factType).when(factPattern).getFactType();
        doReturn(constraint).when(popupCreator).makeSingleFieldConstraintEBLeftSide(factType);
        doReturn(ruleModeller).when(popupCreator).getModeller();
        doReturn(factPattern).when(popupCreator).getPattern();

        final ClickHandler clickHandler = popupCreator.onExpressionEditorButtonClick(hasConstraints, popup);

        clickHandler.onClick(clickEvent);

        verify(hasConstraints).addConstraint(constraint);
        verify(ruleModeller).refreshWidget();
        verify(popup).hide();
    }

    @Test
    public void testMakeSingleFieldConstraintEBLeftSide() {

        final String expectedFactType = "factType";
        final ExpressionFormLine expectedExpressionFormLine = mock(ExpressionFormLine.class);
        final int expectedConstraintValueType = SingleFieldConstraint.TYPE_UNDEFINED;

        doReturn(expectedExpressionFormLine).when(popupCreator).makeExpressionFormLine(expectedFactType);

        final SingleFieldConstraintEBLeftSide constraint = popupCreator.makeSingleFieldConstraintEBLeftSide(expectedFactType);

        assertEquals(expectedConstraintValueType, constraint.getConstraintValueType());
        assertEquals(expectedExpressionFormLine, constraint.getExpressionLeftSide());
        assertEquals(expectedFactType, constraint.getFactType());
    }

    @Test
    public void testMakeExpressionFormLine() {

        final String expectedFactType = "factType";

        final ExpressionFormLine expressionFormLine = popupCreator.makeExpressionFormLine(expectedFactType);

        assertEquals(1, size(expressionFormLine));
        assertEquals(expectedFactType, first(expressionFormLine).getFactType());
    }

    private ExpressionUnboundFact first(final ExpressionFormLine expressionFormLine) {

        final ExpressionPart firstPart = expressionFormLine.getParts().get(0);

        return (ExpressionUnboundFact) firstPart;
    }

    private int size(final ExpressionFormLine expressionFormLine) {

        final List<ExpressionPart> parts = expressionFormLine.getParts();

        return parts.size();
    }
}
