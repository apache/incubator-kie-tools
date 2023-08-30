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


package org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox;

import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class IntegerBoxViewImpl extends Composite implements IntegerBoxView {

    private IntegerBox presenter;

    @Inject
    @DataField
    private TextInput input;

    @Override
    public void setPresenter(IntegerBox presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setValue(String value) {
        input.setValue(value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        input.setDisabled(!enabled);
    }

    public void updateValue(Event event) {
        presenter.notifyValueChange(getTextValue());
    }

    @Override
    public String getTextValue() {
        return input.getValue();
    }

    @Override
    public void setId(String id) {
        input.setId(id);
    }

    @Override
    public void setPlaceholder(String placeholder) {
        input.setAttribute("placeholder",
                           placeholder);
    }

    @Override
    public void setMaxLength(int maxLength) {
        input.setMaxLength(maxLength);
    }

    public void onKeyDown(Event event) {

        int key = event.getKeyCode();
        boolean isShiftPressed = event.getShiftKey();

        if (presenter.isInvalidKeyCode(key,
                                       isShiftPressed)) {
            event.stopPropagation();
            event.preventDefault();
        }
    }

    @SinkNative(Event.ONKEYDOWN | Event.ONCHANGE)
    @EventHandler("input")
    public void onEvent(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONCHANGE:
                updateValue(event);
                break;
            case Event.ONKEYDOWN:
                onKeyDown(event);
                break;
            default:
                break;
        }
    }

    @Override
    public HasValue<Long> wrapped() {
        return presenter;
    }
}
