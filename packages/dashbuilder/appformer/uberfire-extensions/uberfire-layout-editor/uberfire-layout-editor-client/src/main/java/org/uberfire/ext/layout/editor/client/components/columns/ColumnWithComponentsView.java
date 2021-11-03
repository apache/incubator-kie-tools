/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.columns;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class ColumnWithComponentsView
        implements UberElement<ColumnWithComponents>,
                   ColumnWithComponents.View,
                   IsElement {

    private static final String COL_CSS_CLASS = "col-md-";
    private final int originalLeftRightWidth = 15;
    @Inject
    @DataField
    Div colWithComponents;
    @Inject
    @DataField
    Div row;
    String cssSize = "";
    private ColumnWithComponents presenter;
    @Inject
    @DataField
    private Div content;
    @Inject
    @DataField
    private Div left;
    @Inject
    @DataField
    private Div right;
    @Inject
    private Document document;
    @Inject
    @DataField("inner-col-colwithComponents")
    Div innerCol;

    @Override
    public void init(ColumnWithComponents presenter) {
        this.presenter = presenter;
        setupEvents();
    }

    private void setupEvents() {
        setupLeftEvents();
        setupRightEvents();
        setupOnResize();
    }

    @Override
    public void setupPageLayout() {
        addCSSClass(colWithComponents,
                    "page-col");
        addCSSClass(innerCol,
                    "page-col");
        addCSSClass(row,
                    "page-col");
    }

    private void setupOnResize() {
        document.getBody().setOnresize(event -> calculateWidth());
    }

    public void dockSelectEvent(@Observes UberfireDocksInteractionEvent event) {
        calculateWidth();
    }

    private void setupRightEvents() {
        right.setOndragenter(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                addCSSClass(right,
                            "columnDropPreview");
                addCSSClass(right,
                            "dropPreview");
                addCSSClass(content,
                            "centerPreview");
            }
        });
        right.setOndragleave(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                removeCSSClass(right,
                               "columnDropPreview");
                removeCSSClass(right,
                               "dropPreview");
                removeCSSClass(content,
                               "centerPreview");
            }
        });
        right.setOndrop(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                removeCSSClass(right,
                               "columnDropPreview");
                removeCSSClass(right,
                               "dropPreview");
                removeCSSClass(content,
                               "centerPreview");
                presenter.onDrop(ColumnDrop.Orientation.RIGHT,
                                 extractDndData(e));
            }
        });
        right.setOndragover(e -> {
            e.preventDefault();
        });
        right.setOnmouseover(e -> {
            e.preventDefault();
            if (presenter.canResizeRight()) {
                addCSSClass(right,
                            "colResizeRight");
            } else {
                removeCSSClass(right,
                               "colResizeRight");
            }
        });
        right.setOnmouseout(e -> {
            e.preventDefault();
            if (!presenter.canResizeRight()) {
                removeCSSClass(right,
                               "colResizeRight");
            }
        });
        right.setOnclick(e -> {
            e.preventDefault();
            if (presenter.canResizeRight()) {
                presenter.resizeRight();
            }
        });
    }

    private void setupLeftEvents() {
        left.setOndragenter(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                addCSSClass(left,
                            "columnDropPreview");
                addCSSClass(left,
                            "dropPreview");
                addCSSClass(content,
                            "centerPreview");
            }
        });
        left.setOndragover(e -> e.preventDefault());
        left.setOndragleave(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                removeCSSClass(left,
                               "columnDropPreview");
                removeCSSClass(left,
                               "dropPreview");
                removeCSSClass(content,
                               "centerPreview");
            }
        });
        left.setOndrop(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                removeCSSClass(left,
                               "columnDropPreview");
                removeCSSClass(left,
                               "dropPreview");
                removeCSSClass(content,
                               "centerPreview");
                presenter.onDrop(ColumnDrop.Orientation.LEFT,
                                 extractDndData(e));
            }
        });
        left.setOnmouseover(e -> {
            e.preventDefault();
            if (presenter.canResizeLeft()) {
                addCSSClass(left,
                            "colResizeLeft");
            } else {
                removeCSSClass(left,
                               "colResizeLeft");
            }
        });
        left.setOnmouseout(e -> {
            e.preventDefault();
            if (!presenter.canResizeLeft()) {
                removeCSSClass(left,
                               "colResizeLeft");
            }
        });
        left.setOnclick(e -> {
            e.preventDefault();
            if (presenter.canResizeLeft()) {
                presenter.resizeLeft();
            }
        });
    }

    @Override
    public void setWidth(Integer size) {
        if (hasCssSizeClass()) {
            removeCSSClass(colWithComponents,
                           cssSize);
        }
        cssSize = COL_CSS_CLASS + size;
        addCSSClass(colWithComponents,
                    cssSize);
        addCSSClass(colWithComponents,
                    "container");
    }

    private boolean hasCssSizeClass() {
        return !cssSize.isEmpty() && hasCSSClass(colWithComponents,
                                                 cssSize);
    }

    @Override
    public void addRow(UberElement<Row> view) {
        content.appendChild(view.getElement());
    }

    @Override
    public void clear() {
        removeAllChildren(content);
    }

    public void resizeEventObserver(@Observes ContainerResizeEvent event) {
        calculateWidth();
    }

    @Override
    public void calculateWidth() {

        Scheduler.get().scheduleDeferred(() -> {

            final int colWidth = row.getBoundingClientRect().getWidth().intValue();

            int padding = 2;
            final int contentWidth = colWidth - (originalLeftRightWidth * 2) - padding;

            left.getStyle().setProperty("width",
                                        originalLeftRightWidth + "px");
            right.getStyle().setProperty("width",
                                         originalLeftRightWidth + "px");

            content.getStyle().setProperty("width",
                                           contentWidth + "px");
            presenter.calculateSizeChilds();
        });
    }
}