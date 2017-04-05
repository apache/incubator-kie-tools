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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionSetFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionWorkItemSetFieldPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLActionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.BRLConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;

public class AdditionalInfoPageInitializer {

    public static AdditionalInfoPage<ActionRetractFactPlugin> init(final AdditionalInfoPage<ActionRetractFactPlugin> page,
                                                                   final ActionRetractFactPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();

        return page;
    }

    public static AdditionalInfoPage<ActionSetFactPlugin> init(final AdditionalInfoPage<ActionSetFactPlugin> page,
                                                               final ActionSetFactPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();
        page.enableLogicallyInsert();
        page.enableUpdateEngineWithChanges();

        return page;
    }

    public static AdditionalInfoPage<ConditionColumnPlugin> init(final AdditionalInfoPage<ConditionColumnPlugin> page,
                                                                 final ConditionColumnPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();

        return page;
    }

    public static AdditionalInfoPage<ActionWorkItemPlugin> init(final AdditionalInfoPage<ActionWorkItemPlugin> page,
                                                                final ActionWorkItemPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();

        return page;
    }

    public static AdditionalInfoPage<ActionWorkItemSetFieldPlugin> init(final AdditionalInfoPage<ActionWorkItemSetFieldPlugin> page,
                                                                        final ActionWorkItemSetFieldPlugin plugin) {

        page.setPlugin(plugin);
        page.enableHeader();
        page.enableLogicallyInsert();
        page.enableUpdateEngineWithChanges();
        page.enableHideColumn();

        return page;
    }

    public static AdditionalInfoPage<BRLConditionColumnPlugin> init(final AdditionalInfoPage<BRLConditionColumnPlugin> page,
                                                                    final BRLConditionColumnPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();

        return page;
    }

    public static AdditionalInfoPage<BRLActionColumnPlugin> init(final AdditionalInfoPage<BRLActionColumnPlugin> page,
                                                                 final BRLActionColumnPlugin plugin) {
        page.setPlugin(plugin);
        page.enableHeader();
        page.enableHideColumn();

        return page;
    }
}
