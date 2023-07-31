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

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectorLabelItemView implements SelectorLabelItem.View, IsElement {

    private static final String SELECTED_CLASS = "pf-m-primary";
    private static final String UNSELECTED_CLASS = "pf-m-secondary";

    @Inject
    @DataField
    HTMLButtonElement item;

    SelectorLabelItem presenter;

    @Override
    public void init(SelectorLabelItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setValue(String value) {
        item.textContent = value;
        // setTitle to make the whole value visible on mouse over when selector width is restricted and value is trimmed
        item.title = value;
    }

    @Override
    public void setDescription(String description) {
        item.title = description;
    }

    @Override
    public void setWidth(int percentage) {
        var style = item.style;
        style.setProperty("width", percentage + "%");

        // Labels too long to fit into the button width will be trimmed and ended with "..."
        // Based on https://www.w3schools.com/cssref/css3_pr_text-overflow.asp
        style.setProperty("white-space", "nowrap");
        style.setProperty("overflow", "hidden");
        style.setProperty("text-overflow", "ellipsis");
    }

    @Override
    public void select() {
        item.classList.add(SELECTED_CLASS);
        item.classList.remove(UNSELECTED_CLASS);
    }

    @Override
    public void reset() {
        item.classList.add(UNSELECTED_CLASS);
        item.classList.remove(SELECTED_CLASS);
    }

    @EventHandler("item")
    public void onItemClick(ClickEvent event) {
        presenter.onItemClick();
    }
}
