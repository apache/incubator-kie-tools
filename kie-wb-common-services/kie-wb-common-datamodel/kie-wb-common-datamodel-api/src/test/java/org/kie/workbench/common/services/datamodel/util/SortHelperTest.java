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

package org.kie.workbench.common.services.datamodel.util;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SortHelperTest {

    @Test
    public void nullCompareToNullTest() {
        assertEquals( 0, SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare( null, null ) );
    }

    @Test
    public void stringCompareToNullTest() {
        assertEquals( 1, SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare( "a", null ) );
    }

    @Test
    public void nullCompareToStringTest() {
        assertEquals( -1, SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare( null, "a" ) );
    }

    @Test
    public void sortAlphabeticallyTest() {
        String[] names = { "Wilson", "victor", "john", "rose", "Mary Jane", "Rose", "mary", "peter" };

        Arrays.sort( names, SortHelper.ALPHABETICAL_ORDER_COMPARATOR );

        assertEquals( 8, names.length );
        assertEquals( names[0], "john" );
        assertEquals( names[1], "mary" );
        assertEquals( names[2], "Mary Jane" );
        assertEquals( names[3], "peter" );
        assertEquals( names[4], "Rose" );
        assertEquals( names[5], "rose" );
        assertEquals( names[6], "victor" );
        assertEquals( names[7], "Wilson" );
    }

}
