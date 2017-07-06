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
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class BRLConditionColumnPluginTest {

    @Mock
    private RuleModellerPage ruleModellerPage;

    @Mock
    private AdditionalInfoPage additionalInfoPage;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private TranslationService translationService;

    @Mock
    private BRLConditionColumn editingCol;

    @Mock
    private HandlerRegistration registration;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @InjectMocks
    private BRLConditionColumnPlugin plugin = spy(new BRLConditionColumnPlugin(ruleModellerPage,
                                                                               additionalInfoPage,
                                                                               changeEvent,
                                                                               translationService));

    @Before
    public void setup() {
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(model).when(presenter).getModel();
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.BRLConditionColumnPlugin_AddConditionBRL;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testInit() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(plugin).tableFormat();
        doReturn(mock(EventBus.class)).when(presenter).getEventBus();
        doReturn(presenter).when(wizard).getPresenter();

        plugin.init(wizard);

        verify(plugin).setupEditingCol();
        verify(plugin).setupRuleModellerEvents();
    }

    @Test
    public void testGetPages() throws Exception {
        final List<WizardPage> pages = plugin.getPages();

        assertEquals(2,
                     pages.size());
    }

    @Test
    public void testOnClose() throws Exception {
        plugin.onClose();

        verify(plugin).teardownRuleModellerEvents();
    }

    @Test
    public void testGenerateColumnWhenTheColumnIsNew() throws Exception {
        final String header = "header";

        doReturn(true).when(plugin).isNewColumn();
        doReturn(model).when(presenter).getModel();
        doReturn(header).when(editingCol).getHeader();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);

        verify(plugin).getDefinedVariables(any());
        verify(editingCol).setDefinition(any());
        verify(presenter).appendColumn(editingCol);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testGenerateColumnWhenTheColumnIsNotNew() throws Exception {
        ConditionCol52 col52 = mock(ConditionCol52.class);
        final String header = "header";

        doReturn(false).when(plugin).isNewColumn();
        doReturn(model).when(presenter).getModel();
        doReturn(header).when(editingCol).getHeader();
        doReturn(col52).when(plugin).getOriginalColumn();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);

        verify(plugin).getDefinedVariables(any());
        verify(editingCol).setDefinition(any());
        verify(presenter).updateColumn(col52,
                                       editingCol);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testGetHeader() throws Exception {
        plugin.getHeader();

        verify(editingCol).getHeader();
    }

    @Test
    public void testSetHeader() throws Exception {
        final String header = "header";

        plugin.setHeader(header);

        verify(editingCol).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGetRuleModel() throws Exception {
        assertNotNull(plugin.getRuleModel());
    }

    @Test
    public void testSetRuleModellerPageAsCompletedWhenItIsCompleted() throws Exception {
        doReturn(true).when(plugin).isRuleModellerPageCompleted();

        plugin.setRuleModellerPageAsCompleted();

        verify(plugin,
               never()).setRuleModellerPageCompleted();
        verify(plugin,
               never()).fireChangeEvent(ruleModellerPage);
    }

    @Test
    public void testSetRuleModellerPageAsCompletedWhenItIsNotCompleted() throws Exception {
        doReturn(false).when(plugin).isRuleModellerPageCompleted();

        plugin.setRuleModellerPageAsCompleted();

        verify(plugin).setRuleModellerPageCompleted();
        verify(plugin).fireChangeEvent(ruleModellerPage);
    }

    @Test
    public void testTableFormat() throws Exception {
        final GuidedDecisionTable52.TableFormat expectedTableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;

        doReturn(expectedTableFormat).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();

        final GuidedDecisionTable52.TableFormat actualTableFormat = plugin.tableFormat();

        assertEquals(expectedTableFormat,
                     actualTableFormat);
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

    @Test
    public void testGetRuleModellerDescription() throws Exception {
        plugin.getRuleModellerDescription();
        verify(translationService,
               never()).format(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAnActionBRLFragment);
        verify(translationService).format(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAConditionBRLFragment);
    }

    @Test
    public void testCloneWhenColumnIsALimitedEntryBRLConditionColumn() throws Exception {
        final List<IPattern> definition = new ArrayList<>();
        final boolean hideColumn = false;
        final LimitedEntryBRLConditionColumn column = makeLimitedEntryBRLConditionColumn("header",
                                                                                         hideColumn,
                                                                                         definition);

        final BRLConditionColumn clone = plugin.clone(column);

        assertEquals(column.getHeader(),
                     clone.getHeader());
        assertEquals(column.isHideColumn(),
                     clone.isHideColumn());
        assertEquals(column.getDefinition(),
                     clone.getDefinition());
        assertNotSame(column,
                      clone);
        assertTrue(clone.getChildColumns().isEmpty());
    }

    @Test
    public void testCloneWhenColumnIsABRLConditionColumn() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();

        final List<IPattern> definition = new ArrayList<>();
        final Boolean hideColumn = false;
        final List<BRLConditionVariableColumn> childColumns = new ArrayList<BRLConditionVariableColumn>() {{
            add(mock(BRLConditionVariableColumn.class));
        }};
        final BRLConditionColumn column = makeBRLConditionColumn("header",
                                                                 hideColumn,
                                                                 definition,
                                                                 childColumns);

        final BRLConditionColumn clone = plugin.clone(column);

        assertEquals(column.getHeader(),
                     clone.getHeader());
        assertEquals(column.isHideColumn(),
                     clone.isHideColumn());
        assertEquals(column.getDefinition(),
                     clone.getDefinition());
        assertNotSame(column,
                      clone);
        assertFalse(clone.getChildColumns().isEmpty());
    }

    @Test
    public void testCloneVariables() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();

        final List<BRLConditionVariableColumn> variables = new ArrayList<BRLConditionVariableColumn>() {{
            add(mock(BRLConditionVariableColumn.class));
            add(mock(BRLConditionVariableColumn.class));
            add(mock(BRLConditionVariableColumn.class));
        }};

        final List<BRLConditionVariableColumn> clones = plugin.cloneVariables(variables);

        assertEquals(3,
                     clones.size());
        verify(plugin,
               times(3)).cloneVariable(any(BRLConditionVariableColumn.class));
    }

    @Test
    public void testCloneVariable() throws Exception {
        final BRLConditionVariableColumn variable = makeVariable("variableName",
                                                                 "variableFieldType",
                                                                 "variableFactType",
                                                                 "variableFactField",
                                                                 "variableHeader",
                                                                 false,
                                                                 999);

        final BRLConditionVariableColumn clone = plugin.cloneVariable(variable);

        assertEquals(variable.getVarName(),
                     clone.getVarName());
        assertEquals(variable.getFieldType(),
                     clone.getFieldType());
        assertEquals(variable.getFactType(),
                     clone.getFactType());
        assertEquals(variable.getFactField(),
                     clone.getFactField());
        assertEquals(variable.getHeader(),
                     clone.getHeader());
        assertEquals(variable.isHideColumn(),
                     clone.isHideColumn());
        assertEquals(variable.getWidth(),
                     clone.getWidth());
        assertNotSame(variable,
                      clone);
    }

    private LimitedEntryBRLConditionColumn makeLimitedEntryBRLConditionColumn(final String header,
                                                                              final boolean hideColumn,
                                                                              final List<IPattern> definition) {
        final LimitedEntryBRLConditionColumn column = new LimitedEntryBRLConditionColumn();

        column.setHeader(header);
        column.setHideColumn(hideColumn);
        column.setDefinition(definition);

        return column;
    }

    private BRLConditionColumn makeBRLConditionColumn(final String header,
                                                      final boolean hideColumn,
                                                      final List<IPattern> definition,
                                                      final List<BRLConditionVariableColumn> childColumns) {
        final BRLConditionColumn column = new BRLConditionColumn();

        column.setHeader(header);
        column.setHideColumn(hideColumn);
        column.setDefinition(definition);
        column.setChildColumns(childColumns);

        return column;
    }

    private BRLConditionVariableColumn makeVariable(final String variableName,
                                                    final String variableFieldType,
                                                    final String variableFactType,
                                                    final String variableFactField,
                                                    final String variableHeader,
                                                    final boolean hideColumn,
                                                    final int width) {
        final BRLConditionVariableColumn clone = new BRLConditionVariableColumn(variableName,
                                                                                variableFieldType,
                                                                                variableFactType,
                                                                                variableFactField);
        clone.setHeader(variableHeader);
        clone.setHideColumn(hideColumn);
        clone.setWidth(width);

        return clone;
    }
}
