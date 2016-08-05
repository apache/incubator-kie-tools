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

import com.google.gwtmockito.GwtMock;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.client.wizard.datasource.NewDataSourceDefWizard;
import org.kie.workbench.common.screens.datasource.management.client.wizard.driver.NewDriverDefWizard;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryResult;
import org.kie.workbench.common.screens.datasource.management.service.DefExplorerQueryService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

public abstract class ExplorerBaseTest {


    protected DefExplorerBase explorerBase;

    @GwtMock
    protected DefExplorerContent explorerContent;

    @GwtMock
    protected NewDataSourceDefWizard dataSourceDefWizard;

    @GwtMock
    protected NewDriverDefWizard driverDefWizard;

    @Mock
    protected DefExplorerQueryService queryService;

    protected Caller<DefExplorerQueryService> queryServiceCaller;

    protected DefExplorerQueryResult result;

    protected void setup() {
        queryServiceCaller = new CallerMock<>( queryService );
        result = new DefExplorerQueryResult();
    }

    /**
     * Tests that the NewDataSourceWizard is opened when the user clicks on the "Add Datasource button"
     */
    @Test
    public void testOpenCreateDataSourceWizard() {
        //emulates user interaction
        explorerBase.onAddDataSource();
        verify( dataSourceDefWizard, times( 1 ) ).start();
    }

    /**
     * Tests that the NewDriverWizard is opened when the user clicks on the "Add Driver button"
     */
    @Test
    public void testOpenCreateDriverWizard() {
        //emulates user interaction
        explorerBase.onAddDriver();
        verify( driverDefWizard, times( 1 ) ).start();
    }

    /**
     * Tests a successful refresh of the ProjectDataSourceExplorer.
     */
    @Test
    public void testValidRefresh() {
        when( queryService.executeQuery( explorerBase.createRefreshQuery() ) ).thenReturn( result );

        //invoke the content refresh
        explorerBase.refresh();

        //the query service should have been invoked with the expected query.
        verify( queryService, times( 1 ) ).executeQuery( explorerBase.createRefreshQuery() );
        //the content expected results should have been loaded into the explorerContent.
        verify( explorerContent, times( 1 ) ).loadDataSources( result.getDataSourceDefs() );
        verify( explorerContent, times( 1 ) ).loadDrivers( result.getDriverDefs() );
    }
}