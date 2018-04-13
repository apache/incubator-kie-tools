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
package org.kie.workbench.common.stunner.project.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectDiagramEditorViewTest {

    private static final int WIDTH = 10;

    private static final int HEIGHT = 20;

    private SimplePanel parent;

    private FlowPanel editorPanel;

    private ProjectDiagramEditorView tested;

    @Before
    public void setup() throws Exception {
        this.parent = GWT.create(SimplePanel.class);
        this.editorPanel = GWT.create(FlowPanel.class);
        this.tested = spy(new ProjectDiagramEditorView(editorPanel));
        when(tested.getParent()).thenReturn(parent);
        when(parent.getOffsetWidth()).thenReturn(WIDTH);
        when(parent.getOffsetHeight()).thenReturn(HEIGHT);
        parent.setWidget(tested);
    }

    @Test
    public void testSetWidget() {
        final IsWidget editor = mock(IsWidget.class);
        tested.setWidget(editor);

        verify(editorPanel).clear();
        verify(editorPanel).add(eq(editor));
    }

    @Test
    public void testOnResizeWithEditor() {
        //Any Widget implementing RequiresResize will suffice
        final ScrollPanel editor = GWT.create(ScrollPanel.class);
        when(editorPanel.getWidgetCount()).thenReturn(1);
        when(editorPanel.getWidget(eq(0))).thenReturn(editor);

        tested.onResize();

        verify(tested).setPixelSize(eq(WIDTH), eq(HEIGHT));
        verify(editor).onResize();
    }

    @Test
    public void testOnResizeWithNoEditor() {
        tested.onResize();

        verify(tested).setPixelSize(eq(WIDTH), eq(HEIGHT));
    }
}
