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
package org.uberfire.ext.widgets.common.client.dropdown.items;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchSelectorItem;
import org.uberfire.mvp.Command;

@Dependent
public class LiveSearchSelectorDropDownItem<TYPE> implements LiveSearchSelectorItem<TYPE> {

    private LiveSearchSelectorDropDownItemView<TYPE> view;
    private TYPE key;
    private String value;
    private Command selectionCallback;

    @Inject
    public LiveSearchSelectorDropDownItem(LiveSearchSelectorDropDownItemView view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public void init(TYPE key,
                     String value) {

        this.key = key;
        this.value = value;
        view.render(value);
    }

    @Override
    public void setSelectionCallback(Command selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    @Override
    public TYPE getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void onItemClick() {
        if(selectionCallback != null) {
            selectionCallback.execute();
        }
    }

    @Override
    public void select() {
        view.select();
    }

    @Override
    public void reset() {
        view.reset();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void setMultipleSelection(boolean enable) {
        view.setSelectionIconVisible(enable);
        view.setMultiSelect(enable);
    }
}
