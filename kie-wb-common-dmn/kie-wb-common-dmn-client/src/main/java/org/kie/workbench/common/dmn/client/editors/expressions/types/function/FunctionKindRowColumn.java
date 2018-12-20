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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public class FunctionKindRowColumn extends EmptyColumn {

    public FunctionKindRowColumn(final Supplier<FunctionDefinition> functionSupplier,
                                 final CellEditorControlsView.Presenter cellEditorControls,
                                 final KindPopoverView.Presenter editor,
                                 final Optional<String> editorTitle,
                                 final FunctionGrid gridWidget) {
        super(Collections.singletonList(new FunctionKindRowColumnHeaderMetaData(functionSupplier,
                                                                                cellEditorControls,
                                                                                editor,
                                                                                editorTitle,
                                                                                gridWidget)));
        setMovable(false);
        setResizable(false);
    }
}