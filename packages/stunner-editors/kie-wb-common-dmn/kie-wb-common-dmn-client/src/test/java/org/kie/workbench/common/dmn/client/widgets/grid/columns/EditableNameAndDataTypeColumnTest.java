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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameAndDataTypeCell;
import org.kie.workbench.common.dmn.client.editors.types.HasValueAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
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

    private static final String NAME = "name";

    private static final String NEW_NAME = "new name";

    private static final String POPOVER_TITLE = "title";

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private ContextGrid gridWidget;

    @Mock
    private Predicate<Integer> isEditable;

    @Mock
    private Consumer<HasName> clearValueConsumer;

    @Mock
    private BiConsumer<HasName, Name> setValueConsumer;

    @Mock
    private BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    private TranslationService translationService;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ValueAndDataTypePopoverView.Presenter editor;

    @Mock
    private GridBodyCellEditContext context;

    @Mock
    private Consumer<GridCellValue<InformationItemCell.HasNameCell>> callback;

    @Spy
    private InformationItem informationItem = new InformationItem();

    @Captor
    private ArgumentCaptor<HasValueAndTypeRef<Name>> hasNameAndDataTypeControlCaptor;

    @Captor
    private ArgumentCaptor<Name> nameCaptor;

    private GridCell<InformationItemCell.HasNameCell> cell;

    private EditableNameAndDataTypeColumn column;

    @Before
    public void setup() {
        when(gridWidget.getExpression()).thenReturn(Optional::empty);

        this.cell = new BaseGridCell<>(new BaseGridCellValue<>(HasNameAndDataTypeCell.wrap(informationItem)));
        this.column = spy(new EditableNameAndDataTypeColumn<ContextGrid>(headerMetaData,
                                                                         DMNGridColumn.DEFAULT_WIDTH,
                                                                         gridWidget,
                                                                         isEditable,
                                                                         clearValueConsumer,
                                                                         setValueConsumer,
                                                                         setTypeRefConsumer,
                                                                         translationService,
                                                                         cellEditorControls,
                                                                         editor) {
            @Override
            protected String getPopoverTitle() {
                return POPOVER_TITLE;
            }
        });

        when(context.getRelativeLocation()).thenReturn(Optional.of(new Point2D(RELATIVE_X, RELATIVE_Y)));
        when(context.getRowIndex()).thenReturn(UI_ROW_INDEX);
        when(context.getColumnIndex()).thenReturn(UI_COLUMN_INDEX);
        when(context.getAbsoluteCellX()).thenReturn(ABSOLUTE_CELL_X);
        when(context.getAbsoluteCellY()).thenReturn(ABSOLUTE_CELL_Y);
        when(gridWidget.getParentInformation()).thenReturn(new GridCellTuple(0, 0, gridWidget));

        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
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

        verify(editor).bind(any(HasValueAndTypeRef.class),
                            eq(UI_ROW_INDEX),
                            eq(UI_COLUMN_INDEX));

        verify(cellEditorControls).show(eq(editor),
                                        eq((int) RELATIVE_X),
                                        eq((int) RELATIVE_Y));
    }

    @Test
    public void testEditGetters() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasValueAndTypeRef.getValue();
        verify(informationItem).getName();

        hasValueAndTypeRef.getTypeRef();
        verify(informationItem).getTypeRef();

        hasValueAndTypeRef.getHasTypeRefs();
        verify(informationItem).getHasTypeRefs();

        assertThat(hasValueAndTypeRef.asDMNModelInstrumentedBase()).isEqualTo(informationItem);
    }

    @Test
    public void testEditSetNameNoChange() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasValueAndTypeRef.setValue(informationItem.getName());

        verify(clearValueConsumer, never()).accept(any(HasName.class));
        verify(setValueConsumer, never()).accept(anyObject(), any(Name.class));
    }

    @Test
    public void testEditSetNameChanged() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasValueAndTypeRef.setValue(new Name(NEW_NAME));

        verify(clearValueConsumer, never()).accept(any(HasName.class));
        verify(setValueConsumer).accept(eq(cell.getValue().getValue()), nameCaptor.capture());

        assertThat(nameCaptor.getValue().getValue()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testEditSetNameChangedToEmpty() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasValueAndTypeRef.setValue(null);

        verify(clearValueConsumer).accept(eq(cell.getValue().getValue()));
        verify(setValueConsumer, never()).accept(anyObject(), any(Name.class));
    }

    @Test
    public void testEditSetTypeRefNoChange() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        hasValueAndTypeRef.setTypeRef(new QName());

        verify(setTypeRefConsumer, never()).accept(anyObject(), any(QName.class));
    }

    @Test
    public void testEditSetTypeRefChanged() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        final QName feel = new QName(DMNModelInstrumentedBase.Namespace.DMN.getUri(),
                                     "",
                                     DMNModelInstrumentedBase.Namespace.DMN.getPrefix());
        hasValueAndTypeRef.setTypeRef(feel);

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

    @Test
    public void testGetPopoverTitle() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        assertThat(hasValueAndTypeRef.getPopoverTitle()).isEqualTo(POPOVER_TITLE);
    }

    @Test
    public void testToModelValue() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        assertThat(hasValueAndTypeRef.toModelValue(NAME).getValue()).isEqualTo(NAME);
    }

    @Test
    public void testToWidgetValue() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        assertThat(hasValueAndTypeRef.toWidgetValue(new Name(NAME))).isEqualTo(NAME);
    }

    @Test
    public void testGetValueLabel() {
        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        assertThat(hasValueAndTypeRef.getValueLabel()).isEqualTo(DMNEditorConstants.NameAndDataTypePopover_NameLabel);
    }

    @Test
    public void testNormaliseValue() {
        final String value = "   " + NAME + "   ";

        mockEditAction();

        final HasValueAndTypeRef<Name> hasValueAndTypeRef = hasNameAndDataTypeControlCaptor.getValue();
        assertThat(hasValueAndTypeRef.normaliseValue(value)).isEqualTo(NAME);
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
