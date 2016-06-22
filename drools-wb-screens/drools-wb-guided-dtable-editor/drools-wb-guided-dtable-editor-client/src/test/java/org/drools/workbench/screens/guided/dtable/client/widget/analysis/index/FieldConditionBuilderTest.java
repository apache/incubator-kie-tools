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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class FieldConditionBuilderTest {

    @Mock
    private ColumnUtilities utils;

    @Mock
    private ConditionCol52 conditionCol52;

    @Mock
    private DTCellValue52 realCellValue;

    private FieldConditionBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new FieldConditionBuilder( mock( Field.class ),
                                             utils,
                                             mock( Column.class ),
                                             conditionCol52,
                                             realCellValue );
    }

    @Test
    public void testIn() throws Exception {
        when( utils.getValueList( conditionCol52 ) ).thenReturn( new String[0] );
        when( utils.getType( conditionCol52 ) ).thenReturn( DataType.TYPE_STRING );

        when( conditionCol52.getOperator() ).thenReturn( "in" );
        when( realCellValue.getStringValue() ).thenReturn( "a, b" );


        final FieldCondition condition = ( FieldCondition ) builder.build();

        assertEquals( "in", condition.getOperator() );
        assertEquals( 2, condition.getValues().size() );
        assertTrue( condition.getValues().contains( "a" ) );
        assertTrue( condition.getValues().contains( "b" ) );
    }

    @Test
    public void testNotIn() throws Exception {
        when( utils.getValueList( conditionCol52 ) ).thenReturn( new String[0] );
        when( utils.getType( conditionCol52 ) ).thenReturn( DataType.TYPE_STRING );

        when( conditionCol52.getOperator() ).thenReturn( "not in" );
        when( realCellValue.getStringValue() ).thenReturn( "a, b" );


        final FieldCondition condition = ( FieldCondition ) builder.build();

        assertEquals( "not in", condition.getOperator() );
        assertEquals( 2, condition.getValues().size() );
        assertTrue( condition.getValues().contains( "a" ) );
        assertTrue( condition.getValues().contains( "b" ) );
    }
}