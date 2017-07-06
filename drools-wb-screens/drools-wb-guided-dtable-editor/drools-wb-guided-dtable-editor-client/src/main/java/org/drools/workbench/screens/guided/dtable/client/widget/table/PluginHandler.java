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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

@Dependent
public class PluginHandler {

    private final ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance;
    private final ManagedInstance<BRLConditionColumnPlugin> brlConditionColumnPlugin;
    private final ManagedInstance<ConditionColumnPlugin> conditionColumnPlugin;
    private final ManagedInstance<ActionRetractFactPlugin> actionRetractFactPlugin;
    private final ManagedInstance<ActionSetFactPlugin> actionSetFactPlugin;
    private final ManagedInstance<ActionWorkItemSetFieldPlugin> actionWorkItemSetFieldPlugin;
    private final ManagedInstance<ActionWorkItemPlugin> actionWorkItemPlugin;
    private final ManagedInstance<BRLActionColumnPlugin> brlActionColumnPlugin;

    private GuidedDecisionTableView.Presenter presenter;

    @Inject
    public PluginHandler(final ManagedInstance<NewGuidedDecisionTableColumnWizard> wizardManagedInstance,
                         final ManagedInstance<BRLConditionColumnPlugin> brlConditionColumnPlugin,
                         final ManagedInstance<ConditionColumnPlugin> conditionColumnPlugin,
                         final ManagedInstance<ActionRetractFactPlugin> actionRetractFactPlugin,
                         final ManagedInstance<ActionSetFactPlugin> actionSetFactPlugin,
                         final ManagedInstance<ActionWorkItemSetFieldPlugin> actionWorkItemSetFieldPlugin,
                         final ManagedInstance<ActionWorkItemPlugin> actionWorkItemPlugin,
                         final ManagedInstance<BRLActionColumnPlugin> brlActionColumnPlugin) {

        this.wizardManagedInstance = wizardManagedInstance;
        this.brlConditionColumnPlugin = brlConditionColumnPlugin;
        this.conditionColumnPlugin = conditionColumnPlugin;
        this.actionRetractFactPlugin = actionRetractFactPlugin;
        this.actionSetFactPlugin = actionSetFactPlugin;
        this.actionWorkItemSetFieldPlugin = actionWorkItemSetFieldPlugin;
        this.actionWorkItemPlugin = actionWorkItemPlugin;
        this.brlActionColumnPlugin = brlActionColumnPlugin;
    }

    public void init(final GuidedDecisionTableView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void edit(final BRLConditionColumn column) {
        final BRLConditionColumnPlugin plugin = brlConditionColumnPlugin.get();

        plugin.setOriginalColumnConfig52(column);

        openWizard(plugin);
    }

    public void edit(final ActionCol52 column) {
        final DecisionTableColumnPlugin plugin;

        if (column instanceof ActionWorkItemSetFieldCol52 || column instanceof ActionWorkItemInsertFactCol52) {
            plugin = actionWorkItemSetFieldPlugin.get();
        } else if (column instanceof ActionInsertFactCol52 || column instanceof ActionSetFieldCol52) {
            plugin = actionSetFactPlugin.get();
        } else if (column instanceof ActionRetractFactCol52) {
            plugin = actionRetractFactPlugin.get();
        } else if (column instanceof ActionWorkItemCol52) {
            plugin = actionWorkItemPlugin.get();
        } else if (column instanceof LimitedEntryBRLActionColumn) {
            plugin = brlActionColumnPlugin.get();
        } else if (column instanceof BRLActionColumn) {
            plugin = brlActionColumnPlugin.get();
        } else {
            return;
        }

        plugin.setOriginalColumnConfig52(column);

        openWizard(plugin);
    }

    public void edit(final Pattern52 pattern,
                     final ConditionCol52 column) {
        final ConditionColumnPlugin plugin = conditionColumnPlugin.get();

        plugin.setOriginalPattern52(pattern);
        plugin.setOriginalColumnConfig52(column);

        openWizard(plugin);
    }

    void openWizard(final DecisionTableColumnPlugin plugin) {
        if (presenter.isReadOnly()) {
            return;
        }

        final NewGuidedDecisionTableColumnWizard wizard = wizardManagedInstance.get();

        wizard.init(presenter);
        wizard.start(plugin);
    }
}
