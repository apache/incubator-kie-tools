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

package org.kie.workbench.common.services.verifier.reporting.client.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;

public class IssuePresenterViewImpl
        extends Composite
        implements IssuePresenterView {

    interface Binder
            extends
            UiBinder<Widget, IssuePresenterViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Heading title;

    @UiField
    FlowPanel explanation;

    @UiField
    InlineLabel rows;

    @UiField
    InlineLabel rowsLabel;

    public IssuePresenterViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setIssueTitle(final String title) {
        this.title.setText(title);
    }

    @Override
    public void setExplanation(final SafeHtml explanation) {
        this.explanation.getElement().setInnerHTML(explanation.asString());
    }

    @Override
    public void setLines(final String lineNumbers) {
        this.rowsLabel.setVisible(true);
        this.rows.setVisible(true);
        this.rows.setText(lineNumbers);
    }

    @Override
    public void hideLines() {
        this.rowsLabel.setVisible(false);
        this.rows.setVisible(false);
    }
}
