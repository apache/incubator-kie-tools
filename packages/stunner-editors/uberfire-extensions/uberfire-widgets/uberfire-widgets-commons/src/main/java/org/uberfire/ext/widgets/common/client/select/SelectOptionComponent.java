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

import java.util.function.Consumer;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElemental;

public class SelectOptionComponent {

    private View view;
    private SelectOption selectOption;
    private Consumer<SelectOption> callback;

    @Inject
    public SelectOptionComponent(final View view) {
        this.view = view;
    }

    public void initialize(SelectOption selectOption,
                           Consumer<SelectOption> callback) {
        this.view.init(this);
        this.selectOption = selectOption;
        this.callback = callback;
        this.view.setName(selectOption.getName());
    }

    public void select() {
        this.callback.accept(selectOption);
    }

    public void activate() {
        this.view.setActive(true);
    }

    public void deactivate() {
        this.view.setActive(false);
    }

    public View getView() {
        return this.view;
    }

    public interface View extends UberElemental<SelectOptionComponent> {

        void setName(String name);

        void setActive(boolean isActive);
    }
}
