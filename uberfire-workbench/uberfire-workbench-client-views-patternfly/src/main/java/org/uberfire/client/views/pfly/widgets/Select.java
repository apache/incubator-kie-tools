/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.OptionsCollection;

import static com.google.common.base.Strings.isNullOrEmpty;

@Dependent
public class Select implements IsElement,
                               TakesValue<String> {

    @Inject
    private Document document;

    @Inject
    private org.jboss.errai.common.client.dom.Select select;

    @Override
    public HTMLElement getElement() {
        return select;
    }

    public void addOption(final String text) {
        addOption(text,
                  text);
    }

    public void addOption(final String text,
                          final String value) {
        addOption(text,
                  value,
                  false);
    }

    public void addOption(final String text,
                          final String value,
                          final Boolean selected) {
        addOption(text,
                  null,
                  value,
                  selected);
    }

    public void addOption(final String text,
                          final String subText,
                          final String value,
                          final Boolean selected) {
        final Option option = (Option) document.createElement("option");
        option.setText(text);
        option.setValue(value);
        option.setSelected(selected);
        if (isNullOrEmpty(subText) == false) {
            option.setAttribute("data-subtext",
                                subText);
        }
        select.add(option);
    }

    public OptionsCollection getOptions() {
        return select.getOptions();
    }

    public void removeAllOptions() {
        removeAllOptions(select);
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(final Consumer<Select> consumer) {
        Scheduler.get().scheduleDeferred(() -> {
            if (consumer != null) {
                consumer.accept(this);
            }
            refreshElement(select);
        });
    }

    @Override
    public String getValue() {
        return select.getValue();
    }

    @Override
    public void setValue(final String value) {
        setValue(select,
                 value);
    }

    public void enable() {
        enable(select);
        refresh();
    }

    public void disable() {
        disable(select);
        refresh();
    }

    public void hide() {
        selectpicker(select, "hide");
    }

    public void show() {
        selectpicker(select, "show");
    }

    public void toggle() {
        selectpicker(select, "toggle");
    }

    public void setTitle(final String title) {
        select.setTitle(title);
    }

    public void setLiveSearch(final Boolean liveSearch){
        select.setAttribute("data-live-search", String.valueOf(liveSearch));
    }

    public void setWidth(final String width){
        select.setAttribute("data-width", width);
    }

    public void init(){
        selectpicker(select);
    }

    private native void refreshElement(final HTMLElement e) /*-{
        $wnd.jQuery(e).selectpicker('refresh');
    }-*/;

    private native void setValue(final HTMLElement e,
                                 final String value) /*-{
        $wnd.jQuery(e).selectpicker('val', value);
    }-*/;

    private native void disable(final HTMLElement e) /*-{
        $wnd.jQuery(e).prop('disabled', true);
    }-*/;

    private native void enable(final HTMLElement e) /*-{
        $wnd.jQuery(e).prop('disabled', false);
    }-*/;

    private native void removeAllOptions(final HTMLElement e) /*-{
        $wnd.jQuery(e).find('option').remove();
    }-*/;

    private native void selectpicker(final HTMLElement e) /*-{
        $wnd.jQuery(e).selectpicker();
    }-*/;

    private native void selectpicker(final HTMLElement e, final String method) /*-{
        $wnd.jQuery(e).selectpicker(method);
    }-*/;

}