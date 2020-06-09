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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.MockInstanceImpl;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.DefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class BRLActionColumnPluginTest {

    @Mock
    private RuleModellerPage ruleModellerPage;

    @Mock
    private AdditionalInfoPage<BRLActionColumnPlugin> additionalInfoPage;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private TranslationService translationService;

    @Mock
    private HandlerRegistration registration;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private BRLActionColumn editingCol;

    @InjectMocks
    private BRLActionColumnPlugin plugin = spy(new BRLActionColumnPlugin(ruleModellerPage,
                                                                         mock(DefaultValuesPage.class),
                                                                         new MockInstanceImpl<>(Collections.singletonList(mock(RuleModellerActionPlugin.class))),
                                                                         additionalInfoPage,
                                                                         changeEvent,
                                                                         translationService));

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.BRLActionColumnPlugin_AddActionBRL;
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
    public void testSetupEditingColWhenColumnIsNew() throws Exception {
        final BRLActionColumn column = mock(BRLActionColumn.class);

        doReturn(true).when(plugin).isNewColumn();
        doReturn(column).when(plugin).newBRLActionColumn();

        plugin.setupEditingCol();

        verify(column,
               never()).setHeader(any());
        verify(column,
               never()).setDefinition(any());
        verify(column,
               never()).setChildColumns(any());
        verify(column,
               never()).setHideColumn(anyBoolean());
        assertEquals(column,
                     plugin.editingCol());
    }

    @Test
    public void testSetupEditingColWhenColumnIsNotNew() throws Exception {
        final BRLActionColumn originalColumn = mock(BRLActionColumn.class);
        final BRLActionColumn editingColumn = mock(BRLActionColumn.class);
        final String header = "header";
        final ArrayList<IAction> definition = new ArrayList<>();
        final ArrayList<BRLActionVariableColumn> childColumns = new ArrayList<>();
        final boolean isHideColumn = false;

        doReturn(false).when(plugin).isNewColumn();
        doReturn(editingColumn).when(plugin).newBRLActionColumn();
        doReturn(originalColumn).when(plugin).getOriginalColumnConfig52();
        doReturn(header).when(originalColumn).getHeader();
        doReturn(definition).when(originalColumn).getDefinition();
        doReturn(childColumns).when(originalColumn).getChildColumns();
        doReturn(isHideColumn).when(originalColumn).isHideColumn();

        plugin.setupEditingCol();

        verify(editingColumn).setHeader(header);
        verify(editingColumn).setDefinition(definition);
        verify(editingColumn).setChildColumns(childColumns);
        verify(editingColumn).setHideColumn(isHideColumn);
        assertEquals(editingColumn,
                     plugin.editingCol());
    }

    @Test
    public void testGetPages() throws Exception {
        final List<WizardPage> pages = plugin.getPages();

        assertEquals(3,
                     pages.size());
    }

    @Test
    public void testOnClose() throws Exception {
        plugin.onClose();

        verify(plugin).teardownRuleModellerEvents();
    }

    @Test
    public void testGenerateColumnWhenColumnIsNew() throws Exception {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final ArrayList<ActionCol52> actionCol52s = new ArrayList<>();

        when(model.getActionCols()).thenReturn(actionCol52s);
        when(presenter.getModel()).thenReturn(model);
        when(editingCol.getHeader()).thenReturn("header");
        when(plugin.isNewColumn()).thenReturn(true);

        final Boolean success = plugin.generateColumn();

        assertTrue(success);

        verify(plugin).setupDefinedVariables(any());
        verify(editingCol).setDefinition(any());
        verify(presenter).appendColumn(editingCol);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNew() throws Exception {
        final BRLActionColumn originalCol = mock(BRLActionColumn.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getActionCols()).thenReturn(new ArrayList<>());
        when(presenter.getModel()).thenReturn(model);
        when(editingCol.getHeader()).thenReturn("header");
        when(plugin.originalCol()).thenReturn(originalCol);
        when(plugin.isNewColumn()).thenReturn(false);

        final Boolean success = plugin.generateColumn();

        assertTrue(success);

        verify(plugin).setupDefinedVariables(any());
        verify(editingCol).setDefinition(any());
        verify(presenter).updateColumn(originalCol,
                                       editingCol);
        verify(translationService,
               never()).format(any());
    }

    @Test
    public void testGenerateColumnVeto() throws Exception {

        doReturn(false).when(plugin).isNewColumn();
        doThrow(new ModelSynchronizer.VetoException()).when(presenter).updateColumn(any(BRLActionColumn.class),
                                                                                    any(BRLActionColumn.class));
        final Boolean success = plugin.generateColumn();

        assertFalse(success);

        verify(wizard).showGenericVetoError();
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
    public void testGetRuleModellerDescription() throws Exception {
        plugin.getRuleModellerDescription();
        verify(translationService).format(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAnActionBRLFragment);
        verify(translationService,
               never()).format(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAConditionBRLFragment);
    }

    @Test
    public void testIsHideColumn() {
        plugin.isHideColumn();

        verify(editingCol).isHideColumn();
    }

    @Test
    public void testSetHideColumn() throws Exception {
        final boolean hideColumn = false;

        plugin.setHideColumn(hideColumn);

        verify(editingCol).setHideColumn(hideColumn);
    }

    @Test
    public void testRuleModellerActionPlugins() throws Exception {
        Assertions.assertThat(plugin.getRuleModellerActionPlugins()).hasSize(1);
        Assertions.assertThat(plugin.getRuleModellerActionPlugins()).hasAtLeastOneElementOfType(RuleModellerActionPlugin.class);
    }
}
