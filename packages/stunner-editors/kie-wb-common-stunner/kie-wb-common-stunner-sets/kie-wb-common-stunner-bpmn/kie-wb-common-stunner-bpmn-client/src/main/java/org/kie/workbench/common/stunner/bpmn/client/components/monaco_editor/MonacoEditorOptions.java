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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class MonacoEditorOptions {

    private static final String OPT_LANGUAGE = "language";
    private static final String OPT_VALUE = "value";
    private static final String OPT_THEME = "theme";
    private static final String OPT_RENDER_LINE_HIGHLIGHT = "renderLineHighlight";
    private static final String OPT_LINE_NUMBERS = "lineNumbers";
    private static final String OPT_FONT_SIZE = "fontSize";
    private static final String OPT_LINE_NUMBERS_MIN_CHARS = "lineNumbersMinChars";
    private static final String OPT_LINE_DECORATIONS_WIDTH = "lineDecorationsWidth";
    private static final String OPT_READ_ONLY = "readOnly";
    private static final String OPT_OVERVIEW_RULE_BORDER = "overviewRulerBorder";
    private static final String OPT_SCROLL_BEYOND_LAST_TIME = "scrollBeyondLastLine";
    private static final String OPT_SNIPPET_SUGGESTIONS = "snippetSuggestions";
    private static final String OPT_USE_TAB_STOPS = "useTabStops";
    private static final String OPT_CONTEXT_MENU = "contextmenu";
    private static final String OPT_FOLDING = "folding";
    private static final String OPT_ENABLED = "enabled";
    private static final String OPT_USE_SHADOWS = "useShadows";
    private static final String OPT_AUTO_LAYOUT = "automaticLayout";
    private static final String OPT_RENDER_WHITESPACES = "renderWhitespace";
    private static final String OPT_HIDE_CURSOR_IN_OVERVIEW_RULER = "hideCursorInOverviewRuler";
    private static final String OPT_SCROLLBAR = "scrollbar";
    private static final String OPT_MINIMAP = "minimap";

    private int widthPx;
    private int heightPx;
    private String language;
    private String theme;
    private String value;
    private String renderLineHighlight;
    private String lineNumbers;
    private int fontSize;
    private int lineNumbersMinChars;
    private int lineDecorationsWidth;
    private boolean readOnly;
    private boolean overviewRulerBorder;
    private boolean scrollBeyondLastLine;
    private boolean snippetSuggestions;
    private boolean useTabStops;
    private boolean contextmenu;
    private boolean folding;
    private boolean miniMapEnabled;
    private boolean scrollbarUseShadows;
    private boolean automaticLayout;
    private boolean renderWhitespace;
    private boolean hideCursorInOverviewRuler;

    public static MonacoEditorOptions buildDefaultOptions(String lang) {
        MonacoEditorOptions properties = new MonacoEditorOptions();
        properties.setLanguage(lang);
        properties.setWidthPx(500);
        properties.setHeightPx(300);
        properties.setRenderLineHighlight("none");
        properties.setLineNumbers("off");
        properties.setFontSize(12);
        properties.setLineNumbersMinChars(1);
        properties.setLineDecorationsWidth(1);
        properties.setReadOnly(false);
        properties.setOverviewRulerBorder(false);
        properties.setScrollBeyondLastLine(false);
        properties.setSnippetSuggestions(false);
        properties.setUseTabStops(false);
        properties.setContextmenu(false);
        properties.setFolding(false);
        properties.setMiniMapEnabled(false);
        properties.setScrollbarUseShadows(false);
        properties.setAutomaticLayout(false);
        properties.setRenderWhitespace(true);
        properties.setHideCursorInOverviewRuler(true);
        return properties;
    }

    public static JSONObject createDimensions(int widthPx, int heightPx) {
        JSONObject dimensions = new JSONObject();
        dimensions.put("width", new JSONNumber(widthPx));
        dimensions.put("height", new JSONNumber(heightPx));
        return dimensions;
    }

    JSONObject toJSONObject() {

        final JSONObject options = makeJSONObject();
        final JSONObject scrollbar = makeJSONObject();
        final JSONObject miniMap = makeJSONObject();

        options.put(OPT_LANGUAGE, makeJSONString(language));
        if (null != value) {
            options.put(OPT_VALUE, new JSONString(value));
        }
        if (null != theme) {
            options.put(OPT_THEME, makeJSONString(theme));
        }

        options.put(OPT_RENDER_LINE_HIGHLIGHT, makeJSONString(renderLineHighlight));
        options.put(OPT_LINE_NUMBERS, makeJSONString(lineNumbers));

        options.put(OPT_FONT_SIZE, makeJSONNumber(fontSize));
        options.put(OPT_LINE_NUMBERS_MIN_CHARS, makeJSONNumber(lineNumbersMinChars));
        options.put(OPT_LINE_DECORATIONS_WIDTH, makeJSONNumber(lineDecorationsWidth));

        options.put(OPT_READ_ONLY, makeJSONBoolean(readOnly));
        options.put(OPT_OVERVIEW_RULE_BORDER, makeJSONBoolean(overviewRulerBorder));
        options.put(OPT_SCROLL_BEYOND_LAST_TIME, makeJSONBoolean(scrollBeyondLastLine));
        options.put(OPT_SNIPPET_SUGGESTIONS, makeJSONBoolean(snippetSuggestions));
        options.put(OPT_USE_TAB_STOPS, makeJSONBoolean(useTabStops));
        options.put(OPT_CONTEXT_MENU, makeJSONBoolean(contextmenu));
        options.put(OPT_FOLDING, makeJSONBoolean(folding));
        miniMap.put(OPT_ENABLED, makeJSONBoolean(miniMapEnabled));
        scrollbar.put(OPT_USE_SHADOWS, makeJSONBoolean(scrollbarUseShadows));

        options.put(OPT_AUTO_LAYOUT, makeJSONBoolean(automaticLayout));
        options.put(OPT_RENDER_WHITESPACES, makeJSONBoolean(renderWhitespace));
        options.put(OPT_HIDE_CURSOR_IN_OVERVIEW_RULER, makeJSONBoolean(hideCursorInOverviewRuler));

        options.put(OPT_SCROLLBAR, scrollbar);
        options.put(OPT_MINIMAP, miniMap);

        return options;
    }

    JavaScriptObject toJavaScriptObject() {
        return toJSONObject().getJavaScriptObject();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public MonacoEditorOptions copy() {
        MonacoEditorOptions properties = new MonacoEditorOptions();
        properties.setLanguage(this.getLanguage());
        properties.setValue(this.getValue());
        properties.setWidthPx(this.getWidthPx());
        properties.setHeightPx(this.getHeightPx());
        properties.setRenderLineHighlight(this.getRenderLineHighlight());
        properties.setLineNumbers(this.getLineNumbers());
        properties.setFontSize(this.getFontSize());
        properties.setLineNumbersMinChars(this.getLineNumbersMinChars());
        properties.setLineDecorationsWidth(this.getLineDecorationsWidth());
        properties.setReadOnly(this.isReadOnly());
        properties.setOverviewRulerBorder(this.isOverviewRulerBorder());
        properties.setScrollBeyondLastLine(this.isScrollBeyondLastLine());
        properties.setSnippetSuggestions(this.isSnippetSuggestions());
        properties.setUseTabStops(this.isUseTabStops());
        properties.setContextmenu(this.isContextmenu());
        properties.setFolding(this.isFolding());
        properties.setMiniMapEnabled(this.isMiniMapEnabled());
        properties.setScrollbarUseShadows(this.isScrollbarUseShadows());
        properties.setAutomaticLayout(this.isAutomaticLayout());
        properties.setRenderWhitespace(this.isRenderWhitespace());
        properties.setHideCursorInOverviewRuler(this.isHideCursorInOverviewRuler());
        properties.setTheme(this.getTheme());
        return properties;
    }

    public int getWidthPx() {
        return widthPx;
    }

    public MonacoEditorOptions setWidthPx(int widthPx) {
        this.widthPx = widthPx;
        return this;
    }

    public int getHeightPx() {
        return heightPx;
    }

    public MonacoEditorOptions setHeightPx(int heightPx) {
        this.heightPx = heightPx;
        return this;
    }

    public MonacoEditorOptions setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public MonacoEditorOptions setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public MonacoEditorOptions setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MonacoEditorOptions setValue(String value) {
        this.value = value;
        return this;
    }

    public String getRenderLineHighlight() {
        return renderLineHighlight;
    }

    public MonacoEditorOptions setRenderLineHighlight(String renderLineHighlight) {
        this.renderLineHighlight = renderLineHighlight;
        return this;
    }

    public String getLineNumbers() {
        return lineNumbers;
    }

    public MonacoEditorOptions setLineNumbers(String lineNumbers) {
        this.lineNumbers = lineNumbers;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public MonacoEditorOptions setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public int getLineNumbersMinChars() {
        return lineNumbersMinChars;
    }

    public MonacoEditorOptions setLineNumbersMinChars(int lineNumbersMinChars) {
        this.lineNumbersMinChars = lineNumbersMinChars;
        return this;
    }

    public int getLineDecorationsWidth() {
        return lineDecorationsWidth;
    }

    public MonacoEditorOptions setLineDecorationsWidth(int lineDecorationsWidth) {
        this.lineDecorationsWidth = lineDecorationsWidth;
        return this;
    }

    public boolean isOverviewRulerBorder() {
        return overviewRulerBorder;
    }

    public MonacoEditorOptions setOverviewRulerBorder(boolean overviewRulerBorder) {
        this.overviewRulerBorder = overviewRulerBorder;
        return this;
    }

    public boolean isScrollBeyondLastLine() {
        return scrollBeyondLastLine;
    }

    public MonacoEditorOptions setScrollBeyondLastLine(boolean scrollBeyondLastLine) {
        this.scrollBeyondLastLine = scrollBeyondLastLine;
        return this;
    }

    public boolean isSnippetSuggestions() {
        return snippetSuggestions;
    }

    public MonacoEditorOptions setSnippetSuggestions(boolean snippetSuggestions) {
        this.snippetSuggestions = snippetSuggestions;
        return this;
    }

    public boolean isUseTabStops() {
        return useTabStops;
    }

    public MonacoEditorOptions setUseTabStops(boolean useTabStops) {
        this.useTabStops = useTabStops;
        return this;
    }

    public boolean isContextmenu() {
        return contextmenu;
    }

    public MonacoEditorOptions setContextmenu(boolean contextmenu) {
        this.contextmenu = contextmenu;
        return this;
    }

    public boolean isFolding() {
        return folding;
    }

    public MonacoEditorOptions setFolding(boolean folding) {
        this.folding = folding;
        return this;
    }

    public boolean isMiniMapEnabled() {
        return miniMapEnabled;
    }

    public MonacoEditorOptions setMiniMapEnabled(boolean miniMapEnabled) {
        this.miniMapEnabled = miniMapEnabled;
        return this;
    }

    public boolean isScrollbarUseShadows() {
        return scrollbarUseShadows;
    }

    public MonacoEditorOptions setScrollbarUseShadows(boolean scrollbarUseShadows) {
        this.scrollbarUseShadows = scrollbarUseShadows;
        return this;
    }

    public boolean isAutomaticLayout() {
        return automaticLayout;
    }

    public MonacoEditorOptions setAutomaticLayout(boolean automaticLayout) {
        this.automaticLayout = automaticLayout;
        return this;
    }

    public boolean isRenderWhitespace() {
        return renderWhitespace;
    }

    public MonacoEditorOptions setRenderWhitespace(boolean renderWhitespace) {
        this.renderWhitespace = renderWhitespace;
        return this;
    }

    public boolean isHideCursorInOverviewRuler() {
        return hideCursorInOverviewRuler;
    }

    public MonacoEditorOptions setHideCursorInOverviewRuler(boolean hideCursorInOverviewRuler) {
        this.hideCursorInOverviewRuler = hideCursorInOverviewRuler;
        return this;
    }

    private static JSONBoolean makeJSONBoolean(final boolean value) {
        return JSONBoolean.getInstance(value);
    }

    private static JSONString makeJSONString(final String value) {
        return new JSONString(value);
    }

    private static JSONValue makeJSONNumber(final int value) {
        return new JSONNumber(value);
    }

    private static JSONObject makeJSONObject() {
        return new JSONObject();
    }
}
