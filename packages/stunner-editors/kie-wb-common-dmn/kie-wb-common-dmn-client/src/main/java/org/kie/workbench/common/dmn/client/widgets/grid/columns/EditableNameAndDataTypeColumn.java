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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.types.Point2D;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils;
import org.kie.workbench.common.dmn.client.editors.types.HasValueAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNSimpleGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getNotNullHasTypeRefs;

public abstract class EditableNameAndDataTypeColumn<G extends BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> extends DMNSimpleGridColumn<G, InformationItemCell.HasNameCell> {

    protected final Predicate<Integer> isEditable;
    protected final Consumer<HasName> clearValueConsumer;
    protected final BiConsumer<HasName, Name> setValueConsumer;
    protected final BiConsumer<HasTypeRef, QName> setTypeRefConsumer;
    protected final TranslationService translationService;
    protected final CellEditorControlsView.Presenter cellEditorControls;
    protected final ValueAndDataTypePopoverView.Presenter editor;

    public EditableNameAndDataTypeColumn(final HeaderMetaData headerMetaData,
                                         final double width,
                                         final G gridWidget,
                                         final Predicate<Integer> isEditable,
                                         final Consumer<HasName> clearValueConsumer,
                                         final BiConsumer<HasName, Name> setValueConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final TranslationService translationService,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final ValueAndDataTypePopoverView.Presenter editor) {
        this(Collections.singletonList(headerMetaData),
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

    public EditableNameAndDataTypeColumn(final List<HeaderMetaData> headerMetaData,
                                         final double width,
                                         final G gridWidget,
                                         final Predicate<Integer> isEditable,
                                         final Consumer<HasName> clearValueConsumer,
                                         final BiConsumer<HasName, Name> setValueConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final TranslationService translationService,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final ValueAndDataTypePopoverView.Presenter editor) {
        super(headerMetaData,
              new NameAndDataTypeColumnRenderer(),
              width,
              gridWidget);
        this.isEditable = isEditable;
        this.clearValueConsumer = clearValueConsumer;
        this.setValueConsumer = setValueConsumer;
        this.setTypeRefConsumer = setTypeRefConsumer;
        this.translationService = translationService;
        this.cellEditorControls = cellEditorControls;
        this.editor = editor;

        setMovable(false);
        setResizable(true);
    }

    protected abstract String getPopoverTitle();

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

        editor.bind(new HasValueAndTypeRef<Name>() {
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
                        public Name getValue() {
                            return binding.getName();
                        }

                        @Override
                        public void setValue(final Name name) {
                            if (Objects.equals(name, getValue())) {
                                return;
                            }

                            if (name == null || name.getValue() == null || name.getValue().trim().isEmpty()) {
                                clearValueConsumer.accept(binding);
                            } else {
                                setValueConsumer.accept(binding, name);
                            }
                        }

                        @Override
                        public String getPopoverTitle() {
                            return EditableNameAndDataTypeColumn.this.getPopoverTitle();
                        }

                        @Override
                        public Name toModelValue(final String componentValue) {
                            return new Name(componentValue);
                        }

                        @Override
                        public String toWidgetValue(final Name modelValue) {
                            return modelValue.getValue();
                        }

                        @Override
                        public String getValueLabel() {
                            return translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_NameLabel);
                        }

                        @Override
                        public String normaliseValue(final String componentValue) {
                            return NameUtils.normaliseName(componentValue);
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
