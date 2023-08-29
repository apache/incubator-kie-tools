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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorDividerItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorHeaderItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;

import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

@Templated
@Dependent
public class ListSelectorViewImpl implements ListSelectorView {

    static final String OPEN = "open";

    @DataField("items-container")
    private UnorderedList itemsContainer;

    private ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews;
    private ManagedInstance<ListSelectorDividerItemView> listSelectorDividerItemViews;
    private ManagedInstance<ListSelectorHeaderItemView> listSelectorHeaderItemViews;
    private Presenter presenter;

    private Optional<Consumer<CanBeClosedByKeyboard>> closedByKeyboardCallback = Optional.empty();

    public ListSelectorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ListSelectorViewImpl(final UnorderedList itemsContainer,
                                final ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews,
                                final ManagedInstance<ListSelectorDividerItemView> listSelectorDividerItemViews,
                                final ManagedInstance<ListSelectorHeaderItemView> listSelectorHeaderItemViews) {
        this.itemsContainer = itemsContainer;
        this.listSelectorTextItemViews = listSelectorTextItemViews;
        this.listSelectorDividerItemViews = listSelectorDividerItemViews;
        this.listSelectorHeaderItemViews = listSelectorHeaderItemViews;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        registerOnCloseHandler();
    }

    void registerOnCloseHandler() {
        getElement().addEventListener(KEYDOWN, onKeyDown(), false);
    }

    EventListener<Event> onKeyDown() {
        return (event) -> {
            if (isEscape(event)) {
                hide();
                returnFocusToPanel();
            }
        };
    }

    boolean isEscape(final Event event) {
        final KeyboardEvent keyboardEvent = asElemental2Event(event);
        final boolean isEscape = keyboardEvent.key.equals("Esc"); /* IE/Edge specific value */
        final boolean isEsc = keyboardEvent.key.equals("Escape");
        return isEscape || isEsc;
    }

    @Override
    public void setItems(final List<ListSelectorItem> items) {
        DOMUtil.removeAllChildren(itemsContainer);
        items.forEach(item -> makeListSelectorItemView(item).ifPresent(child -> itemsContainer.appendChild(child.getElement())));
    }

    private Optional<IsElement> makeListSelectorItemView(final ListSelectorItem item) {
        Optional<IsElement> listSelectorItemView = Optional.empty();

        if (item instanceof ListSelectorTextItem) {
            final ListSelectorTextItem ti = (ListSelectorTextItem) item;
            final ListSelectorTextItemView selector = listSelectorTextItemViews.get();
            selector.setText(ti.getText());
            selector.setEnabled(ti.isEnabled());
            selector.addClickHandler(() -> {
                if (ti.isEnabled()) {
                    presenter.onItemSelected(item);
                }
            });
            listSelectorItemView = Optional.of(selector);
        } else if (item instanceof ListSelectorDividerItem) {
            listSelectorItemView = Optional.of(listSelectorDividerItemViews.get());
        } else if (item instanceof ListSelectorHeaderItem) {
            final ListSelectorHeaderItem ti = (ListSelectorHeaderItem) item;
            final ListSelectorHeaderItemView selector = listSelectorHeaderItemViews.get();
            selector.setText(ti.getText());
            selector.setIconClass(ti.getIconClass());
            listSelectorItemView = Optional.of(selector);
        }

        return listSelectorItemView;
    }

    @Override
    public void show(final Optional<String> title) {
        getElement().getClassList().add(OPEN);
        schedule(() -> getElement().focus());
    }

    @Override
    public void hide() {
        getElement().getClassList().remove(OPEN);
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        closedByKeyboardCallback = Optional.ofNullable(callback);
    }

    void returnFocusToPanel() {
        closedByKeyboardCallback.ifPresent(c -> c.accept(this));
    }

    void schedule(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }

    KeyboardEvent asElemental2Event(final Event event) {
        return (KeyboardEvent) event;
    }
}
