/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.dashbuilder.client.screens;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.perspective.DashboardsListPerspective;
import org.dashbuilder.client.perspective.EmptyPerspective;
import org.dashbuilder.client.perspective.RuntimePerspective;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class RouterScreenTest {
    
    @Mock
    PlaceManager placeManager;

    @Mock
    RuntimeScreen runtimeScreen;
    
    @Mock
    RuntimeClientLoader clientLoader;
    
    @Mock
    DashboardsListScreen dashboardsListScreen;
    
    @InjectMocks
    RouterScreen routerScreen;
    
    @Test
    public void testRouteToRuntimePerspective() {
        RuntimeModel runtimeModel = mock(RuntimeModel.class);
        RuntimeServiceResponse response = new RuntimeServiceResponse(DashbuilderRuntimeMode.SINGLE_IMPORT, 
                                                                     Optional.of(runtimeModel), 
                                                                     Collections.emptyList(),
                                                                     false);
        routerScreen.route(response);
        
        verify(runtimeScreen).loadDashboards(eq(runtimeModel));
        verify(placeManager).goTo(eq(RuntimePerspective.ID));
    }
    
    @Test
    public void testRouteToEmptyPerspective() {
        RuntimeServiceResponse response = new RuntimeServiceResponse(DashbuilderRuntimeMode.SINGLE_IMPORT, 
                                                                     Optional.empty(), 
                                                                     Collections.emptyList(),
                                                                     false);
        routerScreen.route(response);
        
        verify(placeManager).goTo(eq(EmptyPerspective.ID));
    }
    
    
    @Test
    public void testRouteToDashboardsListPerspective() {
        List<String> models = Arrays.asList("m1", "m2");
        RuntimeServiceResponse response = new RuntimeServiceResponse(DashbuilderRuntimeMode.MULTIPLE_IMPORT, 
                                                                     Optional.empty(), 
                                                                     models,
                                                                     false);
        routerScreen.route(response);
        
        verify(dashboardsListScreen).disableUpload();
        verify(dashboardsListScreen).loadList(eq(models));
        verify(placeManager).goTo(eq(DashboardsListPerspective.ID));
    }
    
    @Test
    public void testRouteToDashboardsListPerspectiveWithUploadEnabled() {
        List<String> models = Arrays.asList("m1", "m2");
        RuntimeServiceResponse response = new RuntimeServiceResponse(DashbuilderRuntimeMode.MULTIPLE_IMPORT, 
                                                                     Optional.empty(), 
                                                                     models,
                                                                     true);
        routerScreen.route(response);
        
        verify(dashboardsListScreen, times(0)).disableUpload();
        verify(dashboardsListScreen).loadList(eq(models));
        verify(placeManager).goTo(eq(DashboardsListPerspective.ID));
    }

}