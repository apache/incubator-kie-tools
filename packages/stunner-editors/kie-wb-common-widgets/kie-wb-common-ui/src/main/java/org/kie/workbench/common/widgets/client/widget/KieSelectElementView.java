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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
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
    public void clear() {
        clear(select);
    }

    private native void clear(final HTMLSelectElement select)/*-{
       $wnd.jQuery(select).empty().selectpicker('refresh');
    }-*/;

    @Override
    public void setValue(final String value) {
        setValue(select, value);
    }

    public native void setValue(final HTMLSelectElement select, final String value) /*-{
        $wnd.jQuery(select).val(value);
        $wnd.jQuery(select).selectpicker('refresh');
    }-*/;

    @Override
    public String getValue() {
        return select.value;
    }

    private native void selectpicker(final HTMLSelectElement select)/*-{
        $wnd.jQuery(select).selectpicker();
    }-*/;
}
