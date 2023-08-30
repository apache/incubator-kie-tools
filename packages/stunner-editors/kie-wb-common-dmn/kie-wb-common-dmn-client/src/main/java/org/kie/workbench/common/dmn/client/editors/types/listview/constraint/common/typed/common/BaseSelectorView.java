/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class BaseSelectorView implements BaseSelector.View {

    private BaseSelector presenter;

    @DataField("generic-input")
    private final HTMLInputElement input;

    private Consumer<BlurEvent> onValueInputBlur;

    @Inject
    public BaseSelectorView(final HTMLInputElement input) {
        this.input = input;
    }

    @Override
    public void init(final BaseSelector presenter) {
        this.presenter = presenter;
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
    public void setOnInputChangeCallback(final Consumer<Event> onValueChangeConsumer) {
        input.onkeyup = event -> consume(event, onValueChangeConsumer);
        input.onchange = event -> consume(event, onValueChangeConsumer);
    }

    @Override
    public void select() {
        input.select();
    }

    @Override
    public void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur) {
        this.onValueInputBlur = onValueInputBlur;
    }

    @Override
    public void setInputType(final String type) {
        input.setAttribute("type", type);
    }

    @EventHandler("generic-input")
    void onGenericInputBlur(final BlurEvent blurEvent) {
        getOnValueInputBlur().ifPresent(consumer -> consumer.accept(blurEvent));
    }

    Optional<Consumer<BlurEvent>> getOnValueInputBlur() {
        return Optional.ofNullable(onValueInputBlur);
    }

    private boolean consume(final Event event,
                            final Consumer<Event> onValueChange) {
        onValueChange.accept(event);
        return true;
    }
}
