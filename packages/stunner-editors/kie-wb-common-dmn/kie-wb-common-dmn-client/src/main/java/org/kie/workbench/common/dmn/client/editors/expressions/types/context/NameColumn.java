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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableNameAndDataTypeColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public class NameColumn extends EditableNameAndDataTypeColumn<ContextGrid> {

    public NameColumn(final List<HeaderMetaData> headerMetaData,
                      final double width,
                      final ContextGrid gridWidget,
                      final Predicate<Integer> isEditable,
                      final Consumer<HasName> clearValueConsumer,
                      final BiConsumer<HasName, Name> setValueConsumer,
                      final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                      final TranslationService translationService,
                      final CellEditorControlsView.Presenter cellEditorControls,
                      final ValueAndDataTypePopoverView.Presenter editor) {
        super(headerMetaData,
              width,
              gridWidget,
              isEditable,
              clearValueConsumer,
              setValueConsumer,
              setTypeRefConsumer,
              translationService,
              cellEditorControls,
              editor);
    }

    @Override
    protected String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.ContextEditor_EditContextEntry);
    }
}
