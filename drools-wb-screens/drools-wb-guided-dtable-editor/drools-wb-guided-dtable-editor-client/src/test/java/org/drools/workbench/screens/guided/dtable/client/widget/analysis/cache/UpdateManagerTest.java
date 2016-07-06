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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.ExtendedGuidedDecisionTableBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.UpdateHandler;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.IndexBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class UpdateManagerTest {

    private UpdateManager         updateManager;
    private GuidedDecisionTable52 table52;

    @Mock
    private UpdateHandler updateHandler;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Captor
    private ArgumentCaptor<List<Coordinate>> coordinateArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", 1, true},
                        {2, "description", null, null}} )
                .buildTable();


        updateManager = new UpdateManager( new IndexBuilder( table52,
                                                             new ColumnUtilities( table52,
                                                                                  oracle ) ).build(),
                                           table52,
                                           updateHandler );
    }

    @Test
    public void testDoNotUpdateWhenDescriptionChanges() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 1, 1 ) );

        updateManager.update( coordinates );

        verify( updateHandler, never() ).updateCoordinates( any( List.class ) );
    }


    @Test
    public void testDoNotUpdateConditionWhenValueDidNotChange() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 1, 2 ) );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        assertTrue( coordinateArgumentCaptor.getValue().isEmpty() );
    }

    @Test
    public void testDoNotUpdateActionWhenValueDidNotChange() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add( new Coordinate( 0, 3 ) );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        assertTrue( coordinateArgumentCaptor.getValue().isEmpty() );
    }

    @Test
    public void testSetIntegerConditionToNewInteger() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0, 2 );
        coordinates.add( coordinate );
        table52.getData().get( 0 ).get( 2 ).setNumericValue( 123 );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testSetBooleanActionToNewBoolean() throws Exception {
        final ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        final Coordinate coordinate = new Coordinate( 0, 3 );
        coordinates.add( coordinate );
        table52.getData().get( 0 ).get( 3 ).setBooleanValue( false );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        final List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testSetActionToNull() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0, 3 );
        coordinates.add( coordinate );
        table52.getData().get( 0 ).get( 3 ).setBooleanValue( null );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testSetConditionToNull() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 0, 2 );
        coordinates.add( coordinate );
        table52.getData().get( 0 ).get( 2 ).setNumericValue( ( Integer ) null );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testFillNullCondition() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1, 2 );
        coordinates.add( coordinate );
        table52.getData().get( 1 ).get( 2 ).setNumericValue( 123 );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testFillNullAction() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1, 3 );
        coordinates.add( coordinate );
        table52.getData().get( 1 ).get( 3 ).setBooleanValue( false );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        List<Coordinate> list = coordinateArgumentCaptor.getValue();
        assertEquals( 1, list.size() );
        assertTrue( list.contains( coordinate ) );
    }

    @Test
    public void testFillNullActionWithNull() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1, 3 );
        coordinates.add( coordinate );
        table52.getData().get( 1 ).get( 3 ).setBooleanValue( null );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        assertTrue( coordinateArgumentCaptor.getValue().isEmpty() );
    }

    @Test
    public void testFillNullConditionWithNull() throws Exception {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate( 1, 2 );
        coordinates.add( coordinate );
        table52.getData().get( 1 ).get( 2 ).setNumericValue( ( Integer ) null );

        updateManager.update( coordinates );

        verify( updateHandler ).updateCoordinates( coordinateArgumentCaptor.capture() );
        assertTrue( coordinateArgumentCaptor.getValue().isEmpty() );
    }
}