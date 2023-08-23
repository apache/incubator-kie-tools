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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverImpl.BINDING_EXCEPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValueAndDataTypePopoverImplTest {

    private static final String NAME = "name";

    private static final String NAME_LABEL = "label";

    @Mock
    private ValueAndDataTypePopoverView view;

    @Mock
    private HasValueAndTypeRef<Name> bound;

    @Mock
    private Decision decision;

    @Mock
    private QName typeRef;

    private ValueAndDataTypePopoverImpl editor;

    @Before
    public void setup() {
        this.editor = new ValueAndDataTypePopoverImpl(view);

        when(bound.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(bound.toWidgetValue(any(Name.class))).thenAnswer(i -> ((Name) i.getArguments()[0]).getValue());
        when(bound.toModelValue(Mockito.<String>any())).thenAnswer(i -> new Name((String) i.getArguments()[0]));
        when(bound.normaliseValue(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
        when(bound.getValueLabel()).thenReturn(NAME_LABEL);
        when(bound.getValue()).thenReturn(new Name(NAME));
        when(bound.getTypeRef()).thenReturn(typeRef);
    }

    @Test
    public void testInit() {
        verify(view).init(eq(editor));
    }

    @Test
    public void testShow() {
        editor.bind(bound, 0, 0);

        editor.show();

        verify(view).show(Optional.empty());
    }

    @Test
    public void testHide() {
        editor.bind(bound, 0, 0);

        editor.hide();

        verify(view).hide();
    }

    @Test
    public void testBind() {
        editor.bind(bound, 0, 0);

        verify(view).setDMNModel(eq(decision));
        verify(view).initValue(eq(NAME));
        verify(view).initSelectedTypeRef(eq(typeRef));

        editor.show();

        verify(view).show(Optional.empty());
    }

    @Test
    public void testSetDisplayName() {
        editor.bind(bound, 0, 0);

        editor.setValue(NAME);

        verify(bound).setValue(eq(new Name(NAME)));
    }

    @Test
    public void testSetTypeRef() {
        editor.bind(bound, 0, 0);

        editor.setTypeRef(typeRef);

        verify(bound).setTypeRef(eq(typeRef));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetOnClosedByKeyboardCallback_WhenBound() {
        final Consumer callback = mock(Consumer.class);

        editor.bind(bound, 0, 0);

        editor.setOnClosedByKeyboardCallback(callback);

        verify(view).setOnClosedByKeyboardCallback(callback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetOnClosedByKeyboardCallback_WhenNotBound() {
        final Consumer callback = mock(Consumer.class);

        editor.setOnClosedByKeyboardCallback(callback);

        verify(view, never()).setOnClosedByKeyboardCallback(any(Consumer.class));
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent_WhenBound() {
        editor.bind(bound, 0, 0);

        editor.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(view).hide();
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent_WhenNotBound() {
        editor.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(view, never()).hide();
    }

    @Test
    public void testGetValueLabel_WhenBound() {
        editor.bind(bound, 0, 0);

        assertThat(editor.getValueLabel()).isEqualTo(NAME_LABEL);
    }

    @Test
    public void testGetValueLabel_WhenNotBound() {
        final Throwable thrown = catchThrowable(() -> editor.getValueLabel());

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(BINDING_EXCEPTION);
    }

    @Test
    public void testNormaliseValue_WhenBound() {
        editor.bind(bound, 0, 0);

        assertThat(editor.normaliseValue(NAME)).isEqualTo(NAME);
    }

    @Test
    public void testNormaliseValue_WhenNotBound() {
        final Throwable thrown = catchThrowable(() -> editor.normaliseValue(NAME));

        assertThat(thrown).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(BINDING_EXCEPTION);
    }
}
