/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.impl.old.perspective.editor.*;
import org.uberfire.ext.plugin.type.TagsConverterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayoutUpgradeTool {

    static final String HTML_DRAG_TYPE = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";
    static final String SCREEN_DRAG_TYPE = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent";
    static final String DASHBUILDER_DRAG_TYPE = "org.dashbuilder.client.editor.DisplayerDragComponent";


    public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean isVersion1(String layoutEditorModel) {
        return layoutEditorModel.contains("layoutProperties");
    }

    public static LayoutTemplate convert(String layoutEditorModel) {
        try {
            PerspectiveEditor editor = getPerspectiveEditor(layoutEditorModel);
            LayoutTemplate layoutTemplate = new LayoutTemplate(editor.getName());
            extractTags(editor, layoutTemplate);
            extractRows(editor.getRows(), layoutTemplate);
            return layoutTemplate;
        } catch (Exception e) {
            e.printStackTrace();
            return LayoutTemplate.defaultLayout("");
        }
    }

    private static void extractTags(PerspectiveEditor editor, LayoutTemplate layoutTemplate) {

        if (templateHasTags(editor)) {
            String tags = TagsConverterUtil.convertTagsToString(editor.getTags());
            layoutTemplate.addLayoutProperty(TagsConverterUtil.LAYOUT_PROPERTY, tags);
        }
    }

    private static boolean templateHasTags(PerspectiveEditor editor) {
        return editor.getTags() != null && !editor.getTags().isEmpty();
    }

    private static void extractRows(List<RowEditor> rows,
                                    LayoutTemplate layoutTemplate) {
        for (RowEditor rowEditor : rows) {
            LayoutRow layoutRow = new LayoutRow(rowEditor.getRowSpam());
            for (ColumnEditor columnEditor : rowEditor.getColumnEditors()) {
                LayoutColumn column = new LayoutColumn(columnEditor.getSpan());
                if (columnHasNestedRows(columnEditor)) {
                    extractRows(columnEditor.getRows(), layoutTemplate);
                } else {
                    generateScreens(columnEditor, column);
                    generateHTML(columnEditor, column);
                }
                layoutRow.add(column);
            }
            layoutTemplate.addRow(layoutRow);
        }
    }

    private static void generateHTML(ColumnEditor columnEditor,
                                     LayoutColumn column) {
        for (HTMLEditor htmlEditor : columnEditor.getHtmls()) {
            LayoutComponent layoutComponent = new LayoutComponent(HTML_DRAG_TYPE);
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("HTML_CODE", htmlEditor.getHtmlCode());
            layoutComponent.addProperties(properties);
            column.addLayoutComponent(layoutComponent);
        }
    }

    private static void generateScreens(ColumnEditor columnEditor,
                                        LayoutColumn column) {
        for (ScreenEditor screenEditor : columnEditor.getScreens()) {
            String screenDragType;
            if (screenEditor.isAExternalComponent()) {
                screenDragType = DASHBUILDER_DRAG_TYPE;
            } else {
                screenDragType = SCREEN_DRAG_TYPE;
            }
            LayoutComponent layoutComponent = new LayoutComponent(screenDragType);
            layoutComponent.addProperties(screenEditor.getParameters());
            column.addLayoutComponent(layoutComponent);
        }
    }

    private static boolean columnHasNestedRows(ColumnEditor columnEditor) {
        return columnEditor.getRows() != null && !columnEditor.getRows().isEmpty();
    }

    private static PerspectiveEditor getPerspectiveEditor(String layoutEditorModel) {
        PerspectiveEditor perspectiveEditor = gson.fromJson(layoutEditorModel, PerspectiveEditor.class);
        return perspectiveEditor;
    }
}
