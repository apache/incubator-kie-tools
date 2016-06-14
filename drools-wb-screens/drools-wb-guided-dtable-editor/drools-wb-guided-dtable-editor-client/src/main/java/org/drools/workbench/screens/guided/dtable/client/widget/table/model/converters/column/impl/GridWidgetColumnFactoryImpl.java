/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

@Dependent
public class GridWidgetColumnFactoryImpl implements GridWidgetColumnFactory {

    private final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();

    @Override
    public void setConverters( final List<BaseColumnConverter> converters ) {
        this.converters.clear();
        Collections.sort( converters,
                          new Comparator<BaseColumnConverter>() {
                              @Override
                              public int compare( final BaseColumnConverter o1,
                                                  final BaseColumnConverter o2 ) {
                                  return o2.priority() - o1.priority();
                              }
                          } );
        this.converters.addAll( converters );
    }

    @Override
    public void initialise( final GuidedDecisionTable52 model,
                            final AsyncPackageDataModelOracle oracle,
                            final ColumnUtilities columnUtilities,
                            final GuidedDecisionTableView.Presenter presenter ) {
        for ( BaseColumnConverter converter : converters ) {
            converter.initialise( model,
                                  oracle,
                                  columnUtilities,
                                  presenter );
        }
    }

    @Override
    public GridColumn<?> convertColumn( final BaseColumn column,
                                        final GuidedDecisionTablePresenter.Access access,
                                        final GuidedDecisionTableView gridWidget ) {
        for ( BaseColumnConverter converter : converters ) {
            if ( converter.handles( column ) ) {
                return converter.convertColumn( column,
                                                access,
                                                gridWidget );
            }
        }
        throw new IllegalArgumentException( "Column '" + column.getHeader() + "' was not converted." );
    }

}
