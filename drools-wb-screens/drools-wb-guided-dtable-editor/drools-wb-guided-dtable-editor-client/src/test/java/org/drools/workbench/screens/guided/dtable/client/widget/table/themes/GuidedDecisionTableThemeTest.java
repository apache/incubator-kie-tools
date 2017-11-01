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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableThemeTest {

    @Mock
    private GuidedDecisionTableUiModel uiModel;

    @Mock
    private GridColumn<?> uiColumn;

    @Mock
    private List<GridColumn<?>> uiColumns;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private List<BaseColumn> columns;

    private GuidedDecisionTableTheme theme;

    @Before
    public void setup() {
        this.theme = new GuidedDecisionTableTheme( uiModel,
                                                   model );

        when( uiModel.getColumns() ).thenReturn( uiColumns );
        when( uiColumns.indexOf( uiColumn ) ).thenReturn( 0 );
        when( model.getExpandedColumns() ).thenReturn( columns );
    }

    @Test
    public void rowNumberColumnIdentification() {
        doTest( RowNumberCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.ROW_NUMBER );
    }

    @Test
    public void descriptionColumnIdentification() {
        doTest( DescriptionCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.DESCRIPTION );
    }

    @Test
    public void metaDataColumnIdentification() {
        doTest( MetadataCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.METADATA );
    }

    @Test
    public void attributeColumnIdentification() {
        doTest( AttributeCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.ATTRIBUTE );
    }

    @Test
    public void conditionColumnIdentification() {
        doTest( ConditionCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.CONDITION );
    }

    @Test
    public void actionColumnIdentification() {
        doTest( ActionCol52.class,
                GuidedDecisionTableTheme.ModelColumnType.ACTION );
    }

    private <T extends BaseColumn> void doTest( final Class<T> columnClass,
                                                final GuidedDecisionTableTheme.ModelColumnType modelType ) {
        final BaseColumn column = mock( columnClass );
        when( columns.get( eq( 0 ) ) ).thenReturn( column );

        final GuidedDecisionTableTheme.ModelColumnType type = theme.getModelColumnType( uiColumn );
        assertEquals( modelType,
                      type );

    }

}
