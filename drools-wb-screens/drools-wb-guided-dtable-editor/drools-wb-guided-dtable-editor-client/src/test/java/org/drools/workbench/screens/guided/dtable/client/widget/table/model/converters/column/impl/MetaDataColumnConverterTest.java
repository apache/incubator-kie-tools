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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.PriorityListUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MetaDataColumnConverterTest {

    private MetaDataColumnConverter metaDataColumnConverter;

    @Before
    public void setUp() throws
                        Exception {
        metaDataColumnConverter = new MetaDataColumnConverter();
    }

    @Test
    public void handles() throws
                          Exception {
        assertTrue( metaDataColumnConverter.handles( new MetadataCol52() ) );
        assertFalse( metaDataColumnConverter.handles( new MetaDataColumnConverterCantTouchThisBreakItDownStopHammerTime() ) );
    }

    @Test
    public void convert() throws
                          Exception {
        final MetadataCol52 metadataCol = new MetadataCol52();

        metadataCol.setMetadata( "hello" );

        final GridColumn<?> column = metaDataColumnConverter.convertColumn( metadataCol,
                                                                            mock( GuidedDecisionTablePresenter.Access.class ),
                                                                            mock( GuidedDecisionTableView.class ) );
        assertTrue( column instanceof StringUiColumn );
    }

    @Test
    public void convertRulePriorityColumn() throws
                                            Exception {
        final MetadataCol52 metadataCol = new MetadataCol52();

        metadataCol.setMetadata( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME );

        final GridColumn<?> column = metaDataColumnConverter.convertColumn( metadataCol,
                                                                            mock( GuidedDecisionTablePresenter.Access.class ),
                                                                            mock( GuidedDecisionTableView.class ) );
        assertTrue( column instanceof PriorityListUiColumn );
    }

    private class MetaDataColumnConverterCantTouchThisBreakItDownStopHammerTime
            implements BaseColumn {
        @Override
        public String getHeader() {
            return null;
        }

        @Override
        public void setHeader( final String header ) {

        }

        @Override
        public boolean isHideColumn() {
            return false;
        }

        @Override
        public void setHideColumn( final boolean hideColumn ) {

        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public void setWidth( final int width ) {

        }

        @Override
        public DTCellValue52 getDefaultValue() {
            return null;
        }

        @Override
        public void setDefaultValue( final DTCellValue52 defaultValue ) {

        }
    }
}