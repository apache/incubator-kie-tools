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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPageTest {

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private PatternWrapper pattern52;

    @Mock
    private ConditionCol52 editingCol;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private FieldPage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private PatternPage<ConditionColumnPlugin> patternPage;

    @Mock
    private CalculationTypePage calculationTypePage;

    @Mock
    private FieldPage fieldPage;

    @Mock
    private OperatorPage operatorPage;

    @Mock
    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    @Mock
    private ValueOptionsPage valueOptionsPage;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Captor
    private ArgumentCaptor<Consumer<String>> consumer;

    @InjectMocks
    private FieldPage<ConditionColumnPlugin> page = spy(new FieldPage<ConditionColumnPlugin>(view,
                                                                                             translationService));

    @BeforeClass
    public static void setupPreferences() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(page.plugin()).thenReturn(plugin);
    }

    @Test
    public void testSetEditingCol() throws Exception {
        page.setEditingCol("factField");

        verify(plugin).setFactField(eq("factField"));
    }

    @Test
    public void testIsConstraintValuePredicateWhenItIsTypePredicate() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);

        assertTrue(page.isConstraintValuePredicate());
    }

    @Test
    public void testIsConstraintValuePredicateWhenItIsNotTypePredicate() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED);

        assertFalse(page.isConstraintValuePredicate());
    }

    @Test
    public void testHasEditingPatternWhenFactPatternIsNotNull() throws Exception {
        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);

        assertTrue(page.hasEditingPattern());
    }

    @Test
    public void testHasEditingPatternWhenFactPatternIsNull() throws Exception {
        when(pattern52.getFactType()).thenReturn("");
        when(plugin.patternWrapper()).thenReturn(pattern52);

        assertFalse(page.hasEditingPattern());
    }

    @Test
    public void testIsConstraintRetValueWhenItIsTypeRetValue() throws Exception {
        page.filterEnumFields();

        verify(plugin).filterEnumFields();
    }

    @Test
    public void testForEachFactFieldWhenEditingPatternIsNotNull() throws Exception {
        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(plugin.getAccessor()).thenReturn(FieldAccessorsAndMutators.ACCESSOR);
        when(presenter.getDataModelOracle()).thenReturn(oracle);

        page.forEachFactField(s -> {
        });

        verify(oracle).getFieldCompletions(eq("factType"),
                                           eq(FieldAccessorsAndMutators.ACCESSOR),
                                           any());
    }

    @Test
    public void testForEachFactFieldWhenEditingPatternIsNull() throws Exception {
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(presenter.getDataModelOracle()).thenReturn(oracle);

        page.forEachFactField(s -> {
        });

        verify(oracle,
               never()).getFieldCompletions(any(),
                                            any(),
                                            any());
    }

    @Test
    public void testFieldsCallbackWhenConstraintIsRetValue() throws Exception {
        final ConditionColumnPlugin plugin = spy(new ConditionColumnPlugin(patternPage,
                                                                           calculationTypePage,
                                                                           fieldPage,
                                                                           operatorPage,
                                                                           valueOptionsPage,
                                                                           additionalInfoPage,
                                                                           changeEvent,
                                                                           translationService));

        doReturn(BaseSingleFieldConstraint.TYPE_RET_VALUE).when(plugin).constraintValue();

        when(page.plugin()).thenReturn(plugin);
        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(true);

        final List<String> expected = new ArrayList<String>() {{
            add("modelField1");
            add("modelField3");
        }};

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1"),
                modelField("modelField2"),
                modelField("modelField3")
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);
    }

    @Test
    public void testFieldsCallbackWhenConstraintIsNotRetValue() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();

        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(true);

        final List<String> expected = new ArrayList<String>() {{
            add("modelField1");
            add("modelField2");
            add("modelField3");
        }};

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1"),
                modelField("modelField2"),
                modelField("modelField3")
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);
    }

    @Test
    public void testFieldsCallbackWhenConstraintIsRetValueButItDoesNotHaveEnums() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_RET_VALUE).when(plugin).constraintValue();

        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(false);

        final List<String> expected = new ArrayList<String>() {{
            add("modelField1");
            add("modelField2");
            add("modelField3");
        }};

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1"),
                modelField("modelField2"),
                modelField("modelField3")
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);
    }

    @Test
    public void testIsCompleteWhenFactFieldIsNull() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        when(plugin.getFactField()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenFactFieldIsNotNull() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        when(plugin.getFactField()).thenReturn("factField");

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testIsCompleteWhenConstraintValueIsPredicate() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);
        when(plugin.getFactField()).thenReturn(null);

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testGetFactField() {
        page.getFactField();

        verify(plugin).getFactField();
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.FieldPage_Field;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        doReturn(pattern52).when(plugin).patternWrapper();

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
    public void testSetupField() {
        final String factField = "factField";

        doReturn(factField).when(page).getFactField();

        doNothing().when(page).forEachFactField(this.consumer.capture());

        page.setupField();

        final Consumer<String> consumer = this.consumer.getValue();

        consumer.accept(factField);

        verify(view).addItem(factField,
                             factField);
        verify(view).setupFieldList();
        verify(view).selectField(factField);
    }

    private ModelField modelField(final String name) {
        return new ModelField(name,
                              null,
                              null,
                              null,
                              null,
                              null);
    }
}
