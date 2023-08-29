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
import static org.junit.Assert.assertTrue;

public class MonacoEditorOptionsTest {

    @Test
    public void testState() {
        MonacoEditorOptions options = new MonacoEditorOptions();
        options.setValue("value1");
        options.setWidthPx(100);
        options.setHeightPx(50);
        options.setReadOnly(true);
        options.setAutomaticLayout(true);
        options.setContextmenu(true);
        options.setFolding(true);
        options.setFontSize(12);
        options.setHideCursorInOverviewRuler(true);
        options.setLanguage("lang1");
        options.setLineDecorationsWidth(5);
        options.setLineNumbers("off");
        options.setLineNumbersMinChars(3);
        options.setMiniMapEnabled(false);
        options.setOverviewRulerBorder(true);
        options.setRenderLineHighlight("off");
        options.setRenderWhitespace(true);
        options.setScrollbarUseShadows(true);
        options.setScrollBeyondLastLine(true);
        options.setSnippetSuggestions(true);
        options.setUseTabStops(true);
        options.setTheme("theme1");
        assertEquals("value1", options.getValue());
        assertEquals(100, options.getWidthPx());
        assertEquals(50, options.getHeightPx());
        assertTrue(options.isReadOnly());
        assertTrue(options.isAutomaticLayout());
        assertTrue(options.isContextmenu());
        assertTrue(options.isFolding());
        assertEquals(12, options.getFontSize());
        assertTrue(options.isHideCursorInOverviewRuler());
        assertEquals("lang1", options.getLanguage());
        assertEquals(5, options.getLineDecorationsWidth());
        assertEquals("off", options.getLineNumbers());
        assertEquals(3, options.getLineNumbersMinChars());
        assertEquals(false, options.isMiniMapEnabled());
        assertTrue(options.isOverviewRulerBorder());
        assertEquals("off", options.getRenderLineHighlight());
        assertTrue(options.isRenderWhitespace());
        assertTrue(options.isScrollbarUseShadows());
        assertTrue(options.isScrollBeyondLastLine());
        assertTrue(options.isSnippetSuggestions());
        assertTrue(options.isUseTabStops());
        assertEquals("theme1", options.getTheme());
    }

    @Test
    public void testCopy() {
        MonacoEditorOptions options = new MonacoEditorOptions();
        options.setValue("value1");
        options.setWidthPx(100);
        options.setHeightPx(50);
        options.setReadOnly(true);
        options.setAutomaticLayout(true);
        options.setContextmenu(true);
        options.setFolding(true);
        options.setFontSize(12);
        options.setHideCursorInOverviewRuler(true);
        options.setLanguage("lang1");
        options.setLineDecorationsWidth(5);
        options.setLineNumbers("off");
        options.setLineNumbersMinChars(3);
        options.setMiniMapEnabled(false);
        options.setOverviewRulerBorder(true);
        options.setRenderLineHighlight("off");
        options.setRenderWhitespace(true);
        options.setScrollbarUseShadows(true);
        options.setScrollBeyondLastLine(true);
        options.setSnippetSuggestions(true);
        options.setUseTabStops(true);
        options.setTheme("theme1");
        MonacoEditorOptions options1 = options.copy();
        assertEquals("value1", options1.getValue());
        assertEquals(100, options.getWidthPx());
        assertEquals(50, options.getHeightPx());
        assertTrue(options1.isReadOnly());
        assertTrue(options1.isAutomaticLayout());
        assertTrue(options1.isContextmenu());
        assertTrue(options1.isFolding());
        assertEquals(12, options.getFontSize());
        assertTrue(options1.isHideCursorInOverviewRuler());
        assertEquals("lang1", options1.getLanguage());
        assertEquals(5, options1.getLineDecorationsWidth());
        assertEquals("off", options1.getLineNumbers());
        assertEquals(3, options1.getLineNumbersMinChars());
        assertEquals(false, options1.isMiniMapEnabled());
        assertTrue(options1.isOverviewRulerBorder());
        assertEquals("off", options1.getRenderLineHighlight());
        assertTrue(options1.isRenderWhitespace());
        assertTrue(options1.isScrollbarUseShadows());
        assertTrue(options1.isScrollBeyondLastLine());
        assertTrue(options1.isSnippetSuggestions());
        assertTrue(options1.isUseTabStops());
        assertEquals("theme1", options1.getTheme());
    }
}
