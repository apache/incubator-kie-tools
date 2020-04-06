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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorDividerItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorHeaderItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.uberfire.client.views.pfly.selectpicker.JQuery;

@Templated
@Dependent
public class ListSelectorViewImpl implements ListSelectorView {

    static final String DROPDOWN_ACTION = "toggle";

    static final String DROPDOWN_SELECTOR = "#dropdown";

    static final String DROPDOWN_TRIGGER_SELECTOR = "#dropdown-trigger";

    static final String DROPDOWN_MENU_SELECTOR = "#dropdown-menu";

    static final String DROPDOWN_HIDDEN_SELECTOR = ":hidden";

    static final String DROPDOWN_HIDDEN_EVENT = "hidden.bs.dropdown";

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
            listSelectorItemView = Optional.of(selector);
        }

        return listSelectorItemView;
    }

    @Override
    public void show(final Optional<String> title) {
        //Schedule programmatic display of dropdown as it has not been attached to the DOM at this point.
        schedule(() -> {
            if (isDropdownMenuHidden()) {
                dropdownHiddenHandler(returnFocusToPanel());
                dropdownTrigger().dropdown(DROPDOWN_ACTION);
            }
        });
    }

    @Override
    public void hide() {
        if (!isDropdownMenuHidden()) {
            dropdownTrigger().dropdown(DROPDOWN_ACTION);
        }
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        closedByKeyboardCallback = Optional.ofNullable(callback);
    }

    void schedule(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }

    boolean isDropdownMenuHidden() {
        return dropdown().find(DROPDOWN_MENU_SELECTOR).is(DROPDOWN_HIDDEN_SELECTOR);
    }

    JQueryDropdownMenu dropdown() {
        return JQueryDropdownMenu.$(DROPDOWN_SELECTOR);
    }

    JQueryDropdownMenu dropdownTrigger() {
        return JQueryDropdownMenu.$(DROPDOWN_TRIGGER_SELECTOR);
    }

    JQuery dropdownHiddenHandler(final JQuery.CallbackFunction function) {
        return JQuery.$(DROPDOWN_SELECTOR).on(DROPDOWN_HIDDEN_EVENT, function);
    }

    JQuery.CallbackFunction returnFocusToPanel() {
        return (event) -> closedByKeyboardCallback.ifPresent(c -> c.accept(this));
    }
}
