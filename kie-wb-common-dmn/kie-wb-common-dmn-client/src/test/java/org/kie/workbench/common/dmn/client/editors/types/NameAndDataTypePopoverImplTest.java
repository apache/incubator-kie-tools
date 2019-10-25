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
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NameAndDataTypePopoverImplTest {

    private static final String NAME = "name";

    @Mock
    private NameAndDataTypePopoverView view;

    @Mock
    private HasNameAndTypeRef bound;

    @Mock
    private Decision decision;

    @Mock
    private QName typeRef;

    private NameAndDataTypePopoverView.Presenter editor;

    @Before
    public void setup() {
        this.editor = new NameAndDataTypePopoverImpl(view);

        when(bound.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(bound.getName()).thenReturn(new Name(NAME));
        when(bound.getTypeRef()).thenReturn(typeRef);
    }

    @Test
    public void testInit() {
        verify(view).init(eq(editor));
    }

    @Test
    public void testShow() {
        editor.bind(bound, 0, 0);

        editor.show(Optional.empty());

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
        verify(view).initName(eq(NAME));
        verify(view).initSelectedTypeRef(eq(typeRef));

        editor.show(Optional.empty());

        verify(view).show(Optional.empty());
    }

    @Test
    public void testSetDisplayName() {
        editor.bind(bound, 0, 0);

        editor.setName(NAME);

        verify(bound).setName(eq(new Name(NAME)));
    }

    @Test
    public void testSetTypeRef() {
        editor.bind(bound, 0, 0);

        editor.setTypeRef(typeRef);

        verify(bound).setTypeRef(eq(typeRef));
    }

    @Test
    public void testSetOnClosedByKeyboardCallback(){
        final Consumer callback = mock(Consumer.class);

        editor.setOnClosedByKeyboardCallback(callback);

        verify(view).setOnClosedByKeyboardCallback(callback);
    }
}
