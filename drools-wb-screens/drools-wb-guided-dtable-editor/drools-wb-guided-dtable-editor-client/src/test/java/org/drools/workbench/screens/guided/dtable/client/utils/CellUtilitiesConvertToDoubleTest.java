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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CellUtilitiesConvertToDoubleTest {

    private Object expected;
    private Object value;

    private CellUtilities cellUtilities;

    public CellUtilitiesConvertToDoubleTest( final Object expected,
                                             final Object value ) {
        this.expected = expected;
        this.value = value;
    }

    @Before
    public void setup() {
        cellUtilities = new CellUtilities();
    }

    @Parameterized.Parameters
    public static Collection testParameters() {
        return Arrays.asList( new Object[][]{
                { new Double( "1" ), new BigDecimal( "1" ) },
                { new Double( "2" ), new BigInteger( "2" ) },
                { new Double( "3" ), new Byte( "3" ) },
                { new Double( "4.0" ), new Double( "4.0" ) },
                { new Double( "5.0" ), new Float( "5.0" ) },
                { new Double( "6" ), new Integer( "6" ) },
                { new Double( "7" ), new Long( "7" ) },
                { new Double( "8" ), new Short( "8" ) },
                { new Double( "9" ), "9" },
                { null, true },
                { null, new Date() },
                { null, "banana" }
        } );
    }

    @Test
    public void conversion() {
        assertEquals( expected,
                      cellUtilities.convertToDouble( new DTCellValue52( value ) ) );
    }

}
