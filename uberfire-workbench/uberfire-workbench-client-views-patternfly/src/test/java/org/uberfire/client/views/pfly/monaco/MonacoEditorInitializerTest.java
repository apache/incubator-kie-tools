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

import java.util.function.Consumer;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.Monaco;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLoader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.uberfire.client.views.pfly.monaco.MonacoEditorInitializer.VS_EDITOR_EDITOR_MAIN_MODULE;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoEditorInitializerTest {

    @Mock
    private JsArrayString monacoModule;

    @Mock
    private Monaco monaco;

    @Captor
    private ArgumentCaptor<MonacoLoader.CallbackFunction> callbackFunctionCaptor;

    private MonacoEditorInitializer initializer;

    @Before
    public void setup() {
        initializer = spy(new MonacoEditorInitializer());
    }

    @Test
    public void testRequire() {

        final int[] monacoConsumerCalls = new int[1];
        final Consumer<Monaco> monacoConsumer = (e) -> monacoConsumerCalls[0]++;

        doNothing().when(initializer).require(any(), any());
        doReturn(monacoModule).when(initializer).monacoModule();

        initializer.require(monacoConsumer);

        final InOrder inOrder = inOrder(initializer);

        inOrder.verify(initializer).switchAMDLoaderFromDefaultToMonaco();
        inOrder.verify(initializer).require(eq(monacoModule), callbackFunctionCaptor.capture());
        callbackFunctionCaptor.getValue().call(monaco);
        assertEquals(1, monacoConsumerCalls[0]);
        inOrder.verify(initializer).switchAMDLoaderFromMonacoToDefault();
    }

    @Test
    public void testMonacoModule() {

        final JsArrayString expectedModules = mock(JsArrayString.class);

        doReturn(expectedModules).when(initializer).makeJsArrayString();

        final JsArrayString actualModules = initializer.monacoModule();

        verify(expectedModules).push(VS_EDITOR_EDITOR_MAIN_MODULE);
        assertEquals(expectedModules, actualModules);
    }
}
