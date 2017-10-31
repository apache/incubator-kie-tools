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

package org.drools.workbench.services.verifier.core;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.IndexImpl;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.core.cache.RuleInspectorCache;
import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.drools.workbench.services.verifier.core.checks.AnalyzerConfigurationMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class RuleInspectorCacheTest {

    private RuleInspectorCache cache;

    @Before
    public void setUp() throws
                        Exception {
        final Index index = new IndexImpl();
        final AnalyzerConfiguration configuration = new AnalyzerConfigurationMock();

        cache = new RuleInspectorCache( index,
                                        configuration );

        cache.addRule( new Rule( 0,
                                 configuration ) );
        cache.addRule( new Rule( 1,
                                 configuration ) );
        cache.addRule( new Rule( 2,
                                 configuration ) );
        cache.addRule( new Rule( 3,
                                 configuration ) );
        cache.addRule( new Rule( 4,
                                 configuration ) );
        cache.addRule( new Rule( 5,
                                 configuration ) );
        cache.addRule( new Rule( 6,
                                 configuration ) );
    }

    @Test
    public void testInit() throws
                           Exception {
        assertEquals( 7,
                      cache.all()
                              .size() );
    }

    @Test
    public void testRemoveRow() throws
                                Exception {
        cache.removeRow( 3 );

        final Collection<RuleInspector> all = cache.all();
        assertEquals( 6,
                      all.size() );


        assertContainsRowNumbers( all,
                                  0,
                                  1,
                                  2,
                                  3,
                                  4,
                                  5 );
    }

    private void assertContainsRowNumbers( final Collection<RuleInspector> all,
                                           final int... numbers ) {
        final ArrayList<Integer> rowNumbers = new ArrayList<>();
        for ( final RuleInspector ruleInspector : all ) {
            final int rowIndex = ruleInspector.getRowIndex();
            rowNumbers.add( rowIndex );
        }

        for ( final int number : numbers ) {
            assertTrue( rowNumbers.toString(),
                        rowNumbers.contains( number ) );
        }
    }

}