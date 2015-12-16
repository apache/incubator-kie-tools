/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import java.util.ArrayList;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EnumConditionInspectorRedundancyTest {

    @Test
    public void test001() throws Exception {
        EnumConditionInspector a = getCondition( "Approved", "!=" );
        EnumConditionInspector b = getCondition( "Approved", "!=" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void test002() throws Exception {
        EnumConditionInspector a = getCondition( "Approved", "==" );
        EnumConditionInspector b = getCondition( "Approved", "==" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void test003() throws Exception {
        EnumConditionInspector a = getCondition( "Approved", "!=" );
        EnumConditionInspector b = getCondition( "Declined", "!=" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void test004() throws Exception {
        EnumConditionInspector a = getCondition( "Approved", "==" );
        EnumConditionInspector b = getCondition( "Declined", "==" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    private EnumConditionInspector getCondition( String value,
                                                 String operator ) {
        ArrayList<String> allValueList = new ArrayList<String>();

        allValueList.add( "Approved" );
        allValueList.add( "Declined" );

        return new EnumConditionInspector( mock( Pattern52.class ),
                                           "status",
                                           allValueList,
                                           value,
                                           operator );
    }
}