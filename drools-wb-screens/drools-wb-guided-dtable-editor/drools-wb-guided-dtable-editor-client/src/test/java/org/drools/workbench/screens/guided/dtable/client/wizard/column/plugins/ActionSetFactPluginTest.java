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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionInsertFactWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionSetFactWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DefaultWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.LimitedWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({DefaultWidgetFactory.class, LimitedWidgetFactory.class})
public class ActionSetFactPluginTest {

    @Mock
    private PatternPage patternPage;

    @Mock
    private FieldPage fieldPage;

    @Mock
    private ValueOptionsPage<ActionSetFactPlugin> valueOptionsPage;

    @Mock
    private AdditionalInfoPage<ActionSetFactPlugin> additionalInfoPage;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTable52 model;

    @InjectMocks
    private ActionSetFactPlugin plugin = spy(new ActionSetFactPlugin(patternPage,
                                                                     fieldPage,
                                                                     valueOptionsPage,
                                                                     additionalInfoPage,
                                                                     changeEvent,
                                                                     translationService));

    @Before
    public void setup() {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(model).when(presenter).getModel();
    }

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.ActionInsertFactPlugin_SetTheValueOfAField;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testGetPages() {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(plugin).tableFormat();

        final List<WizardPage> pages = plugin.getPages();

        assertEquals(4,
                     pages.size());
    }

    @Test
    public void testGenerateColumn() {
        final ActionCol52 actionCol52 = mock(ActionCol52.class);
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionCol52).when(actionWrapper).getActionCol52();
        doReturn(actionWrapper).when(plugin).editingWrapper();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        verify(presenter).appendColumn(actionCol52);
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNewAndVetoed() throws Exception {
        doReturn(false).when(plugin).isNewColumn();
        doThrow(VetoException.class).when(presenter).updateColumn(Mockito.<ActionCol52>any(),
                                                                  Mockito.<ActionCol52>any());

        assertFalse(plugin.generateColumn());

        verify(wizard).showGenericVetoError();
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
    public void testSetEditingPattern() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        when(plugin.editingWrapper()).thenReturn(actionWrapper);
        when(patternWrapper.getFactType()).thenReturn("factType");
        when(patternWrapper.getBoundName()).thenReturn("boundName");

        plugin.setEditingPattern(patternWrapper);

        verify(actionWrapper).setFactField("");
        verify(actionWrapper).setFactType("factType");
        verify(actionWrapper).setBoundName("boundName");
        verify(actionWrapper).setType("");

        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testConstraintValue() {
        final int expectedConstraintValue = BaseSingleFieldConstraint.TYPE_UNDEFINED;
        final int actualConstraintValue = plugin.constraintValue();

        assertEquals(expectedConstraintValue,
                     actualConstraintValue);
    }

    @Test
    public void testGetFactType() {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final String expectedFactType = "factType";

        when(patternWrapper.getFactType()).thenReturn(expectedFactType);
        when(plugin.patternWrapper()).thenReturn(patternWrapper);

        final String actualFactType = plugin.getFactType();

        verify(patternWrapper).getFactType();
        assertEquals(expectedFactType,
                     actualFactType);
    }

    @Test
    public void testGetAccessor() {
        final FieldAccessorsAndMutators expectedAccessor = FieldAccessorsAndMutators.MUTATOR;
        final FieldAccessorsAndMutators actualAccessor = plugin.getAccessor();

        assertEquals(expectedAccessor,
                     actualAccessor);
    }

    @Test
    public void testFilterEnumFields() {
        assertFalse(plugin.filterEnumFields());
    }

    @Test
    public void testGetFactField() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String expectedFactField = "factField";

        when(actionWrapper.getFactField()).thenReturn(expectedFactField);
        when(plugin.editingWrapper()).thenReturn(actionWrapper);

        final String actualFactField = plugin.getFactField();

        verify(actionWrapper).getFactField();
        assertEquals(expectedFactField,
                     actualFactField);
    }

    @Test
    public void testSetFactFieldWhenFactPatternIsNew() {
        final ActionInsertFactWrapper actionWrapper = mock(ActionInsertFactWrapper.class);
        final PatternWrapper patternWrapperMock = patternWrapperMock("factType",
                                                                     "boundName");

        doReturn(true).when(plugin).isNewFactPattern();
        doReturn(actionWrapper).when(plugin).newActionInsertFactWrapper();
        doReturn(patternWrapperMock).when(plugin).patternWrapper();
        doReturn("type").when(oracle).getFieldType(any(),
                                                   any());

        plugin.setFactField("selectedValue");

        verify(actionWrapper).setFactField(eq("selectedValue"));
        verify(actionWrapper).setFactType(eq("factType"));
        verify(actionWrapper).setBoundName(eq("boundName"));
        verify(actionWrapper).setType(eq("type"));
        verify(plugin).fireChangeEvent(fieldPage);
    }

    @Test
    public void testSetFactFieldWhenFactPatternIsNotNew() {
        final ActionSetFactWrapper actionWrapper = spy(new ActionSetFactWrapper(plugin));
        final Pattern52 patternMock = patternMock("factType");
        final PatternWrapper patternWrapperMock = patternWrapperMock("factType",
                                                                     "boundName");

        doReturn(false).when(plugin).isNewFactPattern();
        doReturn(actionWrapper).when(plugin).newActionSetFactWrapper();
        doReturn(patternWrapperMock).when(plugin).patternWrapper();
        doReturn(patternMock).when(model).getConditionPattern(eq("boundName"));
        doReturn("type").when(oracle).getFieldType(eq("factType"),
                                                   eq("selectedValue"));

        plugin.setFactField("selectedValue");

        verify(actionWrapper).setFactField(eq("selectedValue"));
        verify(actionWrapper).setFactType(eq("factType"));
        verify(actionWrapper).setBoundName(eq("boundName"));
        verify(actionWrapper).setType(eq("type"));
        verify(plugin).fireChangeEvent(fieldPage);
    }

    @Test
    public void testSetFactFieldWhenColumnIsNotNew() {
        final ActionInsertFactWrapper actionWrapper = mock(ActionInsertFactWrapper.class);
        final PatternWrapper patternWrapperMock = patternWrapperMock("factType",
                                                                     "boundName");

        doReturn(false).when(plugin).isNewColumn();
        doReturn(true).when(plugin).isNewFactPattern();
        doReturn(actionWrapper).when(plugin).editingWrapper();
        doReturn(patternWrapperMock).when(plugin).patternWrapper();
        doReturn("type").when(oracle).getFieldType(any(),
                                                   any());

        plugin.setFactField("selectedValue");

        verify(actionWrapper).setFactField(eq("selectedValue"));
        verify(actionWrapper).setFactType(eq("factType"));
        verify(actionWrapper).setBoundName(eq("boundName"));
        verify(actionWrapper).setType(eq("type"));
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin,
               never()).newActionInsertFactWrapper();
    }

    private PatternWrapper patternWrapperMock(final String factType,
                                              final String boundName) {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(factType).when(patternWrapper).getFactType();
        doReturn(boundName).when(patternWrapper).getBoundName();
        return patternWrapper;
    }

    private Pattern52 patternMock(final String factType) {
        final Pattern52 pattern = mock(Pattern52.class);

        doReturn(factType).when(pattern).getFactType();

        return pattern;
    }

    @Test
    public void testEditingPattern() {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(patternWrapper).when(plugin).patternWrapper();

        when(patternWrapper.getFactType()).thenReturn("factType");
        when(patternWrapper.getBoundName()).thenReturn("boundName");
        when(patternWrapper.isNegated()).thenReturn(false);
        when(patternWrapper.getEntryPointName()).thenReturn("entryPoint");

        final Pattern52 pattern52 = plugin.editingPattern();

        assertEquals("factType",
                     pattern52.getFactType());
        assertEquals("boundName",
                     pattern52.getBoundName());
        assertEquals(false,
                     pattern52.isNegated());
        assertEquals("entryPoint",
                     pattern52.getEntryPointName());
    }

    @Test
    public void testEditingCol() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.editingCol();

        verify(actionWrapper).getActionCol52();
    }

    @Test
    public void testGetHeader() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.getHeader();

        verify(actionWrapper).getHeader();
    }

    @Test
    public void testSetHeader() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String header = "header";

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setHeader(header);

        verify(actionWrapper).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testSetInsertLogical() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final boolean insertLogical = false;

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setInsertLogical(insertLogical);

        verify(actionWrapper).setInsertLogical(insertLogical);
    }

    @Test
    public void testSetUpdate() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final boolean update = false;

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setUpdate(update);

        verify(actionWrapper).setUpdate(update);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNew() {
        doReturn(true).when(plugin).isNewFactPattern();

        final boolean showUpdateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertEquals(false,
                     showUpdateEngineWithChanges);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNotNew() {
        doReturn(mock(ActionSetFactWrapper.class)).when(plugin).editingWrapper();

        final boolean showUpdateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertEquals(true,
                     showUpdateEngineWithChanges);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNew() {
        doReturn(mock(ActionInsertFactWrapper.class)).when(plugin).editingWrapper();

        final boolean showLogicallyInsert = plugin.showLogicallyInsert();

        assertEquals(true,
                     showLogicallyInsert);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNotNew() {
        doReturn(false).when(plugin).isNewFactPattern();

        final boolean showLogicallyInsert = plugin.showLogicallyInsert();

        assertEquals(false,
                     showLogicallyInsert);
    }

    @Test
    public void testGetValueList() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.getValueList();

        verify(actionWrapper).getValueList();
    }

    @Test
    public void testSetValueList() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String valueList = "valueList";

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setValueList(valueList);

        verify(actionWrapper).setValueList(valueList);
    }

    @Test
    public void testTableFormat() {
        final GuidedDecisionTable52.TableFormat expectedTableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getTableFormat()).thenReturn(expectedTableFormat);
        when(presenter.getModel()).thenReturn(model);

        final GuidedDecisionTable52.TableFormat actualTableFormat = plugin.tableFormat();

        assertEquals(expectedTableFormat,
                     actualTableFormat);
    }

    @Test
    public void testDefaultValueWidget() {
        final IsWidget defaultWidget = plugin.defaultValueWidget();

        assertNotNull(defaultWidget);
    }

    @Test
    public void testLimitedValueWidget() {
        final IsWidget limitedValueWidget = plugin.limitedValueWidget();

        assertNotNull(limitedValueWidget);
    }

    @Test
    public void testInitializedPatternPage() {
        plugin.initializedPatternPage();

        verify(patternPage).disableEntryPoint();
    }

    @Test
    public void testInitializedAdditionalInfoPage() throws Exception {
        plugin.initializedAdditionalInfoPage();

        verify(additionalInfoPage).setPlugin(plugin);
        verify(additionalInfoPage).enableHeader();
        verify(additionalInfoPage).enableHideColumn();
        verify(additionalInfoPage).enableLogicallyInsert();
    }

    @Test
    public void testInitializedValueOptionsPageWhenTableIsALimitedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(plugin).tableFormat();

        plugin.initializedValueOptionsPage();

        verify(valueOptionsPage).setPlugin(plugin);
        verify(valueOptionsPage).enableLimitedValue();
    }

    @Test
    public void testGetAlreadyUsedColumnNames() throws Exception {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.getActionCols().add(new ActionCol52() {{
            setHeader("a");
        }});
        model.getActionCols().add(new ActionCol52() {{
            setHeader("b");
        }});
        when(presenter.getModel()).thenReturn(model);

        assertEquals(2,
                     plugin.getAlreadyUsedColumnHeaders().size());
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("a"));
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("b"));
    }

    @Test
    public void testNewActionWrapperWhenColumnIsAnActionInsertFactCol52() throws Exception {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        final ActionWrapper wrapper = plugin.newActionWrapper(mock(ActionInsertFactCol52.class));

        assertTrue(wrapper instanceof ActionInsertFactWrapper);
    }

    @Test
    public void testNewActionWrapperWhenColumnIsAnActionSetFactWrapper() throws Exception {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        final ActionWrapper wrapper = plugin.newActionWrapper(mock(ActionSetFieldCol52.class));

        assertTrue(wrapper instanceof ActionSetFactWrapper);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewActionWrapperWhenColumnIsInvalid() throws Exception {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        plugin.newActionWrapper(mock(ConditionCol52.class));
    }

    @Test
    public void testNewPatternWrapperWhenPatternIsFound() throws Exception {
        final PatternWrapper expectedWrapper = mockPatternWrapper("boundName");
        final Set<PatternWrapper> actionWrappers = new HashSet<PatternWrapper>() {{
            add(expectedWrapper);
        }};

        doReturn(actionWrappers).when(plugin).getPatterns();

        final PatternWrapper actualWrapper = plugin.newPatternWrapper(mockActionWrapper("boundName",
                                                                                        "factType"));

        assertSame(expectedWrapper,
                   actualWrapper);
    }

    @Test
    public void testNewPatternWrapperWhenPatternIsNotFound() throws Exception {
        final Set<PatternWrapper> actionWrappers = new HashSet<>();
        final ActionWrapper actionWrapper = mockActionWrapper("boundName",
                                                              "factType");

        doReturn(actionWrappers).when(plugin).getPatterns();

        final PatternWrapper patternWrapper = plugin.newPatternWrapper(actionWrapper);

        assertEquals(actionWrapper.getBoundName(),
                     patternWrapper.getBoundName());
        assertEquals(actionWrapper.getFactType(),
                     patternWrapper.getFactType());
    }

    @Test
    public void testSetupValuesWhenColumnIsNew() throws Exception {
        doReturn(true).when(plugin).isNewColumn();

        plugin.setupValues();

        verify(plugin,
               never()).setValueOptionsPageAsCompleted();
        verify(plugin,
               never()).fireChangeEvent(patternPage);
        verify(plugin,
               never()).fireChangeEvent(fieldPage);
        verify(plugin,
               never()).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testSetupValuesWhenColumnIsNotNew() throws Exception {
        final DTColumnConfig52 column = mock(DTColumnConfig52.class);
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(column).when(plugin).getOriginalColumnConfig52();
        doReturn(actionWrapper).when(plugin).newActionWrapper(column);
        doReturn(patternWrapper).when(plugin).newPatternWrapper(actionWrapper);

        doReturn(false).when(plugin).isNewColumn();

        plugin.setupValues();

        verify(plugin).setValueOptionsPageAsCompleted();
        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGenerateColumnWhenColumnIsNew() throws Exception {
        final ActionCol52 actionCol52 = mock(ActionCol52.class);

        doReturn(actionCol52).when(plugin).editingCol();
        doReturn(true).when(plugin).isNewColumn();

        assertTrue(plugin.generateColumn());

        verify(presenter).appendColumn(actionCol52);
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNew() throws Exception {
        final ActionCol52 editingCol = mock(ActionCol52.class);
        final ActionCol52 originalCol = mock(ActionCol52.class);

        doReturn(editingCol).when(plugin).editingCol();
        doReturn(originalCol).when(plugin).originalCol();
        doReturn(false).when(plugin).isNewColumn();

        assertTrue(plugin.generateColumn());

        verify(presenter).updateColumn(originalCol,
                                       editingCol);
    }

    @Test
    public void testGetPatternsWhenColumnIsNew() throws Exception {
        mockPatterns();

        doReturn(true).when(plugin).isNewColumn();

        final Set<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(2,
                     patterns.size());
        assertTrue(patterns.contains(new PatternWrapper("factType",
                                                        "boundName",
                                                        true)));
        assertTrue(patterns.contains(new PatternWrapper("factType",
                                                        "boundName")));
    }

    @Test
    public void testGetPatternsWhenColumnIsNotNewButFactPatternIsNew() throws Exception {
        mockPatterns();

        doReturn(false).when(plugin).isNewColumn();
        doReturn(true).when(plugin).isNewFactPattern();

        final Set<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(1,
                     patterns.size());
        assertTrue(patterns.contains(new PatternWrapper("factType",
                                                        "boundName")));
    }

    @Test
    public void testGetPatternsWhenColumnAndFactPatternAreNotNew() throws Exception {
        mockPatterns();

        doReturn(false).when(plugin).isNewColumn();
        doReturn(false).when(plugin).isNewFactPattern();

        final Set<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(1,
                     patterns.size());
        assertTrue(patterns.contains(new PatternWrapper("factType",
                                                        "boundName",
                                                        true)));
    }

    @Test
    public void testIsHideColumn() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.isHideColumn();

        verify(actionWrapper).isHideColumn();
    }

    @Test
    public void testSetHideColumn() {
        final boolean hideColumn = false;
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setHideColumn(hideColumn);

        verify(actionWrapper).setHideColumn(hideColumn);
    }

    @Test
    public void testIsNewFactPatternWhenIsNew() throws Exception {
        mockPatterns();

        plugin.setEditingPattern(new PatternWrapper("factType",
                                                    "bananna"));

        assertTrue(plugin.isNewFactPattern());
    }

    @Test
    public void testIsNewFactPatternWhenIsExisting() throws Exception {
        mockPatterns();

        plugin.setEditingPattern(new PatternWrapper("factType",
                                                    "boundName"));

        assertFalse(plugin.isNewFactPattern());
    }

    @Test
    public void testIsFieldBindingValid() {
        assertTrue(plugin.isFieldBindingValid());
    }

    @Test
    public void testIsBindable() {
        assertFalse(plugin.isBindable());
    }

    private void mockPatterns() {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final List<CompositeColumn<?>> patterns = Collections.singletonList(fakePattern());
        final List<ActionCol52> actions = Arrays.asList(fakeActionCol(),
                                                        fakeActionCol());

        when(model.getConditions()).thenReturn(patterns);
        when(model.getActionCols()).thenReturn(actions);

        when(presenter.getModel()).thenReturn(model);
    }

    private ActionWrapper mockActionWrapper(final String boundName,
                                            final String factType) {
        final ActionWrapper wrapper = mock(ActionWrapper.class);

        when(wrapper.getBoundName()).thenReturn(boundName);
        when(wrapper.getFactType()).thenReturn(factType);

        return wrapper;
    }

    private PatternWrapper mockPatternWrapper(final String boundName) {
        final PatternWrapper wrapper = mock(PatternWrapper.class);

        when(wrapper.getBoundName()).thenReturn(boundName);

        return wrapper;
    }

    private Pattern52 fakePattern() {
        return new Pattern52() {{
            setFactType("factType");
            setBoundName("boundName");
            setNegated(true);
        }};
    }

    private ActionInsertFactCol52 fakeActionCol() {
        return new ActionInsertFactCol52() {{
            setFactType("factType");
            setBoundName("boundName");
        }};
    }
}
