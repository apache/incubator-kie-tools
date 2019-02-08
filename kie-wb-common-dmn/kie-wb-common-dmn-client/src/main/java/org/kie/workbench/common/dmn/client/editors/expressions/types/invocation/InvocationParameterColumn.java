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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableNameAndDataTypeColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public class InvocationParameterColumn extends EditableNameAndDataTypeColumn<InvocationGrid> {

    public InvocationParameterColumn(final List<HeaderMetaData> headerMetaData,
                                     final double width,
                                     final InvocationGrid gridWidget,
                                     final Predicate<Integer> isEditable,
                                     final Consumer<HasName> clearDisplayNameConsumer,
                                     final BiConsumer<HasName, Name> setDisplayNameConsumer,
                                     final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final NameAndDataTypePopoverView.Presenter editor,
                                     final Optional<String> editorTitle) {
        super(headerMetaData,
              width,
              gridWidget,
              isEditable,
              clearDisplayNameConsumer,
              setDisplayNameConsumer,
              setTypeRefConsumer,
              cellEditorControls,
              editor,
              editorTitle);
    }
}
