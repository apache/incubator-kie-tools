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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListSelectorTest {

    @Mock
    private ListSelectorItem listSelectorItem1;

    @Mock
    private ListSelectorItem listSelectorItem2;

    @Mock
    private ListSelectorView view;

    @Mock
    private HasListSelectorControl bound;

    @Captor
    private ArgumentCaptor<List<ListSelectorItem>> itemsCaptor;

    private ListSelectorView.Presenter listSelector;

    @Before
    public void setup() {
        this.listSelector = new ListSelector(view);

        when(bound.getItems(anyInt(), anyInt())).thenReturn(Arrays.asList(listSelectorItem1, listSelectorItem2));
    }

    @Test
    public void testInit() {
        verify(view).init(eq(listSelector));
    }

    @Test
    public void testShow() {
        listSelector.bind(bound, 0, 0);

        listSelector.show();

        verify(view).show(Optional.empty());
    }

    @Test
    public void testHide() {
        listSelector.bind(bound, 0, 0);

        listSelector.hide();

        verify(view).hide();
    }

    @Test
    public void testBindWithItems() {
        listSelector.bind(bound, 0, 0);

        verify(view).setItems(itemsCaptor.capture());

        assertThat(itemsCaptor.getValue()).containsOnly(listSelectorItem1, listSelectorItem2);

        listSelector.show();

        verify(view).show(Optional.empty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindWithNoItems() {
        when(bound.getItems(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        listSelector.bind(bound, 0, 0);

        verify(view, never()).setItems(anyList());

        listSelector.show();

        verify(view, never()).show(Optional.empty());
    }

    @Test
    public void testOnItemSelected() {
        listSelector.bind(bound, 0, 0);
        listSelector.onItemSelected(listSelectorItem2);

        verify(bound).onItemSelected(eq(listSelectorItem2));
    }
}
