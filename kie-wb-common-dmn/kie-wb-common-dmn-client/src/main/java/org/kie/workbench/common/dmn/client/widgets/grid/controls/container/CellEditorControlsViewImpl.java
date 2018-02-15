/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
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
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;

@Templated
@ApplicationScoped
public class CellEditorControlsViewImpl implements CellEditorControlsView {

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

    private Optional<HasCellEditorControls.Editor> activeEditor = Optional.empty();

    private Presenter presenter;

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

        RootPanel.get().add(ElementWrapperWidget.getWidget(getElement()));

        this.body.addEventListener(BrowserEvents.MOUSEDOWN,
                                   hidePopoverHandler,
                                   false);
        this.body.addEventListener(BrowserEvents.MOUSEWHEEL,
                                   hidePopoverHandler,
                                   false);
    }

    @PreDestroy
    public void destroy() {
        this.body.removeEventListener(BrowserEvents.MOUSEDOWN,
                                      hidePopoverHandler,
                                      false);
        this.body.removeEventListener(BrowserEvents.MOUSEWHEEL,
                                      hidePopoverHandler,
                                      false);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(final HasCellEditorControls.Editor editor,
                     final int x,
                     final int y) {
        DOMUtil.removeAllChildren(cellEditorControlsContainer);
        cellEditorControlsContainer.appendChild(editor.getElement());

        final int tx = presenter.getTransformedX(x);
        final int ty = presenter.getTransformedY(y);
        final CSSStyleDeclaration style = getElement().getStyle();
        style.setProperty("left", tx + "px");
        style.setProperty("top", ty + "px");

        activeEditor = Optional.of(editor);

        editor.show();
    }

    @Override
    public void hide() {
        activeEditor.ifPresent(HasCellEditorControls.Editor::hide);
        activeEditor = Optional.empty();
    }

    private boolean isOverCellEditorControlsContainer(final Event event) {
        HTMLElement e = (HTMLElement) event.getTarget();
        while (e != null) {
            if (e.getClassList().contains("kie-dmn-cell-editor-controls")) {
                return true;
            }
            e = (HTMLElement) e.getParentElement();
        }
        return false;
    }
}
