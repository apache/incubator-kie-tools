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


package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoEditorPresenterTest {

    @Mock
    private MonacoEditorView view;

    @Mock
    private MonacoEditorPresenter.OnChangeCallback changeCallback;

    private MonacoEditorPresenter tested;

    @Before
    public void setUp() {
        tested = new MonacoEditorPresenter(view);
        tested.setOnChangeCallback(changeCallback);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view, times(1)).init(eq(tested));
    }

    @Test
    public void testGetters() {
        assertEquals(view, tested.getView());
        when(view.getLanguage()).thenReturn("lang1");
        when(view.getValue()).thenReturn("value1");
        assertEquals("lang1", tested.getLanguageId());
        assertEquals("value1", tested.getValue());
    }

    @Test
    public void testAddLanguage() {
        tested.addLanguage(MonacoEditorLanguage.JAVA);
        verify(view, times(1)).addLanguage(eq(MonacoEditorLanguage.TITLE_JAVA),
                                           eq(MonacoEditorLanguage.LANG_JAVA));
    }

    @Test
    public void testValueChangedCallback() {
        tested.onValueChanged();
        verify(changeCallback, times(1)).onChange();
    }

    @Test
    public void testLanguageChangedCallback() {
        tested.addLanguage(MonacoEditorLanguage.JAVA);
        tested.onLanguageChanged(MonacoEditorLanguage.LANG_JAVA);
        verify(changeCallback, times(1)).onChange();
        assertEquals(MonacoEditorLanguage.LANG_JAVA, tested.current);
    }

    @Test
    public void testSetValue() {
        tested.setWidthPx(300);
        tested.setHeightPx(100);
        tested.setReadOnly(true);
        tested.addLanguage(MonacoEditorLanguage.JAVA);
        tested.setValue(MonacoEditorLanguage.LANG_JAVA, "someJavaCode");
        assertEquals(MonacoEditorLanguage.LANG_JAVA, tested.current);
        verify(view, never()).dispose();
        ArgumentCaptor<MonacoEditorOptions> optionsCaptor = ArgumentCaptor.forClass(MonacoEditorOptions.class);
        ArgumentCaptor<Runnable> callbackCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(view, times(1)).loadingStarts();
        verify(view, times(1)).setLanguage(eq(MonacoEditorLanguage.LANG_JAVA));
        verify(view, times(1)).load(optionsCaptor.capture(),
                                    callbackCaptor.capture());
        MonacoEditorOptions options = optionsCaptor.getValue();
        assertEquals(300, options.getWidthPx());
        assertEquals(100, options.getHeightPx());
        assertEquals(MonacoEditorLanguage.LANG_JAVA, options.getLanguage());
        assertEquals("someJavaCode", options.getValue());
        assertTrue(options.isReadOnly());
        assertFalse(options.isAutomaticLayout());
        callbackCaptor.getValue().run();
        verify(view, times(1)).loadingEnds();
        verify(view, times(1)).setLanguageReadOnly(eq(true));
        verify(view, times(1)).attachListenerToPanelTitle();
    }

    @Test
    public void testSetValueWithRefresh() {
        tested.setWidthPx(300);
        tested.setHeightPx(100);
        tested.setReadOnly(true);
        tested.addLanguage(MonacoEditorLanguage.JAVA);
        tested.requestRefresh();
        tested.setValue(MonacoEditorLanguage.LANG_JAVA, "someJavaCode");
        assertEquals(MonacoEditorLanguage.LANG_JAVA, tested.current);
        verify(view, times(1)).dispose();
        ArgumentCaptor<MonacoEditorOptions> optionsCaptor = ArgumentCaptor.forClass(MonacoEditorOptions.class);
        ArgumentCaptor<Runnable> callbackCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(view, times(1)).loadingStarts();
        verify(view, times(1)).setLanguage(eq(MonacoEditorLanguage.LANG_JAVA));
        verify(view, times(1)).load(optionsCaptor.capture(),
                                    callbackCaptor.capture());
        MonacoEditorOptions options = optionsCaptor.getValue();
        assertEquals(300, options.getWidthPx());
        assertEquals(100, options.getHeightPx());
        assertEquals(MonacoEditorLanguage.LANG_JAVA, options.getLanguage());
        assertEquals("someJavaCode", options.getValue());
        assertTrue(options.isReadOnly());
        assertFalse(options.isAutomaticLayout());
        callbackCaptor.getValue().run();
        verify(view, times(1)).loadingEnds();
        verify(view, times(1)).setLanguageReadOnly(eq(true));
        verify(view, times(1)).attachListenerToPanelTitle();
    }

    @Test
    public void testSetValueUsingCurrentLang() {
        tested.current = "lang1";
        tested.setValue("lang1", "anotherValue");
        assertEquals("lang1", tested.current);
        verify(view, never()).dispose();
        verify(view, times(1)).setValue(eq("anotherValue"));
    }

    @Test
    public void testUpdateValueAndLanguage() {
        tested.addLanguage(MonacoEditorLanguage.JAVA);
        tested.current = "lang1";
        tested.setValue(MonacoEditorLanguage.LANG_JAVA, "anotherValue");
        assertEquals(MonacoEditorLanguage.LANG_JAVA, tested.current);
        verify(view, times(1)).dispose();
        verify(view, times(1)).loadingStarts();
        verify(view, times(1)).setLanguage(eq(MonacoEditorLanguage.LANG_JAVA));
        verify(view, times(1)).load(any(), any());
        verify(view, never()).setValue(eq("anotherValue"));
    }

    @Test
    public void testDestroy() {
        tested.current = "some";
        tested.onChangeCallback = () -> {
        };
        tested.destroy();
        assertNull(tested.current);
        assertNull(tested.onChangeCallback);
    }
}
