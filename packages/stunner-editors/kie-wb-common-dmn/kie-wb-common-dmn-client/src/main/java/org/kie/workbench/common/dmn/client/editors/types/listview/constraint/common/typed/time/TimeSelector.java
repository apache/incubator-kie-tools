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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TimeSelector implements TypedValueSelector {

    private final View view;
    private final TimeValueFormatter formatter;

    @Inject
    public TimeSelector(final View view,
                        final TimeValueFormatter formatter) {
        this.view = view;
        this.formatter = formatter;
    }

    @Override
    public String getValue() {
        return view.getValue();
    }

    @Override
    public void setValue(final String value) {
        view.setValue(value);
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        view.setPlaceholder(placeholder);
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    @Override
    public void setOnInputChangeCallback(final Consumer<Event> onValueChanged) {
        view.setOnInputChangeCallback(onValueChanged);
    }

    @Override
    public void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur) {
        view.setOnInputBlurCallback(onValueInputBlur);
    }

    @Override
    public void select() {
        view.select();
    }

    @Override
    public String toDisplay(final String rawValue) {
        return formatter.toDisplay(rawValue);
    }

    public boolean isChild(final Object element) {
        return view.isChildOfView(element);
    }

    public interface View extends UberElemental<TimeSelectorView>,
                                  IsElement {

        String getValue();

        void setValue(final String value);

        void setPlaceholder(final String placeholder);

        void setOnInputChangeCallback(final Consumer<Event> onValueChanged);

        void select();

        void setOnInputBlurCallback(final Consumer<BlurEvent> blurEvent);

        boolean isChildOfView(final Object element);
    }
}
