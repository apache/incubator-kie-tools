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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FieldConditionTest {

    @Test
    public void testNullValueDefaultsToNoValue() throws Exception {

        final FieldCondition<Comparable> condition = new FieldCondition<>( mock( Field.class ),
                                                                           mock( Column.class ),
                                                                           "!=",
                                                                           new Values() );

        assertEquals( "!=", condition.getOperator() );
        assertTrue( condition.getValues().isEmpty() );
    }

    @Test
    public void testIsNullOperator() throws Exception {

        final FieldCondition<Comparable> condition = new FieldCondition<>( mock( Field.class ),
                                                                           mock( Column.class ),
                                                                           "!= null",
                                                                           Values.nullValue() );

        assertEquals( "!=", condition.getOperator() );
        assertTrue( condition.getValues().contains( null ) );

    }

}