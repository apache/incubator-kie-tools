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


package org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup;

import java.text.ParseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.text.shared.Parser;

/*
This class fixes an issue on GWT-BS3 StringRadioGroup. It seems that it doesn't unselect the previous selected radio
when value change
TODO: remove when possible
*/
public abstract class RadioGroupBase<TYPE> extends org.gwtbootstrap3.client.ui.base.RadioGroupBase<TYPE> {

    protected Parser<TYPE> parser;

    public RadioGroupBase(String name, Parser<TYPE> parser) {
        super(name, parser);
        this.parser = parser;
    }

    @Override
    public void setValue(TYPE value,
                         boolean fireEvents) {
        TYPE oldValue = getValue();
        getRadioChildren().forEach(radio -> {
            try {
                if (this.parser.parse(radio.getFormValue()).equals(oldValue)) {
                    radio.setValue(false);
                }
            } catch (ParseException e) {
                radio.setValue(false);
                GWT.log("Error parsing value: " + e.getMessage());
            }
        });
        super.setValue(value,
                       fireEvents);
    }
}
