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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DateTimeSelector implements TypedValueSelector {

    private final View view;
    private final DateTimeValueConverter converter;

    @Inject
    public DateTimeSelector(final DateTimeSelector.View view,
                            final DateTimeValueConverter converter) {
        this.view = view;
        this.converter = converter;
    }

    @Override
    public String getValue() {
        final DateTimeValue value = view.getValue();
        return converter.toDMNString(value);
    }

    @Override
    public void setValue(final String value) {
        final DateTimeValue dateTimeValue = converter.fromDMNString(value);
        view.setValue(dateTimeValue);
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        // Empty: This component is composed by TimeSelector and DateSelector which have their own placeholders.
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    @Override
    public void setOnInputChangeCallback(final Consumer<Event> onValueChanged) {
        view.setOnValueChanged(onValueChanged);
    }

    @Override
    public void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur) {
        view.setOnValueInputBlur(onValueInputBlur);
    }

    @Override
    public void select() {
        view.select();
    }

    @Override
    public String toDisplay(final String rawValue) {
        return converter.toDisplay(rawValue);
    }

    public interface View extends UberElemental<DateTimeSelector>,
                                  IsElement {

        DateTimeValue getValue();

        void setValue(final DateTimeValue value);

        void setOnValueChanged(final Consumer<Event> onValueChanged);

        void setOnValueInputBlur(final Consumer<BlurEvent> blurEvent);

        void select();
    }
}
