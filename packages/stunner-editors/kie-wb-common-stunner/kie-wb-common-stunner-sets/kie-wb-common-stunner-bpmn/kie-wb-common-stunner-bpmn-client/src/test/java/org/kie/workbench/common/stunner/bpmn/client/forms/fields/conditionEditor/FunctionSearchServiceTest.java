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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.PromiseMock;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.Pair;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FunctionSearchServiceTest {

    private static final String FUNCTION1 = "FUNCTION1";
    private static final String FUNCTION1_NAME = "FUNCTION1_NAME";
    private static final String FUNCTION2 = "FUNCTION2";
    private static final String FUNCTION2_NAME = "FUNCTION2_NAME";
    private static final String TYPE = "TYPE";

    @Mock
    private ConditionEditorAvailableFunctionsService availableFunctionsService;

    @Mock
    private FunctionNamingService functionNamingService;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private Command command;

    @Mock
    private LiveSearchCallback<String> searchCallback;

    @Captor
    private ArgumentCaptor<LiveSearchResults<String>> searchResultsCaptor;

    private FunctionSearchService searchService;
    private List<FunctionDef> testFunctions;

    @Before
    public void setUp() {
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
        searchService = new FunctionSearchService(availableFunctionsService, functionNamingService);
    }

    @Test
    public void testInit() {
        searchService.init(clientSession);
        verify(clientSession).getCanvasHandler();
        verify(canvasHandler).getDiagram();
        verify(diagram).getMetadata();
        verify(metadata).getPath();
    }

    @Test
    public void testReload() {
        searchService.init(clientSession);
        FunctionDef someFunction = searchService.getFunction(FUNCTION1);
        assertNull(someFunction);

        prepareForLoad();
        searchService.reload(TYPE, command);

        ArgumentCaptor<ConditionEditorAvailableFunctionsService.Input> inputArgumentCaptor =
                ArgumentCaptor.forClass(ConditionEditorAvailableFunctionsService.Input.class);
        verify(availableFunctionsService).call(inputArgumentCaptor.capture());
        assertEquals(path, inputArgumentCaptor.getValue().path);
        assertEquals(TYPE, inputArgumentCaptor.getValue().clazz);
        someFunction = searchService.getFunction(FUNCTION1);
        assertNotNull(someFunction);
        assertEquals(testFunctions.get(0), someFunction);
        verify(command).execute();
    }

    @Test
    public void testClear() {
        loadTestFunctions();
        assertNotNull(searchService.getFunction(FUNCTION1));
        searchService.clear();
        assertNull(searchService.getFunction(FUNCTION1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchWithResults() {
        loadTestFunctions();

        searchService.search("FUNCTION", 10, searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        LiveSearchResults<String> results = searchResultsCaptor.getValue();
        verifyContains(results, new Pair<>(FUNCTION1, FUNCTION1_NAME), new Pair<>(FUNCTION2, FUNCTION2_NAME));

        searchService.search("FUNCTION1", 10, searchCallback);
        verify(searchCallback, times(2)).afterSearch(searchResultsCaptor.capture());
        results = searchResultsCaptor.getValue();
        verifyContains(results, new Pair<>(FUNCTION1, FUNCTION1_NAME));
        verifyNotContains(results, new Pair<>(FUNCTION2, FUNCTION2_NAME));

        searchService.search("FUNCTION2", 10, searchCallback);
        verify(searchCallback, times(3)).afterSearch(searchResultsCaptor.capture());
        results = searchResultsCaptor.getValue();
        verifyContains(results, new Pair<>(FUNCTION2, FUNCTION2_NAME));
        verifyNotContains(results, new Pair<>(FUNCTION1, FUNCTION1_NAME));
    }

    @Test
    public void testSearchWithoutResults() {
        loadTestFunctions();

        searchService.search("SOME_OTHER_FUNCTION", 10, searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        LiveSearchResults<String> results = searchResultsCaptor.getValue();
        assertEquals(0, results.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchEntryWithResults() {
        loadTestFunctions();

        searchService.searchEntry(FUNCTION1, searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        LiveSearchResults<String> results = searchResultsCaptor.getValue();
        verifyContains(results, new Pair<>(FUNCTION1, FUNCTION1_NAME));

        searchService.searchEntry(FUNCTION2, searchCallback);
        verify(searchCallback, times(2)).afterSearch(searchResultsCaptor.capture());
        results = searchResultsCaptor.getValue();
        verifyContains(results, new Pair<>(FUNCTION2, FUNCTION2_NAME));
    }

    @Test
    public void testSearchEntryWithoutResults() {
        loadTestFunctions();

        searchService.searchEntry("SOME_OTHER_FUNCTION", searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        assertEquals(0, searchResultsCaptor.getValue().size());
    }

    @Test
    public void voidTestGetFunctionWithResult() {
        loadTestFunctions();
        FunctionDef functionDef = searchService.getFunction(FUNCTION1);
        assertEquals(testFunctions.get(0), functionDef);
    }

    @Test
    public void voidTestGetFunctionWithoutResult() {
        FunctionDef functionDef = searchService.getFunction(FUNCTION1);
        assertNull(functionDef);
        loadTestFunctions();
        functionDef = searchService.getFunction("SOME_OTHER_FUNCTION");
        assertNull(functionDef);
    }

    public static void verifyContains(LiveSearchResults<String> results, Pair<String, String>... values) {
        verifyContains(results, Arrays.asList(values));
    }

    public static void verifyContains(LiveSearchResults<String> results, List<Pair<String, String>> values) {
        assertEquals(results.size(), values.size());
        values.forEach(value -> assertTrue("Expected value <" + value.getK1() + ", " + value.getK2() + "> is not present in results but was expected",
                                           results.stream()
                                                   .anyMatch(entry -> value.getK1().equals(entry.getKey()) && value.getK2().equals(entry.getValue()))));
    }

    public static void verifyNotContains(LiveSearchResults<String> results, Pair<String, String>... values) {
        verifyNotContains(results, Arrays.asList(values));
    }

    public static void verifyNotContains(LiveSearchResults<String> results, List<Pair<String, String>> values) {
        values.forEach(value -> assertFalse("Expected value <" + value.getK1() + ", " + value.getK2() + "> is present in results but wasn't expected",
                                            results.stream()
                                                    .anyMatch(entry -> value.getK1().equals(entry.getKey()) && value.getK2().equals(entry.getValue()))));
    }

    private void prepareForLoad() {
        testFunctions = new ArrayList<>();
        testFunctions.add(mockFunctionDef(FUNCTION1, FUNCTION1_NAME));
        testFunctions.add(mockFunctionDef(FUNCTION2, FUNCTION2_NAME));
        doReturn(PromiseMock.success(testFunctions)).when(availableFunctionsService).call(any(ConditionEditorAvailableFunctionsService.Input.class));
    }

    private void loadTestFunctions() {
        searchService.init(clientSession);
        prepareForLoad();
        searchService.reload(TYPE, command);
    }

    private FunctionDef mockFunctionDef(String function, String translatedName) {
        FunctionDef functionDef = mock(FunctionDef.class);
        when(functionDef.getName()).thenReturn(function);
        when(functionNamingService.getFunctionName(function)).thenReturn(translatedName);
        return functionDef;
    }
}
