/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import org.gwtbootstrap3.client.ui.TextBox;

public class LiteralTextBox
        extends TextBox {

    @Override
    public void setText(String text) {
        super.setText(set(text));
    }

    @Override
    public void setValue(String value) {
        super.setValue(set(value));
    }

    String set(String value) {
        value = value.replace("\\\\", "\\");
        value = value.replace("\\\"", "\"");
        return value;
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        super.setValue(set(value), fireEvents);
    }

    @Override
    public String getValue() {
        return get(super.getValue());
    }

    String get(String value) {
        value = value.replace("\\", "\\\\");
        value = value.replace("\"", "\\\"");
        return value;
    }
}
