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

import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionSetFactPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;

public class ValueOptionsPageInitializer {

    public static ValueOptionsPage<ActionSetFactPlugin> init(final ValueOptionsPage<ActionSetFactPlugin> page,
                                                             final ActionSetFactPlugin plugin) {
        page.setPlugin(plugin);

        switch (plugin.tableFormat()) {
            case EXTENDED_ENTRY:
                page.enableValueList();
                page.enableDefaultValue();
                break;
            case LIMITED_ENTRY:
                page.enableLimitedValue();
                break;
        }

        return page;
    }

    public static ValueOptionsPage<ConditionColumnPlugin> init(final ValueOptionsPage<ConditionColumnPlugin> page,
                                                               final ConditionColumnPlugin plugin) {
        page.setPlugin(plugin);

        switch (plugin.tableFormat()) {
            case EXTENDED_ENTRY:
                page.enableValueList();
                page.enableDefaultValue();
                break;
            case LIMITED_ENTRY:
                page.enableCepOperators();
                page.enableLimitedValue();
                break;
        }

        page.enableBinding();

        return page;
    }
}
