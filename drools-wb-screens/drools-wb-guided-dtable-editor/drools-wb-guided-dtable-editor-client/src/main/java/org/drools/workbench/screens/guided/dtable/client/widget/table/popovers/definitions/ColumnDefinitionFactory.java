/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.client.callbacks.Callback;

@Dependent
public class ColumnDefinitionFactory {

    private Map<Class, ColumnDefinitionBuilder> builders = new HashMap<>();

    @Inject
    public ColumnDefinitionFactory( final Instance<ColumnDefinitionBuilder> builders ) {
        for ( ColumnDefinitionBuilder builder : builders ) {
            this.builders.put( builder.getSupportedColumnType(),
                               builder );
        }
    }

    public void generateColumnDefinition( final GuidedDecisionTableView.Presenter dtPresenter,
                                          final BaseColumn column,
                                          final Callback<String> afterGenerationCallback ) {
        if ( !builders.containsKey( column.getClass() ) ) {
            return;
        }
        final ColumnDefinitionBuilder builder = builders.get( column.getClass() );
        builder.generateDefinition( dtPresenter,
                                    column,
                                    afterGenerationCallback );
    }

}
