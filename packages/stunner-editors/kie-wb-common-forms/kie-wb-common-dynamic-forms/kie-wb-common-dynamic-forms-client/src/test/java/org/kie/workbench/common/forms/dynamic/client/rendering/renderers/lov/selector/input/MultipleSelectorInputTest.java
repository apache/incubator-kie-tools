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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MultipleSelectorInputTest {
    public static int MAX_ITEMS = 5;

    @Mock
    private LiveSearchService searchService;

    @Spy
    private MultipleLiveSearchSelectionHandler searchSelectionHandler;

    @Mock
    private LiveSearchDropDown dropDown;

    @Mock
    private MultipleSelectorInputView view;

    private MultipleSelectorInput input;

    @Before
    public void init() {
        input = new MultipleSelectorInput(view, dropDown);

        input.init(searchService, searchSelectionHandler);
    }

    @Test
    public void testFunctinality() {
        verify(view).setPresenter(input);

        verify(dropDown).init(searchService, searchSelectionHandler);

        input.asWidget();

        verify(view).asWidget();

        dropDown.setOnChange(any());

        input.setEnabled(true);

        verify(dropDown).setEnabled(true);

        input.setClearSelectionEnabled(true);

        verify(dropDown).setClearSelectionEnabled(true);

        input.setFilterEnabled(true);

        verify(dropDown).setSearchEnabled(true);

        input.setMaxItems(MAX_ITEMS);

        verify(dropDown).setMaxItems(MAX_ITEMS);

        input.getValue();

        verify(searchSelectionHandler).getSelectedValues();
    }

    @Test
    public void testSetSameComponentValues() {
        List<String> values = new ArrayList<>();

        input.setValue(values);

        verify(searchSelectionHandler, never()).clearSelection();
        verify(dropDown, never()).setSelectedItem(Mockito.<String>any());
    }

    @Test
    public void testSetComponentValues() {
        List<String> values = Arrays.asList("a", "b", "c");

        input.setValue(values);

        verify(dropDown, times(1)).clearSelection();
        verify(dropDown, times(3)).setSelectedItem(Mockito.<String>any());
    }
}
