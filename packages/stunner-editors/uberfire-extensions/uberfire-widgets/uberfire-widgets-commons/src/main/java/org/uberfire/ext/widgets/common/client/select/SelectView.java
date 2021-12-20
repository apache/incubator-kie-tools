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

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SelectView implements SelectComponent.View,
                                   IsElement {

    private SelectComponent presenter;

    @Inject
    @Named("span")
    @DataField("selected")
    private HTMLElement selected;

    @Inject
    @DataField("options")
    private HTMLUListElement options;

    @Override
    public void init(SelectComponent presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addOption(SelectOptionComponent selectOptionComponent) {
        this.options.appendChild(selectOptionComponent.getView().getElement());
    }

    @Override
    public void setSelected(String label) {
        this.selected.textContent = label;
    }

    @Override
    public void addOptions(List<SelectOptionComponent> components) {
        components.forEach(this::addOption);
    }

    @Override
    public void clear() {
        this.options.textContent = "";
    }
}
