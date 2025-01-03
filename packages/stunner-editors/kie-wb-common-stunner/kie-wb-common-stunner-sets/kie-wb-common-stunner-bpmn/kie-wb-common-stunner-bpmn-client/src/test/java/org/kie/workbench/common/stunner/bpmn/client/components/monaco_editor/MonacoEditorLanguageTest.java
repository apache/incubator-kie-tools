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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MonacoEditorLanguageTest {

    @Test
    public void testInstanceGetters() {
        MonacoEditorOptions options = mock(MonacoEditorOptions.class);
        when(options.copy()).thenReturn(options);
        MonacoEditorLanguage language = new MonacoEditorLanguage("id1", "title1", "code1", new String[]{"module1", "module2"}, options);
        assertEquals("id1", language.getId());
        assertEquals("title1", language.getTitle());
        assertEquals("code1", language.getLanguageCode());
        assertEquals(2, language.getModules().length);
        assertEquals("module1", language.getModules()[0]);
        assertEquals("module2", language.getModules()[1]);
    }

    @Test
    public void testJavaOptions() {
        MonacoEditorLanguage language = MonacoEditorLanguage.JAVA;
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.getId());
        assertEquals(MonacoEditorLanguage.TITLE_JAVA, language.getTitle());
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.getLanguageCode());
        assertEquals(1, language.getModules().length);
        assertEquals(MonacoEditorLanguage.JAVA_MODULE, language.getModules()[0]);
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.buildOptions().getLanguage());
    }

    @Test
    public void testMVELOptions() {
        MonacoEditorLanguage language = MonacoEditorLanguage.MVEL;
        assertEquals(MonacoEditorLanguage.LANG_MVEL, language.getId());
        assertEquals(MonacoEditorLanguage.TITLE_MVEL, language.getTitle());
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.getLanguageCode());
        assertEquals(1, language.getModules().length);
        assertEquals(MonacoEditorLanguage.JAVA_MODULE, language.getModules()[0]);
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.buildOptions().getLanguage());
    }

    @Test
    public void testDroolsOptions() {
        MonacoEditorLanguage language = MonacoEditorLanguage.DROOLS;
        assertEquals(MonacoEditorLanguage.LANG_DROOLS, language.getId());
        assertEquals(MonacoEditorLanguage.TITLE_DROOLS, language.getTitle());
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.getLanguageCode());
        assertEquals(1, language.getModules().length);
        assertEquals(MonacoEditorLanguage.JAVA_MODULE, language.getModules()[0]);
        assertEquals(MonacoEditorLanguage.LANG_JAVA, language.buildOptions().getLanguage());
    }

    @Test
    public void testFEELOptions() {
        MonacoEditorLanguage language = MonacoEditorLanguage.FEEL;
        assertEquals(MonacoEditorLanguage.LANG_FEEL, language.getId());
        assertEquals(MonacoEditorLanguage.TITLE_FEEL, language.getTitle());
        assertEquals(MonacoEditorLanguage.LANG_FEEL, language.getLanguageCode());
        assertEquals(0, language.getModules().length);
        assertEquals(MonacoEditorLanguage.LANG_FEEL, language.buildOptions().getLanguage());
    }
}
