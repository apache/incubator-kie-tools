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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class SmallSwitchComponent {

    private final View view;

    @Inject
    public SmallSwitchComponent(final View view) {
        this.view = view;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public boolean getValue() {
        return view.getValue();
    }

    public void setValue(final boolean value) {
        view.setValue(value);
    }

    public void setOnValueChanged(final Consumer<Boolean> onValueChanged) {
        view.setOnValueChanged(onValueChanged);
    }

    public interface View extends UberElemental<SmallSwitchComponent>,
                                  IsElement {

        boolean getValue();

        void setValue(final boolean value);

        void setOnValueChanged(final Consumer<Boolean> onValueChanged);
    }
}
