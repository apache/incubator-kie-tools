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

package org.uberfire.ext.widgets.common.client.dropdown;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractEntryCreationLiveSearchServiceTest<EDITOR extends EntryCreationEditor> {

    protected static final String VALUE = "value";

    protected EDITOR editor;

    @Mock
    protected EntryCreationLiveSearchService searchService;

    @Mock
    protected SingleLiveSearchSelectionHandler searchSelectionHandler;

    @Mock
    protected LiveSearchDropDownView view;

    @Mock
    private ManagedInstance<LiveSearchSelectorItem> selectorItems;

    protected LiveSearchDropDown dropDown;

    protected ParameterizedCommand<LiveSearchEntry> onAddCommand;

    protected Command onCancelCommand;

    @Before
    public void init() {
        when(selectorItems.get()).thenAnswer((Answer<LiveSearchSelectorItem<String>>) invocationOnMock -> mock(LiveSearchSelectorItem.class));

        editor = mock(getEditorType());

        when(searchService.getEditor()).thenReturn(editor);

        dropDown = spy(new LiveSearchDropDown(view, selectorItems));
    }

    @Test
    public void testEditorAddNewEntryAction() {

        startTest();

        LiveSearchEntry<String> entry = new LiveSearchEntry<>(VALUE, VALUE);

        onAddCommand.execute(entry);

        verify(dropDown).addNewItem(entry);
        verify(searchSelectionHandler).selectItem(any());
        verify(dropDown).search(any());
        verify(dropDown).doSearch(any());
        verify(view).searchInProgress(any());
        verify(searchService).search(anyString(), anyInt(), any());
        verify(view).restoreFooter();
    }

    protected void startTest() {
        dropDown.init(searchService, searchSelectionHandler);

        verify(view).setNewInstanceEnabled(true);

        ArgumentCaptor<ParameterizedCommand> onAddCommandCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        ArgumentCaptor<Command> onCancelCommandCaptor = ArgumentCaptor.forClass(Command.class);

        verify(editor).init(onAddCommandCaptor.capture(), onCancelCommandCaptor.capture());

        onAddCommand = onAddCommandCaptor.getValue();

        onCancelCommand = onCancelCommandCaptor.getValue();

        assertNotNull(onAddCommand);
        assertNotNull(onCancelCommand);

        dropDown.showNewItem();
    }

    abstract Class<EDITOR> getEditorType();
}
