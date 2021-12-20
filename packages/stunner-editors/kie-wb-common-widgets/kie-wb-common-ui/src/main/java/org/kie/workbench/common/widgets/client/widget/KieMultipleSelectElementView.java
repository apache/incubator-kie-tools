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
import javax.inject.Inject;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
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
    private void onSelectChanged(final ChangeEvent ignore) {
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

    public native void setValue(final HTMLSelectElement select, final JsArrayString value) /*-{
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

    public native JsArrayString getValue(final HTMLSelectElement select) /*-{
        return $wnd.jQuery(select).val();
    }-*/;

    private native void selectpicker(final HTMLSelectElement select)/*-{
        $wnd.jQuery(select).selectpicker();
    }-*/;
}
