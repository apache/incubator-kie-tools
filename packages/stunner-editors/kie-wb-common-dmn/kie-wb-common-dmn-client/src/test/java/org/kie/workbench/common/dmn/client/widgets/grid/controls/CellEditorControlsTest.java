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

package org.kie.workbench.common.dmn.client.widgets.grid.controls;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class CellEditorControlsTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private CellEditorControlsView view;

    @Mock
    private HasCellEditorControls.Editor editor;

    private CellEditorControlsView.Presenter controls;

    @Before
    public void setup() {
        this.controls = new CellEditorControls(view);

        this.controls.setGridPanelSupplier(Optional.of(() -> gridPanel));

        doReturn(viewport).when(gridPanel).getViewport();
        doReturn(transform).when(viewport).getTransform();
    }

    @Test
    public void testShow() {
        doReturn(0.5).when(transform).getScaleX();
        doReturn(-100.0).when(transform).getTranslateX();
        doReturn(-50).when(gridPanel).getAbsoluteLeft();

        doReturn(0.25).when(transform).getScaleY();
        doReturn(-200.0).when(transform).getTranslateY();
        doReturn(-75).when(gridPanel).getAbsoluteTop();

        controls.show(editor, 10, 20);

        verify(view).show(eq(editor),
                          eq(-145),
                          eq(-270));
    }

    @Test
    public void testHide() {
        controls.hide();

        verify(view).hide();
    }
}
