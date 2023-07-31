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
package org.dashbuilder.renderer.client.selector;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.CSSProperties.OpacityUnionType;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectorDropDownItemView implements SelectorDropDownItem.View, IsElement {

    private static final String SHOW_ICON_CLASS = "pf-v5-c-menu__item.pf-m-selected";

    @Inject
    @DataField
    HTMLLIElement item;

    @Inject
    @DataField
    HTMLButtonElement itemButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement itemText;

    @Inject
    @DataField
    @Named("span")
    HTMLElement itemIcon;

    SelectorDropDownItem presenter;
    boolean iconVisible = true;

    @Override
    public void init(SelectorDropDownItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setValue(String value) {
        itemText.textContent = value;
    }

    @Override
    public void setDescription(String description) {
        item.title = description;
    }

    @Override
    public void select() {
        showIcon();
    }

    @Override
    public void reset() {
        hideIcon();
    }

    @Override
    public void setSelectionIconVisible(boolean visible) {
        if (visible) {
            showIcon();
        } else {
            hideIcon();
        }
    }

    @EventHandler("itemButton")
    public void onItemClick(ClickEvent event) {
        presenter.onItemClick();
    }

    private void hideIcon() {
        itemIcon.style.opacity = OpacityUnionType.of(0);
    }

    private void showIcon() {
        itemIcon.style.opacity = OpacityUnionType.of(1);
    }
}
