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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DataBuilderProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.DTableUpdateManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.drools.workbench.services.verifier.api.client.checks.base.CheckRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DTableUpdateManagerIsNullTest {

    private DTableUpdateManager updateManager;
    private GuidedDecisionTable52 table52;

    @Spy
    private CheckRunner checkRunner;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws
                        Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                .withConditionBooleanColumn( "a",
                                             "Person",
                                             "name",
                                             "== null" )

                .withPersonApprovedActionSetField()
                .withData( DataBuilderProvider
                                   .row( true,
                                         true )
                                   .row( false,
                                         true )
                                   .row( null,
                                         true )
                                   .end() )
                .buildTable();

        updateManager = analyzerProvider.getUpdateManager( checkRunner,
                                                           table52 );
    }

    @Test
    public void testTrueDidNotChange() throws
                                       Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add( new Coordinate( 0,
                                         2 ) );

        updateManager.update( coordinates );

        verify( checkRunner,
                never() ).addChecks( anySet() );
    }

    @Test
    public void testFalseDidNotChange() throws
                                        Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add( new Coordinate( 1,
                                         2 ) );

        updateManager.update( coordinates );

        verify( checkRunner,
                never() ).addChecks( anySet() );
    }

    @Test
    public void testNullDidNotChange() throws
                                       Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add( new Coordinate( 2,
                                         2 ) );

        updateManager.update( coordinates );

        verify( checkRunner,
                never() ).addChecks( anySet() );
    }

    @Test
    public void testSetTrueToFalse() throws
                                     Exception {
        set( 0,
             2,
             false );

        verify( checkRunner ).addChecks( anySet() );
    }

    @Test
    public void testSetTrueToNull() throws
                                    Exception {
        set( 0,
             2,
             (Boolean) null );

        verify( checkRunner ).addChecks( anySet() );
    }

    @Test
    public void testSetFalseToTrue() throws
                                     Exception {
        set( 1,
             2,
             true );

        verify( checkRunner ).addChecks( anySet() );
    }

    @Test
    public void testSetFalseToNull() throws
                                     Exception {
        set( 1,
             2,
             (Boolean) null );

        verify( checkRunner,
                never() ).addChecks( anySet() );
    }

    @Test
    public void testSetNullToTrue() throws
                                    Exception {
        set( 2,
             2,
             true );

        verify( checkRunner ).addChecks( anySet() );
    }

    @Test
    public void testSetNullToFalse() throws
                                     Exception {
        set( 2,
             2,
             false );

        verify( checkRunner,
                never() ).addChecks( anySet() );
    }

    private void set( final int row,
                      final int col,
                      final Boolean value ) {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( row,
                                                col );
        coordinates.add( coordinate );
        table52.getData()
                .get( row )
                .get( col )
                .setBooleanValue(
                        value );

        updateManager.update( coordinates );
    }
}