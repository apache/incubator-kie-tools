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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.CancellableRepeatingCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RowInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Checks;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.PairCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ChecksTest {

    @Spy
    private Checks checks = new Checks() {
        @Override
        protected ArrayList<Check> makeSingleRowChecks( RowInspector rowInspector ) {
            ArrayList<Check> checks = new ArrayList<Check>();
            checks.add( new MockSingleCheck( rowInspector ) );
            return checks;
        }

        @Override
        protected ArrayList<Check> makePairRowChecks( RowInspector rowInspector,
                                                      RowInspector other ) {
            ArrayList<Check> checks = new ArrayList<Check>();
            checks.add( new MockPairCheck( rowInspector, other ) );
            return checks;
        }

        @Override
        protected void doRun( final CancellableRepeatingCommand command ) {
            while ( command.execute() ) {
                //loop
            }
        }

    };

    @Mock
    private RowInspectorCache cache;

    private RowInspector rowInspector1;
    private RowInspector rowInspector2;
    private RowInspector rowInspector3;
    private ArrayList<RowInspector> rowInspectors;

    @Before
    public void setUp() throws Exception {

        rowInspector1 = mockRowInspector( 1 );
        rowInspector2 = mockRowInspector( 2 );
        rowInspector3 = mockRowInspector( 3 );

        rowInspectors = new ArrayList<RowInspector>();
        rowInspectors.add( rowInspector1 );
        rowInspectors.add( rowInspector2 );
        rowInspectors.add( rowInspector3 );
        when( cache.all() ).thenReturn( rowInspectors );

        checks.add( rowInspector1 );
        checks.add( rowInspector2 );
        checks.add( rowInspector3 );
    }

    @Test
    public void testChecksGetGenerated() throws Exception {
        assertEquals( 3, checks.get( rowInspector1 ).size() );
        assertEquals( 3, checks.get( rowInspector2 ).size() );
        assertEquals( 3, checks.get( rowInspector3 ).size() );
    }

    @Test
    public void testRemove() throws Exception {

        Collection<Check> removed = this.checks.remove( rowInspector2 );

        assertEquals( 5, removed.size() );
        assertNotNull( this.checks.get( rowInspector1 ) );
        assertNull( this.checks.get( rowInspector2 ) );
        assertNotNull( this.checks.get( rowInspector3 ) );
    }

    @Test
    public void testRunTests() throws Exception {

        for ( RowInspector rowInspector : cache.all() ) {
            assertNoIssues( rowInspector );
        }

        this.checks.run();

        for ( RowInspector rowInspector : cache.all() ) {
            assertHasIssues( rowInspector );
        }
    }

    @Test
    public void testOnlyTestChanges() throws Exception {
        // First run
        this.checks.run();

        RowInspector newRowInspector = mockRowInspector( 3 );
        rowInspectors.remove( rowInspector3 );
        rowInspectors.add( newRowInspector );

        this.checks.update( rowInspector3, newRowInspector );

        assertNull( checks.get( rowInspector3 ) );
        Collection<Check> checks = this.checks.get( newRowInspector );
        assertEquals( 3, checks.size() );
        assertNoIssues( newRowInspector );

        // Second run
        this.checks.run();

        assertHasIssues( newRowInspector );

        assertEquals( 3, this.checks.get( rowInspector1 ).size() );
        assertEquals( 3, this.checks.get( rowInspector2 ).size() );
        assertEquals( 3, this.checks.get( newRowInspector ).size() );
    }

    private RowInspector mockRowInspector( int rowNumber ) {
        return new RowInspector( rowNumber, null, cache );
    }

    private void assertHasIssues( RowInspector rowInspector ) {
        for ( Check check : checks.get( rowInspector ) ) {
            assertTrue( check.hasIssues() );
            assertEquals( "1", check.getIssue().getTitle() );
        }
    }

    private void assertNoIssues( RowInspector rowInspector ) {
        for ( Check check : checks.get( rowInspector ) ) {
            assertFalse( check.hasIssues() );
        }
    }

    private class MockSingleCheck
            extends SingleCheck {

        int runCount = 0;

        public MockSingleCheck( RowInspector rowInspector ) {
            super( rowInspector );
        }

        @Override
        public void check() {
            hasIssues = true;
        }

        @Override
        public Issue getIssue() {
            return new Issue( Severity.NOTE,
                              ++runCount + "" );
        }
    }

    private class MockPairCheck
            extends PairCheck {

        int runCount = 0;

        public MockPairCheck( RowInspector rowInspector,
                              RowInspector other ) {
            super( rowInspector, other );

        }

        @Override
        public void check() {
            hasIssues = true;
        }

        @Override
        public Issue getIssue() {
            return new Issue( Severity.NOTE,
                              ++runCount + "" );
        }
    }
}