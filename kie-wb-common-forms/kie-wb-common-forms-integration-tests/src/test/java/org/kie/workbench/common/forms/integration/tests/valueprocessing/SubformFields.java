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

package org.kie.workbench.common.forms.integration.tests.valueprocessing;

import static org.kie.workbench.common.forms.integration.tests.valueprocessing.TestUtils.createDate;

public enum SubformFields implements FormFields {

    CHECKBOX_BINDING("checkbox", true, false, true, true),
    TEXBOX_BINDING("textbox", "Joseph", "John", "Martin", "Joe"),
    TEXTAREA_BINDING("textarea", "Hello\n my\n name\n is Joseph\n", "Hello\n my\n name\n is John\n", "Hello\n my\n name\n is Martin\n", "This\n is\n not\n a joke!\n"),
    INTEGERBOX_BINDING("integerbox", 15, 100, 520, 2),
    DECIMALBOX_BINDING("decimalbox", 1.564, 40.5684, 20.1569, 3.14),
    DATEPICKER_BINDING("datepicker", createDate("06/06/1989"), createDate("17/11/1989"), createDate("11/09/2011"), createDate("06/06/1989")),
    SLIDER_BINDING("slider", 10.0, 26.0, 49.0, 5.0),
    LISTBOX_BINDING("listbox", "2", "2", "3", "2"),
    RADIOGROUP_BINDING("radiogroup", "one", "two", "three", "two");

    private final String binding;
    private final Object
            firstLineValue,
            secondLineValue,
            thirdLineValue,
            subformValue;

    @Override
    public String getBinding() {
        return binding;
    }

    @Override
    public Object getFirstLineValue() {
        return firstLineValue;
    }

    @Override
    public Object getSecondLineValue() {
        return secondLineValue;
    }

    @Override
    public Object getThirdLineValue() {
        return thirdLineValue;
    }

    @Override
    public Object getSubformValue() {
        return subformValue;
    }

    SubformFields(String label, Object firstLineValue, Object secondLineValue, Object thirdLineValue, Object subformValue) {
        this.binding = label;
        this.firstLineValue = firstLineValue;
        this.secondLineValue = secondLineValue;
        this.thirdLineValue = thirdLineValue;
        this.subformValue = subformValue;
    }
}
