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
import java.util.HashSet;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.CancellableRepeatingCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Check;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
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
    private CheckManager             checkManager;

    @Before
    public void setUp() throws Exception {
        checkManager = new CheckManager() {
            @Override
            public HashSet<Check> makeSingleChecks( final RuleInspector ruleInspector ) {
                final HashSet<Check> result = new HashSet<>();
                result.add( new MockSingleCheck( ruleInspector ) );
                return result;
            }

        };

        ruleInspectors = new ArrayList<>();
        when( cache.all() ).thenReturn( ruleInspectors );

        ruleInspector1 = mockRowInspector( 1 );
        ruleInspectors.add( ruleInspector1 );
        ruleInspector2 = mockRowInspector( 2 );
        ruleInspectors.add( ruleInspector2 );
        ruleInspector3 = mockRowInspector( 3 );
        ruleInspectors.add( ruleInspector3 );

        checkRunner.addChecks( ruleInspector1.getChecks() );
        checkRunner.addChecks( ruleInspector2.getChecks() );
        checkRunner.addChecks( ruleInspector3.getChecks() );
    }

    @Test
    public void testChecksGetGenerated() throws Exception {
        assertEquals( 5, ruleInspector1.getChecks().size() );
        assertEquals( 5, ruleInspector2.getChecks().size() );
        assertEquals( 5, ruleInspector3.getChecks().size() );
    }

    @Test
    public void testRemove() throws Exception {

        this.checkRunner.remove( ruleInspector2 );

        final Set<Check> checks = ruleInspector1.getChecks();
        assertEquals( 3, checks.size() );
        assertTrue( ruleInspector2.getChecks().isEmpty() );
        assertEquals( 3, ruleInspector3.getChecks().size() );
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
        ruleInspectors.add( newRuleInspector );

        this.checkRunner.addChecks( newRuleInspector.getChecks() );

        assertNoIssues( newRuleInspector );

        // Second run
        this.checkRunner.run( null,
                              null );

        assertHasIssues( newRuleInspector );

        assertEquals( 7, ruleInspector1.getChecks().size() );
        assertEquals( 7, newRuleInspector.getChecks().size() );
    }

    private RuleInspector mockRowInspector( final int rowNumber ) {
        return new RuleInspector( new Rule( rowNumber ),
                                  checkManager,
                                  cache );
    }

    private void assertHasIssues( final RuleInspector ruleInspector ) {
        for ( Check check : ruleInspector.getChecks() ) {
            assertTrue( check.hasIssues() );
        }
    }

    private void assertNoIssues( final RuleInspector ruleInspector ) {
        for ( Check check : (ruleInspector.getChecks()) ) {
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

}