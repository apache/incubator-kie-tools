/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public class TargetDivList {

    public static List<String> list(LayoutTemplate layout) {
        List<String> ids = new ArrayList<>();
        List<LayoutRow> rows = layout.getRows();
        searchForComponents(rows,
                            ids);
        return ids;
    }

    private static void searchForComponents(List<LayoutRow> rows,
                                            List<String> ids) {
        for (LayoutRow layoutRow : rows) {
            for (LayoutColumn layoutColumn : layoutRow.getLayoutColumns()) {
                if (columnHasNestedRows(layoutColumn)) {
                    searchForComponents(layoutColumn.getRows(),
                                        ids);
                } else {
                    extractTargetDiv(layoutColumn.getLayoutComponents(),
                                     ids);
                }
            }
        }
    }

    private static void extractTargetDiv(List<LayoutComponent> layoutComponents,
                                         List<String> ids) {
        for (LayoutComponent layoutComponent : layoutComponents) {
            if (isATargetDiv(layoutComponent)) {
                ids.add(layoutComponent.getProperties().get(TargetDivDragComponent.ID_PARAMETER));
            }
        }
    }

    private static boolean isATargetDiv(LayoutComponent layoutComponent) {
        return layoutComponent.getDragTypeName().equalsIgnoreCase(TargetDivDragComponent.class.getName());
    }

    private static boolean columnHasNestedRows(LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }
}
