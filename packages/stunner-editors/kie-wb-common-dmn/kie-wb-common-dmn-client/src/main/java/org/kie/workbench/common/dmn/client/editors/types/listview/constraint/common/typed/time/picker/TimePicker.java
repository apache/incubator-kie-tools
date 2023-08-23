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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.FocusEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.views.pfly.selectpicker.ElementHelper;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
public class TimePicker {

    static final String TIME_FORMAT = "HH:mm:ss";

    private final View view;

    private HTMLInputElement inputBind;

    private Element.OnblurFn previousCallback;

    private Consumer<Moment> onDateChanged;

    @Inject
    public TimePicker(final View view) {
        this.view = view;
    }

    public void bind(final HTMLInputElement input) {

        this.inputBind = input;

        final HTMLElement viewElement = view.getElement();
        ElementHelper.insertAfter(viewElement, input);

        input.onclick = this::inputOnClick;
        previousCallback = input.onblur;
        input.onblur = this::inputOnBlur;

        viewElement.scrollTop = input.scrollTop;
        viewElement.scrollLeft = input.scrollLeft;

        view.setOnBlur(this::onViewElementBlur);
        view.setOnDateChanged(this::onDateChanged);
    }

    HTMLInputElement getInputBind() {
        return this.inputBind;
    }

    void refreshDateInPopup() {

        if (isDateSetInInput()) {
            final Moment moment = getDateInInput();
            if (moment.isValid()) {
                view.setDate(moment);
            }
        } else {
            setDefaultData();
        }
    }

    Moment getDateInInput() {
        return moment(getInputBind().value, TIME_FORMAT);
    }

    boolean isDateSetInInput() {
        return !StringUtils.isEmpty(getInputBind().value)
                && !StringUtils.isEmpty(getInputBind().value.trim())
                && getInputBind().value.contains(":");
    }

    void setDefaultData() {
        final Moment now = moment();
        view.setDate(now);
    }

    void onDateChanged(final Moment nativeDate) {

        getInputBind().value = nativeDate.format(TIME_FORMAT);
        if (!Objects.isNull(onDateChanged)) {
            onDateChanged.accept(nativeDate);
        }
    }

    public void setOnDateChanged(final Consumer<Moment> onDateChanged) {
        this.onDateChanged = onDateChanged;
    }

    Object inputOnClick(final Event event) {
        refreshDateInPopup();
        view.getElement().style.top = $(inputBind).offset().top - 5 + "px";
        HiddenHelper.show(view.getElement());
        return this;
    }

    private Object onViewElementBlur(final Event event) {

        final FocusEvent focusEvent = (FocusEvent) event;

        onBlur(focusEvent, getInputBind());

        return this;
    }

    private Object inputOnBlur(final Event event) {

        final FocusEvent focusEvent = (FocusEvent) event;

        onBlur(focusEvent, view.getElement());

        return this;
    }

    private void onBlur(final FocusEvent focusEvent, final HTMLElement targetElement) {

        if (!Objects.equals(focusEvent.relatedTarget, targetElement)
                && !isChildOfView((Element) focusEvent.relatedTarget)) {

            HiddenHelper.hide(view.getElement());
            if (!Objects.isNull(previousCallback)) {
                previousCallback.onInvoke(focusEvent);
            }
        }
    }

    boolean isChildOfView(final Element element) {

        final Element viewElement = view.getElement();
        return viewElement.contains(element);
    }

    public String getValue() {

        final Moment currentDate = view.getDate();
        if (Objects.isNull(currentDate)) {
            return "";
        }

        return currentDate.format(TIME_FORMAT);
    }

    public void setValue(final String value) {

        final Moment moment = moment(value, TIME_FORMAT);
        if (moment.isValid()) {
            view.setDate(moment);
        }
    }

    public interface View extends UberElemental<TimePickerView>,
                                  IsElement {

        void setDate(final Moment moment);

        void setOnDateChanged(final Consumer<Moment> onDateChanged);

        Moment getDate();

        void setOnBlur(final Consumer<Event> onViewElementBlur);
    }
}
