/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.util.Map;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnumLoaderUtilitiesTest {

    @Mock
    protected EnumDropdownService enumDropdownService;
    protected Caller<EnumDropdownService> enumDropdownServiceCaller;
    protected EnumLoaderUtilities enumLoaderUtilities;

    @Mock
    private GuidedDecisionTablePresenter presenter;

    @Mock
    private GuidedDecisionTableView view;

    @Mock
    private Command onFetchCommand;

    @Mock
    private Command onFetchCompleteCommand;

    @Before
    public void setup() {
        enumDropdownServiceCaller = new CallerMock<>(enumDropdownService);
        enumLoaderUtilities = spy(new EnumLoaderUtilities(enumDropdownServiceCaller));
        when(presenter.getView()).thenReturn(view);
    }

    @Test
    public void checkNullDefinition() {
        final Callback<Map<String, String>> callback = (result) -> assertTrue(result.isEmpty());
        enumLoaderUtilities.getEnums(null,
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);

        assertNoInteractions();
    }

    @Test
    public void checkEmptyDefinition() {
        final Callback<Map<String, String>> callback = (result) -> assertTrue(result.isEmpty());
        enumLoaderUtilities.getEnums(new DropDownData(),
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);

        assertNoInteractions();
    }

    private void assertNoInteractions() {
        verify(enumDropdownService,
               never()).loadDropDownExpression(any(Path.class),
                                               any(String[].class),
                                               any(String.class));
        verify(onFetchCommand,
               never()).execute();
        verify(onFetchCompleteCommand,
               never()).execute();
        verify(enumLoaderUtilities,
               never()).convertDropDownData(any(String[].class));
    }

    @Test
    public void checkFixedListDefinitionWithCaching() {
        final Callback<Map<String, String>> callback = (result) -> {
            assertFalse(result.isEmpty());
            assertEquals(2,
                         result.size());
            assertTrue(result.containsKey("one"));
            assertTrue(result.containsKey("two"));
        };
        final String[] fixedList = {"one", "two"};
        final DropDownData enumDefinition = DropDownData.create(fixedList);

        //Call twice to check caching
        enumLoaderUtilities.getEnums(enumDefinition,
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);
        enumLoaderUtilities.getEnums(enumDefinition,
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);

        verify(enumDropdownService,
               never()).loadDropDownExpression(any(Path.class),
                                               any(String[].class),
                                               any(String.class));
        verify(onFetchCommand,
               never()).execute();
        verify(onFetchCompleteCommand,
               never()).execute();
        verify(enumLoaderUtilities,
               times(1)).convertDropDownData(any(String[].class));
    }

    @Test
    public void checkQueryExpressionDefinitionWithCaching() {
        final Callback<Map<String, String>> callback = (result) -> {
            assertFalse(result.isEmpty());
            assertEquals(2,
                         result.size());
            assertTrue(result.containsKey("one"));
            assertTrue(result.containsKey("two"));
        };
        final String[] fixedList = {"one", "two"};
        final String[] valuePairs = {"param1=a", "param2=b"};
        final DropDownData enumDefinition = DropDownData.create("expression",
                                                                valuePairs);

        when(enumDropdownService.loadDropDownExpression(any(),
                                                        any(),
                                                        any())).thenReturn(fixedList);

        //Call twice to check caching
        enumLoaderUtilities.getEnums(enumDefinition,
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);
        enumLoaderUtilities.getEnums(enumDefinition,
                                     callback,
                                     presenter,
                                     onFetchCommand,
                                     onFetchCompleteCommand);

        verify(enumDropdownService,
               times(1)).loadDropDownExpression(any(),
                                                any(String[].class),
                                                any(String.class));
        verify(onFetchCommand,
               times(1)).execute();
        verify(onFetchCompleteCommand,
               times(1)).execute();
        verify(enumLoaderUtilities,
               times(1)).convertDropDownData(any(String[].class));
        verify(view,
               times(1)).batch();
    }
}
