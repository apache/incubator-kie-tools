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

package org.uberfire.experimental.client.editor.group.feature;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLabelElement;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ExperimentalFeatureEditorViewImpl implements ExperimentalFeatureEditorView,
                                                          IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private HTMLLabelElement name;

    @DataField
    @Inject
    private HTMLLabelElement description;

    @Inject
    @DataField
    private ToggleSwitch enabled;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        this.enabled.setSize(SizeType.MINI);
    }

    @Override
    public void render(String name, String description, boolean enabled) {

        this.name.textContent = name;

        if (description != null) {
            this.description.textContent = description;
        }

        this.enabled.setValue(enabled);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled, true);
    }

    private void maybeAddTitle(HTMLElement element) {
        if (element.offsetWidth < element.scrollWidth && element.title.isEmpty()) {
            element.title = element.textContent;
        }
    }

    @SinkNative(Event.ONMOUSEOVER)
    @EventHandler("name")
    public void onLoadName(Event event) {
        maybeAddTitle(name);
    }

    @SinkNative(Event.ONMOUSEOVER)
    @EventHandler("description")
    public void onLoadDescription(Event event) {
        maybeAddTitle(description);
    }

    @EventHandler("enabled")
    public void onToggleChange(ChangeEvent event) {
        presenter.notifyChange(enabled.getValue());
    }
}
