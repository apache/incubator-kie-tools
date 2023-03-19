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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class LiveSearchSelectorDropDownItemViewImpl<TYPE> implements LiveSearchSelectorDropDownItemView<TYPE>,
                                                                     IsElement {

    private final static String ICON_VISIBLE_CLASSNAME = "appformer-live-search-selector-dditem-icon-visible";
    private final static String ICON_HIDDEN_CLASSNAME = "appformer-live-search-selector-dditem-icon-hidden";

    @Inject
    @DataField
    private ListItem item;

    @Inject
    @DataField
    private Anchor itemAnchor;

    @Inject
    @DataField
    private Span itemText;

    @Inject
    @DataField
    private Span itemIcon;

    private LiveSearchSelectorDropDownItem presenter;

    private boolean iconVisible = false;

    private boolean multiSelect = false;

    @Override
    public void render(String value) {
        itemText.setTextContent(value);
    }

    @Override
    public void init(LiveSearchSelectorDropDownItem presenter) {
        this.presenter = presenter;
        this.reset();
    }

    @Override
    public void select() {
        item.setClassName("appformer-live-search-selector-dditem selected");
        if (iconVisible) {
            DOMUtil.removeCSSClass(itemIcon, ICON_HIDDEN_CLASSNAME);
            DOMUtil.addCSSClass(itemIcon, ICON_VISIBLE_CLASSNAME);
        }
    }

    @Override
    public void reset() {
        item.setClassName("appformer-live-search-selector-dditem");
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

    private void onItemSelected(DomEvent event) {
        presenter.onItemClick();

        if (multiSelect) {
            event.stopPropagation();
        }
    }

    @EventHandler("itemAnchor")
    public void onItemClick(ClickEvent event) {
        onItemSelected(event);
    }

    @EventHandler("itemAnchor")
    void onEnterKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            onItemSelected(event);
        }
    }
}