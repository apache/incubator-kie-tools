/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;

import static org.jgroups.util.Util.assertFalse;
import static org.jgroups.util.Util.assertTrue;
import static org.junit.Assert.assertEquals;

public class ResultsTest {

    @Test
    public void testNewInstance() {
        final Results results = new DefaultResultsImpl();
        assertTrue( results.getMessages().isEmpty() );
        assertTrue( results.getMessages( ResultType.ERROR ).isEmpty() );
        assertTrue( results.getMessages( ResultType.WARNING ).isEmpty() );
    }

    @Test
    public void testAdd() {
        final Results results = new DefaultResultsImpl();
        results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                   "An error" ) );
        results.addMessage( new DefaultResultImpl( ResultType.WARNING,
                                                   "A warning" ) );
        assertEquals( 2,
                      results.getMessages().size() );
        assertEquals( 1,
                      results.getMessages( ResultType.ERROR ).size() );
        assertEquals( 1,
                      results.getMessages( ResultType.WARNING ).size() );
    }

    @Test
    public void testContains() {
        final Results results = new DefaultResultsImpl();
        results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                   "An error" ) );
        assertTrue( results.contains( ResultType.ERROR ) );
        assertFalse( results.contains( ResultType.WARNING ) );
    }

}
