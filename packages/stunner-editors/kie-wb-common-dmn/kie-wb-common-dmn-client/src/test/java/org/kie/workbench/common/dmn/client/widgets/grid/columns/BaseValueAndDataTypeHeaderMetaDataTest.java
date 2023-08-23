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

import com.ait.lienzo.client.core.shape.Text;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasValue;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseValueAndDataTypeHeaderMetaDataTest<V, HV extends HasValue<V>> {

    protected static final double BLOCK_WIDTH = 10.0;

    protected static final double BLOCK_HEIGHT = 20.0;

    protected final String PLACEHOLDER = "placeholder";

    protected static final String POPOVER_TITLE = "title";

    @Mock
    protected HasTypeRef hasTypeRef;

    @Mock
    protected Consumer<HV> clearValueConsumer;

    @Mock
    protected BiConsumer<HV, V> setValueConsumer;

    @Mock
    protected BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected ValueAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    protected EditableHeaderMetaData headerMetaData;

    @Mock
    protected GridHeaderColumnRenderContext context;

    @Mock
    protected ValueAndDataTypeHeaderMetaData<V, HV> metaData;

    protected abstract V makeValue();

    protected abstract V makeEmptyValue();

    protected abstract HV makeHasValue();

    protected abstract void setup(final Optional<HV> hasValue);

    @Test
    public abstract void testToModelValue();

    @Test
    public abstract void testToWidgetValue();

    @Test
    public abstract void testGetValueLabel();

    @Test
    public abstract void testNormaliseValue();

    @Test
    public abstract void testRender();

    @Test
    public void testGetPresenter() {
        setup(Optional.empty());

        assertThat(metaData.getPresenter()).isEqualTo(metaData);
    }

    @Test
    public void testGetTitleWithoutHasName() {
        setup(Optional.empty());

        assertThat(metaData.getTitle()).isEqualTo(HasName.NOP.getName().getValue());
    }

    @Test
    public void testGetDisplayNameWithoutHasName() {
        setup(Optional.empty());

        assertThat(metaData.getValue()).isEqualTo(HasName.NOP.getName());
    }

    @Test
    public void testGetTypeRef() {
        setup(Optional.empty());

        metaData.getTypeRef();

        verify(hasTypeRef).getTypeRef();
    }

    @Test
    public void testSetDisplayNameWithHasValue() {
        final V value = makeValue();
        final HV hasValue = makeHasValue();

        setup(Optional.of(hasValue));

        metaData.setValue(value);

        verify(setValueConsumer).accept(eq(hasValue), eq(value));
    }

    @Test
    public void testSetValueWithHasValueWithEmptyValue() {
        final V value = makeValue();
        final V emptyValue = makeEmptyValue();
        final HV hasValue = makeHasValue();
        hasValue.setValue(value);

        setup(Optional.of(hasValue));

        metaData.setValue(emptyValue);

        verify(clearValueConsumer).accept(eq(hasValue));
    }

    @Test
    public void testSetValueWithHasValueWithoutChange() {
        final HV hasValue = makeHasValue();

        setup(Optional.of(hasValue));

        metaData.setValue(hasValue.getValue());

        verify(clearValueConsumer, never()).accept(any());
        verify(setValueConsumer, never()).accept(any(), any());
    }

    @Test
    public void testSetValueWithoutHasValue() {
        setup(Optional.empty());

        metaData.setValue(makeValue());

        verify(setValueConsumer, never()).accept(any(), any());
    }

    @Test
    public void testSetTypeRef() {
        setup(Optional.empty());

        final QName typeRef = new QName();

        metaData.setTypeRef(typeRef);

        verify(setTypeRefConsumer).accept(eq(hasTypeRef), eq(typeRef));
    }

    @Test
    public void testSetTypeRefWithoutChange() {
        setup(Optional.empty());

        final QName typeRef = new QName();
        when(hasTypeRef.getTypeRef()).thenReturn(typeRef);

        metaData.setTypeRef(typeRef);

        verify(setTypeRefConsumer, never()).accept(any(HasTypeRef.class), any(QName.class));
    }

    @Test
    public void testIsEmptyValue_WhenNull() {
        setup(Optional.empty());

        assertThat(metaData.isEmptyValue(null)).isTrue();
    }

    @Test
    public void testIsEmptyValue_WhenEmptyString() {
        setup(Optional.empty());

        assertThat(metaData.isEmptyValue(makeEmptyValue())).isTrue();
    }

    @Test
    public void testGetValue() {
        final V value = makeEmptyValue();
        final HV hasValue = makeHasValue();

        setup(Optional.of(hasValue));

        hasValue.setValue(value);

        assertThat(metaData.getValue()).isNotNull();
        assertThat(metaData.getValue()).isEqualTo(value);
    }

    @Test
    public void testGetPopoverTitle() {
        setup(Optional.empty());

        assertThat(metaData.getPopoverTitle()).isEqualTo(POPOVER_TITLE);
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        final Decision decision = new Decision();
        setup(Optional.empty());

        when(hasTypeRef.asDMNModelInstrumentedBase()).thenReturn(decision);

        assertThat(metaData.asDMNModelInstrumentedBase()).isEqualTo(decision);
    }

    @Test
    public void testRenderPlaceHolder() {
        final GridRenderer renderer = mock(GridRenderer.class);
        final GridRendererTheme theme = mock(GridRendererTheme.class);
        final Text text = mock(Text.class);
        setup(Optional.empty());

        when(context.getRenderer()).thenReturn(renderer);
        when(renderer.getTheme()).thenReturn(theme);
        when(theme.getPlaceholderText()).thenReturn(text);

        metaData.renderPlaceHolder(context, BLOCK_WIDTH, BLOCK_HEIGHT);

        verify(text).setX(BLOCK_WIDTH / 2);
        verify(text).setY(BLOCK_HEIGHT / 2);
        verify(text).setText(PLACEHOLDER);
        verify(text).setListening(false);
    }
}