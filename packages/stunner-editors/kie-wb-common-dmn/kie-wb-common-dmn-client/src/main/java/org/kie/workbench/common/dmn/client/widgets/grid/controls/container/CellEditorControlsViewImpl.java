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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.jboss.errai.common.client.dom.Body;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;

@Templated
@ApplicationScoped
public class CellEditorControlsViewImpl implements CellEditorControlsView {

    private static final String CONTAINER_CLASS = "kie-dmn-cell-editor-controls";

    private static final String BOOTSTRAP_SELECT_CLASS = "bootstrap-select";

    private static final String BOOTSTRAP_SELECT_OPEN_CLASS = "open";

    static final String DMN_CONTAINER_SELECT = "[id^='dmn_container_']";

    static final String PX = "px";

    public static final String LEFT = "left";

    public static final String TOP = "top";

    @DataField("cellEditorControls")
    private Div cellEditorControls;

    @DataField("cellEditorControlsContainer")
    private Div cellEditorControlsContainer;

    private Body body;

    private EventListener hidePopoverHandler = event -> {
        if (!isOverCellEditorControlsContainer(event)) {
            hide();
        }
    };

    public Optional<PopupEditorControls> getActiveEditor() {
        return activeEditor;
    }

    public void setActiveEditor(final Optional<PopupEditorControls> activeEditor) {
        this.activeEditor = activeEditor;
    }

    private Optional<PopupEditorControls> activeEditor = Optional.empty();

    private Optional<ElementWrapperWidget<?>> elementWrapperWidget = Optional.empty();

    public CellEditorControlsViewImpl() {
        //CDI proxy
    }

    @Inject
    public CellEditorControlsViewImpl(final Document document,
                                      final Div cellEditorControls,
                                      final Div cellEditorControlsContainer) {
        this.cellEditorControls = cellEditorControls;
        this.cellEditorControlsContainer = cellEditorControlsContainer;
        this.body = document.getBody();
    }

    @PostConstruct
    public void setup() {
        this.cellEditorControls.setId(DOM.createUniqueId());

        this.elementWrapperWidget = Optional.of(getWidget());
        this.elementWrapperWidget.ifPresent(this::addWidgetToRootPanel);

        this.body.addEventListener(BrowserEvents.MOUSEDOWN,
                                   hidePopoverHandler,
                                   false);
        this.body.addEventListener(BrowserEvents.MOUSEWHEEL,
                                   hidePopoverHandler,
                                   false);
    }

    ElementWrapperWidget<?> getWidget() {
        return ElementWrapperWidget.getWidget(getElement());
    }

    @PreDestroy
    public void destroy() {
        this.elementWrapperWidget.ifPresent(this::removeWidgetFromRootPanel);

        this.body.removeEventListener(BrowserEvents.MOUSEDOWN,
                                      hidePopoverHandler,
                                      false);
        this.body.removeEventListener(BrowserEvents.MOUSEWHEEL,
                                      hidePopoverHandler,
                                      false);
    }

    void addWidgetToRootPanel(final ElementWrapperWidget<?> widget) {
        RootPanel.get().add(widget);
    }

    void removeWidgetFromRootPanel(final ElementWrapperWidget<?> widget) {
        RootPanel.get().remove(widget);
    }

    @Override
    public void show(final PopupEditorControls editor,
                     final int x,
                     final int y) {
        DOMUtil.removeAllChildren(cellEditorControlsContainer);
        cellEditorControlsContainer.appendChild(editor.getElement());

        final CSSStyleDeclaration style = getElement().getStyle();
        style.setProperty(LEFT, x + PX);
        style.setProperty(TOP, y + PX);

        setActiveEditor(Optional.of(editor));

        setOnClosedByKeyboardCallback(editor);

        editor.show();
    }

    void setOnClosedByKeyboardCallback(final PopupEditorControls editor) {
        if (editor instanceof CanBeClosedByKeyboard) {
            ((CanBeClosedByKeyboard) editor).setOnClosedByKeyboardCallback(this::focusOnDMNContainer);
        }
    }

    void removeOnClosedByKeyboardCallback(final PopupEditorControls editor) {
        if (editor instanceof CanBeClosedByKeyboard) {
            ((CanBeClosedByKeyboard) editor).setOnClosedByKeyboardCallback(null);
        }
    }

    void focusOnDMNContainer(final Object o) {
        final Element element = getDMNContainer();
        if (!Objects.isNull(element)) {
            element.focus();
        }
    }

    Element getDMNContainer() {
        return DomGlobal.document.querySelector(DMN_CONTAINER_SELECT);
    }

    @Override
    public void hide() {
        getActiveEditor().ifPresent(PopupEditorControls::hide);
        getActiveEditor().ifPresent(e -> removeOnClosedByKeyboardCallback(e));
        setActiveEditor(Optional.empty());
    }

    private boolean isOverCellEditorControlsContainer(final Event event) {
        HTMLElement e = (HTMLElement) event.getTarget();
        while (e != null) {
            if (isOverCellEditorContainer(e)) {
                return true;
            }
            if (isOverBootstrapSelectContainer(e)) {
                return true;
            }
            e = (HTMLElement) e.getParentElement();
        }
        return false;
    }

    private boolean isOverCellEditorContainer(final HTMLElement e) {
        return e.getClassList().contains(CONTAINER_CLASS);
    }

    // This is a hack to check whether the Event occurred over an 'open' Bootstrap dropdown.
    // The dropdown content is added dynamically to the DOM when it is opened. If the dropdown container is set to the
    // CellEditorControlsView element the content is incorrectly positioned as it does not work in a parent element
    // that is absolutely positioned. If the dropdown container is set to body the CellEditorControlsView is not an
    // ancestor however content positioning works properly.
    private boolean isOverBootstrapSelectContainer(final HTMLElement e) {
        if (!e.getClassList().contains(BOOTSTRAP_SELECT_CLASS)) {
            return false;
        } else if (!e.getClassList().contains(BOOTSTRAP_SELECT_OPEN_CLASS)) {
            return false;
        }
        return true;
    }
}
