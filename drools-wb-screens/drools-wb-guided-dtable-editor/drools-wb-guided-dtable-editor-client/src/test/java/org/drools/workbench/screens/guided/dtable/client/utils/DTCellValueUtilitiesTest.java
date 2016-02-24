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

package org.drools.workbench.screens.guided.dtable.client.utils;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DTCellValueUtilitiesTest {

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    private DTCellValueUtilities utilities;

    @Before
    public void setup() {
        utilities = new DTCellValueUtilities( model,
                                              oracle );
    }

    @Test
    public void testRemoveCommaSeparatedValue() {
        DTCellValue52 dcv;

        dcv = new DTCellValue52( 1 );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( 1,
                      dcv.getNumericValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getStringValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.NUMERIC_INTEGER,
                      dcv.getDataType() );

        dcv = new DTCellValue52( 1L );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( 1L,
                      dcv.getNumericValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getStringValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.NUMERIC_LONG,
                      dcv.getDataType() );

        dcv = new DTCellValue52( 1.0 );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( 1.0,
                      dcv.getNumericValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getStringValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.NUMERIC_DOUBLE,
                      dcv.getDataType() );

        dcv = new DTCellValue52( "Fred" );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( "Fred",
                      dcv.getStringValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getNumericValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.STRING,
                      dcv.getDataType() );

        dcv = new DTCellValue52( "Fred,Ginger" );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( "Fred",
                      dcv.getStringValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getNumericValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.STRING,
                      dcv.getDataType() );

        dcv = new DTCellValue52( ",Ginger" );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( "",
                      dcv.getStringValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getNumericValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.STRING,
                      dcv.getDataType() );

        dcv = new DTCellValue52( "Fred," );
        utilities.removeCommaSeparatedValue( dcv );
        assertEquals( "Fred",
                      dcv.getStringValue() );
        assertNull( dcv.getBooleanValue() );
        assertNull( dcv.getNumericValue() );
        assertNull( dcv.getDateValue() );
        assertEquals( DataType.DataTypes.STRING,
                      dcv.getDataType() );
    }

}
