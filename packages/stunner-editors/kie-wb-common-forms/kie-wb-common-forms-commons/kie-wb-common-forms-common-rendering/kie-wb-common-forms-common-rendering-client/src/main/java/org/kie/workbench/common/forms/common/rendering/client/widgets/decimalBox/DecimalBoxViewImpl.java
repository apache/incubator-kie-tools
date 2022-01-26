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
 */

package org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;

@Templated
@Dependent
public class DecimalBoxViewImpl extends Composite implements DecimalBoxView {

    private DecimalBox presenter;

    @Inject
    @DataField
    private HTMLInputElement input;

    @PostConstruct
    protected void init() {
        input.type = "text";
    }

    @Override
    public void setPresenter(DecimalBox presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setValue(String value) {
        input.value = (value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        input.disabled = (!enabled);
    }

    public void updateValue(Event event) {
        presenter.notifyValueChange(getTextValue());
    }

    @Override
    public String getTextValue() {
        return input.value;
    }

    @Override
    public void setId(String id) {
        input.id = (id);
    }

    @Override
    public void setPlaceholder(String placeHolder) {
        input.setAttribute("placeholder",
                           placeHolder);
    }

    @Override
    public void setMaxLength(Integer maxLength) {
        input.maxLength = (maxLength);
    }

    public void onKeyDown(KeyboardEvent event) {

        int key = Integer.parseInt(event.code);
        boolean isShiftPressed = event.shiftKey;

        if (presenter.isInvalidKeyCode(key,
                                       isShiftPressed)) {
            event.stopPropagation();
            event.preventDefault();
        }
    }

    //@SinkNative(Event.ONKEYDOWN | Event.ONCHANGE)
    @EventHandler("input")
    public void onEvent(@ForEvent({"keydown","change"}) KeyboardEvent event) {
        switch (event.code) {
            case "change":
                updateValue(event);
                break;
            case "keydown":
                onKeyDown(event);
                break;
            default:
                break;
        }
    }

    @Override
    public HasValue<Double> wrapped() {
        return presenter;
    }
}
