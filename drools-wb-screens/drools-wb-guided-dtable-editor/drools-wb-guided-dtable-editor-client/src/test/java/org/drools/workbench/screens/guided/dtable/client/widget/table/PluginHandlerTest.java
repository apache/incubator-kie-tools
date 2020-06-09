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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.MockInstanceImpl;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.CalculationTypePage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.DefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.OperatorPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternToDeletePage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.WorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionSetFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemSetFieldPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PluginHandlerTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private RuleModellerPage ruleModellerPage;

    @Mock
    private SummaryPage summaryPage;

    @Mock
    private PatternToDeletePage patternToDeletePage;

    @Mock
    private WorkItemPage workItemPage;

    @Mock
    private PatternPage patternPage;

    @Mock
    private CalculationTypePage calculationTypePage;

    @Mock
    private FieldPage fieldPage;

    @Mock
    private OperatorPage operatorPage;

    @Mock
    private ValueOptionsPage valueOptionsPage;

    @Mock
    private AdditionalInfoPage additionalInfoPage;

    @Mock
    private DefaultValuesPage defaultValuesPage;

    @Mock
    private ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance;

    @Mock
    private ManagedInstance<BRLConditionColumnPlugin> brlConditionColumnPlugin;

    @Mock
    private ManagedInstance<ConditionColumnPlugin> conditionColumnPlugin;

    @Mock
    private ManagedInstance<ActionRetractFactPlugin> actionRetractFactPlugin;

    @Mock
    private ManagedInstance<ActionSetFactPlugin> actionSetFactPlugin;

    @Mock
    private ManagedInstance<ActionWorkItemSetFieldPlugin> actionWorkItemSetFieldPlugin;

    @Mock
    private ManagedInstance<ActionWorkItemPlugin> actionWorkItemPlugin;

    @Mock
    private ManagedInstance<BRLActionColumnPlugin> brlActionColumnPlugin;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private EventBus eventBus;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> event;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    private PluginHandler pluginHandler;

    private NewGuidedDecisionTableColumnWizard wizard;

    @Before
    public void setup() {
        pluginHandler = spy(new PluginHandler(wizardManagedInstance,
                                              brlConditionColumnPlugin,
                                              conditionColumnPlugin,
                                              actionRetractFactPlugin,
                                              actionSetFactPlugin,
                                              actionWorkItemSetFieldPlugin,
                                              actionWorkItemPlugin,
                                              brlActionColumnPlugin));
        pluginHandler.init(presenter);

        wizard = spy(new NewGuidedDecisionTableColumnWizard(mock(WizardView.class),
                                                            summaryPage,
                                                            translationService,
                                                            popoverUtils));

        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(eventBus).when(presenter).getEventBus();
        doReturn(oracle).when(presenter).getDataModelOracle();
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemSetFieldCol52() {
        final ActionWorkItemSetFieldCol52 originalColumn = mock(ActionWorkItemSetFieldCol52.class);

        final ActionWorkItemSetFieldPlugin plugin = spy(new ActionWorkItemSetFieldPlugin(patternPage,
                                                                                         fieldPage,
                                                                                         additionalInfoPage,
                                                                                         workItemPage,
                                                                                         event,
                                                                                         translationService));

        doReturn(wizard).when(wizardManagedInstance).get();
        doReturn(plugin).when(actionWorkItemSetFieldPlugin).get();

        pluginHandler.edit(originalColumn);

        verify(plugin).setOriginalColumnConfig52(originalColumn);
        verify(pluginHandler).openWizard(plugin);
        verify(wizard).start(plugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemInsertFactCol52() {
        final ActionWorkItemInsertFactCol52 originalColumn = mock(ActionWorkItemInsertFactCol52.class);

        final ActionWorkItemSetFieldPlugin plugin = spy(new ActionWorkItemSetFieldPlugin(patternPage,
                                                                                         fieldPage,
                                                                                         additionalInfoPage,
                                                                                         workItemPage,
                                                                                         event,
                                                                                         translationService));

        doReturn(wizard).when(wizardManagedInstance).get();
        doReturn(plugin).when(actionWorkItemSetFieldPlugin).get();

        pluginHandler.edit(originalColumn);

        verify(plugin).setOriginalColumnConfig52(originalColumn);
        verify(pluginHandler).openWizard(plugin);
        verify(wizard).start(plugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionInsertFactCol52() {
        final ActionInsertFactCol52 originalColumn = mock(ActionInsertFactCol52.class);

        final ActionSetFactPlugin plugin = spy(new ActionSetFactPlugin(patternPage,
                                                                       fieldPage,
                                                                       valueOptionsPage,
                                                                       additionalInfoPage,
                                                                       event,
                                                                       translationService));

        testEditActionColumn(plugin,
                             actionSetFactPlugin,
                             originalColumn);
    }

    @Test
    public void testEditWhenColumnIsAnActionSetFieldCol52() {
        final ActionSetFieldCol52 originalColumn = mock(ActionSetFieldCol52.class);

        final ActionSetFactPlugin plugin = spy(new ActionSetFactPlugin(patternPage,
                                                                       fieldPage,
                                                                       valueOptionsPage,
                                                                       additionalInfoPage,
                                                                       event,
                                                                       translationService));

        testEditActionColumn(plugin,
                             actionSetFactPlugin,
                             originalColumn);
    }

    @Test
    public void testEditWhenColumnIsAnActionRetractFactCol52() {
        final ActionRetractFactCol52 originalColumn = mock(ActionRetractFactCol52.class);

        final ActionRetractFactPlugin plugin = spy(new ActionRetractFactPlugin(patternToDeletePage,
                                                                               additionalInfoPage,
                                                                               event,
                                                                               translationService));
        testEditActionColumn(plugin,
                             actionRetractFactPlugin,
                             originalColumn);
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemCol52() {
        final ActionWorkItemCol52 originalColumn = mock(ActionWorkItemCol52.class);

        final ActionWorkItemPlugin plugin = spy(new ActionWorkItemPlugin(additionalInfoPage,
                                                                         workItemPage,
                                                                         event,
                                                                         translationService));

        testEditActionColumn(plugin,
                             actionWorkItemPlugin,
                             originalColumn);
    }

    @Test
    public void testEditWhenColumnIsALimitedEntryBRLActionColumn() {
        final LimitedEntryBRLActionColumn originalColumn = mock(LimitedEntryBRLActionColumn.class);

        final BRLActionColumnPlugin plugin = spy(new BRLActionColumnPlugin(ruleModellerPage,
                                                                           defaultValuesPage,
                                                                           new MockInstanceImpl<>(new ArrayList<>()),
                                                                           additionalInfoPage,
                                                                           event,
                                                                           translationService));
        testEditActionColumn(plugin,
                             brlActionColumnPlugin,
                             originalColumn);
    }

    @Test
    public void testEditWhenColumnIsABRLActionColumn() {
        final BRLActionColumn originalColumn = mock(BRLActionColumn.class);

        final BRLActionColumnPlugin plugin = spy(new BRLActionColumnPlugin(ruleModellerPage,
                                                                           defaultValuesPage,
                                                                           new MockInstanceImpl<>(new ArrayList<>()),
                                                                           additionalInfoPage,
                                                                           event,
                                                                           translationService));
        testEditActionColumn(plugin,
                             brlActionColumnPlugin,
                             originalColumn);
    }

    private void testEditActionColumn(BaseDecisionTableColumnPlugin plugin,
                                      ManagedInstance pluginManagedInstance,
                                      ActionCol52 originalColumn) {
        doReturn(wizard).when(wizardManagedInstance).get();
        doReturn(plugin).when(pluginManagedInstance).get();

        pluginHandler.edit(originalColumn);

        verify(plugin).setOriginalColumnConfig52(originalColumn);
        verify(pluginHandler).openWizard(plugin);
        verify(wizard).start(plugin);
    }

    @Test
    public void testEditWhenColumnIsInvalid() {
        final ActionCol52 column = mock(ActionCol52.class);
        final DecisionTableColumnPlugin plugin = mock(DecisionTableColumnPlugin.class);

        doReturn(plugin).when(actionWorkItemSetFieldPlugin).get();

        pluginHandler.edit(column);

        verify(plugin,
               never()).setOriginalColumnConfig52(column);
        verify(pluginHandler,
               never()).openWizard(plugin);
    }

    @Test
    public void testEditWhenColumnIsABRLConditionColumn() {
        final BRLConditionColumn originalColumn = mock(BRLConditionColumn.class);

        testEditBrlConditionColumn(originalColumn);
    }

    @Test
    public void testEditWhenColumnIsALimitedEntryBRLConditionColumn() {
        final LimitedEntryBRLConditionColumn originalColumn = mock(LimitedEntryBRLConditionColumn.class);

        testEditBrlConditionColumn(originalColumn);
    }

    private void testEditBrlConditionColumn(BRLConditionColumn originalColumn) {
        final BRLConditionColumnPlugin plugin = spy(new BRLConditionColumnPlugin(ruleModellerPage,
                                                                                 defaultValuesPage,
                                                                                 additionalInfoPage,
                                                                                 event,
                                                                                 translationService));

        doReturn(wizard).when(wizardManagedInstance).get();
        doReturn(plugin).when(brlConditionColumnPlugin).get();

        pluginHandler.edit(originalColumn);

        verify(plugin).setOriginalColumnConfig52(originalColumn);
        verify(pluginHandler).openWizard(plugin);
        verify(wizard).start(plugin);
    }

    @Test
    public void testEditWhenColumnIsAConditionCol52() {
        final Pattern52 originalPattern = mock(Pattern52.class);
        final ConditionCol52 originalColumn = mock(ConditionCol52.class);

        final ConditionColumnPlugin plugin = spy(new ConditionColumnPlugin(patternPage,
                                                                           calculationTypePage,
                                                                           fieldPage,
                                                                           operatorPage,
                                                                           valueOptionsPage,
                                                                           additionalInfoPage,
                                                                           event,
                                                                           translationService));

        doReturn(wizard).when(wizardManagedInstance).get();
        doReturn(plugin).when(conditionColumnPlugin).get();

        doReturn(new Pattern52()).when(plugin).getEditingPattern();

        pluginHandler.edit(originalPattern,
                           originalColumn);

        verify(plugin).setOriginalPattern52(originalPattern);
        verify(plugin).setOriginalColumnConfig52(originalColumn);
        verify(pluginHandler).openWizard(plugin);
        verify(wizard).start(plugin);
    }

    @Test
    public void testOpenWizardWhenTableIsReadOnly() throws Exception {
        final DecisionTableColumnPlugin plugin = mock(DecisionTableColumnPlugin.class);
        final NewGuidedDecisionTableColumnWizard wizard = mock(NewGuidedDecisionTableColumnWizard.class);

        doReturn(true).when(presenter).isReadOnly();
        doReturn(wizard).when(wizardManagedInstance).get();

        pluginHandler.openWizard(plugin);

        verify(wizard,
               never()).init(presenter);
        verify(wizard,
               never()).start(plugin);
    }

    @Test
    public void testOpenWizardWhenTableIsNotReadOnly() throws Exception {
        final DecisionTableColumnPlugin plugin = mock(DecisionTableColumnPlugin.class);
        final NewGuidedDecisionTableColumnWizard wizard = mock(NewGuidedDecisionTableColumnWizard.class);

        doReturn(false).when(presenter).isReadOnly();
        doReturn(wizard).when(wizardManagedInstance).get();

        pluginHandler.openWizard(plugin);

        verify(wizard).init(presenter);
        verify(wizard).start(plugin);
    }
}