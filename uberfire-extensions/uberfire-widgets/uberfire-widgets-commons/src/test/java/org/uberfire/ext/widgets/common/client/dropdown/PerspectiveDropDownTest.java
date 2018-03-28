/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveDropDownTest {

    @Mock
    ActivityBeansCache activityBeansCache;

    @Mock
    LiveSearchDropDown liveSearchDropDown;

    PerspectiveDropDown perspectiveDropDown;

    @Before
    public void setUp() {
        perspectiveDropDown = new PerspectiveDropDown(activityBeansCache, liveSearchDropDown);

        SyncBeanDef mock1 = mock(SyncBeanDef.class);
        when(mock1.getName()).thenReturn("A");
        when(mock1.isAssignableTo(PerspectiveActivity.class)).thenReturn(true);

        SyncBeanDef mock2 = mock(SyncBeanDef.class);
        when(mock2.getName()).thenReturn("B");
        when(mock2.isAssignableTo(PerspectiveActivity.class)).thenReturn(true);

        when(activityBeansCache.getPerspectiveActivities()).thenReturn(Arrays.asList(mock1, mock2));
    }

    @Test
    public void testSearchAll() {
        perspectiveDropDown.searchService.search("", -1, itemList -> {
            assertEquals(itemList.size(), 2);
        });
    }

    @Test
    public void testSearchItem() {
        perspectiveDropDown.searchService.search("A", -1, itemList -> {
            assertEquals(itemList.size(), 1);
        });
        perspectiveDropDown.searchService.search("a", -1, itemList -> {
            assertEquals(itemList.size(), 1);
        });
    }

    @Test
    public void testSearchEmpty() {
        perspectiveDropDown.searchService.search("x", -1, itemList -> {
            assertEquals(itemList.size(), 0);
        });
    }

    @Test
    public void testExcludeItems() {
        Set<String> excluded = new HashSet<>();
        excluded.add("A");
        perspectiveDropDown.setPerspectiveIdsExcluded(excluded);
        perspectiveDropDown.searchService.search("", -1, itemList -> {
            assertEquals(itemList.size(), 1);
            assertEquals(itemList.get(0).getKey(), "B");
        });
    }

    @Test
    public void testCustomNames() {
        perspectiveDropDown.setPerspectiveNameProvider(itemId -> "x");
        perspectiveDropDown.searchService.search("A", -1, itemList -> {
            assertEquals(itemList.size(), 0);
        });
        perspectiveDropDown.searchService.search("x", -1, itemList -> {
            assertEquals(itemList.size(), 2);
        });
    }
}

