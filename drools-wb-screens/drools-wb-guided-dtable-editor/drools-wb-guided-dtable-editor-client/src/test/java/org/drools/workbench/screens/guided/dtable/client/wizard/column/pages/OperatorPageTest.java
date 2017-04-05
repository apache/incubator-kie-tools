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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.css.GuidedRuleEditorCss;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.ItemImages;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class OperatorPageTest {

    @Captor
    ArgumentCaptor<Callback<String[]>> callbackArgumentCaptor;

    @Captor
    ArgumentCaptor<String[]> stringArrayArgumentCaptor;

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private TranslationService translationService;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private Pattern52 pattern52;

    @Mock
    private ConditionCol52 editingCol;

    @Mock
    private OperatorPage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> eventSourceMock;

    @InjectMocks
    private OperatorPage page = spy(new OperatorPage(view,
                                                     eventSourceMock,
                                                     translationService));

    @BeforeClass
    public static void setupPreferences() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(plugin.editingCol()).thenReturn(editingCol);
        when(page.plugin()).thenReturn(plugin);
    }

    @Test
    public void testIsConstraintValuePredicateWhenConstraintValueIsPredicate() {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);

        assertTrue(page.isConstraintValuePredicate());
    }

    @Test
    public void testIsConstraintValuePredicateWhenConstraintValueIsNotPredicate() {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);

        assertFalse(page.isConstraintValuePredicate());
    }

    @Test
    public void testCanSetOperatorWhenEditingColIsBlank() {
        when(plugin.getFactField()).thenReturn("");

        assertFalse(page.hasFactField());
    }

    @Test
    public void testCanSetOperatorWhenEditingColIsNull() {
        when(plugin.getFactField()).thenReturn(null);

        assertFalse(page.hasFactField());
    }

    @Test
    public void testCanSetOperatorWhenEditingColIsNotNull() {
        when(plugin.getFactField()).thenReturn("factField");

        assertTrue(page.hasFactField());
    }

    @Test
    public void testGetOperator() {
        final String expectedOperator = "operator";

        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.editingCol()).thenReturn(editingCol);
        when(editingCol.getOperator()).thenReturn(expectedOperator);

        final String operator = page.getOperator();

        verify(plugin).editingCol();
        verify(editingCol).getOperator();

        assertEquals(expectedOperator,
                     operator);
    }

    @Test
    public void testOperatorDropdownWhenOperatorCanBeSet() {
        registerFakeProvider();

        when(translationService.format(GuidedDecisionTableErraiConstants.OperatorPage_NoOperator)).thenReturn("(no operator)");
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.editingCol()).thenReturn(editingCol);

        mockGetOperatorCompletionsToReturn(OperatorsOracle.STANDARD_OPERATORS);

        spyOperatorsDropdown();

        page.operatorDropdown(widget -> {
            assertTrue(widget instanceof CEPOperatorsDropdown);

            final CEPOperatorsDropdown operatorsDropdown = (CEPOperatorsDropdown) widget;

            verify(operatorsDropdown).insertItem("(no operator)",
                                                 "",
                                                 1);
            verify(operatorsDropdown).addValueChangeHandler(any());
        });
    }

    @Test
    public void testOperatorDropdownWhenOperatorCanNotBeSet() {
        final Element elementMock = mock(Element.class);
        final ListBox listBoxMock = mock(ListBox.class);

        when(listBoxMock.getElement()).thenReturn(elementMock);
        when(plugin.getFactField()).thenReturn("");
        when(translationService.format(GuidedDecisionTableErraiConstants.OperatorPage_PleaseChoose)).thenReturn("Choose...");

        doReturn(listBoxMock).when(page).newListBox();

        page.operatorDropdown(widget -> {
            assertTrue(widget instanceof ListBox);

            verify(listBoxMock).addItem("Choose...");
            verify(elementMock).setAttribute("disabled",
                                             "disabled");
        });
    }

    @Test
    public void testFilterOptionsForConstraintTypeLiteralWhenConstraintValueIsLiteral() {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);

        final String[] result = page.filterOptionsForConstraintTypeLiteral(OperatorsOracle.EXPLICIT_LIST_OPERATORS);
        final List<String> operators = Arrays.asList(result);

        assertTrue(operators.contains("in"));
        assertTrue(operators.contains("not in"));
        assertEquals(2,
                     operators.size());
    }

    @Test
    public void testFilterOptionsForConstraintTypeLiteralWhenConstraintValueIsNotLiteral() {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);

        final String[] result = page.filterOptionsForConstraintTypeLiteral(OperatorsOracle.EXPLICIT_LIST_OPERATORS);
        final List<String> operators = Arrays.asList(result);

        assertFalse(operators.contains("in"));
        assertFalse(operators.contains("not in"));
        assertEquals(0,
                     operators.size());
    }

    @Test
    public void testGetOperatorCompletions() {
        when(plugin.getFactType()).thenReturn("factType");
        when(plugin.getFactField()).thenReturn("factField");
        when(presenter.getDataModelOracle()).thenReturn(oracle);

        page.getOperatorCompletions(s -> {
        });

        verify(oracle).getOperatorCompletions(eq("factType"),
                                              eq("factField"),
                                              any());
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.OperatorPage_Operator;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).init(page);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    @Test
    public void testIsCompleteWhenFactFieldIsNull() {
        when(editingCol.getOperator()).thenReturn("operator");
        when(plugin.getFactField()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenOperatorIsNull() {
        when(editingCol.getOperator()).thenReturn(null);
        when(plugin.getFactField()).thenReturn("factType");

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenFactFieldAndOperatorAreNotNull() {
        when(editingCol.getOperator()).thenReturn("operator");
        when(plugin.getFactField()).thenReturn("factType");

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testSetOperator() {
        final String operator = "operator";

        page.setOperator(operator);

        verify(plugin).setOperator(operator);
    }

    @Test
    public void testNewListBox() {
        final ListBox listBox = page.newListBox();

        assertNotNull(listBox);
    }

    private void spyOperatorsDropdown() {
        doAnswer(answer -> {
            final Object firstArgument = answer.getArguments()[0];
            final String[] operators = (String[]) firstArgument;

            return spy(new CEPOperatorsDropdown(operators,
                                                editingCol));
        }).when(page).newCepOperatorsDropdown(any());
    }

    private void mockGetOperatorCompletionsToReturn(final String[] standardOperators) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Callback<String[]> callback = callbackArgumentCaptor.getValue();

                callback.callback(standardOperators);

                return null;
            }
        }).when(page).getOperatorCompletions(callbackArgumentCaptor.capture());
    }

    private void registerFakeProvider() {
        GwtMockito.useProviderForType(GuidedRuleEditorResources.class,
                                      fakeProvider());
    }

    private FakeProvider<GuidedRuleEditorResources> fakeProvider() {
        return provider -> new GuidedRuleEditorResources() {
            @Override
            public ItemImages itemImages() {
                return mock(ItemImages.class);
            }

            @Override
            public GuidedRuleEditorCss css() {
                return mock(GuidedRuleEditorCss.class);
            }

            @Override
            public GuidedRuleEditorImages images() {
                return mock(GuidedRuleEditorImages.class);
            }
        };
    }
}
