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

package org.kie.workbench.common.screens.datasource.management.client.explorer.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DefExplorerContentTest {

    private DefExplorerContent explorerContent;

    @GwtMock
    private DefExplorerContentView view;

    @GwtMock
    private DefItem defItem;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @Mock
    private Path path3;

    @Mock
    private Path path4;

    @Mock
    private DriverDefInfo driver1;

    @Mock
    private DriverDefInfo driver2;

    @Mock
    private DataSourceDefInfo dataSource1;

    @Mock
    private DataSourceDefInfo dataSource2;

    private List<DriverDefInfo> driverDefInfos;

    private List<DataSourceDefInfo> dataSourceDefInfos;

    @Mock
    private ObservablePath observablePath;

    @Before
    public void setup() {
        this.explorerContent = new DefExplorerContent( view, null, placeManager ) {
            @Override
            protected DefItem createItem() {
                return defItem;
            }

            @Override
            protected void onDataSourceItemClick( DataSourceDefInfo dataSourceDefInfo ) {
                super.onDataSourceItemClick( dataSourceDefInfo );
            }
        };

        driverDefInfos = new ArrayList<>( );
        dataSourceDefInfos = new ArrayList<>( );

        when( driver1.getPath() ).thenReturn( path1 );
        when( driver2.getPath() ).thenReturn( path2 );
        when( dataSource1.getPath() ).thenReturn( path3 );
        when( dataSource1.isManaged() ).thenReturn( true );
        when( dataSource2.getPath() ).thenReturn( path4 );
        when( dataSource2.isManaged() ).thenReturn( true );
        driverDefInfos.add( driver1 );
        driverDefInfos.add( driver2 );
        dataSourceDefInfos.add( dataSource1 );
        dataSourceDefInfos.add( dataSource2 );
    }

    /**
     * Test that the elements are properly loaded and the view is properly populated.
     */
    @Test
    public void testContentLoad() {
        explorerContent.loadDrivers( driverDefInfos );
        explorerContent.loadDataSources( dataSourceDefInfos );

        verify( view, times( 1 ) ).clearDataSources();
        verify( view, times( 2 ) ).addDataSourceItem( defItem );

        verify( view, times( 1 ) ).clearDrivers();
        verify( view, times( 2 ) ).addDriverItem( defItem );
    }

    /**
     * Tests that the data source def editor is properly launched when a data source is clicked.
     */
    @Test
    public void testDataSourceDefClick() {

        PathPlaceRequestMock placeRequest = new PathPlaceRequestMock( dataSource1.getPath() );

        when( view.createEditorPlaceRequest( dataSource1.getPath() ) ).thenReturn( placeRequest );

        explorerContent.onDataSourceItemClick( dataSource1 );

        verify( placeManager, times( 1 ) ).goTo( new PathPlaceRequestMock( dataSource1.getPath() ) );
    }

    /**
     * Tests that the driver def editor is properly launched when a data source is clicked.
     */
    @Test
    public void testDriverDefClick() {

        PathPlaceRequestMock placeRequest = new PathPlaceRequestMock( driver1.getPath() );

        when( view.createEditorPlaceRequest( driver1.getPath() ) ).thenReturn( placeRequest );

        explorerContent.onDriverItemClick( driver1 );

        verify( placeManager, times( 1 ) ).goTo( new PathPlaceRequestMock( driver1.getPath() ) );
    }

    private class PathPlaceRequestMock extends PathPlaceRequest {

        Path path;

        public PathPlaceRequestMock( Path path ) {
            this.path = path;
        }

        @Override
        protected ObservablePath createObservablePath( Path path ) {
            return observablePath;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            PathPlaceRequestMock that = ( PathPlaceRequestMock ) o;

            return !( path != null ? !path.equals( that.path ) : that.path != null );
        }

        @Override
        public int hashCode() {
            int result = 31 + ( path != null ? path.hashCode() : 0 );
            return result;
        }
    }
}
