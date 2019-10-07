/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.Dependent;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.DialectUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.SalienceUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxStringSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxIntegerSingletonDOMElementFactory;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

@Dependent
public class AttributeColumnConverter extends BaseColumnConverterImpl {

    @Override
    public boolean handles( final BaseColumn column ) {
        return column instanceof AttributeCol52;
    }

    @Override
    public GridColumn<?> convertColumn( final BaseColumn column,
                                        final GuidedDecisionTablePresenter.Access access,
                                        final GuidedDecisionTableView gridWidget ) {
        final AttributeCol52 attributeColumn = (AttributeCol52) column;
        final String attribute = ((AttributeCol52) column).getAttribute();
        if ( Objects.equals(attribute, Attribute.SALIENCE.getAttributeName()) ) {
            return newSalienceColumn( makeSalienceHeaderMetaData( column ),
                                      Math.max( column.getWidth(),
                                                DEFAULT_COLUMN_WIDTH + 30 ),
                                      true,
                                      !column.isHideColumn(),
                                      access,
                                      attributeColumn.isUseRowNumber(),
                                      gridWidget );

        } else if ( Objects.equals(attribute, Attribute.DIALECT.getAttributeName()) ) {
            return newDialectColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( Objects.equals(attribute, Attribute.DURATION.getAttributeName()) ) {
            return newLongColumn( makeHeaderMetaData( column ),
                                  Math.max( column.getWidth(),
                                            DEFAULT_COLUMN_WIDTH ),
                                  true,
                                  !column.isHideColumn(),
                                  access,
                                  gridWidget );

        } else if ( needBooleanColumn(attribute) ) {
            return newBooleanColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( needDateColumn(attribute) ) {
            return newDateColumn( makeHeaderMetaData( column ),
                                  Math.max( column.getWidth(),
                                            DEFAULT_COLUMN_WIDTH ),
                                  true,
                                  !column.isHideColumn(),
                                  access,
                                  gridWidget );

        }
        return newStringColumn( makeHeaderMetaData( column ),
                                Math.max( column.getWidth(),
                                          DEFAULT_COLUMN_WIDTH ),
                                true,
                                !column.isHideColumn(),
                                access,
                                gridWidget );
    }

    private boolean needBooleanColumn(final String attribute) {
        return Objects.equals(Attribute.getAttributeDataType(attribute), DataType.TYPE_BOOLEAN);
    }

    private boolean needDateColumn(final String attribute) {
        return Objects.equals(Attribute.getAttributeDataType(attribute), DataType.TYPE_DATE);
    }

    private List<GridColumn.HeaderMetaData> makeSalienceHeaderMetaData( final BaseColumn column ) {
        final List<GridColumn.HeaderMetaData> headerMetaData;

        if ( model.getHitPolicy().equals(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT) ) {
            headerMetaData = new ArrayList<GridColumn.HeaderMetaData>() {{
                add( new BaseHeaderMetaData( GuidedDecisionTableConstants.INSTANCE.HasPriorityOverRow(),
                                             AttributeCol52.class.getName() ) );
            }};
        } else {
            headerMetaData = makeHeaderMetaData( column );
        }
        return headerMetaData;
    }

    protected GridColumn<?> newSalienceColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                               final double width,
                                               final boolean isResizable,
                                               final boolean isVisible,
                                               final GuidedDecisionTablePresenter.Access access,
                                               final boolean useRowNumber,
                                               final GuidedDecisionTableView gridWidget ) {
        return new SalienceUiColumn( headerMetaData,
                                     width,
                                     isResizable,
                                     isVisible,
                                     access,
                                     useRowNumber,
                                     new TextBoxIntegerSingletonDOMElementFactory( gridPanel,
                                                                                   gridLayer,
                                                                                   gridWidget ) );
    }

    protected GridColumn<String> newDialectColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                   final double width,
                                                   final boolean isResizable,
                                                   final boolean isVisible,
                                                   final GuidedDecisionTablePresenter.Access access,
                                                   final GuidedDecisionTableView gridWidget ) {
        return new DialectUiColumn( headerMetaData,
                                    width,
                                    isResizable,
                                    isVisible,
                                    access,
                                    new ListBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                 gridLayer,
                                                                                 gridWidget ) );
    }

    @Override
    public List<GridColumn.HeaderMetaData> makeHeaderMetaData( final BaseColumn column ) {
        return new ArrayList<GridColumn.HeaderMetaData>() {{
            add( new BaseHeaderMetaData( ( (AttributeCol52) column ).getAttribute(),
                                         AttributeCol52.class.getName() ) );
        }};
    }

}
