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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;

public interface CellEditorControlsView extends org.jboss.errai.ui.client.local.api.IsElement {

    void show(final PopupEditorControls editor,
              final Optional<String> editorTitle,
              final int x,
              final int y);

    void hide();

    interface Presenter {

        void setGridPanelSupplier(final Optional<Supplier<DMNGridPanel>> gridPanelSupplier);

        void show(final HasCellEditorControls.Editor<?> editor,
                  final Optional<String> editorTitle,
                  final int x,
                  final int y);

        void hide();
    }
}
