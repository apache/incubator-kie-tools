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

package org.kie.workbench.common.stunner.client.widgets.screens;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DiagramEditorExplorerScreenView extends Composite implements DiagramEditorExplorerScreen.View {

    @Inject
    @DataField
    private FlowPanel previewPanelBody;

    @Inject
    @DataField
    private FlowPanel explorerPanelBody;

    @Override
    public DiagramEditorExplorerScreen.View setPreviewWidget(final IsWidget widget) {
        clearPreviewWidget();
        previewPanelBody.add(widget);
        return this;
    }

    @Override
    public DiagramEditorExplorerScreen.View clearPreviewWidget() {
        previewPanelBody.clear();
        return this;
    }

    @Override
    public DiagramEditorExplorerScreen.View setExplorerWidget(final IsWidget widget) {
        clearExplorerWidget();
        explorerPanelBody.add(widget);
        return this;
    }

    @Override
    public DiagramEditorExplorerScreen.View clearExplorerWidget() {
        explorerPanelBody.clear();
        return this;
    }

    @Override
    public DiagramEditorExplorerScreen.View clear() {
        clearPreviewWidget();
        clearExplorerWidget();
        return this;
    }

    @PreDestroy
    public void destroy() {
        clear();
        previewPanelBody.removeFromParent();
        explorerPanelBody.removeFromParent();
    }
}
