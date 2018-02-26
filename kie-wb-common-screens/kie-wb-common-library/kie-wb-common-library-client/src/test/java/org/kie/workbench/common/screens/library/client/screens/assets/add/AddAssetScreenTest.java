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

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.CategoryUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceHandlerManager;
import org.kie.workbench.common.screens.library.client.widgets.project.NewAssetHandlerCardWidget;
import org.guvnor.common.services.project.categories.Decision;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.category.Undefined;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddAssetScreenTest {

    private AddAssetScreen addAssetScreen;

    @Mock
    private AddAssetScreen.View view;

    @Mock
    private TranslationService ts;

    @Mock
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

    @Before
    public void setUp() {
        this.addAssetScreen = new AddAssetScreen(this.view,
                                                 this.ts,
                                                 this.resourceHandlerManager,
                                                 this.categoriesManagerCache,
                                                 this.newAssetHandlerCardWidgets,
                                                 this.libraryConstants,
                                                 this.categoryUtils,
                                                 this.libraryPlaces);
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
}