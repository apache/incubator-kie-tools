/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.persistence.properties;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class PropertiesItemView implements PropertiesItemPresenter.View,
                                           IsElement {

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @DataField("value")
    private HTMLInputElement value;

    private PropertiesItemPresenter presenter;

    @Override
    public void init(final PropertiesItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemove(final ClickEvent ignore) {
        presenter.remove();
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setValue(final String value) {
        this.value.value = value;
    }

    @EventHandler("value")
    public void onValueChange(final @ForEvent("change") Event event) {
        presenter.onValueChange(value.value);
    }

    @EventHandler("name")
    public void onNameChange(final @ForEvent("change") Event event) {
        presenter.onNameChange(name.value);
    }
}
