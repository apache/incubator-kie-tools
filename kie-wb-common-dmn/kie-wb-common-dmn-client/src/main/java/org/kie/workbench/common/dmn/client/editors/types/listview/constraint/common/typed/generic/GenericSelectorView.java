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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class GenericSelectorView implements GenericSelector.View {

    private GenericSelectorView presenter;

    @DataField("generic-input")
    private final HTMLInputElement input;

    private Consumer<BlurEvent> onValueInputBlur;

    @Inject
    public GenericSelectorView(final HTMLInputElement input) {
        this.input = input;
    }

    @Override
    public String getValue() {
        return input.value;
    }

    @Override
    public void setValue(final String value) {
        input.value = value;
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        input.setAttribute("placeholder", placeholder);
    }

    @Override
    public void onValueChanged(final Consumer<Event> onValueChanged) {
        input.onkeyup = event -> {
            onValueChanged.accept(event);
            return this;
        };
    }

    @Override
    public void select() {
        input.select();
    }

    @Override
    public void onValueInputBlur(final Consumer<BlurEvent> blurEvent) {
        this.onValueInputBlur = blurEvent;
    }

    @Override
    public void init(final GenericSelectorView presenter) {
        this.presenter = presenter;
    }

    @EventHandler("generic-input")
    public void onGenericInputBlur(final BlurEvent blurEvent) {

        if (!Objects.isNull(onValueInputBlur)) {
            onValueInputBlur.accept(blurEvent);
        }
    }
}
