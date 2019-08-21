/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.testscenario.client.page.settings;

import java.util.Objects;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ListBox;

public class ScorecardSelectorViewImpl
        implements ScorecardSelectorView {

    private ListBox listBox = new ListBox();

    @Override
    public void init(final ScorecardSelector presenter) {
        listBox.setWidth("202px");
        listBox.addChangeHandler(event -> presenter.onValueSelected(listBox.getSelectedValue()));
    }

    @Override
    public void setSelected(final String modelName) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (Objects.equals(listBox.getItemText(i), modelName)) {
                listBox.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public void hide() {
        listBox.setVisible(false);
    }

    @Override
    public Widget asWidget() {
        return listBox;
    }

    @Override
    public void add(final String modelName) {
        listBox.addItem(modelName);
    }

    @Override
    public void clear() {
        listBox.clear();
    }
}
