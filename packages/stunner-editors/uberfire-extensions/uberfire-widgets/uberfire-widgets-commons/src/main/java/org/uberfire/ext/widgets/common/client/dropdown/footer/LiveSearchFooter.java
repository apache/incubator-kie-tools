/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.dropdown.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.widgets.common.client.dropdown.InlineCreationEditor;
import org.uberfire.mvp.Command;

@Dependent
public class LiveSearchFooter implements LiveSearchFooterView.Presenter,
                                         IsElement {

    private LiveSearchFooterView view;

    private Command onNewEntry;
    private Command onReset;

    @Inject
    public LiveSearchFooter(LiveSearchFooterView view) {
        this.view = view;
        view.init(this);
    }

    public void init(Command onNewEntry, Command onReset) {
        this.onNewEntry = onNewEntry;
        this.onReset = onReset;
    }

    public void showEditor(InlineCreationEditor editor) {
        view.show(editor.getElement());
    }

    public void showReset(boolean show) {
        view.showReset(show);
    }

    public void showAddNewEntry(boolean show) {
        view.showAddNewEntry(show);
    }

    @Override
    public void onNewEntryPressed() {
        onNewEntry.execute();
    }

    @Override
    public void onResetPressed() {
        onReset.execute();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void restore() {
        view.restore();
    }

    public void setResetLabel(String resetLabel) {
        view.setResetLabel(resetLabel);
    }

    public void setNewEntryLabel(String newEntryLabel) {
        view.setNewEntryLabel(newEntryLabel);
    }
}
