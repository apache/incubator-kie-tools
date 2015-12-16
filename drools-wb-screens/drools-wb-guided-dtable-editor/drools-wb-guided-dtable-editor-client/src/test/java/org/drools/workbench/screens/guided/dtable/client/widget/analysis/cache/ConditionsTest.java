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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;
import org.junit.Before;
import org.junit.Test;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConditionsTest {

    private Pattern52 pattern;

    @Before
    public void setUp() throws Exception {
        pattern = mock( Pattern52.class );

    }

    @Test
    public void testSubsume001() throws Exception {
        Conditions a = getConditions( getNumericIntegerCondition( pattern, "age", "==", 1 ) );
        Conditions b = getConditions( getNumericIntegerCondition( pattern, "age", "==", 1 ) );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume002() throws Exception {
        Conditions a = getConditions( getNumericIntegerCondition( pattern, "age", "==", 1 ) );

        Conditions b = getConditions( getNumericIntegerCondition( pattern, "age", "==", 1 ),
                                      getNumericIntegerCondition( pattern, "number", "==", 111111111 ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume003() throws Exception {
        Conditions a = getConditions( getStringCondition( pattern, "name", "==", "Toni" ) );

        Conditions b = getConditions( getNumericIntegerCondition( pattern, "age", "==", 12 ),
                                      getStringCondition( pattern, "name", "==", "Toni" ),
                                      getStringCondition( pattern, "lastName", "==", "Rikkola" ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    private Conditions getConditions( ConditionInspector... numericIntegerConditions ) {
        Conditions conditions = new Conditions();
        for ( ConditionInspector inspector : numericIntegerConditions ) {
            conditions.put( inspector.getKey(), inspector );
        }
        return conditions;
    }

}