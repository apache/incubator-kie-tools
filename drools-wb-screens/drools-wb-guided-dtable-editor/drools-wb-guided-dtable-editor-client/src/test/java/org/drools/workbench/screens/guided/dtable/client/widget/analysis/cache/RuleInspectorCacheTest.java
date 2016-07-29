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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.AnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DataBuilderProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class RuleInspectorCacheTest {

    private RuleInspectorCache    cache;
    private GuidedDecisionTable52 table52;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                                  .withPersonAgeColumn( "==" )
                                  .withPersonAgeColumn( "==" )
                                  .withPersonApprovedActionSetField()
                                  .withData( DataBuilderProvider
                                                     .row( 0, 1, true )
                                                     .row( 0, 1, true )
                                                     .row( 0, 1, true )
                                                     .row( 0, 1, true )
                                                     .row( 0, 1, false )
                                                     .row( 0, 1, true )
                                                     .row( 0, 1, true )
                                                     .end() )
                                  .buildTable();

        cache = analyzerProvider.getCache( table52 );
    }

    @Test
    public void testInit() throws Exception {
        assertEquals( 7, cache.all().size() );
    }

    @Test
    public void testRemoveRow() throws Exception {
        cache.removeRow( 3 );

        final Collection<RuleInspector> all = cache.all();
        assertEquals( 6, all.size() );


        assertContainsRowNumbers( all,
                                  0, 1, 2, 3, 4, 5 );
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

    @Test
    public void testRemoveColumn() throws Exception {

        table52.getActionCols().clear();
        table52.getData().get( 0 ).remove( 4 );
        table52.getData().get( 1 ).remove( 4 );
        table52.getData().get( 2 ).remove( 4 );
        table52.getData().get( 3 ).remove( 4 );
        table52.getData().get( 4 ).remove( 4 );
        table52.getData().get( 5 ).remove( 4 );
        table52.getData().get( 6 ).remove( 4 );

        cache.deleteColumns( 4, 1 );

        Collection<RuleInspector> all = cache.all();
        assertEquals( 7, all.size() );

        for ( RuleInspector ruleInspector : all ) {
            assertFalse( ruleInspector.atLeastOneActionHasAValue() );
        }
    }

}