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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNSimpleGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;

public abstract class EditableNameAndDataTypeColumn<G extends BaseExpressionGrid> extends DMNSimpleGridColumn<G, InformationItemCell.HasNameCell> {

    private final Predicate<Integer> isEditable;
    private final Consumer<HasName> clearDisplayNameConsumer;
    private final BiConsumer<HasName, Name> setDisplayNameConsumer;
    private final BiConsumer<HasTypeRef, QName> setTypeRefConsumer;
    private final CellEditorControlsView.Presenter cellEditorControls;
    private final NameAndDataTypePopoverView.Presenter editor;
    private final Optional<String> editorTitle;

    public EditableNameAndDataTypeColumn(final HeaderMetaData headerMetaData,
                                         final G gridWidget,
                                         final Predicate<Integer> isEditable,
                                         final Consumer<HasName> clearDisplayNameConsumer,
                                         final BiConsumer<HasName, Name> setDisplayNameConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final NameAndDataTypePopoverView.Presenter editor,
                                         final Optional<String> editorTitle) {
        this(Collections.singletonList(headerMetaData),
             gridWidget,
             isEditable,
             clearDisplayNameConsumer,
             setDisplayNameConsumer,
             setTypeRefConsumer,
             cellEditorControls,
             editor,
             editorTitle);
    }

    public EditableNameAndDataTypeColumn(final List<HeaderMetaData> headerMetaData,
                                         final G gridWidget,
                                         final Predicate<Integer> isEditable,
                                         final Consumer<HasName> clearDisplayNameConsumer,
                                         final BiConsumer<HasName, Name> setDisplayNameConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final NameAndDataTypePopoverView.Presenter editor,
                                         final Optional<String> editorTitle) {
        super(headerMetaData,
              new NameAndDataTypeColumnRenderer(),
              gridWidget);
        this.isEditable = isEditable;
        this.clearDisplayNameConsumer = clearDisplayNameConsumer;
        this.setDisplayNameConsumer = setDisplayNameConsumer;
        this.setTypeRefConsumer = setTypeRefConsumer;
        this.cellEditorControls = cellEditorControls;
        this.editor = editor;
        this.editorTitle = editorTitle;
        setMovable(false);
        setResizable(true);
    }

    @Override
    public void edit(final GridCell<InformationItemCell.HasNameCell> cell,
                     final GridBodyCellRenderContext context,
                     final Consumer<GridCellValue<InformationItemCell.HasNameCell>> callback) {
        final int rowIndex = context.getRowIndex();
        if (!isEditable.test(rowIndex)) {
            return;
        }

        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double cellWidth = context.getCellWidth();
        final double cellHeight = context.getCellHeight();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        final InformationItemCell.HasNameAndDataTypeCell binding = (InformationItemCell.HasNameAndDataTypeCell) cell.getValue().getValue();

        editor.bind(new HasNameAndTypeRef() {
                        @Override
                        public QName getTypeRef() {
                            return binding.getTypeRef();
                        }

                        @Override
                        public void setTypeRef(final QName typeRef) {
                            if (Objects.equals(typeRef, getTypeRef())) {
                                return;
                            }

                            setTypeRefConsumer.accept(binding, typeRef);
                        }

                        @Override
                        public Name getName() {
                            return binding.getName();
                        }

                        @Override
                        public void setName(final Name name) {
                            if (Objects.equals(name, getName())) {
                                return;
                            }

                            if (name == null || name.getValue() == null || name.getValue().trim().isEmpty()) {
                                clearDisplayNameConsumer.accept(binding);
                            } else {
                                setDisplayNameConsumer.accept(binding, name);
                            }
                        }

                        @Override
                        public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
                            return binding.asDMNModelInstrumentedBase();
                        }

                        @Override
                        public List<HasTypeRef> getHasTypeRefs() {
                            return getNotNullHasTypeRefs(binding);
                        }
                    },
                    uiRowIndex,
                    uiColumnIndex);
        final double[] dxy = {absoluteCellX + cellWidth / 2, absoluteCellY + cellHeight / 2};
        final Optional<Point2D> rx = ((GridBodyCellEditContext) context).getRelativeLocation();
        rx.ifPresent(r -> {
            dxy[0] = r.getX();
            dxy[1] = r.getY();
        });
        cellEditorControls.show(editor,
                                editorTitle,
                                (int) (dxy[0]),
                                (int) (dxy[1]));
    }

    @Override
    protected GridCellValue<InformationItemCell.HasNameCell> makeDefaultCellValue() {
        return new BaseGridCellValue<>(InformationItemCell.HasNameCell.wrap(""));
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}
