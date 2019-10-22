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
package org.kie.workbench.common.stunner.kogito.client.view;

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
public class DiagramEditorScreenViewImpl implements DiagramEditorScreenView,
                                                    IsElement {

    private FlowPanel loadingPanel;
    private FlowPanel widgetPanel;

    @Inject
    public DiagramEditorScreenViewImpl(final @DataField FlowPanel loadingPanel,
                                       final @DataField FlowPanel widgetPanel) {
        this.loadingPanel = loadingPanel;
        this.widgetPanel = widgetPanel;
    }

    @Override
    public DiagramEditorScreenView setWidget(final IsWidget widget) {
        widgetPanel.clear();
        widgetPanel.add(widget);
        return this;
    }

    @Override
    public DiagramEditorScreenView showLoading() {
        widgetPanel.setVisible(false);
        loadingPanel.setVisible(true);
        return this;
    }

    @Override
    public DiagramEditorScreenView hideLoading() {
        loadingPanel.setVisible(false);
        widgetPanel.setVisible(true);
        return this;
    }

    @Override
    public IsWidget asWidget() {
        return ElementWrapperWidget.getWidget(this.getElement());
    }

    @PreDestroy
    public void destroy() {
        loadingPanel.removeFromParent();
        widgetPanel.removeFromParent();
    }
}
