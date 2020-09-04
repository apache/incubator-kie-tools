/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.monaco;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLoader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer.VS_EDITOR_EDITOR_MAIN_MODULE;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoEditorInitializerTest {

    private MonacoEditorInitializer tested;

    @Before
    public void setup() {
        tested = spy(new MonacoEditorInitializer());
    }

    @Test
    public void testRequireOnlyEditorModule() {
        assertFalse(testRequire(new String[0]));
    }

    @Test
    public void testRequireModules() {
        assertTrue(testRequire(new String[]{"someModulePathHere"}));
    }

    @SuppressWarnings("all")
    private boolean testRequire(final String[] modules) {

        final JsArrayString mainModuleJsArray = mock(JsArrayString.class);
        final JsArrayString modulesArray = mock(JsArrayString.class);
        doAnswer(new Answer<JsArrayString>() {
            @Override
            public JsArrayString answer(InvocationOnMock invocation) throws Throwable {
                String arg = (String) invocation.getArguments()[0];
                if (VS_EDITOR_EDITOR_MAIN_MODULE.equals(arg)) {
                    return mainModuleJsArray;
                }
                if (modules.length > 0 && modules[0].equals(arg)) {
                    return modulesArray;
                }
                return null;
            }
        }).when(tested).toJsArrayString(any());

        final boolean[] callbackExecuted = new boolean[]{false};
        final boolean[] mainModulesLoadingExecuted = new boolean[]{false};
        final boolean[] modulesLoadingExecuted = new boolean[]{false};
        Consumer<Monaco> monacoConsumer = monaco -> {
            callbackExecuted[0] = true;
        };
        Monaco monaco = mock(Monaco.class);
        BiConsumer<JsArrayString, MonacoLoader.CallbackFunction> monacoLoader =
                (array, callback) -> {
                    if (array == mainModuleJsArray) {
                        mainModulesLoadingExecuted[0] = true;
                        callback.call(monaco);
                    }
                    if (array == modulesArray) {
                        modulesLoadingExecuted[0] = true;
                        callback.call(monaco);
                    }
                };

        tested.require(monacoLoader,
                       monacoConsumer,
                       modules);

        final InOrder inOrder = inOrder(tested);
        inOrder.verify(tested, times(1)).switchAMDLoaderFromDefaultToMonaco();
        inOrder.verify(tested, times(1)).switchAMDLoaderFromMonacoToDefault();
        assertTrue(mainModulesLoadingExecuted[0]);
        assertTrue(callbackExecuted[0]);
        return modulesLoadingExecuted[0];
    }
}
