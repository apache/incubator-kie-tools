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
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class ComponentColumnView
        implements UberElement<ComponentColumn>,
                   ComponentColumn.View,
                   IsElement {

    public static final String COL_CSS_CLASS = "col-md-";
    private final int originalLeftRightWidth = 15;
    String cssSize = "";
    private ComponentColumn presenter;
    @Inject
    @DataField
    private Div col;
    @Inject
    @DataField
    private Div colUp;
    @Inject
    @DataField
    private Div row;
    @Inject
    @DataField
    private Div colDown;
    @Inject
    @DataField
    private Div left;
    @Inject
    @DataField("resize-left")
    private Button resizeLeft;
    @Inject
    @DataField
    private Div right;
    @Inject
    @DataField("resize-right")
    private Button resizeRight;
    @Inject
    @DataField
    private Div content;
    @Inject
    private KebabWidget kebabWidget;
    @Inject
    private Document document;
    private ColumnDrop.Orientation contentDropOrientation;

    @Inject
    private DragHelperComponentColumn helper;

    @Override
    public void init(ComponentColumn presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget() {
        setupEvents();
        setupKebabWidget();
        setupResize();
        setupOnResize();
    }

    private void setupOnResize() {
        document.getBody().setOnresize(event -> calculateSize());
    }

    @Override
    public void setupResize() {
        resizeLeft.getStyle().setProperty("display",
                                          "none");
        resizeRight.getStyle().setProperty("display",
                                           "none");
    }

    public void dockSelectEvent(@Observes UberfireDocksInteractionEvent event) {
        calculateSize();
    }

    private void setupKebabWidget() {
        kebabWidget.init(() -> presenter.remove(),
                         () -> presenter.edit());
    }

    private void setupEvents() {
        setupLeftEvents();
        setupRightEvents();
        setupColUpEvents();
        setupColDownEvents();
        setupContentEvents();
        setupColEvents();
        setupRowEvents();
        setupResizeEvents();
    }

    private void setupRowEvents() {
        row.setOnmouseout(event -> {
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
        });
    }

    private void setupResizeEvents() {
        resizeLeft.setOnclick(event -> presenter.resizeLeft());
        resizeRight.setOnclick(event -> presenter.resizeRight());
    }

    private void setupColEvents() {
        col.setOnmouseup(e -> {
            e.preventDefault();
            if (hasCSSClass(col,
                            "rowDndPreview")) {
                removeCSSClass(col,
                               "rowDndPreview");
            }
        });
        col.setOnmouseover(e -> {
            e.preventDefault();
        });
        col.setOnmouseout(event -> {
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
        });
    }

    private void setupColUpEvents() {

        colUp.setOndragleave(event -> {
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
        });
        colUp.setOndragexit(event -> {
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
        });

        colUp.setOndragover(event -> {
            event.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                contentDropOrientation = ColumnDrop.Orientation.UP;
                addCSSClass(colUp,
                            "componentDropInColumnPreview");
            }
        });
        colUp.setOndrop(e -> {
            if (contentDropOrientation != null) {
                presenter.onDrop(contentDropOrientation,
                                 extractDndData(e));
            }
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
        });
        colUp.setOnmouseout(event -> {
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
        });
    }

    private void setupColDownEvents() {
        colDown.setOndrop(e -> {
            if (contentDropOrientation != null) {
                presenter.onDrop(contentDropOrientation,
                                 extractDndData(e));
            }
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
        });
    }

    private void setupRightEvents() {
        right.setOndragenter(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop() && presenter.enableSideDnD()) {
                addCSSClass(right,
                            "columnDropPreview");
                addCSSClass(right,
                            "dropPreview");
                addCSSClass(content,
                            "centerPreview");
                removeCSSClass(colUp,
                               "componentDropInColumnPreview");
            }
        });
        right.setOndragleave(e -> {
            e.preventDefault();
            removeCSSClass(right,
                           "columnDropPreview");
            removeCSSClass(right,
                           "dropPreview");
            removeCSSClass(content,
                           "centerPreview");
        });
        right.setOndragover(event -> event.preventDefault());
        right.setOndrop(e -> {
            e.preventDefault();
            if (presenter.enableSideDnD() && presenter.shouldPreviewDrop()) {
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
        right.setOnmouseover(e -> {
            e.preventDefault();
            if (presenter.canResizeRight()) {
                resizeRight.getStyle().setProperty("display",
                                                   "block");
            }
        });
        right.setOnmouseout(e -> {
            e.preventDefault();
            if (presenter.canResizeRight()) {
                resizeRight.getStyle().setProperty("display",
                                                   "none");
            }
        });
    }

    private void setupContentEvents() {
        content.setOndragover(e -> {
            e.preventDefault();
            if (presenter.shouldPreviewDrop()) {
                if (dragOverUp(content,
                               (MouseEvent) e)) {
                    addCSSClass(colUp,
                                "componentDropInColumnPreview");
                    removeCSSClass(colDown,
                                   "componentDropInColumnPreview");
                    contentDropOrientation = ColumnDrop.Orientation.UP;
                } else {
                    addCSSClass(colDown,
                                "componentDropInColumnPreview");
                    removeCSSClass(colUp,
                                   "componentDropInColumnPreview");
                    contentDropOrientation = ColumnDrop.Orientation.DOWN;
                }
            }
        });
        content.setOndragleave(e -> {
            e.preventDefault();
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
            contentDropOrientation = null;
        });
        content.setOndrop(e -> {
            e.preventDefault();
            if (contentDropOrientation != null) {
                presenter.onDrop(contentDropOrientation,
                                 extractDndData(e));
            }
            removeCSSClass(colUp,
                           "componentDropInColumnPreview");
            removeCSSClass(colDown,
                           "componentDropInColumnPreview");
        });
        content.setOnmouseout(e -> {
            removeCSSClass(content,
                           "componentMovePreview");
        });
        content.setOnmouseover(e -> {
            e.preventDefault();
            addCSSClass(content,
                        "componentMovePreview");
        });

        content.setOndragend(e -> {
            e.stopPropagation();
            removeCSSClass(row,
                           "rowDndPreview");
            presenter.dragEndComponent();
        });
        content.setOndragstart(e -> {
            e.stopPropagation();
            e.getDataTransfer().setData("text/plain", "this-is-a-requirement-to-firefox-html5dnd");
            addCSSClass(row,
                        "rowDndPreview");
            presenter.dragStartComponent();
        });
    }

    private void setupLeftEvents() {
        left.setOndragleave(e -> {
            e.preventDefault();
            removeCSSClass(left,
                           "columnDropPreview");
            removeCSSClass(left,
                           "dropPreview");
            removeCSSClass(content,
                           "centerPreview");
        });
        left.setOndrop(e -> {
            e.preventDefault();
            if (presenter.enableSideDnD() && presenter.shouldPreviewDrop()) {
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

        left.setOndragover(event -> {
            if (presenter.enableSideDnD() && presenter.shouldPreviewDrop()) {
                event.preventDefault();
            }
        });
        left.setOndragexit(event -> {
            event.preventDefault();
            removeCSSClass(left,
                           "columnDropPreview");
            removeCSSClass(left,
                           "dropPreview");
            removeCSSClass(content,
                           "centerPreview");
        });
        left.setOndragenter(e -> {
            e.preventDefault();
            if (presenter.enableSideDnD() && presenter.shouldPreviewDrop()) {
                addCSSClass(left,
                            "columnDropPreview");
                addCSSClass(left,
                            "dropPreview");
                addCSSClass(content,
                            "centerPreview");
                removeCSSClass(colUp,
                               "componentDropInColumnPreview");
            }
        });
        left.setOnmouseover(e -> {
            e.preventDefault();
            if (presenter.canResizeLeft()) {
                resizeLeft.getStyle().setProperty("display",
                                                  "block");
            }
        });
        left.setOnmouseout(e -> {
            e.preventDefault();
            if (presenter.canResizeLeft()) {
                resizeLeft.getStyle().setProperty("display",
                                                  "none");
            }
        });
    }

    public void resizeEventObserver(@Observes ContainerResizeEvent event) {
        calculateSize();
    }

    @Override
    public void calculateSize() {
        Scheduler.get().scheduleDeferred(() -> {
            controlPadding();
            calculateLeftRightWidth();
            calculateContentWidth();
            addCSSClass(col,
                        "container");
        });
    }

    private void controlPadding() {
        if (!presenter.isInnerColumn()) {
            addCSSClass(col,
                        "no-padding");
        } else {
            if (hasCSSClass(col,
                            "no-padding")) {
                removeCSSClass(col,
                               "no-padding");
            }
        }
    }

    private void calculateLeftRightWidth() {
        if (originalLeftRightWidth >= 0) {
            left.getStyle().setProperty("width",
                                        originalLeftRightWidth + "px");
            right.getStyle().setProperty("width",
                                         originalLeftRightWidth + "px");
        }
    }

    private void calculateContentWidth() {
        int smallSpace = 2;
        final int colWidth = col.getBoundingClientRect().getWidth().intValue();
        final int contentWidth = colWidth - (originalLeftRightWidth * 2) - smallSpace;
        if (contentWidth >= 0) {
            content.getStyle().setProperty("width",
                                           contentWidth + "px");
            colDown.getStyle().setProperty("width",
                                           "100%");
            colUp.getStyle().setProperty("width",
                                         "100%");
        }
    }

    @Override
    public void setSize(String size) {
        if (!col.getClassName().isEmpty()) {
            removeCSSClass(col,
                           cssSize);
        }
        cssSize = COL_CSS_CLASS + size;
        addCSSClass(col,
                    cssSize);
    }

    @Override
    public void clearContent() {
        removeAllChildren(content);
    }

    @Override
    public void setContent() {
        Scheduler.get().scheduleDeferred(() -> {
            removeAllChildren(content);
            HTMLElement previewWidget = getPreviewElement();
            content.appendChild(kebabWidget.getElement());
            content.appendChild(previewWidget);
        });
    }

    @Override
    public void showConfigComponentModal(Command configurationFinish,
                                         Command configurationCanceled) {
        helper.showConfigModal(configurationFinish,
                               configurationCanceled);
    }

    @Override
    public boolean hasModalConfiguration() {
        return helper.hasModalConfiguration();
    }

    @Override
    public void setup(LayoutComponent layoutComponent) {
        helper.setLayoutComponent(layoutComponent);
    }

    private HTMLElement getPreviewElement() {
        HTMLElement previewElement = helper.getPreviewElement(ElementWrapperWidget.getWidget(content));
        previewElement.getStyle().setProperty("cursor",
                                              "default");
        previewElement.setClassName("le-widget");
        return previewElement;
    }

    private boolean hasColPreview(HTMLElement element) {
        return hasCSSClass(element,
                           "componentDropInColumnPreview");
    }

    private boolean dragOverUp(Div div,
                               MouseEvent e) {

        final int top = div.getBoundingClientRect().getTop().intValue();
        final int bottom = div.getBoundingClientRect().getBottom().intValue();

        int dragOverY = e.getClientY();

        return (dragOverY - top) < (bottom - dragOverY);
    }

    public void cleanUp(@Observes DragComponentEndEvent dragComponentEndEvent) {
        removeCSSClass(colUp,
                       "componentDropInColumnPreview");
    }
}