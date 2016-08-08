/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.client.callbacks.Callback;

/**
 * Definition of a Builder that can generate a String representation of a column's definition.
 */
public interface ColumnDefinitionBuilder {

    /**
     * Returns the {@link Class} that this Builder supports.
     * @return
     */
    Class getSupportedColumnType();

    /**
     * Generates a {@link String} representation of a column's definition. This method does not
     * return a {@link String} as the construction may require asynchronous calls to the server.
     * @param dtPresenter
     *         The Decision Table to which the column belongs.
     * @param column
     *         The column for which we need a build the definition.
     * @param afterGenerationCallback
     *         A callback to be invoked with the definition.
     */
    void generateDefinition( final GuidedDecisionTableView.Presenter dtPresenter,
                             final BaseColumn column,
                             final Callback<String> afterGenerationCallback );

}
