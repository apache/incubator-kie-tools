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

package org.drools.workbench.screens.enums.client.editor;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EnumEditorViewImplTest {

    @Mock
    private Button addButton;

    @Spy
    private ListDataProvider<EnumRow> dataProvider;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    private EnumEditorViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new EnumEditorViewImpl(dataProvider, addButton);
    }

    @Test
    public void testClickCauseAdditionOfRow() throws Exception {
        view.init();

        Assertions.assertThat(dataProvider.getList()).hasSize(0);
        verify(dataProvider).addDataDisplay(any(CellTable.class));
        verify(addButton).addClickHandler(clickHandlerCaptor.capture());

        // simulate click on the add button
        clickHandlerCaptor.getValue().onClick(mock(ClickEvent.class));
        Assertions.assertThat(dataProvider.getList()).hasSize(1);

        final EnumRow newRow = dataProvider.getList().get(0);
        Assertions.assertThat(newRow.getContext()).isEmpty();
        Assertions.assertThat(newRow.getFactName()).isEmpty();
        Assertions.assertThat(newRow.getFieldName()).isEmpty();
        Assertions.assertThat(newRow.getRaw()).isEmpty();
    }

    @Test
    public void testSetAndGetContent() throws Exception {
        final List<EnumRow> testedRows = Collections.singletonList(new EnumRow());

        view.setContent(testedRows);

        Assertions.assertThat(dataProvider.getList()).hasSize(1);
        Assertions.assertThat(dataProvider.getList()).containsExactly(testedRows.get(0));

        Assertions.assertThat(view.getContent()).hasSize(1);
        Assertions.assertThat(view.getContent()).containsExactly(testedRows.get(0));
    }
}
