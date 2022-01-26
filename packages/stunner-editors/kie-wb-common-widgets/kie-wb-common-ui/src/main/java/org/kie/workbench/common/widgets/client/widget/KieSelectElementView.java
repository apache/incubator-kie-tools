/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import elemental2.dom.HTMLSelectElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;

@Templated
@Dependent
public class KieSelectElementView implements KieSelectElement.View,
                                             IsElement {

    @Inject
    @DataField("select")
    private HTMLSelectElement select;

    private KieSelectElement presenter;

    @Override
    public void init(final KieSelectElement presenter) {
        this.presenter = presenter;
    }

    @EventHandler("select")
    public void onSelectChanged(@ForEvent("onchange") final Event ignore) {
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
    public void clear() {
        clear(select);
    }

    private void clear(final HTMLSelectElement select) {
        JQuerySelectPicker.$(select).empty().selectpicker("refresh");
    }/*-{
       $wnd.jQuery(select).empty().selectpicker('refresh');
    }-*/;

    @Override
    public void setValue(final String value) {
        setValue(select, value);
    }

    public void setValue(final HTMLSelectElement select, final String value) {
        JQuerySelectPicker.$(select).val(value);
        JQuerySelectPicker.$(select).selectpicker("refresh");
    }/*-{
        $wnd.jQuery(select).val(value);
        $wnd.jQuery(select).selectpicker('refresh');
    }-*/;

    @Override
    public String getValue() {
        return select.value;
    }

    private void selectpicker(final HTMLSelectElement select) {
        JQuerySelectPicker.$(select).selectpicker();

    }/*-{
        $wnd.jQuery(select).selectpicker();
    }-*/;
}
