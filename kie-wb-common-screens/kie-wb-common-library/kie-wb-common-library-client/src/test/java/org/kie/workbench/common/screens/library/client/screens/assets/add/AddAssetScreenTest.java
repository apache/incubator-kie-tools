/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.library.client.screens.assets.add;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.uberfire.mocks.ParametrizedCommandMock.executeParametrizedCommandWith;

import java.util.Arrays;
import java.util.List;

import org.guvnor.common.services.project.categories.Decision;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.handlers.NewJavaFileTextHandler;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.CategoryUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceHandlerManager;
import org.kie.workbench.common.screens.library.client.widgets.project.NewAssetHandlerCardWidget;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.category.Undefined;

@RunWith(MockitoJUnitRunner.class)
public class AddAssetScreenTest {

    @Mock
    private AddAssetScreen.View view;

    @Mock
    private TranslationService ts;

    @Spy
    private ResourceHandlerManager resourceHandlerManager;

    @Mock
    private ManagedInstance<NewAssetHandlerCardWidget> newAssetHandlerCardWidgets;

    @Mock
    private NewAssetHandlerCardWidget newAssetHandlerCardWidget;

    @Mock
    private LibraryConstants libraryConstants;

    @Mock
    private CategoryUtils categoryUtils;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;
    
    @Mock
    private ProfilePreferences profilePreferences;

    private AddAssetScreen addAssetScreen;

    @Before
    public void setUp() {
        this.addAssetScreen = spy(new AddAssetScreen(this.view,
                                                     this.ts,
                                                     this.resourceHandlerManager,
                                                     this.categoriesManagerCache,
                                                     this.newAssetHandlerCardWidgets,
                                                     this.libraryConstants,
                                                     this.categoryUtils,
                                                     this.libraryPlaces,
                                                     this.profilePreferences));
        doNothing().when(addAssetScreen).update();
        executeParametrizedCommandWith(0, new ProfilePreferences(Profile.FULL))
                .when(profilePreferences).load(any(ParameterizedCommand.class), 
                                                any(ParameterizedCommand.class));       
    }

    @Test
    public void testFilterByCategory() {

        NewResourceHandler resourceHandler = mock(NewResourceHandler.class,
                                                  Answers.RETURNS_DEEP_STUBS.get());
        when(resourceHandler.getDescription()).thenReturn("demo");
        when(resourceHandler.getResourceType().getCategory()).thenReturn(new Decision());

        {
            List<NewResourceHandler> filtered = this.addAssetScreen.filterAndSortHandlers(Arrays.asList(resourceHandler),
                                                                                          "",
                                                                                          new Decision());
            assertEquals(resourceHandler,
                         filtered.get(0));
        }

        {
            List<NewResourceHandler> filtered = this.addAssetScreen.filterAndSortHandlers(Arrays.asList(resourceHandler),
                                                                                          "",
                                                                                          new Others());
            assertTrue(filtered.isEmpty());
        }
    }

    @Test
    public void testFilterByName() {

        NewResourceHandler resourceHandler = mock(NewResourceHandler.class,
                                                  Answers.RETURNS_DEEP_STUBS.get());
        when(resourceHandler.getDescription()).thenReturn("demo");
        when(resourceHandler.getResourceType().getCategory()).thenReturn(new Decision());

        {
            List<NewResourceHandler> filtered = this.addAssetScreen.filterAndSortHandlers(Arrays.asList(resourceHandler),
                                                                                          "de",
                                                                                          new Undefined());
            assertEquals(resourceHandler,
                         filtered.get(0));
        }

        {
            List<NewResourceHandler> filtered = this.addAssetScreen.filterAndSortHandlers(Arrays.asList(resourceHandler),
                                                                                          "ja",
                                                                                          new Undefined());
            assertTrue(filtered.isEmpty());
        }
    }

    @Test
    public void testOnOpen() {
        NewResourceHandler rh1 = new NewFileUploader();
        NewResourceHandler rh2 = new NewJavaFileTextHandler();

        doReturn(Arrays.asList(rh1,
                               rh2)).when(resourceHandlerManager).getNewResourceHandlers();

        addAssetScreen.initialize();
        addAssetScreen.onOpen();

        assertEquals(1,
                     addAssetScreen.newResourceHandlers.size());
        assertEquals(rh2,
                     addAssetScreen.newResourceHandlers.get(0));
    }
    
    @Test
    public void testOnOpenProfileFilter() {
        NewResourceHandler rhFull =  mockResourceHandler(Profile.FULL);
        NewResourceHandler rhDroolsPlanner = mockResourceHandler(Profile.PLANNER_AND_RULES);
        NewResourceHandler rhAll = mockResourceHandler(Profile.values());
        
        addAssetScreen.initialize();
        
        doReturn(Arrays.asList(rhFull,
                rhDroolsPlanner,
                rhAll)).when(resourceHandlerManager).getNewResourceHandlers();

        addAssetScreen.onOpen();
        assertEquals(2,
              addAssetScreen.newResourceHandlers.size());
        
        assertTrue(addAssetScreen.newResourceHandlers.contains(rhFull));
        assertTrue(addAssetScreen.newResourceHandlers.contains(rhAll));
        assertTrue(!addAssetScreen.newResourceHandlers.contains(rhDroolsPlanner));
        
        executeParametrizedCommandWith(0, new ProfilePreferences(Profile.PLANNER_AND_RULES))
                .when(profilePreferences).load(any(ParameterizedCommand.class), 
                                               any(ParameterizedCommand.class)); 

        addAssetScreen.onOpen();
        assertEquals(2,
                     addAssetScreen.newResourceHandlers.size());
        assertTrue(!addAssetScreen.newResourceHandlers.contains(rhFull));
        assertTrue(addAssetScreen.newResourceHandlers.contains(rhAll));
        assertTrue(addAssetScreen.newResourceHandlers.contains(rhDroolsPlanner));
    }    
    
    
    @Test
    public void testProfileFilter() {
        NewResourceHandler rhFull =  mockResourceHandler(Profile.FULL);
        NewResourceHandler rhDroolsPlanner = mockResourceHandler(Profile.PLANNER_AND_RULES);
        NewResourceHandler rhAll = mockResourceHandler(Profile.values());
        
        addAssetScreen.initialize();
        
        doReturn(Arrays.asList(rhFull,rhDroolsPlanner, rhAll))
                       .when(resourceHandlerManager).getNewResourceHandlers();
        List<NewResourceHandler> filteredResourceHandlers = this.addAssetScreen.filterNewResourceHandlers(
                                                                        new ProfilePreferences(Profile.FULL));
        assertEquals(2, filteredResourceHandlers.size());
        
        filteredResourceHandlers = this.addAssetScreen.filterNewResourceHandlers(
                                                                        new ProfilePreferences(Profile.PLANNER_AND_RULES));
        assertEquals(2, filteredResourceHandlers.size());
        
        doReturn(Arrays.asList(rhFull, rhDroolsPlanner))
                       .when(resourceHandlerManager).getNewResourceHandlers();
        
        filteredResourceHandlers = this.addAssetScreen.filterNewResourceHandlers(
                                                                        new ProfilePreferences(Profile.PLANNER_AND_RULES));
        assertEquals(1, filteredResourceHandlers.size());
        assertEquals(Profile.PLANNER_AND_RULES, filteredResourceHandlers.get(0).getProfiles().get(0));
        
        filteredResourceHandlers = this.addAssetScreen.filterNewResourceHandlers(
                                                                        new ProfilePreferences(Profile.FULL));
        assertEquals(1, filteredResourceHandlers.size());
        assertEquals(Profile.FULL, filteredResourceHandlers.get(0).getProfiles().get(0));
         
    }    
    
    private NewResourceHandler mockResourceHandler(Profile...profiles) {
        NewResourceHandler nrh = mock(NewResourceHandler.class);
        when(nrh.getProfiles()).thenReturn(Arrays.asList(profiles));
        when(nrh.isProjectAsset()).thenReturn(true);
        return nrh;
        
    }
}