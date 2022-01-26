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

import elemental2.dom.Document;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOptionsCollection;
import elemental2.dom.HTMLSelectElement;
import io.crysknife.client.IsElement;
import org.gwtbootstrap3.client.shared.js.JQuery;
import org.gwtbootstrap3.extras.select.client.ui.SelectPicker;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.TakesValue;

@Dependent
public class Select implements IsElement,
                               TakesValue<String> {

    private Document document = DomGlobal.document;

    @Inject
    private HTMLSelectElement select;

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
        final HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.text = (text);
        option.value = (value);
        option.selected = (selected);
        if (isNullOrEmpty(subText) == false) {
            option.setAttribute("data-subtext",
                                subText);
        }
        select.add(option);
    }

    private boolean isNullOrEmpty(String subText) {
        return subText == null || subText.isEmpty();
    }

    public HTMLOptionsCollection getOptions() {
        return select.options;
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
        return select.value;
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
        select.title = (title);
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

    private void refreshElement(final HTMLElement e) {
        SelectPicker.jQuery(e).selectpicker("refresh");
    }/*-{
        $wnd.jQuery(e).selectpicker('refresh');
    }-*/;

    private void setValue(final HTMLElement e,
                                 final String value) {
        SelectPicker.jQuery(e).selectpicker("val", value);

    }/*-{
        $wnd.jQuery(e).selectpicker('val', value);
    }-*/;

    private void disable(final HTMLElement e) {
        SelectPicker.jQuery(e).prop("disabled", true);

    }/*-{
        $wnd.jQuery(e).prop('disabled', true);
    }-*/;

    private void enable(final HTMLElement e) {
        SelectPicker.jQuery(e).prop("disabled", false);

    }/*-{
        $wnd.jQuery(e).prop('disabled', false);
    }-*/;

    private void removeAllOptions(final HTMLElement e) {
        SelectPicker.jQuery(e).find("option").remove();

    }/*-{
        $wnd.jQuery(e).find('option').remove();
    }-*/;

    private void selectpicker(final HTMLElement e) {
        SelectPicker.jQuery(e).selectpicker();

    }/*-{
        $wnd.jQuery(e).selectpicker();
    }-*/;

    private void selectpicker(final HTMLElement e, final String method) {
        SelectPicker.jQuery(e).selectpicker(method);

    }/*-{
        $wnd.jQuery(e).selectpicker(method);
    }-*/;

}