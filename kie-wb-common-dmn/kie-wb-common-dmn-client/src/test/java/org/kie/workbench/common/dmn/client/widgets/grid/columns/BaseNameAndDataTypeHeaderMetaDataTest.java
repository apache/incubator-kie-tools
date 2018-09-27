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

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseNameAndDataTypeHeaderMetaDataTest {

    protected static final String NAME_DATA_TYPE_COLUMN_GROUP = "NameAndDataTypeHeaderMetaDataTest$NameAndDataTypeColumn";

    protected static final Optional<String> EDITOR_TITLE = Optional.of("editor");

    protected static final Name NAME = new Name("name");

    @Mock
    protected HasTypeRef hasTypeRef;

    @Mock
    protected Consumer<HasName> clearDisplayNameConsumer;

    @Mock
    protected BiConsumer<HasName, Name> setDisplayNameConsumer;

    @Mock
    protected BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected NameAndDataTypePopoverView.Presenter headerEditor;

    protected NameAndDataTypeHeaderMetaData metaData;

    protected abstract void setup(final Optional<HasName> hasName);

    @Test
    public void testGetPresenter() {
        setup(Optional.empty());

        assertThat(metaData.getPresenter()).isEqualTo(metaData);
    }

    @Test
    public void testGetTitleWithHasName() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        assertThat(metaData.getTitle()).isEqualTo(NAME.getValue());
    }

    @Test
    public void testGetDisplayNameWithHasName() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        assertThat(metaData.getName()).isEqualTo(NAME);
    }

    @Test
    public void testGetTitleWithoutHasName() {
        setup(Optional.empty());

        assertThat(metaData.getTitle()).isEqualTo(HasName.NOP.getName().getValue());
    }

    @Test
    public void testGetDisplayNameWithoutHasName() {
        setup(Optional.empty());

        assertThat(metaData.getName()).isEqualTo(HasName.NOP.getName());
    }

    @Test
    public void testGetTypeRef() {
        setup(Optional.empty());

        metaData.getTypeRef();

        verify(hasTypeRef).getTypeRef();
    }

    @Test
    public void testSetDisplayNameWithHasName() {
        final Decision decision = new Decision();
        setup(Optional.of(decision));

        metaData.setName(NAME);

        verify(setDisplayNameConsumer).accept(eq(decision), eq(NAME));
    }

    @Test
    public void testSetDisplayNameWithHasNameWithEmptyValue() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        metaData.setName(new Name());

        verify(clearDisplayNameConsumer).accept(eq(decision));
    }

    @Test
    public void testSetDisplayNameWithHasNameWithoutChange() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        metaData.setName(NAME);

        verify(clearDisplayNameConsumer, never()).accept(any(HasName.class));
        verify(setDisplayNameConsumer, never()).accept(any(HasName.class), any(Name.class));
    }

    @Test
    public void testSetDisplayNameWithoutHasName() {
        setup(Optional.empty());

        metaData.setName(NAME);

        verify(setDisplayNameConsumer, never()).accept(any(HasName.class), any(Name.class));
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
    public void testAsDMNModelInstrumentedBase() {
        final Decision decision = new Decision();
        setup(Optional.empty());

        when(hasTypeRef.asDMNModelInstrumentedBase()).thenReturn(decision);

        assertThat(metaData.asDMNModelInstrumentedBase()).isEqualTo(decision);
    }
}