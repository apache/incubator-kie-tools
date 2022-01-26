/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.widgets.common.client.select;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLLIElement;

@Templated
@Dependent
public class SelectOptionView implements SelectOptionComponent.View,
                                         IsElement {

    @Inject
    @DataField("selector")
    private HTMLLIElement selector;

    @Inject
    @DataField("option")
    private HTMLAnchorElement option;

    private SelectOptionComponent presenter;

    @Override
    public void init(SelectOptionComponent presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(String name) {
        this.option.textContent = name;
    }

    @Override
    public void setActive(boolean isActive) {
        this.selector.classList.remove("selected");
        if (isActive) {
            this.selector.classList.add("selected");
        }
    }

    @EventHandler("option")
    public void onClick(@ForEvent("click") Event clickEvent) {
        this.presenter.select();
    }
}
