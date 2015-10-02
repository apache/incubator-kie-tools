/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class ConditionInspectorBuilderTest {

    private final GuidedDecisionTableUtils utils = mock( GuidedDecisionTableUtils.class );
    private final ConditionCol52 conditionColumn = mock( ConditionCol52.class );
    private final ConditionInspectorBuilder ciBuilder = new ConditionInspectorBuilder( utils,
                                                                                       mock( Pattern52.class ),
                                                                                       conditionColumn,
                                                                                       mock( DTCellValue52.class ) );

    private final String type;
    private final Class expectedInspector;

    public ConditionInspectorBuilderTest( String type, Class expectedInspector ) {
        this.type = type;
        this.expectedInspector = expectedInspector;
    }

    @Test
    public void testBuildConditionInspector() {
        when( utils.getType( conditionColumn ) ).thenReturn( type );
        when( utils.getValueList( conditionColumn ) ).thenReturn( new String[ 0 ] );
        when( conditionColumn.getOperator() ).thenReturn( "==" );

        assertEquals( "For field type " + type, expectedInspector, ciBuilder.buildConditionInspector().getClass() );
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][] {
            { DataType.TYPE_NUMERIC_INTEGER, NumericIntegerConditionInspector.class },
            { DataType.TYPE_NUMERIC_LONG, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_DOUBLE, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_FLOAT, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_BIGDECIMAL, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_BIGINTEGER, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_SHORT, ComparableConditionInspector.class },
            { DataType.TYPE_NUMERIC_BYTE, ComparableConditionInspector.class },
            { DataType.TYPE_COMPARABLE, ComparableConditionInspector.class }, // this fails, UnrecognizedConditionInspector is returned
            { DataType.TYPE_DATE, ComparableConditionInspector.class },
            { DataType.TYPE_STRING, StringConditionInspector.class },
            { DataType.TYPE_BOOLEAN, BooleanConditionInspector.class },
            { DataType.TYPE_COLLECTION, UnrecognizedConditionInspector.class },
            { DataType.TYPE_OBJECT, UnrecognizedConditionInspector.class },
        } );
    }
}
