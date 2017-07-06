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

import java.util.HashMap;
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
        doReturn(true).when(plugin).isNewColumn();
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
    public void testPrepareValuesWhenConstraintValueIsPredicate() throws Exception {
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(BaseSingleFieldConstraint.TYPE_PREDICATE).when(plugin).constraintValue();

        plugin.prepareValues();

        verify(editingCol).setOperator(plugin.operatorPlaceholder());
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
    public void testPrepareValuesWhenConstraintValueIsNotLiteralAndTableFormatIsLimitedEntry() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(editingCol).when(plugin).editingCol();

        plugin.prepareValues();

        verify(editingCol,
               never()).setBinding(null);
    }

    @Test
    public void testAppendColumnWhenColumnIsNew() throws Exception {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final Pattern52 pattern52 = mock(Pattern52.class);

        doReturn(true).when(plugin).isNewColumn();

        doReturn(editingCol).when(plugin).editingCol();
        doReturn(patternWrapper).when(plugin).patternWrapper();
        doReturn(pattern52).when(plugin).editingPattern();

        plugin.appendColumn();

        verify(presenter).appendColumn(pattern52,
                                       editingCol);
    }

    @Test
    public void testAppendColumnWhenColumnIsNotNew() throws Exception {
        final ConditionCol52 originalColumn = mock(ConditionCol52.class);
        final ConditionCol52 editingColumn = mock(ConditionCol52.class);
        final Pattern52 originalPattern = mock(Pattern52.class);
        final Pattern52 editingPattern = mock(Pattern52.class);

        doReturn(false).when(plugin).isNewColumn();

        doReturn(originalColumn).when(plugin).originalCondition();
        doReturn(editingColumn).when(plugin).editingCol();
        doReturn(originalPattern).when(plugin).getOriginalPattern52();
        doReturn(editingPattern).when(plugin).editingPattern();

        plugin.appendColumn();

        verify(presenter).updateColumn(originalPattern,
                                       originalColumn,
                                       editingPattern,
                                       editingColumn);
    }

    @Test
    public void testGenerateColumn() throws Exception {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();

        plugin.setupDefaultValues();

        final Boolean result = plugin.generateColumn();

        assertTrue(result);
        verify(plugin).prepareValues();
        verify(plugin).appendColumn();
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

    @Test
    public void testGetAlreadyUsedColumnNames() throws Exception {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        Pattern52 pattern = new Pattern52();
        ConditionCol52 conditionOne = new ConditionCol52() {{
            setHeader("a");
        }};
        ConditionCol52 conditionTwo = new ConditionCol52() {{
            setHeader("b");
        }};
        pattern.getChildColumns().add(conditionOne);
        pattern.getChildColumns().add(conditionTwo);
        model.getConditions().add(pattern);
        when(presenter.getModel()).thenReturn(model);

        assertEquals(2,
                     plugin.getAlreadyUsedColumnHeaders().size());
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("a"));
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("b"));
    }

    public void testIsBindableWhenTableIsAnExtendedEntry() {
        doReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED).when(plugin).constraintValue();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        assertFalse(plugin.isBindable());
    }

    @Test
    public void testIsBindableWhenTableIsAnExtendedEntryAndConstraintValueIsTypeLiteral() {
        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(plugin).constraintValue();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        assertTrue(plugin.isBindable());
    }

    @Test
    public void testIsBindableWhenTableIsALimitedEntry() {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();

        assertTrue(plugin.isBindable());
    }

    @Test
    public void testSetupDefaultValuesWhenColumnIsNew() {
        final Pattern52 pattern52 = mock(Pattern52.class);
        final ConditionCol52 conditionCol52 = mock(ConditionCol52.class);

        doReturn(true).when(plugin).isNewColumn();
        doReturn(pattern52).when(plugin).emptyPattern();
        doReturn(conditionCol52).when(plugin).newConditionColumn();
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(false).when(oracle).hasEnums(any(),
                                              any());

        plugin.setupDefaultValues();

        assertEquals(pattern52,
                     plugin.editingPattern());
        assertEquals(conditionCol52,
                     plugin.editingCol());
        assertEquals(BaseSingleFieldConstraint.TYPE_UNDEFINED,
                     plugin.constraintValue());
        assertEquals(Boolean.FALSE,
                     plugin.isValueOptionsPageCompleted());
    }

    @Test
    public void testSetupDefaultValuesWhenColumnIsNotNew() {
        final Pattern52 originalPattern52 = mock(Pattern52.class);
        final Pattern52 clonedPattern52 = mock(Pattern52.class);
        final ConditionCol52 originalConditionCol52 = mock(ConditionCol52.class);
        final ConditionCol52 clonedConditionCol52 = mock(ConditionCol52.class);

        doReturn(BaseSingleFieldConstraint.TYPE_LITERAL).when(clonedConditionCol52).getConstraintValueType();
        doReturn(originalConditionCol52).when(plugin).getOriginalColumnConfig52();
        doReturn(clonedConditionCol52).when(plugin).clone(originalConditionCol52);
        doReturn(originalPattern52).when(plugin).getOriginalPattern52();
        doReturn(clonedPattern52).when(originalPattern52).clonePattern();
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(false).when(plugin).isNewColumn();
        doReturn(false).when(oracle).hasEnums(any(),
                                              any());

        plugin.setupDefaultValues();

        assertEquals(clonedPattern52,
                     plugin.editingPattern());
        assertEquals(BaseSingleFieldConstraint.TYPE_LITERAL,
                     plugin.constraintValue());
        assertEquals(Boolean.TRUE,
                     plugin.isValueOptionsPageCompleted());
        assertEquals(clonedConditionCol52,
                     plugin.editingCol());
    }

    @Test
    public void testCloneDTCellValueWhenDTCellValue52IsNull() {
        assertNull(plugin.cloneDTCellValue(null));
    }

    @Test
    public void testCloneDTCellValueWhenDTCellValue52IsNotNull() {
        final DTCellValue52 dcv = new DTCellValue52() {{
            setStringValue("value");
        }};
        final DTCellValue52 clone = plugin.cloneDTCellValue(dcv);

        assertEquals(dcv,
                     clone);
        assertNotSame(dcv,
                      clone);
    }

    @Test
    public void testCloneWhenColumnIsALimitedEntryConditionCol52() {

    }

    @Test
    public void testCloneWhenColumnIsAConditionCol52() {
        final int constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        final String factField = "FactField";
        final String fieldType = "FieldType";
        final String header = "Header";
        final String operator = "Operator";
        final String valueList = "ValueList";
        final DTCellValue52 defaultValue = null;
        final boolean hideColumn = false;
        final HashMap<String, String> parameters = new HashMap<>();
        final int width = 999;
        final String binding = "Binding";

        final ConditionCol52 column = makeConditionCol52(constraintValueType,
                                                         factField,
                                                         fieldType,
                                                         header,
                                                         operator,
                                                         valueList,
                                                         defaultValue,
                                                         hideColumn,
                                                         parameters,
                                                         width,
                                                         binding);
        final ConditionCol52 clone = plugin.clone(column);

        assertEquals(constraintValueType,
                     clone.getConstraintValueType());
        assertEquals(factField,
                     clone.getFactField());
        assertEquals(fieldType,
                     clone.getFieldType());
        assertEquals(header,
                     clone.getHeader());
        assertEquals(operator,
                     clone.getOperator());
        assertEquals(valueList,
                     clone.getValueList());
        assertEquals(defaultValue,
                     clone.getDefaultValue());
        assertEquals(hideColumn,
                     clone.isHideColumn());
        assertEquals(parameters,
                     clone.getParameters());
        assertEquals(width,
                     clone.getWidth());
        assertEquals(binding,
                     clone.getBinding());
        assertNotSame(column,
                      clone);
    }

    private ConditionCol52 makeConditionCol52(final int constraintValueType,
                                              final String factField,
                                              final String fieldType,
                                              final String header,
                                              final String operator,
                                              final String valueList,
                                              final DTCellValue52 defaultValue,
                                              final boolean hideColumn,
                                              final HashMap<String, String> parameters,
                                              final int width,
                                              final String binding) {

        final ConditionCol52 column = new ConditionCol52();

        column.setConstraintValueType(constraintValueType);
        column.setFactField(factField);
        column.setFieldType(fieldType);
        column.setHeader(header);
        column.setOperator(operator);
        column.setValueList(valueList);
        column.setDefaultValue(defaultValue);
        column.setHideColumn(hideColumn);
        column.setParameters(parameters);
        column.setWidth(width);
        column.setBinding(binding);

        return column;
    }
}
