/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
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
    @DataField
    private HTMLLIElement listGroupItem;

    @Inject
    @DataField
    private HTMLButtonElement categoryIcon;

    @Inject
    @DataField
    private HTMLDivElement floatingPanel;

    @Inject
    @DataField
    private HTMLButtonElement closeCategoryButton;

    @Inject
    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    private double startX = 0;

    private double startY = 0;

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
        categoryIcon.title = (category.getTitle());
        final IsElement glyphElement =
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
        HTMLElement groupHeader = (HTMLElement)DomGlobal.document.createElement("h5");

        groupHeader.textContent = (groupWidget.getItem().getTitle());
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
    public void onMouseDown(@ForEvent("mousedown") MouseEvent event) {
        mouseDown = true;
        startX = event.clientX;
        startY = event.clientY;
    }

    @EventHandler("categoryIcon")
    public void onMouseMove(@ForEvent("mousemove") MouseEvent event) {
        double currentX = event.clientX;
        double currentY = event.clientY;
        if (mouseDown && isDragged(startX,
                startY,
                currentX,
                currentY)) {
            mouseDown = false;
            presenter.onMouseDown(event.clientX,
                    event.clientY,
                    event.x,
                    event.y);
        }
    }

    @EventHandler("categoryIcon")
    public void onMouseUp(@ForEvent("mouseup") MouseEvent event) {
        if (mouseDown) {
            if (isDragged(startX,
                    startY,
                    event.clientX,
                    event.clientY)) {
                mouseDown = false;
                presenter.onMouseDown(event.clientX,
                        event.clientY,
                        event.x,
                        event.y);
            } else {
                mouseDown = false;
                presenter.onOpen();
            }
        }
    }

    @EventHandler("categoryIcon")
    public void onMouseOutEvent(@ForEvent("mouseout") MouseEvent event) {
        mouseDown = false;
    }

    @EventHandler("closeCategoryButton")
    public void onClose(@ForEvent("click") Event event) {
        presenter.onClose();
    }

    @EventHandler("floatingPanel")
    public void onFloatingPanelOutEvent(@ForEvent("mouseout") MouseEvent event) {
        if (isAutoHidePanel()) {
            presenter.onClose();
        }
    }

    private void setCloseButtonVisible(boolean visible) {
        closeCategoryButton.style.removeProperty("display");
        if (!visible) {
            closeCategoryButton.style.setProperty("display", "none");
        }
    }

    private boolean isDragged(double startX,
                              double startY,
                              double endX,
                              double endY) {
        return distance(startX,
                endX) >= DRAG_DELTA || distance(startY,
                endY) >= DRAG_DELTA;
    }

    private double distance(double start,
                            double end) {
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
