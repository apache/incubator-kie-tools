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

package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Templated
@Dependent
public class NoItemsComponentViewImpl implements NoItemsComponentView,
                                                 IsElement {
    @Inject
    @DataField
    private HTMLDivElement container;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement message;

    @Override
    public void setMessage(String msg) {
        message.textContent = (msg);
    }

    @Override
    public void hide() {
        container.style.display = "none";
    }

    @Override
    public void show() {
        container.style.display = "inline";
    }
}
