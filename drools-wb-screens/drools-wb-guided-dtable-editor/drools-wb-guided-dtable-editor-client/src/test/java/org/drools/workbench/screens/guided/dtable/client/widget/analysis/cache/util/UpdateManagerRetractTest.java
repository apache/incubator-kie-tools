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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.UpdateManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DataBuilderProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class UpdateManagerRetractTest {

    private UpdateManager         updateManager;
    private GuidedDecisionTable52 table52;

    @Spy
    private CheckRunner checkRunner;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();

        table52 = analyzerProvider.makeAnalyser()
                                  .withPersonAgeColumn( "==" )
                                  .withRetract()
                                  .withData( DataBuilderProvider
                                                     .row( 1, "a" )
                                                     .row( 1, null )
                                                     .end() )
                                  .buildTable();

        updateManager = analyzerProvider.getUpdateManager( checkRunner,
                                                           table52 );
    }

    @Test
    public void testDoNotUpdateActionWhenValueDidNotChange() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 0, 3 ) );

        updateManager.update( coordinates );

        verify( checkRunner, never() ).addChecks( anySet() );
    }

    @Test
    public void testFillNullAction() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1, 3 );
        coordinates.add( coordinate );
        table52.getData().get( 1 ).get( 3 ).setStringValue( "a" );

        updateManager.update( coordinates );

        verify( checkRunner ).addChecks( anySet() );
    }


}