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
package org.kie.workbench.common.widgets.metadata.client.widget;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;

public class ExternalLinkPresenter
        implements IsWidget {

    private ExternalLinkView view;
    private Callback<String> callback;

    @Inject
    public ExternalLinkPresenter(final ExternalLinkView view) {
        this.view = view;
        this.view.init(this);
    }

    public void onEdit() {
        showEdit();
    }

    private void showEdit() {
        view.setLinkModeVisibility(false);
        view.setEditModeVisibility(true);
    }

    private void showLink() {
        view.setLinkModeVisibility(true);
        view.setEditModeVisibility(false);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setLink(final String text) {
        if (text == null || text.trim()
                .isEmpty()) {
            view.setText("");
            showEdit();
        } else {
            showLink();
            view.setText(text);
            view.setLink(text);
        }
    }

    public void addChangeCallback(final Callback<String> callback) {
        this.callback = callback;
    }

    public void onTextChange(final String text) {
        if (callback == null) {
            throw new IllegalStateException("Callback is not set.");
        }
        callback.callback(text);
    }

    public void onTextChangeDone() {
        view.setLink(view.getTextBoxText());
        showLink();
    }
}
