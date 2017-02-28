/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.PriorityListUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxStringSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

@Dependent
public class MetaDataColumnConverter
        extends BaseColumnConverterImpl {

    @Override
    public boolean handles( final BaseColumn column ) {
        return column instanceof MetadataCol52;
    }

    @Override
    public GridColumn<?> convertColumn( final BaseColumn column,
                                        final GuidedDecisionTablePresenter.Access access,
                                        final GuidedDecisionTableView gridWidget ) {
        final MetadataCol52 metadataCol = (MetadataCol52) column;

        if ( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME.equals( metadataCol.getMetadata() ) ) {
            return new PriorityListUiColumn( makePriorityHeaderMetaData(),
                                             Math.max( column.getWidth(),
                                                       DEFAULT_COLUMN_WIDTH + 20 ),
                                             access,
                                             new ListBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                          gridLayer,
                                                                                          gridWidget ));
        } else {
            return newStringColumn( makeHeaderMetaData( column ),
                                    Math.max( column.getWidth(),
                                              DEFAULT_COLUMN_WIDTH ),
                                    true,
                                    !column.isHideColumn(),
                                    access,
                                    gridWidget );
        }
    }

    private List<GridColumn.HeaderMetaData> makePriorityHeaderMetaData() {
        return new ArrayList<GridColumn.HeaderMetaData>() {{
            add( new BaseHeaderMetaData( GuidedDecisionTableConstants.INSTANCE.HasPriorityOverRow(),
                                         MetadataCol52.class.getName() ) );
        }};
    }

    @Override
    public List<GridColumn.HeaderMetaData> makeHeaderMetaData( final BaseColumn column ) {
        return new ArrayList<GridColumn.HeaderMetaData>() {{
            add( new BaseHeaderMetaData( ( (MetadataCol52) column ).getMetadata(),
                                         MetadataCol52.class.getName() ) );
        }};
    }

}
