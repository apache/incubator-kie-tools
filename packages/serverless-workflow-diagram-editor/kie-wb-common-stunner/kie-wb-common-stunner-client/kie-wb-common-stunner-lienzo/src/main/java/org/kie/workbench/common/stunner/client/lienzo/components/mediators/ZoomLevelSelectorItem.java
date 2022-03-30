/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class ZoomLevelSelectorItem implements IsElement {

    static final String ITEM_CLASS_NAME = "zoom-selector-item";
    static final String ITEM_SELECTED = "selected";

    @Inject
    @DataField
    HTMLLIElement levelItem;

    @Inject
    @DataField
    HTMLAnchorElement levelItemAnchor;

    @Inject
    @DataField
    @Named("span")
    HTMLElement levelItemText;

    private Command onClick;

    public ZoomLevelSelectorItem setText(final String value) {
        levelItemText.textContent = (value);
        return this;
    }

    public ZoomLevelSelectorItem setOnClick(final Command onClick) {
        this.onClick = onClick;
        return this;
    }

    public void select() {
        levelItem.className = (ITEM_CLASS_NAME + " " + ITEM_SELECTED);
    }

    public void reset() {
        levelItem.className = (ITEM_CLASS_NAME);
    }

    @EventHandler("levelItemAnchor")
    void onLevelItemClick(@ForEvent("click") Event event) {
        onClick.execute();
    }

    @PreDestroy
    public void destroy() {
        onClick = null;
    }
}
