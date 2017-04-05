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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.CalculationTypePage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.OperatorPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ConditionColumnPluginTest {

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
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private ConditionCol52 editingCol;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @InjectMocks
    private ConditionColumnPlugin plugin = spy(new ConditionColumnPlugin(patternPage,
                                                                         calculationTypePage,
                                                                         fieldPage,
                                                                         operatorPage,
                                                                         valueOptionsPage,
                                                                         additionalInfoPage,
                                                                         changeEvent,
                                                                         translationService));

    @Before
    public void setup() {
        final GuidedDecisionTable52.TableFormat tableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;

        doReturn(tableFormat).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
    }

    @Test
    public void testGetPagesWhenItIsAnExtendedEntryTable() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        final List<WizardPage> pages = plugin.getPages();

        assertTrue(pages.stream().anyMatch(a -> a instanceof CalculationTypePage));
        assertEquals(6,
                     pages.size());
    }

    @Test
    public void testGetPagesWhenItIsALimitedEntryTable() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();

        final List<WizardPage> pages = plugin.getPages();

        assertFalse(pages.stream().anyMatch(a -> a instanceof CalculationTypePage));
        assertEquals(5,
                     pages.size());
    }

    @Test
    public void testIsValidWhenHeaderIsBlank() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_YouMustEnterAColumnHeaderValueDescription;
        final String errorMessage = "YouMustEnterAColumnHeaderValueDescription";

        doReturn("").when(editingCol).getHeader();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(errorMessage).when(translationService).format(errorKey);

        final boolean isValid = plugin.isValid();

        assertFalse(isValid);
        verify(plugin).showError(eq(errorMessage));
    }

    @Test
    public void testIsValidWhenConstraintValueIsNotPredicateAndFactFieldIsBlank() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_PleaseSelectOrEnterField;
        final String errorMessage = "PleaseSelectOrEnterField";

        doReturn("Header").when(editingCol).getHeader();
        doReturn("").when(editingCol).getFactField();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();
        doReturn(errorMessage).when(translationService).format(errorKey);

        final boolean isValid = plugin.isValid();

        assertFalse(isValid);
        verify(plugin).showError(eq(errorMessage));
    }

    @Test
    public void testIsValidWhenConstraintValueIsNotPredicateAndOperatorIsBlank() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_NotifyNoSelectedOperator;
        final String errorMessage = "NotifyNoSelectedOperator";

        doReturn("Header").when(editingCol).getHeader();
        doReturn("FactField").when(editingCol).getFactField();
        doReturn("").when(editingCol).getOperator();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();
        doReturn(errorMessage).when(translationService).format(errorKey);

        final boolean isValid = plugin.isValid();

        assertFalse(isValid);
        verify(plugin).showError(eq(errorMessage));
    }

    @Test
    public void testIsValidWhenBindingIsNotUnique() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern;
        final String errorMessage = "PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern";

        doReturn("Header").when(editingCol).getHeader();
        doReturn("FactField").when(editingCol).getFactField();
        doReturn("Operator").when(editingCol).getOperator();
        doReturn("Binding").when(editingCol).getBinding();
        doReturn(true).when(plugin).isBindingNotUnique();
        doReturn(true).when(editingCol).isBound();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();
        doReturn(errorMessage).when(translationService).format(errorKey);

        final boolean isValid = plugin.isValid();

        assertFalse(isValid);
        verify(plugin).showError(eq(errorMessage));
    }

    @Test
    public void testIsValidWhenHeaderIsNotUnique() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_ThatColumnNameIsAlreadyInUsePleasePickAnother;
        final String errorMessage = "ThatColumnNameIsAlreadyInUsePleasePickAnother";

        doReturn("Header").when(editingCol).getHeader();
        doReturn("FactField").when(editingCol).getFactField();
        doReturn("Operator").when(editingCol).getOperator();
        doReturn("Binding").when(editingCol).getBinding();
        doReturn(false).when(plugin).isBindingNotUnique();
        doReturn(true).when(plugin).isHeaderNotUnique();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();
        doReturn(errorMessage).when(translationService).format(errorKey);

        final boolean isValid = plugin.isValid();

        assertFalse(isValid);
        verify(plugin).showError(eq(errorMessage));
    }

    @Test
    public void testIsValidWhenItIsValid() throws Exception {
        doReturn("Header").when(editingCol).getHeader();
        doReturn("FactField").when(editingCol).getFactField();
        doReturn("Operator").when(editingCol).getOperator();
        doReturn("Binding").when(editingCol).getBinding();
        doReturn(false).when(plugin).isBindingNotUnique();
        doReturn(false).when(plugin).isHeaderNotUnique();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();

        final boolean isValid = plugin.isValid();

        assertTrue(isValid);
        verify(plugin,
               never()).showError(any());
    }

    @Test
    public void testPrepareValuesWhenConstraintValueIsPredicate() throws Exception {
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_PREDICATE).when(plugin).constraintValue();

        plugin.prepareValues();

        verify(editingCol).setOperator(null);
    }

    @Test
    public void testPrepareValuesWhenConstraintValueIsNotLiteral() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(editingCol).when(plugin).editingCol();

        plugin.prepareValues();

        verify(editingCol).setBinding(null);
    }

    @Test
    public void testAppendColumn() throws Exception {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final Pattern52 pattern52 = mock(Pattern52.class);

        doReturn(editingCol).when(plugin).editingCol();
        doReturn(patternWrapper).when(plugin).patternWrapper();
        doReturn(pattern52).when(plugin).editingPattern();

        plugin.appendColumn();

        verify(presenter).appendColumn(pattern52,
                                       editingCol);
    }

    @Test
    public void testGenerateColumnWhenItIsValid() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();
        doReturn(true).when(plugin).isValid();

        plugin.setupDefaultValues();

        final Boolean result = plugin.generateColumn();

        assertTrue(result);
        verify(plugin).prepareValues();
        verify(plugin).appendColumn();
    }

    @Test
    public void testGenerateColumnWhenItIsNotValid() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();
        doReturn(false).when(plugin).isValid();

        final Boolean result = plugin.generateColumn();

        assertFalse(result);
        verify(plugin,
               never()).prepareValues();
        verify(plugin,
               never()).appendColumn();
    }

    @Test
    public void testGetEditingPattern() throws Exception {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        plugin.setEditingPattern(patternWrapper);

        verify(plugin).setupDefaultValues();
        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(calculationTypePage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(operatorPage);
        verify(plugin).fireChangeEvent(valueOptionsPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGetEntryPointNameWheeunEditingPatternIsNotPresent() throws Exception {
        final PatternWrapper patternWrapper = new PatternWrapper();

        doReturn(patternWrapper).when(plugin).patternWrapper();

        final String entryPointName = plugin.getEntryPointName();

        assertEquals("",
                     entryPointName);
    }

    @Test
    public void testGetEntryPointNameWhenEditingPatternIsNotNull() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);

        doReturn("EntryPointName").when(pattern).getEntryPointName();
        doReturn(pattern).when(plugin).patternWrapper();

        final String entryPointName = plugin.getEntryPointName();

        assertEquals("EntryPointName",
                     entryPointName);
    }

    @Test
    public void testSetEntryPointName() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);

        doReturn(pattern).when(plugin).patternWrapper();

        plugin.setEntryPointName("EntryPointName");

        verify(pattern).setEntryPointName("EntryPointName");
    }

    @Test
    public void testEditingColWhenEditingPatternIsNull() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);

        doReturn("").when(pattern).getFactType();
        doReturn(pattern).when(plugin).patternWrapper();

        plugin.editingCol();

        verify(plugin).resetFieldAndOperator();
    }

    @Test
    public void testEditingColWhenEditingPatternIsNotNull() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);

        doReturn("factType").when(pattern).getFactType();
        doReturn(pattern).when(plugin).patternWrapper();

        plugin.editingCol();

        verify(plugin,
               never()).resetFieldAndOperator();
    }

    @Test
    public void testSetHeader() throws Exception {
        final String header = "Header";

        doReturn(editingCol).when(plugin).editingCol();

        plugin.setHeader(header);

        verify(editingCol).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGetFactType() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);
        final String expectedFactType = "FactType";

        doReturn(expectedFactType).when(pattern).getFactType();
        doReturn(pattern).when(plugin).patternWrapper();

        final String factType = plugin.getFactType();

        assertEquals(expectedFactType,
                     factType);
    }

    @Test
    public void testGetFactField() throws Exception {
        final String expectedFactField = "FactField";

        doReturn(expectedFactField).when(editingCol).getFactField();

        final String factField = plugin.getFactField();

        assertEquals(expectedFactField,
                     factField);
    }

    @Test
    public void testSetFactField() throws Exception {
        final PatternWrapper pattern = mock(PatternWrapper.class);

        doReturn("FactField").when(editingCol).getFactField();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn("FactType").when(pattern).getFactType();
        doReturn(pattern).when(plugin).patternWrapper();
        doReturn(oracle).when(presenter).getDataModelOracle();

        plugin.setFactField("FactField");

        verify(editingCol).setFactField("FactField");
        verify(editingCol).setFieldType(oracle.getFieldType("FactField",
                                                            "FactType"));

        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(operatorPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
        verify(plugin).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testSetOperator() throws Exception {
        doReturn(editingCol).when(plugin).editingCol();

        plugin.setOperator("operator");

        verify(editingCol).setOperator("operator");
        verify(plugin).fireChangeEvent(operatorPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
        verify(plugin).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testConstraintValueWhenItHasEnums() throws Exception {
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(true).when(oracle).hasEnums(anyString(),
                                             anyString());

        final int constraintValue = plugin.constraintValue();

        verify(plugin).setConstraintValueFieldAndUpdateEditingCol(BaseSingleFieldConstraint.TYPE_LITERAL);

        assertEquals(BaseSingleFieldConstraint.TYPE_LITERAL,
                     constraintValue);
    }

    @Test
    public void testConstraintValueWhenItDoesNotHaveEnums() throws Exception {
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(false).when(oracle).hasEnums(anyString(),
                                              anyString());

        final int constraintValue = plugin.constraintValue();

        verify(plugin,
               never()).setConstraintValueFieldAndUpdateEditingCol(BaseSingleFieldConstraint.TYPE_LITERAL);

        assertEquals(BaseSingleFieldConstraint.TYPE_UNDEFINED,
                     constraintValue);
    }

    @Test
    public void testSetConstraintValue() throws Exception {
        final int constraintValue = BaseSingleFieldConstraint.TYPE_LITERAL;

        doReturn(editingCol).when(plugin).editingCol();

        plugin.setConstraintValue(constraintValue);

        verify(editingCol).setConstraintValueType(constraintValue);
        verify(plugin).setConstraintValueFieldAndUpdateEditingCol(constraintValue);
        verify(plugin).resetFieldAndOperator();
        verify(plugin).fireChangeEvent(calculationTypePage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(operatorPage);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsCompleted() throws Exception {
        doReturn(true).when(plugin).isValueOptionsPageCompleted();

        plugin.setValueOptionsPageAsCompleted();

        verify(plugin,
               never()).setValueOptionsPageCompleted();
        verify(plugin,
               never()).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsNotCompleted() throws Exception {
        doReturn(false).when(plugin).isValueOptionsPageCompleted();

        plugin.setValueOptionsPageAsCompleted();

        verify(plugin).setValueOptionsPageCompleted();
        verify(plugin).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testSetValueList() throws Exception {
        final String valueList = "valueList";
        final DTCellValue52 cellValue52 = mock(DTCellValue52.class);
        final PatternWrapper pattern = mock(PatternWrapper.class);
        final DataType.DataTypes dataTypes = DataType.DataTypes.STRING;

        plugin.setupDefaultValues();

        doReturn("FactType").when(pattern).getFactType();
        doReturn(dataTypes).when(cellValue52).getDataType();
        doReturn(cellValue52).when(editingCol).getDefaultValue();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(pattern).when(plugin).patternWrapper();
        doReturn(oracle).when(presenter).getDataModelOracle();

        plugin.setValueList(valueList);

        verify(editingCol).setValueList(valueList);
        verify(plugin).assertDefaultValue();
        verify(plugin).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.ConditionColumnPlugin_AddNewConditionSimpleColumn;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testInit() {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(plugin).model();

        plugin.init(wizard);

        verify(plugin).setupDefaultValues();
    }
}
