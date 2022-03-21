/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.contextmenu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;

@Templated
public class ContextMenuView implements ContextMenu.View,
                                        HasListSelectorControl {

    private ContextMenu presenter;

    @DataField("list-selector")
    private ListSelector listSelector;

    @Inject
    public ContextMenuView(final ListSelector listSelector) {
        this.listSelector = listSelector;
    }

    @Override
    public void init(final ContextMenu presenter) {
        this.presenter = presenter;
    }

    @PreDestroy
    private void removeDOMEventListeners() {
        DomGlobal.document.removeEventListener(BrowserEvents.MOUSEDOWN,
                                               hideContextMenuHandler,
                                               false);
        DomGlobal.document.removeEventListener(BrowserEvents.MOUSEWHEEL,
                                               hideContextMenuHandler,
                                               false);
    }

    @Override
    public void show() {
        listSelector.bind(this, 0, 0);
        listSelector.show();

        DomGlobal.document.addEventListener(BrowserEvents.MOUSEDOWN,
                                            hideContextMenuHandler,
                                            false);
        DomGlobal.document.addEventListener(BrowserEvents.MOUSEWHEEL,
                                            hideContextMenuHandler,
                                            false);
    }

    @Override
    public void hide() {
        listSelector.hide();
        removeDOMEventListeners();
    }

    private final EventListener hideContextMenuHandler = event -> {
        if (!getEventPath(event).contains(getElement())) {
            listSelector.hide();
        }
    };

    List<Element> getEventPath(Event event) {
        return Optional
                .ofNullable(event.path)
                .map(JsArrayLike::asList)
                .orElseGet(() -> event.composedPath()
                        .asList()
                        .stream()
                        .filter(e -> e instanceof Element)
                        .map(obj -> (Element) Js.uncheckedCast(obj))
                        .collect(Collectors.toList())
                );
    }

    /**
     * <p>This methods returns all items belonging to list selector instance</p>
     * <strong><p>Note that:</p></strong>
     * <p>Since this class is exploiting ListSelector controls, in the original design, this method accepts two parameters</p>
     * <p>For our purposes, these two parameters are just ignored</p>
     *
     * @param uiRowIndex    unused parameter
     * @param uiColumnIndex unused parameter
     * @return items belonging to the context menu
     */
    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex, final int uiColumnIndex) {
        return presenter.getItems();
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        ((ListSelectorTextItem) item).getCommand().execute();
        hide();
    }
}
