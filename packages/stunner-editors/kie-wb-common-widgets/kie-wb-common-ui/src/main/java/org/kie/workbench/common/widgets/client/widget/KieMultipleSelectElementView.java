/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import jsinterop.base.Js;
import org.gwtproject.core.client.JsArrayString;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLSelectElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuery;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;

import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

@Templated
@Dependent
public class KieMultipleSelectElementView implements KieMultipleSelectElement.View,
                                                     IsElement {

    @Inject
    @DataField("root")
    private HTMLDivElement root;

    @Inject
    @DataField("select")
    private HTMLSelectElement select;

    private KieMultipleSelectElement presenter;

    @Override
    public void init(final KieMultipleSelectElement presenter) {
        this.presenter = presenter;
    }

    @EventHandler("select")
    public void onSelectChanged(@ForEvent("onchange")final Event ignore) {
        presenter.onChange();
    }

    @Override
    public HTMLSelectElement getSelect() {
        return select;
    }

    public void initSelect() {
        selectpicker(select);
    }

    @Override
    public void setValue(final List<String> value) {
        setValue(select, toJsArray(value));
    }

    public void setValue(final HTMLSelectElement select, final JsArrayString value) {
        JQuerySelectPicker.$(select).val(value);
        JQuerySelectPicker.$(select).selectpicker("refresh");

    }/*-{
        $wnd.jQuery(select).val(value);
        $wnd.jQuery(select).selectpicker('refresh');
    }-*/;

    private JsArrayString toJsArray(final List<String> list) {
        JsArrayString jsArrayString = JsArrayString.createArray().cast();
        list.forEach(jsArrayString::push);
        return jsArrayString;
    }

    @Override
    public List<String> getValue() {
        final List<String> value = new ArrayList<>();
        final JsArrayString jsValue = getValue(select);

        if (jsValue != null) {
            for (int i = 0; i < jsValue.length(); i++) {
                value.add(jsValue.get(i));
            }
        }

        return value;
    }

    public JsArrayString getValue(final HTMLSelectElement select) {
        return Js.uncheckedCast(JQuerySelectPicker.$(select).val());

    }/*-{
        return $wnd.jQuery(select).val();
    }-*/;

    private void selectpicker(final HTMLSelectElement select) {
        JQuerySelectPicker.$(select).selectpicker();


    }/*-{
        $wnd.jQuery(select).selectpicker();
    }-*/;
}
