/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.forms.client.screens;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DiagramEditorPropertiesScreenView implements IsElement {

    private FlowPanel loadingPanel;
    private FlowPanel widgetPanel;

    @Inject
    public DiagramEditorPropertiesScreenView(final @DataField FlowPanel loadingPanel,
                                             final @DataField FlowPanel widgetPanel) {
        this.loadingPanel = loadingPanel;
        this.widgetPanel = widgetPanel;
    }

    public DiagramEditorPropertiesScreenView setWidget(final IsWidget widget) {
        widgetPanel.clear();
        widgetPanel.add(widget);
        return this;
    }

    public DiagramEditorPropertiesScreenView showLoading() {
        widgetPanel.setVisible(false);
        loadingPanel.setVisible(true);
        return this;
    }

    public DiagramEditorPropertiesScreenView hideLoading() {
        loadingPanel.setVisible(false);
        widgetPanel.setVisible(true);
        return this;
    }

    public IsWidget asWidget() {
        return ElementWrapperWidget.getWidget(this.getElement());
    }

    @PreDestroy
    public void destroy() {
        loadingPanel.removeFromParent();
        widgetPanel.removeFromParent();
    }
}
