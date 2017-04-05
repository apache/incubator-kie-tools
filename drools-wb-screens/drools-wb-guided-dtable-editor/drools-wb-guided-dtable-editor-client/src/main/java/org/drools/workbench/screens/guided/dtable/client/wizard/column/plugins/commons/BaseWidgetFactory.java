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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasValueOptionsPage;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

abstract class BaseWidgetFactory<T extends BaseDecisionTableColumnPlugin & HasValueOptionsPage> {

    private final T plugin;

    BaseWidgetFactory(final T plugin) {
        this.plugin = plugin;
    }

    protected T getPlugin() {
        return plugin;
    }

    protected DTCellValueWidgetFactory factory() {
        final GuidedDecisionTable52 model = getPlugin().presenter.getModel();
        final AsyncPackageDataModelOracle oracle = getPlugin().presenter.getDataModelOracle();
        final boolean allowEmptyValues = model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;

        return DTCellValueWidgetFactory.getInstance(model,
                                                    oracle,
                                                    false,
                                                    allowEmptyValues);
    }
}
