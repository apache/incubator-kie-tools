/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;

@Dependent
public class SessionScreenViewImpl implements SessionScreenView {

    private static final String EMPTY_TEXT = "No diagram session active.";

    private final FlowPanel mainPanel = new FlowPanel();
    private final FlowPanel emptyViewPanel = new FlowPanel();
    private final FlowPanel screenViewPanel = new FlowPanel();

    @PostConstruct
    public void init() {
        mainPanel.add(emptyViewPanel);
        mainPanel.add(screenViewPanel);
        mainPanel.setHeight("100%");
        this.emptyViewPanel.setHeight("100%");
        this.emptyViewPanel.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        this.screenViewPanel.setHeight("100%");
        this.emptyViewPanel.add(new Heading(HeadingSize.H5,
                                            EMPTY_TEXT));
        showEmptySession();
    }

    @Override
    public void showEmptySession() {
        emptyViewPanel.setVisible(true);
        screenViewPanel.setVisible(false);
    }

    @Override
    public void showScreenView(final IsWidget viewWidget) {
        emptyViewPanel.setVisible(false);
        setScreenView(viewWidget);
        screenViewPanel.setVisible(true);
    }

    @Override
    public void clear() {
        this.screenViewPanel.clear();
    }

    private SessionScreenView setScreenView(final IsWidget view) {
        this.screenViewPanel.clear();
        this.screenViewPanel.add(view);
        return this;
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }
}
