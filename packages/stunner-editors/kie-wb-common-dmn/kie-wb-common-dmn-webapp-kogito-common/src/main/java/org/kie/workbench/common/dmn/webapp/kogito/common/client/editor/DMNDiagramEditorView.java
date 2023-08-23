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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;

@Dependent
@Templated
public class DMNDiagramEditorView
        extends BaseEditorViewImpl
        implements AbstractDMNDiagramEditor.View {

    @DataField
    private ResizeFlowPanel editorPanel;

    protected DMNDiagramEditorView() {
        //CDI proxy
    }

    @Inject
    public DMNDiagramEditorView(final ResizeFlowPanel editorPanel) {
        this.editorPanel = editorPanel;
    }

    protected void onAttach() {
        super.onAttach();
        if (getElement().getParentElement() != null) {
            getElement().getParentElement().getStyle().setHeight(100, Style.Unit.PCT);
            getElement().getParentElement().getStyle().setWidth(100, Style.Unit.PCT);
            getElement().getParentElement().getStyle().setDisplay(Style.Display.TABLE);
        }
    }

    @Override
    public void setWidget(IsWidget widget) {
        editorPanel.clear();
        editorPanel.add(widget);
    }

    @Override
    public void onResize() {
        editorPanel.onResize();
    }

    @PreDestroy
    public void destroy() {
        editorPanel.clear();
        editorPanel.removeFromParent();
    }
}
