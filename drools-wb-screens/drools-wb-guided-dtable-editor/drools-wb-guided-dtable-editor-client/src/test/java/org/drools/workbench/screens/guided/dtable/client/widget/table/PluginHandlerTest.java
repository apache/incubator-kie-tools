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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

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
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionSetFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemSetFieldPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PluginHandlerTest {

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

    private PluginHandler pluginHandler;

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
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemSetFieldCol52() {
        actionCol52TestCase(mock(ActionWorkItemSetFieldCol52.class),
                            actionWorkItemSetFieldPlugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemInsertFactCol52() {
        actionCol52TestCase(mock(ActionWorkItemInsertFactCol52.class),
                            actionWorkItemSetFieldPlugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionInsertFactCol52() {
        actionCol52TestCase(mock(ActionInsertFactCol52.class),
                            actionSetFactPlugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionSetFieldCol52() {
        actionCol52TestCase(mock(ActionSetFieldCol52.class),
                            actionSetFactPlugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionRetractFactCol52() {
        actionCol52TestCase(mock(ActionRetractFactCol52.class),
                            actionRetractFactPlugin);
    }

    @Test
    public void testEditWhenColumnIsAnActionWorkItemCol52() {
        actionCol52TestCase(mock(ActionWorkItemCol52.class),
                            actionWorkItemPlugin);
    }

    @Test
    public void testEditWhenColumnIsALimitedEntryBRLActionColumn() {
        actionCol52TestCase(mock(LimitedEntryBRLActionColumn.class),
                            brlActionColumnPlugin);
    }

    @Test
    public void testEditWhenColumnIsABRLActionColumn() {
        actionCol52TestCase(mock(BRLActionColumn.class),
                            brlActionColumnPlugin);
    }

    @Test
    public void testEditWhenColumnIsInvalid() {
        final ActionCol52 column = mock(ActionCol52.class);
        final DecisionTableColumnPlugin plugin = mock(DecisionTableColumnPlugin.class);

        doReturn(plugin).when(actionWorkItemSetFieldPlugin).get();
        doNothing().when(pluginHandler).openWizard(any());

        pluginHandler.edit(column);

        verify(plugin,
               never()).setOriginalColumnConfig52(column);
        verify(pluginHandler,
               never()).openWizard(plugin);
    }

    @Test
    public void testEditWhenColumnIsABRLConditionColumn() {
        final BRLConditionColumn column = mock(BRLConditionColumn.class);
        final BRLConditionColumnPlugin plugin = mock(BRLConditionColumnPlugin.class);

        doReturn(plugin).when(brlConditionColumnPlugin).get();
        doNothing().when(pluginHandler).openWizard(any());

        pluginHandler.edit(column);

        verify(plugin).setOriginalColumnConfig52(column);
        verify(pluginHandler).openWizard(plugin);
    }

    @Test
    public void testEditWhenColumnIsAConditionCol52() {
        final Pattern52 pattern = mock(Pattern52.class);
        final ConditionCol52 column = mock(ConditionCol52.class);
        final ConditionColumnPlugin plugin = mock(ConditionColumnPlugin.class);

        doReturn(plugin).when(conditionColumnPlugin).get();
        doNothing().when(pluginHandler).openWizard(plugin);

        pluginHandler.edit(pattern,
                           column);

        verify(plugin).setOriginalPattern52(pattern);
        verify(plugin).setOriginalColumnConfig52(column);
        verify(pluginHandler).openWizard(plugin);
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

    private void actionCol52TestCase(final ActionCol52 column,
                                     final ManagedInstance<? extends DecisionTableColumnPlugin> actionWorkItemSetFieldPlugin) {
        final DecisionTableColumnPlugin plugin = mock(DecisionTableColumnPlugin.class);

        doReturn(plugin).when(actionWorkItemSetFieldPlugin).get();
        doNothing().when(pluginHandler).openWizard(any());

        pluginHandler.edit(column);

        verify(plugin).setOriginalColumnConfig52(column);
        verify(pluginHandler).openWizard(plugin);
    }
}