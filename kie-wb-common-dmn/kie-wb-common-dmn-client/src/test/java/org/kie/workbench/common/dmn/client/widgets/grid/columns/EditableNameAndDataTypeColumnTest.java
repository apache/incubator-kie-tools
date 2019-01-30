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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameAndDataTypeCell;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class EditableNameAndDataTypeColumnTest {

    private static final int UI_ROW_INDEX = 0;

    private static final int UI_COLUMN_INDEX = 1;

    private static final double ABSOLUTE_CELL_X = 100.0;

    private static final double ABSOLUTE_CELL_Y = 200.0;

    private static final double RELATIVE_X = 105.0;

    private static final double RELATIVE_Y = 210.0;

    private static final String NEW_NAME = "new name";

    private static final Optional<String> EDITOR_TITLE = Optional.of("editor");

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private ContextGrid gridWidget;

    @Mock
    private Predicate<Integer> isEditable;

    @Mock
    private Consumer<HasName> clearDisplayNameConsumer;

    @Mock
    private BiConsumer<HasName, Name> setDisplayNameConsumer;

    @Mock
    private BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private NameAndDataTypePopoverView.Presenter editor;

    @Mock
    private GridBodyCellEditContext context;

    @Mock
    private Consumer<GridCellValue<InformationItemCell.HasNameCell>> callback;

    @Spy
    private InformationItem informationItem = new InformationItem();

    @Captor
    private ArgumentCaptor<HasNameAndTypeRef> hasNameAndDataTypeControlCaptor;

    @Captor
    private ArgumentCaptor<Name> nameCaptor;

    private GridCell<InformationItemCell.HasNameCell> cell;

    private EditableNameAndDataTypeColumn column;

    @Before
    public void setup() {
        this.cell = new BaseGridCell<>(new BaseGridCellValue<>(HasNameAndDataTypeCell.wrap(informationItem)));
        this.column = spy(new EditableNameAndDataTypeColumn<ContextGrid>(headerMetaData,
                                                                         gridWidget,
                                                                         isEditable,
                                                                         clearDisplayNameConsumer,
                                                                         setDisplayNameConsumer,
                                                                         setTypeRefConsumer,
                                                                         cellEditorControls,
                                                                         editor,
                                                                         EDITOR_TITLE) {
            //Nothing to implement
        });

        when(context.getRelativeLocation()).thenReturn(Optional.of(new Point2D(RELATIVE_X, RELATIVE_Y)));
        when(context.getRowIndex()).thenReturn(UI_ROW_INDEX);
        when(context.getColumnIndex()).thenReturn(UI_COLUMN_INDEX);
        when(context.getAbsoluteCellX()).thenReturn(ABSOLUTE_CELL_X);
        when(context.getAbsoluteCellY()).thenReturn(ABSOLUTE_CELL_Y);
        when(gridWidget.getParentInformation()).thenReturn(new GridCellTuple(0, 0, gridWidget));
    }

    @Test
    public void testIsMovable() {
        assertThat(column.isMovable()).isFalse();
    }

    @Test
    public void testIsResizable() {
        assertThat(column.isResizable()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWhenRowIsNotEditable() {
        when(isEditable.test(anyInt())).thenReturn(false);

        column.edit(cell,
                    context,
                    callback);

        verifyNoMoreInteractions(editor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWhenRowIsEditable() {
        when(isEditable.test(anyInt())).thenReturn(true);

        column.edit(cell,
                    context,
                    callback);

        verify(editor).bind(any(HasNameAndTypeRef.class),
                            eq(UI_ROW_INDEX),
                            eq(UI_COLUMN_INDEX));

        verify(cellEditorControls).show(eq(editor),
                                        eq(EDITOR_TITLE),
                                        eq((int) RELATIVE_X),
                                        eq((int) RELATIVE_Y));
    }

    @Test
    public void testEditGetters() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasNameAndTypeRef.getName();
        verify(informationItem).getName();

        hasNameAndTypeRef.getTypeRef();
        verify(informationItem).getTypeRef();

        hasNameAndTypeRef.getHasTypeRefs();
        verify(informationItem).getHasTypeRefs();

        assertThat(hasNameAndTypeRef.asDMNModelInstrumentedBase()).isEqualTo(informationItem);
    }

    @Test
    public void testEditSetNameNoChange() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasNameAndTypeRef.setName(informationItem.getName());

        verify(clearDisplayNameConsumer, never()).accept(any(HasName.class));
        verify(setDisplayNameConsumer, never()).accept(anyObject(), any(Name.class));
    }

    @Test
    public void testEditSetNameChanged() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasNameAndTypeRef.setName(new Name(NEW_NAME));

        verify(clearDisplayNameConsumer, never()).accept(any(HasName.class));
        verify(setDisplayNameConsumer).accept(eq(cell.getValue().getValue()), nameCaptor.capture());

        assertThat(nameCaptor.getValue().getValue()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testEditSetNameChangedToEmpty() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasNameAndTypeRef.setName(null);

        verify(clearDisplayNameConsumer).accept(eq(cell.getValue().getValue()));
        verify(setDisplayNameConsumer, never()).accept(anyObject(), any(Name.class));
    }

    @Test
    public void testEditSetTypeRefNoChange() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasNameAndTypeRef.setTypeRef(new QName());

        verify(setTypeRefConsumer, never()).accept(anyObject(), any(QName.class));
    }

    @Test
    public void testEditSetTypeRefChanged() {
        mockEditAction();

        final HasNameAndTypeRef hasNameAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        final QName feel = new QName(DMNModelInstrumentedBase.Namespace.DMN.getUri(),
                                     "",
                                     DMNModelInstrumentedBase.Namespace.DMN.getPrefix());
        hasNameAndTypeRef.setTypeRef(feel);

        verify(setTypeRefConsumer).accept(eq((HasNameAndDataTypeCell) cell.getValue().getValue()), eq(feel));
    }

    @Test
    public void testDefaultValue() {
        assertThat(((InformationItemCell.HasNameCell) column.makeDefaultCellValue().getValue()).getName().getValue()).isEmpty();
    }

    @Test
    public void testSetWidth() {
        column.setWidth(200.0);

        verify(column).updateWidthOfPeers();
    }

    @SuppressWarnings("unchecked")
    private void mockEditAction() {
        when(isEditable.test(anyInt())).thenReturn(true);

        column.edit(cell,
                    context,
                    callback);

        verify(editor).bind(hasNameAndDataTypeControlCaptor.capture(),
                            eq(UI_ROW_INDEX),
                            eq(UI_COLUMN_INDEX));
    }
}
