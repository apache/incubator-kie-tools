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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypeEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionColumnHeaderMetaDataTest {

    private static final String TITLE = "title";

    private static final String TYPE_REF = "type-ref";

    @Mock
    private Supplier<String> nameSupplier;

    @Mock
    private Consumer<String> nameConsumer;

    @Mock
    private Supplier<QName> typeRefSupplier;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private NameAndDataTypeEditorView.Presenter headerEditor;

    @Mock
    private LiteralExpressionGrid gridWidget;

    @Mock
    private QName typeRef;

    private LiteralExpressionColumnHeaderMetaData metaData;

    @Before
    public void setup() {
        this.metaData = new LiteralExpressionColumnHeaderMetaData(nameSupplier,
                                                                  nameConsumer,
                                                                  typeRefSupplier,
                                                                  cellEditorControls,
                                                                  headerEditor,
                                                                  gridWidget);
    }

    @Test
    public void testGetTitle() {
        when(nameSupplier.get()).thenReturn(TITLE);

        assertEquals(TITLE, metaData.getTitle());

        verify(nameSupplier).get();
    }

    @Test
    public void testSetTitle() {
        metaData.setTitle(TITLE);

        verify(nameConsumer).accept(TITLE);
    }

    @Test
    public void testGetTypeRef() {
        when(typeRefSupplier.get()).thenReturn(typeRef);
        when(typeRef.toString()).thenReturn(TYPE_REF);

        assertEquals(TYPE_REF, metaData.getTypeRef());

        verify(typeRefSupplier).get();
    }
}
