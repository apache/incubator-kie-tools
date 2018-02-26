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

package org.kie.workbench.common.screens.library.client.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.guvnor.common.services.project.categories.Decision;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.ext.widgets.common.client.select.SelectOptionImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.workbench.category.Others;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryUtilsTest {

    private CategoryUtils categoryUtils;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;

    @Mock
    private TranslationService ts;

    @Before
    public void setUp() {
        categoryUtils = new CategoryUtils(categoriesManagerCache,
                                          ts);
    }

    @Test
    public void testCreateCategories() {
        when(this.categoriesManagerCache.getCategories()).thenReturn(new HashSet<>(Arrays.asList(new Others(),
                                                                                                 new Decision())));

        List<SelectOption> categories = this.categoryUtils.createCategories();

        assertEquals(3,
                     categories.size());
        assertTrue(categories.stream().anyMatch(selectOption -> selectOption.getSelector().equals("ALL")));
        assertTrue(categories.stream().anyMatch(selectOption -> selectOption.getSelector().equals(new Others().getName())));
        assertTrue(categories.stream().anyMatch(selectOption -> selectOption.getSelector().equals(new Decision().getName())));
    }
}