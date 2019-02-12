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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class RuleModellerSelectorFilter extends HorizontalPanel {

    private final TextBox txtSearch = GWT.create(TextBox.class);

    private final Button btnSearch = GWT.create(Button.class);

    public RuleModellerSelectorFilter() {
        getElement().getStyle().setMarginBottom(5.0, Style.Unit.PX);
        getElement().getStyle().setWidth(100.0, Style.Unit.PCT);
    }

    public void setFilterChangeConsumer(final Consumer<String> filterChangeConsumer) {
        txtSearch.setPlaceholder(GuidedRuleEditorResources.CONSTANTS.filterHint());
        txtSearch.addKeyDownHandler((e) -> {
            if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                filterChangeConsumer.accept(txtSearch.getText());
            }
        });

        btnSearch.addClickHandler((e) -> filterChangeConsumer.accept(txtSearch.getText()));
        btnSearch.setIcon(IconType.SEARCH);
        add(txtSearch);
        add(btnSearch);
    }

    public String getFilterText() {
        return txtSearch.getText();
    }
}
