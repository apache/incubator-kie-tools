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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Lists;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private FieldPage<ConditionColumnPlugin> fieldPage;

    @Mock
    private OperatorPage operatorPage;

    @Mock
    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    @Mock
    private ValueOptionsPage<ConditionColumnPlugin> valueOptionsPage;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Captor
    private ArgumentCaptor<Consumer<String>> consumer;

    @InjectMocks
    private FieldPage<ConditionColumnPlugin> page = spy(new FieldPage<>(view,
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
        doReturn(editingCol).when(plugin).editingCol();
        doReturn("modelField3").when(editingCol).getFactField();

        when(page.plugin()).thenReturn(plugin);
        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(true);

        final List<String> expected = new Lists.Builder<String>()
                .add("modelField1")
                .add("modelField3")
                .build();

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1",
                           DataType.TYPE_OBJECT),
                modelField("modelField2",
                           DataType.TYPE_OBJECT),
                modelField("modelField3",
                           DataType.TYPE_OBJECT)
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsLoadedCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);

        verify(view).setupEmptyFieldList();
        verify(view).selectField("modelField3");
    }

    @Test
    public void testFieldsCallbackWhenConstraintIsNotRetValue() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();

        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(plugin.getFactField()).thenReturn("modelField3");
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(true);

        final List<String> expected = new Lists.Builder<String>()
                .add("modelField1")
                .add("modelField2")
                .add("modelField3")
                .build();

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1",
                           DataType.TYPE_OBJECT),
                modelField("modelField2",
                           DataType.TYPE_OBJECT),
                modelField("modelField3",
                           DataType.TYPE_OBJECT)
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsLoadedCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);

        verify(view).setupEmptyFieldList();
        verify(view).selectField("modelField3");
    }

    @Test
    public void testFieldsCallbackWhenConstraintIsRetValueButItDoesNotHaveEnums() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_RET_VALUE).when(plugin).constraintValue();

        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.patternWrapper()).thenReturn(pattern52);
        when(plugin.getFactField()).thenReturn("modelField3");
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(oracle.hasEnums("factType",
                             "modelField2")).thenReturn(false);

        final List<String> expected = new Lists.Builder<String>()
                .add("modelField1")
                .add("modelField2")
                .add("modelField3")
                .build();

        final List<String> result = new ArrayList<>();

        final ModelField[] modelFields = new ModelField[]{
                modelField("modelField1",
                           DataType.TYPE_OBJECT),
                modelField("modelField2",
                           DataType.TYPE_OBJECT),
                modelField("modelField3",
                           DataType.TYPE_OBJECT)
        };

        final Callback<ModelField[]> fieldsCallback = page.fieldsLoadedCallback(result::add);

        fieldsCallback.callback(modelFields);

        assertEquals(expected,
                     result);

        verify(view).setupEmptyFieldList();
        verify(view).selectField("modelField3");
    }

    @Test
    public void testIsCompleteWhenFactFieldIsNull() throws Exception {

        when(plugin.isFieldBindingValid()).thenReturn(true);
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        when(plugin.getFactField()).thenReturn(null);

        page.isComplete(Assert::assertFalse);

        verify(page).fieldBindingWarningToggle(true);
        verify(page).factFieldWarningToggle(false);
    }

    @Test
    public void testIsCompleteWhenBindingIsNotValid() throws Exception {

        when(plugin.isFieldBindingValid()).thenReturn(false);
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        when(plugin.getFactField()).thenReturn("factField");

        page.isComplete(Assert::assertFalse);

        verify(page).fieldBindingWarningToggle(false);
        verify(page).factFieldWarningToggle(true);
    }

    @Test
    public void testIsCompleteWhenFactFieldIsNotNull() throws Exception {

        when(plugin.isFieldBindingValid()).thenReturn(true);
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        when(plugin.getFactField()).thenReturn("factField");

        page.isComplete(Assert::assertTrue);

        verify(page).fieldBindingWarningToggle(true);
        verify(page).factFieldWarningToggle(true);
    }

    @Test
    public void testIsCompleteWhenConstraintValueIsPredicate() throws Exception {

        when(plugin.isFieldBindingValid()).thenReturn(true);
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);
        when(plugin.getFactField()).thenReturn(null);

        page.isComplete(Assert::assertTrue);

        verify(page).fieldBindingWarningToggle(true);
        verify(page).factFieldWarningToggle(true);
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
        verify(page).setupPatternWarningMessages();
        verify(page).setupPredicateBindingInfoBox();
        verify(page).setupBinding();
        verify(page).setupField();
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    @Test
    public void testSetupFieldWhenConstraintValueIsNotPredicate() {
        final String factField = "factField";

        doReturn(false).when(page).isConstraintValuePredicate();
        doReturn(factField).when(page).getFactField();

        doNothing().when(page).forEachFactField(consumer.capture());

        page.setupField();

        consumer.getValue().accept(factField);

        verify(view).enableListFieldView();
        verify(view).addItem(factField, factField);
    }

    @Test
    public void testSetupFieldWhenConstraintValueIsPredicate() {

        final String factField = "factField";

        doReturn(true).when(page).isConstraintValuePredicate();
        doReturn(factField).when(page).getFactField();

        page.setupField();

        view.enablePredicateFieldView();
        view.setField(factField);
    }

    @Test
    public void testSetupFieldWhenConstraintValueIsFormula() {
        final String factField = "age";
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(BaseSingleFieldConstraint.TYPE_RET_VALUE).when(plugin).constraintValue();
        doReturn(new PatternWrapper("Person", "p")).when(plugin).patternWrapper();
        doReturn(factField).when(page).getFactField();

        page.setupField();

        verify(view).enableListFieldView();
        verify(page).forEachFactField(any(Consumer.class));
        verify(view, never()).enablePredicateFieldView();
    }

    @Test
    public void testSetupPatternWarningMessagesWhenFactTypeIsNotNil() {

        final String factType = "factType";

        doReturn(factType).when(page).factType();

        page.setupPatternWarningMessages();

        verify(view).patternWarningToggle(false);
    }

    @Test
    public void testSetupPatternWarningMessagesWhenFactTypeIsNil() {

        final String factType = "";

        doReturn(factType).when(page).factType();

        page.setupPatternWarningMessages();

        verify(view).patternWarningToggle(true);
    }

    @Test
    public void testFactFieldWarningToggleWhenFactFieldIsValid() {

        final boolean isFactFieldValid = true;

        page.factFieldWarningToggle(isFactFieldValid);

        verify(view, never()).showSelectFieldWarning();
        verify(view).hideSelectFieldWarning();
    }

    @Test
    public void testFactFieldWarningToggleWhenFactFieldIsNotValid() {

        final boolean isFactFieldValid = false;

        page.factFieldWarningToggle(isFactFieldValid);

        verify(view).showSelectFieldWarning();
        verify(view, never()).hideSelectFieldWarning();
    }

    @Test
    public void testFieldBindingWarningToggleWhenFieldBindingIsValid() {

        final boolean isFieldBindingValid = true;

        page.fieldBindingWarningToggle(isFieldBindingValid);

        verify(view, never()).showFieldBindingWarning();
        verify(view).hideFieldBindingWarning();
    }

    @Test
    public void testFieldBindingWarningToggleWhenFieldBindingIsNotValid() {

        final boolean isFieldBindingValid = false;

        page.fieldBindingWarningToggle(isFieldBindingValid);

        verify(view).showFieldBindingWarning();
        verify(view, never()).hideFieldBindingWarning();
    }

    @Test
    public void testSetupPredicateBindingInfoBoxWhenConstraintValueIsPredicate() {

        doReturn(BaseSingleFieldConstraint.TYPE_PREDICATE).when(plugin).constraintValue();

        page.setupPredicateBindingInfoBox();

        verify(view).showPredicateBindingInfo();
        verify(view, never()).hidePredicateBindingInfo();
    }

    @Test
    public void testSetupPredicateBindingInfoBoxWhenConstraintValueIsNotPredicate() {

        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();

        page.setupPredicateBindingInfoBox();

        verify(view).hidePredicateBindingInfo();
        verify(view, never()).showPredicateBindingInfo();
    }

    @Test
    public void testSetupPredicateBindingInfoBoxWhenConstraintValueIsFormula() {

        doReturn(BaseSingleFieldConstraint.TYPE_RET_VALUE).when(plugin).constraintValue();

        page.setupPredicateBindingInfoBox();

        verify(view).hidePredicateBindingInfo();
        verify(view, never()).showPredicateBindingInfo();
    }

    @Test
    public void testSetupBindingWhenItIsBindable() {

        final TextBox textBox = mock(TextBox.class);
        final boolean isBindable = true;

        doReturn(isBindable).when(plugin).isBindable();
        doReturn(textBox).when(page).newBindingTextBox();

        page.setupBinding();

        verify(view).setupBinding(textBox);
        verify(view).bindingToggle(isBindable);
    }

    @Test
    public void testSetupBindingWhenItIsNotBindable() {

        final TextBox textBox = mock(TextBox.class);
        final boolean isBindable = false;

        doReturn(isBindable).when(plugin).isBindable();
        doReturn(textBox).when(page).newBindingTextBox();

        page.setupBinding();

        verify(view, never()).setupBinding(textBox);
        verify(view).bindingToggle(isBindable);
    }
}
