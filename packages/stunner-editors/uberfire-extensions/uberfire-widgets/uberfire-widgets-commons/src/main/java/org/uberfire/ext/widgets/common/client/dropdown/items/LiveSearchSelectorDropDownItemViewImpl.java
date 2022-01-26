/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.dropdown.items;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.*;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.event.dom.client.KeyCodes;
import org.jboss.errai.common.client.dom.DOMUtil;

@Templated
@Dependent
public class LiveSearchSelectorDropDownItemViewImpl<TYPE> implements LiveSearchSelectorDropDownItemView<TYPE>,
                                                                     IsElement {

    private final static String ICON_VISIBLE_CLASSNAME = "appformer-live-search-selector-dditem-icon-visible";
    private final static String ICON_HIDDEN_CLASSNAME = "appformer-live-search-selector-dditem-icon-hidden";

    @Inject
    @DataField
    private HTMLLIElement item;

    @Inject
    @DataField
    private HTMLAnchorElement itemAnchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement itemText;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement itemIcon;

    private LiveSearchSelectorDropDownItem presenter;

    private boolean iconVisible = false;

    private boolean multiSelect = false;

    @Override
    public void render(String value) {
        itemText.textContent = (value);
    }

    @Override
    public void init(LiveSearchSelectorDropDownItem presenter) {
        this.presenter = presenter;
        this.reset();
    }

    @Override
    public void select() {
        item.className = ("appformer-live-search-selector-dditem selected");
        if (iconVisible) {
            DOMUtil.removeCSSClass(itemIcon, ICON_HIDDEN_CLASSNAME);
            DOMUtil.addCSSClass(itemIcon, ICON_VISIBLE_CLASSNAME);
        }
    }

    @Override
    public void reset() {
        item.className = ("appformer-live-search-selector-dditem");
        DOMUtil.removeCSSClass(itemIcon, ICON_VISIBLE_CLASSNAME);
        DOMUtil.addCSSClass(itemIcon, ICON_HIDDEN_CLASSNAME);
    }

    @Override
    public void setSelectionIconVisible(boolean visible) {
        iconVisible = visible;
        if (!iconVisible) {
            DOMUtil.removeCSSClass(itemIcon, ICON_VISIBLE_CLASSNAME);
            DOMUtil.addCSSClass(itemIcon, ICON_HIDDEN_CLASSNAME);
        }
    }

    @Override
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    private void onItemSelected(Event event) {
        presenter.onItemClick();

        if (multiSelect) {
            event.stopPropagation();
        }
    }

    @EventHandler("itemAnchor")
    public void onItemClick(@ForEvent("click")Event event) {
        onItemSelected(event);
    }

    @EventHandler("itemAnchor")
    void onEnterKeyDown(@ForEvent("keydown") KeyboardEvent event) {
        if (event.code.equals(KeyCodes.KEY_ENTER)) {
            onItemSelected(event);
        }
    }
}