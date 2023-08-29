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


package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Templated
@Dependent
public class DefinitionPaletteCategoryWidgetViewImpl implements DefinitionPaletteCategoryWidgetView,
                                                                IsElement {

    private static final String SHOW_FLYOUT_CSS = "kie-palette-show-flyout";

    private static final int DRAG_DELTA = 2;

    @Inject
    private Document document;

    @Inject
    @DataField
    private ListItem listGroupItem;

    @Inject
    @DataField
    private Button categoryIcon;

    @Inject
    @DataField
    private Div floatingPanel;

    @Inject
    @DataField
    private Button closeCategoryButton;

    @Inject
    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    private int startX = 0;

    private int startY = 0;

    private boolean mouseDown = false;

    private boolean autoHidePanel = false;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(Glyph glyph,
                       double width,
                       double height) {
        DefaultPaletteCategory category = presenter.getCategory();
        categoryIcon.setTitle(category.getTitle());
        final org.jboss.errai.common.client.api.IsElement glyphElement =
                domGlyphRenderers.render(glyph,
                                         width,
                                         height);
        categoryIcon.appendChild(glyphElement.getElement());
    }

    @Override
    public void addItem(DefinitionPaletteItemWidget item) {
        floatingPanel.appendChild(item.getElement());
    }

    @Override
    public void addGroup(DefinitionPaletteGroupWidget groupWidget) {
        HTMLElement groupHeader = document.createElement("h5");

        groupHeader.setTextContent(groupWidget.getItem().getTitle());
        floatingPanel.appendChild(groupHeader);

        floatingPanel.appendChild(groupWidget.getElement());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            DOMUtil.addCSSClass(listGroupItem,
                                SHOW_FLYOUT_CSS);
        } else {
            DOMUtil.removeCSSClass(listGroupItem,
                                   SHOW_FLYOUT_CSS);
        }
    }

    @Override
    public void setAutoHidePanel(boolean autoHidePanel) {
        this.autoHidePanel = autoHidePanel;
        setCloseButtonVisible(!autoHidePanel);
    }

    public boolean isAutoHidePanel() {
        return autoHidePanel;
    }

    @Override
    public boolean isVisible() {
        return DOMUtil.hasCSSClass(listGroupItem,
                                   "kie-palette-show-flyout");
    }

    @EventHandler("categoryIcon")
    public void onMouseDown(MouseDownEvent event) {
        mouseDown = true;
        startX = event.getClientX();
        startY = event.getClientY();
    }

    @EventHandler("categoryIcon")
    public void onMouseMove(MouseMoveEvent event) {
        int currentX = event.getClientX();
        int currentY = event.getClientY();
        if (mouseDown && isDragged(startX,
                                   startY,
                                   currentX,
                                   currentY)) {
            mouseDown = false;
            presenter.onMouseDown(event.getClientX(),
                                  event.getClientY(),
                                  event.getX(),
                                  event.getY());
        }
    }

    @EventHandler("categoryIcon")
    public void onMouseUp(MouseUpEvent event) {
        if (mouseDown) {
            if (isDragged(startX,
                          startY,
                          event.getClientX(),
                          event.getClientY())) {
                mouseDown = false;
                presenter.onMouseDown(event.getClientX(),
                                      event.getClientY(),
                                      event.getX(),
                                      event.getY());
            } else {
                mouseDown = false;
                presenter.onOpen();
            }
        }
    }

    @EventHandler("categoryIcon")
    public void onMouseOutEvent(MouseOutEvent event) {
        mouseDown = false;
    }

    @EventHandler("closeCategoryButton")
    public void onClose(ClickEvent event) {
        presenter.onClose();
    }

    @EventHandler("floatingPanel")
    public void onFloatingPanelOutEvent(MouseOutEvent event) {
        if (isAutoHidePanel()) {
            presenter.onClose();
        }
    }

    private void setCloseButtonVisible(boolean visible) {
        closeCategoryButton.getStyle().removeProperty("display");
        if (!visible) {
            closeCategoryButton.getStyle().setProperty("display", "none");
        }
    }

    private boolean isDragged(int startX,
                              int startY,
                              int endX,
                              int endY) {
        return distance(startX,
                        endX) >= DRAG_DELTA || distance(startY,
                                                        endY) >= DRAG_DELTA;
    }

    private int distance(int start,
                         int end) {
        return Math.abs(start - end);
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(listGroupItem);
        DOMUtil.removeAllChildren(categoryIcon);
        DOMUtil.removeAllChildren(floatingPanel);
        DOMUtil.removeAllChildren(closeCategoryButton);
        presenter = null;
    }
}
