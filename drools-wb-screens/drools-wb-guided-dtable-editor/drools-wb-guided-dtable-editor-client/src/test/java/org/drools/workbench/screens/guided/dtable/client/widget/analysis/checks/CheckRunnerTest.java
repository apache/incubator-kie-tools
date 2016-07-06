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
import java.util.HashSet;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.CancellableRepeatingCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.PairCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.SingleCheck;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.ExplanationProvider;
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
public class CheckRunnerTest {

    @Spy
    private CheckRunner checkRunner = new CheckRunner() {
        @Override
        protected CheckManager getCheckManager(final CheckRunner checkRunner) {
            return new CheckManager( checkRunner ) {

                @Override
                protected Set<Check> makeSingleRowChecks( RuleInspector ruleInspector ) {
                    Set<Check> checks = new HashSet<>();
                    checks.add( new MockSingleCheck( ruleInspector ) );
                    return checks;
                }

                @Override
                protected Set<Check> makePairRowChecks( RuleInspector ruleInspector,
                                                              RuleInspector other ) {
                    HashSet<Check> checks = new HashSet<Check>();
                    checks.add( new MockPairCheck( ruleInspector, other ) );
                    return checks;
                }
            };
        }

        @Override
        protected void doRun( final CancellableRepeatingCommand command ) {
            while ( command.execute() ) {
                //loop
            }
        }

    };

    @Mock
    private RuleInspectorCache cache;

    private RuleInspector            ruleInspector1;
    private RuleInspector            ruleInspector2;
    private RuleInspector            ruleInspector3;
    private ArrayList<RuleInspector> ruleInspectors;

    @Before
    public void setUp() throws Exception {

        ruleInspector1 = mockRowInspector( 1 );
        ruleInspector2 = mockRowInspector( 2 );
        ruleInspector3 = mockRowInspector( 3 );

        ruleInspectors = new ArrayList<RuleInspector>();
        ruleInspectors.add( ruleInspector1 );
        ruleInspectors.add( ruleInspector2 );
        ruleInspectors.add( ruleInspector3 );
        when( cache.all() ).thenReturn( ruleInspectors );

        checkRunner.add( ruleInspector1 );
        checkRunner.add( ruleInspector2 );
        checkRunner.add( ruleInspector3 );
    }

    @Test
    public void testChecksGetGenerated() throws Exception {
        assertEquals( 3, checkRunner.get( ruleInspector1 ).size() );
        assertEquals( 3, checkRunner.get( ruleInspector2 ).size() );
        assertEquals( 3, checkRunner.get( ruleInspector3 ).size() );
    }

    @Test
    public void testRemove() throws Exception {

        Collection<Check> removed = this.checkRunner.remove( ruleInspector2 );

        assertEquals( 5, removed.size() );
        assertNotNull( this.checkRunner.get( ruleInspector1 ) );
        assertNull( this.checkRunner.get( ruleInspector2 ) );
        assertNotNull( this.checkRunner.get( ruleInspector3 ) );
    }

    @Test
    public void testRunTests() throws Exception {

        for ( RuleInspector ruleInspector : cache.all() ) {
            assertNoIssues( ruleInspector );
        }

        this.checkRunner.run( null,
                              null );

        for ( RuleInspector ruleInspector : cache.all() ) {
            assertHasIssues( ruleInspector );
        }
    }

    @Test
    public void testOnlyTestChanges() throws Exception {
        // First run
        this.checkRunner.run( null,
                              null );

        RuleInspector newRuleInspector = mockRowInspector( 3 );
        ruleInspectors.remove( ruleInspector3 );
        ruleInspectors.add( newRuleInspector );

        this.checkRunner.update( ruleInspector3, newRuleInspector );

        assertNull( checkRunner.get( ruleInspector3 ) );
        Collection<Check> checks = this.checkRunner.get( newRuleInspector );
        assertEquals( 3, checks.size() );
        assertNoIssues( newRuleInspector );

        // Second run
        this.checkRunner.run( null,
                              null );

        assertHasIssues( newRuleInspector );

        assertEquals( 3, this.checkRunner.get( ruleInspector1 ).size() );
        assertEquals( 3, this.checkRunner.get( ruleInspector2 ).size() );
        assertEquals( 3, this.checkRunner.get( newRuleInspector ).size() );
    }

    private RuleInspector mockRowInspector( final int rowNumber ) {
        return new RuleInspector( new Rule( rowNumber ),
                                  cache );
    }

    private void assertHasIssues( final RuleInspector ruleInspector ) {
        for ( Check check : checkRunner.get( ruleInspector ) ) {
            assertTrue( check.hasIssues() );
            assertEquals( "1", check.getIssue().getTitle() );
        }
    }

    private void assertNoIssues( final RuleInspector ruleInspector ) {
        for ( Check check : checkRunner.get( ruleInspector ) ) {
            assertFalse( check.hasIssues() );
        }
    }

    private class MockSingleCheck
            extends SingleCheck {

        int runCount = 0;

        public MockSingleCheck( RuleInspector ruleInspector ) {
            super( ruleInspector );
        }

        @Override
        public void check() {
            hasIssues = true;
        }

        @Override
        public Issue getIssue() {
            return new Issue( Severity.NOTE,
                              ++runCount + "",
                              mock( ExplanationProvider.class ) );
        }
    }

    private class MockPairCheck
            extends PairCheck {

        int runCount = 0;

        public MockPairCheck( RuleInspector ruleInspector,
                              RuleInspector other ) {
            super( ruleInspector, other );

        }

        @Override
        public void check() {
            hasIssues = true;
        }

        @Override
        public Issue getIssue() {
            return new Issue( Severity.NOTE,
                              ++runCount + "",
                              mock( ExplanationProvider.class ) );
        }
    }
}