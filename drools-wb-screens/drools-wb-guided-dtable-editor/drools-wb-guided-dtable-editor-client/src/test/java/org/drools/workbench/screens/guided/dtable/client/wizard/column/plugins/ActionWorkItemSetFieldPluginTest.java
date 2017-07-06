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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.WorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemInsertWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemSetWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class ActionWorkItemSetFieldPluginTest {

    @Mock
    PatternWrapper patternWrapper;

    @Mock
    private BiConsumer<String, String> biConsumer;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private PatternPage patternPage;

    @Mock
    private FieldPage fieldPage;

    @Mock
    private AdditionalInfoPage additionalInfoPage;

    @Mock
    private WorkItemPage workItemPage;

    @Mock
    private ActionWorkItemWrapper editingWrapper;

    @Mock
    private TranslationService translationService;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @InjectMocks
    private ActionWorkItemSetFieldPlugin plugin = spy(new ActionWorkItemSetFieldPlugin(patternPage,
                                                                                       fieldPage,
                                                                                       additionalInfoPage,
                                                                                       workItemPage,
                                                                                       changeEvent,
                                                                                       translationService));

    @Test
    public void testSetWorkItem() throws Exception {
        final String workItemKey = "workItem";
        final PortableWorkDefinition workDefinition = mock(PortableWorkDefinition.class);
        final PortableParameterDefinition parameterDefinition = mock(PortableParameterDefinition.class);
        final ActionWorkItemSetFieldPlugin.WorkItemParameter parameter = mock(ActionWorkItemSetFieldPlugin.WorkItemParameter.class);
        final HashMap<String, ActionWorkItemSetFieldPlugin.WorkItemParameter> workItems = new HashMap<String, ActionWorkItemSetFieldPlugin.WorkItemParameter>() {{
            put(workItemKey,
                parameter);
        }};
        final List<ActionInsertFactCol52> actions = new ArrayList<ActionInsertFactCol52>() {{
            add(mock(ActionInsertFactCol52.class));
        }};

        doReturn(model).when(presenter).getModel();
        doReturn(actions).when(model).getActionCols();
        doReturn("workName").when(workDefinition).getName();
        doReturn("parameterName").when(parameterDefinition).getName();
        doReturn("parameterClassName").when(parameterDefinition).getClassName();
        doReturn(workItems).when(plugin).getWorkItems();
        doReturn(workDefinition).when(parameter).getWorkDefinition();
        doReturn(parameterDefinition).when(parameter).getWorkParameterDefinition();

        plugin.setWorkItem(workItemKey);

        assertEquals(workItemKey,
                     plugin.getWorkItem());

        verify(editingWrapper).setWorkItemName("workName");
        verify(editingWrapper).setWorkItemResultParameterName("parameterName");
        verify(editingWrapper).setParameterClassName("parameterClassName");
        verify(plugin).fireChangeEvent(workItemPage);
    }

    @Test
    public void testEditingCol() throws Exception {
        final ActionCol52 expectedAction = mock(ActionCol52.class);

        doReturn(expectedAction).when(editingWrapper).getActionCol52();

        final ActionCol52 actualAction = plugin.editingCol();

        assertEquals(expectedAction,
                     actualAction);
    }

    @Test
    public void testIsWorkItemSetWhenItIsSet() throws Exception {
        plugin.setWorkItemPageAsCompleted();

        final Boolean workItemSet = plugin.isWorkItemSet();

        assertTrue(workItemSet);
    }

    @Test
    public void testIsWorkItemSetWhenItIsNotSet() throws Exception {
        final Boolean workItemSet = plugin.isWorkItemSet();

        assertFalse(workItemSet);
    }

    @Test
    public void testSetWorkItemPageAsCompletedWhenItIsCompleted() throws Exception {
        doReturn(true).when(plugin).isWorkItemPageCompleted();

        plugin.setWorkItemPageAsCompleted();

        verify(plugin,
               never()).setWorkItemPageCompleted();
        verify(plugin,
               never()).fireChangeEvent(workItemPage);
    }

    @Test
    public void testSetWorkItemPageAsCompletedWhenItIsNotCompleted() throws Exception {
        doReturn(false).when(plugin).isWorkItemPageCompleted();

        plugin.setWorkItemPageAsCompleted();

        verify(plugin).setWorkItemPageCompleted();
        verify(plugin).fireChangeEvent(workItemPage);
    }

    @Test
    public void testGetHeader() throws Exception {
        final String expectedHeader = "header";

        doReturn(expectedHeader).when(editingWrapper).getHeader();

        final String actualHeader = plugin.getHeader();

        assertEquals(expectedHeader,
                     actualHeader);
    }

    @Test
    public void testSetHeader() throws Exception {
        final String header = "header";

        plugin.setHeader(header);

        verify(editingWrapper).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testSetInsertLogical() throws Exception {
        final boolean isInsertLogical = false;

        plugin.setInsertLogical(isInsertLogical);

        verify(editingWrapper).setInsertLogical(isInsertLogical);
    }

    @Test
    public void testSetUpdate() throws Exception {
        final boolean isUpdate = false;

        plugin.setUpdate(isUpdate);

        verify(editingWrapper).setUpdate(isUpdate);
    }

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_SetValue;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testGetPages() throws Exception {
        assertEquals(4,
                     plugin.getPages().size());
    }

    @Test
    public void testGenerateColumnWhenColumnIsNew() throws Exception {
        final ActionCol52 actionCol52 = mock(ActionCol52.class);
        final String header = "header";

        doReturn(true).when(plugin).isNewColumn();
        doReturn(header).when(editingWrapper).getHeader();
        doReturn("factField").when(editingWrapper).getFactField();
        doReturn("factType").when(editingWrapper).getFactType();
        doReturn(editingWrapper).when(plugin).editingWrapper();
        doReturn(actionCol52).when(editingWrapper).getActionCol52();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        verify(presenter).appendColumn(actionCol52);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNew() throws Exception {
        final ActionCol52 editingColumn = mock(ActionCol52.class);
        final ActionCol52 originalColumn = mock(ActionCol52.class);
        final String header = "header";

        doReturn(false).when(plugin).isNewColumn();
        doReturn(header).when(editingWrapper).getHeader();
        doReturn("factField").when(editingWrapper).getFactField();
        doReturn("factType2").when(editingWrapper).getFactType();
        doReturn(editingWrapper).when(plugin).editingWrapper();
        doReturn(editingColumn).when(editingWrapper).getActionCol52();
        doReturn(originalColumn).when(plugin).getOriginalColumnConfig52();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        verify(presenter).updateColumn(originalColumn,
                                       editingColumn);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testSetEditingPattern() throws Exception {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        plugin.setEditingPattern(patternWrapper);

        verify(editingWrapper).setFactField(null);
        verify(editingWrapper).setFactType(null);
        verify(editingWrapper).setBoundName(null);
        verify(editingWrapper).setType(null);

        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);

        assertEquals(patternWrapper,
                     plugin.patternWrapper());
    }

    @Test
    public void testGetPatternsWhenColumnIsNew() throws Exception {
        mockPatterns();

        doReturn(true).when(plugin).isNewColumn();

        final List<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(3,
                     patterns.size());
    }

    @Test
    public void testGetPatternsWhenColumnIsNotNewButFactPatternIsNew() throws Exception {
        mockPatterns();

        doReturn(false).when(plugin).isNewColumn();
        doReturn(true).when(plugin).isNewFactPattern();

        final List<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(2,
                     patterns.size());
    }

    @Test
    public void testGetPatternsWhenColumnAndFactPatternAreNotNew() throws Exception {
        mockPatterns();

        doReturn(false).when(plugin).isNewColumn();
        doReturn(false).when(plugin).isNewFactPattern();

        final List<PatternWrapper> patterns = plugin.getPatterns();

        assertEquals(1,
                     patterns.size());
    }

    @Test
    public void testConstraintValue() throws Exception {
        assertEquals(BaseSingleFieldConstraint.TYPE_UNDEFINED,
                     plugin.constraintValue());
    }

    @Test
    public void testGetAccessor() throws Exception {
        assertEquals(FieldAccessorsAndMutators.ACCESSOR,
                     plugin.getAccessor());
    }

    @Test
    public void testGetFactField() throws Exception {
        final String expectedFactField = "factField";

        doReturn(expectedFactField).when(editingWrapper).getFactField();

        final String actualFactField = plugin.getFactField();

        assertEquals(expectedFactField,
                     actualFactField);
    }

    @Test
    public void testSetFactFieldWhenFactPattern() throws Exception {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final String factField = "factField";
        final String factType = "factType";
        final String boundName = "boundName";
        final String type = "type";

        doReturn(factType).when(patternWrapper).getFactType();
        doReturn(boundName).when(patternWrapper).getBoundName();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(editingWrapper).when(plugin).editingWrapper();
        doReturn(patternWrapper).when(plugin).patternWrapper();
        doReturn(model).when(presenter).getModel();
        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(true).when(plugin).isNewFactPattern();
        doReturn(type).when(oracle).getFieldType(any(),
                                                 any());

        plugin.setFactField(factField);

        verify(editingWrapper).setFactField(factField);
        verify(editingWrapper).setFactType(factType);
        verify(editingWrapper).setBoundName(boundName);
        verify(editingWrapper).setType(type);

        verify(plugin).fireChangeEvent(fieldPage);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNew() throws Exception {
        doReturn(mock(ActionWorkItemInsertWrapper.class)).when(plugin).editingWrapper();

        final boolean updateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertFalse(updateEngineWithChanges);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNotNew() throws Exception {
        doReturn(mock(ActionWorkItemSetWrapper.class)).when(plugin).editingWrapper();

        final boolean updateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertTrue(updateEngineWithChanges);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNew() throws Exception {
        doReturn(mock(ActionWorkItemInsertWrapper.class)).when(plugin).editingWrapper();

        final boolean logicallyInsert = plugin.showLogicallyInsert();

        assertTrue(logicallyInsert);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNotNew() throws Exception {
        doReturn(mock(ActionWorkItemSetWrapper.class)).when(plugin).editingWrapper();

        final boolean logicallyInsert = plugin.showLogicallyInsert();

        assertFalse(logicallyInsert);
    }

    @Test
    public void testInitializedPatternPage() throws Exception {
        plugin.initializedPatternPage();

        verify(patternPage).disableEntryPoint();
        verify(patternPage).disableNegatedPatterns();
    }

    @Test
    public void testInitializedAdditionalInfoPage() throws Exception {
        plugin.initializedAdditionalInfoPage();

        verify(additionalInfoPage).enableHeader();
        verify(additionalInfoPage).enableLogicallyInsert();
        verify(additionalInfoPage).enableUpdateEngineWithChanges();
        verify(additionalInfoPage).enableHideColumn();
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
    @Ignore("Reproducer for: GUVNOR-3170")
    public void testForEachWorkItemStringField() throws Exception {
        setUpWorkItemDefinitions();
        when(oracle.getFieldClassName("Person",
                                      "factField")).thenReturn("java.lang.String");
        plugin.setupWorkItems();

        plugin.forEachWorkItem(biConsumer);

        verify(biConsumer,
               times(2)).accept(anyString(),
                                anyString());
        verify(biConsumer).accept("StringWorkItem - StringResult",
                                  "StringWorkItemStringResult");
        verify(biConsumer).accept("FloatWorkItem - FloatResult",
                                  "FloatWorkItemFloatResult");
    }

    @Test
    public void testForEachWorkItemFloatField() throws Exception {
        setUpWorkItemDefinitions();
        when(oracle.getFieldClassName("Person",
                                      "factField")).thenReturn("java.lang.Float");
        plugin.setupWorkItems();

        plugin.forEachWorkItem(biConsumer);

        verify(biConsumer,
               times(1)).accept(anyString(),
                                anyString());
        verify(biConsumer).accept("FloatWorkItem - FloatResult",
                                  "FloatWorkItemFloatResult");
    }

    @Test
    public void testNewActionWrapperWhenColumnIsAnActionWorkItemInsertFactCol52() throws Exception {
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        final ActionWorkItemWrapper wrapper = plugin.newActionWorkItemWrapper(mock(ActionWorkItemInsertFactCol52.class));

        assertTrue(wrapper instanceof ActionWorkItemInsertWrapper);
    }

    @Test
    public void testNewActionWrapperWhenColumnIsAnActionSetFactWrapper() throws Exception {
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        final ActionWorkItemWrapper wrapper = plugin.newActionWorkItemWrapper(mock(ActionWorkItemSetFieldCol52.class));

        assertTrue(wrapper instanceof ActionWorkItemSetWrapper);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNewActionWrapperWhenColumnIsInvalid() throws Exception {
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(presenter.getModel()).thenReturn(model);

        plugin.newActionWorkItemWrapper(mock(ConditionCol52.class));
    }

    @Test
    public void testNewPatternWrapperWhenPatternIsFound() throws Exception {
        final PatternWrapper expectedWrapper = mockPatternWrapper("BoundName");
        final ArrayList<PatternWrapper> actionWrappers = new ArrayList<PatternWrapper>() {{
            add(expectedWrapper);
        }};

        doReturn(actionWrappers).when(plugin).getPatterns();

        final PatternWrapper actualWrapper = plugin.newPatternWrapper(mockActionWrapper("BoundName",
                                                                                        "factType"));

        assertSame(expectedWrapper,
                   actualWrapper);
    }

    @Test
    public void testNewPatternWrapperWhenPatternIsNotFound() throws Exception {
        final ArrayList<PatternWrapper> actionWrappers = new ArrayList<>();
        final ActionWorkItemWrapper actionWrapper = mockActionWrapper("BoundName",
                                                                      "FactType");

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
               never()).setWorkItemPageAsCompleted();
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
        final ActionWorkItemWrapper actionWrapper = mock(ActionWorkItemWrapper.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(model).when(presenter).getModel();
        doReturn(column).when(plugin).getOriginalColumnConfig52();
        doReturn(actionWrapper).when(plugin).newActionWorkItemWrapper(column);
        doReturn(patternWrapper).when(plugin).newPatternWrapper(actionWrapper);
        doReturn("WorkItemName").when(actionWrapper).getWorkItemName();
        doReturn("WorkItemResultParameterName").when(actionWrapper).getWorkItemResultParameterName();
        doReturn(new ArrayList<ActionCol52>()).when(model).getActionCols();

        doReturn(false).when(plugin).isNewColumn();

        plugin.setupValues();

        verify(plugin).setupWorkItems();
        verify(plugin).setWorkItem("WorkItemNameWorkItemResultParameterName");
        verify(plugin).setWorkItemPageAsCompleted();
        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    private ActionWorkItemWrapper mockActionWrapper(final String boundName,
                                                    final String factType) {
        final ActionWorkItemWrapper wrapper = mock(ActionWorkItemWrapper.class);

        when(wrapper.getBoundName()).thenReturn(boundName);
        when(wrapper.getFactType()).thenReturn(factType);

        return wrapper;
    }

    private PatternWrapper mockPatternWrapper(final String boundName) {
        final PatternWrapper wrapper = mock(PatternWrapper.class);

        when(wrapper.getBoundName()).thenReturn(boundName);

        return wrapper;
    }

    private void setUpWorkItemDefinitions() {
        when(patternWrapper.getFactType()).thenReturn("Person");
        when(plugin.patternWrapper()).thenReturn(patternWrapper);
        when(editingWrapper.getFactField()).thenReturn("factField");
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(presenter.getModel()).thenReturn(model);
        when(model.getActionCols()).thenReturn(Arrays.asList(firstFakeWorkItem(),
                                                             secondFakeWorkItem()));
    }

    private ActionWorkItemCol52 secondFakeWorkItem() {
        final ActionWorkItemCol52 workItemCol52 = new ActionWorkItemCol52();
        final PortableWorkDefinition portableWorkDefinition = new PortableWorkDefinition();
        final PortableParameterDefinition portableFloatParameterDefinition = new PortableFloatParameterDefinition();

        portableFloatParameterDefinition.setName("FloatResult");
        portableWorkDefinition.setResults(Collections.singleton(portableFloatParameterDefinition));
        portableWorkDefinition.setName("FloatWorkItem");
        portableWorkDefinition.setDisplayName("SecondWorkItem");
        workItemCol52.setWorkItemDefinition(portableWorkDefinition);

        return workItemCol52;
    }

    private ActionWorkItemCol52 firstFakeWorkItem() {
        final ActionWorkItemCol52 workItemCol52 = new ActionWorkItemCol52();
        final PortableWorkDefinition portableWorkDefinition = new PortableWorkDefinition();
        final PortableParameterDefinition portableStringParameterDefinition = new PortableStringParameterDefinition();

        portableStringParameterDefinition.setName("StringResult");
        portableWorkDefinition.setResults(Collections.singleton(portableStringParameterDefinition));
        portableWorkDefinition.setName("StringWorkItem");
        portableWorkDefinition.setDisplayName("FirstWorkItem");
        workItemCol52.setWorkItemDefinition(portableWorkDefinition);

        return workItemCol52;
    }

    private void mockPatterns() {
        when(model.getPatterns()).thenReturn(new ArrayList<Pattern52>() {{
            add(new Pattern52());
        }});
        when(model.getActionCols()).thenReturn(new ArrayList<ActionCol52>() {{
            add(new ActionWorkItemInsertFactCol52());
            add(new ActionWorkItemInsertFactCol52());
        }});
        when(presenter.getModel()).thenReturn(model);
    }
}
