/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import org.gwtbootstrap3.client.ui.FormLabel;

public class FormLabelHelp extends Composite implements HasText {

    private final FormLabel formLabel;
    private final FlowPanel panel;

    private HelpIcon helpIcon;

    public FormLabelHelp() {
        this(new FormLabel(),
             new FlowPanel());
    }

    // Defined for testing purposes
    FormLabelHelp(FormLabel formLabel,
                  FlowPanel panel) {
        this.formLabel = formLabel;
        this.panel = panel;

        init();
    }

    private void init() {
        initWidget(panel);
        addStyleName("uf-form-label");
        panel.add(formLabel);
    }

    public void setHelpTitle(final String title) {
        if (title != null) {
            getHelpIcon().setHelpTitle(title);
        }
    }

    public void setHelpContent(final String content) {
        if (content != null) {
            getHelpIcon().setHelpContent(content);
        }
    }

    private HelpIcon getHelpIcon() {
        if (helpIcon == null) {
            helpIcon = GWT.create(HelpIcon.class);
            panel.add(helpIcon);
        }
        return helpIcon;
    }

    @Override
    public String getText() {
        return formLabel.getText();
    }

    @Override
    public void setText(final String text) {
        formLabel.setText(text);
    }

    public void setFor(final String forValue) {
        formLabel.setFor(forValue);
    }

    public void setShowRequiredIndicator(final boolean required) {
        formLabel.setShowRequiredIndicator(required);
    }
}
