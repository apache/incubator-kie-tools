/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.generator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;

public abstract class AbstractLayoutGenerator implements LayoutGenerator {

    private static final String ROW_CLASS_PREFIX = "uf-perspective-row-";

    private static final String UF_LE_OVERFLOW = "uf-le-overflow";

    private static final String COLUMN_CLASS = "uf-perspective-col";

    public static final String CONTAINER_ID = "mainContainer";

    @Inject
    private LayoutEditorCssHelper cssPropertiesHelper;

    @Inject
    Elemental2DomUtil elemental2Util;

    @Override
    public LayoutInstance build(LayoutTemplate layoutTemplate, LayoutGeneratorDriver driver) {
        var container = driver.createContainer();
        container.id = CONTAINER_ID;
        container.classList.add("uf-perspective-container");
        container.classList.add("uf-perspective-rendered-container");
        applyCssToElement(layoutTemplate.getLayoutProperties(), container);

        var layoutInstance = new LayoutInstance(container);
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(layoutTemplate, layoutInstance, driver, rows, container);
        return layoutInstance;
    }

    protected void generateRows(LayoutTemplate layoutTemplate,
                                LayoutInstance layoutInstance,
                                LayoutGeneratorDriver driver,
                                List<LayoutRow> rows,
                                HTMLElement parentWidget) {
        for (var layoutRow : rows) {
            var row = driver.createRow(layoutRow);
            applyCssToElement(layoutRow.getProperties(), row);

            if (layoutTemplate.isPageStyle()) {
                row.classList.add(buildRowSize(layoutRow.getHeight()));
                row.classList.add(UF_LE_OVERFLOW);
            }
            for (LayoutColumn layoutColumn : layoutRow.getLayoutColumns()) {
                var column = driver.createColumn(layoutColumn);
                applyCssToElement(layoutColumn.getProperties(), column);

                if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                    column.classList.add(COLUMN_CLASS);
                }
                if (columnHasNestedRows(layoutColumn)) {
                    if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                        column.classList.add(COLUMN_CLASS);
                    } else if (!layoutColumn.getHeight().isEmpty()) {
                        column.classList.add(ROW_CLASS_PREFIX + layoutColumn.getHeight());
                    }
                    generateRows(layoutTemplate,
                            layoutInstance,
                            driver,
                            layoutColumn.getRows(),
                            column);
                } else {
                    generateComponents(layoutTemplate,
                            driver,
                            layoutColumn,
                            column);
                }
                column.classList.add("uf-perspective-rendered-col");
                row.appendChild(column);
            }
            row.classList.add("uf-perspective-rendered-row");
            parentWidget.appendChild(row);
        }
    }

    protected void generateComponents(LayoutTemplate layoutTemplate,
                                      final LayoutGeneratorDriver driver,
                                      final LayoutColumn layoutColumn,
                                      final HTMLElement column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents()) {
            final IsWidget componentWidget = driver.createComponent(column, layoutComponent);
            if (componentWidget != null) {
                if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                    componentWidget.asWidget().getElement().addClassName(COLUMN_CLASS);
                } else if (!layoutColumn.getHeight().isEmpty()) {
                    column.classList.add(ROW_CLASS_PREFIX + layoutColumn.getHeight());
                }

                elemental2Util.appendWidgetToElement(column, componentWidget.asWidget());
                applyCssToElement(layoutComponent.getProperties(), componentWidget);
                layoutComponent.getParts().forEach(p -> {
                    final Optional<IsWidget> partWidget = driver.getComponentPart(column, layoutComponent, p
                            .getPartId());
                    partWidget.ifPresent(widget -> applyCssToElement(p.getCssProperties(), widget));
                });
            }
        }
    }

    protected boolean columnHasNestedRows(LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }

    protected void applyCssToElement(Map<String, String> properties, HTMLElement element) {
        if (properties != null && !properties.isEmpty()) {
            CSSStyleDeclaration style = element.style;
            cssPropertiesHelper.readCssValues(properties)
                    .stream().forEach(cssValue -> {
                        String prop = cssValue.getProperty();
                        String val = cssValue.getValue();
                        style.setProperty(prop, val);
                    });
        }
    }

    protected void applyCssToElement(Map<String, String> properties, IsWidget element) {
        if (properties != null && !properties.isEmpty()) {
            final Style style = element.asWidget().getElement().getStyle();
            cssPropertiesHelper.readCssValues(properties)
                    .stream().forEach(cssValue -> {
                        String prop = cssValue.getPropertyInCamelCase();
                        String val = cssValue.getValue();
                        style.setProperty(prop, val);
                    });
        }
    }

    public static String buildRowSize(final String value) {
        return ROW_CLASS_PREFIX + value;
    }
}
