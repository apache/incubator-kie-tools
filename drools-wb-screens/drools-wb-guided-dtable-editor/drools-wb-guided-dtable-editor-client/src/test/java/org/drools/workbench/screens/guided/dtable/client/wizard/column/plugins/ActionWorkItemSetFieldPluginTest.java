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
import java.util.HashMap;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.WorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
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
    public void testGenerateColumn() throws Exception {
        final ActionCol52 actionCol52 = mock(ActionCol52.class);
        final String header = "header";

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
    public void testGetPatterns() throws Exception {
        final List<ActionInsertFactCol52> actions = new ArrayList<ActionInsertFactCol52>() {{
            add(mock(ActionInsertFactCol52.class));
        }};
        final List<Pattern52> patterns = new ArrayList<Pattern52>() {{
            add(mock(Pattern52.class));
        }};

        doReturn(model).when(presenter).getModel();
        doReturn(actions).when(model).getActionCols();
        doReturn(patterns).when(model).getPatterns();

        final List<PatternWrapper> result = plugin.getPatterns();

        assertEquals(2,
                     result.size());
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
        doReturn(true).when(plugin).isNewFactPattern();

        final boolean updateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertFalse(updateEngineWithChanges);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNotNew() throws Exception {
        doReturn(false).when(plugin).isNewFactPattern();

        final boolean updateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertTrue(updateEngineWithChanges);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNew() throws Exception {
        doReturn(true).when(plugin).isNewFactPattern();

        final boolean logicallyInsert = plugin.showLogicallyInsert();

        assertTrue(logicallyInsert);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNotNew() throws Exception {
        doReturn(false).when(plugin).isNewFactPattern();

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
}
