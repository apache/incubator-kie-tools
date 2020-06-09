/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.component.KeyValueWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KeyValueWidgetTest {

    @Mock
    private KeyValueWidget.View view;

    private KeyValueWidget presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new KeyValueWidget(view);
    }

    @Test
    public void name() {
        final IsWidget isWidget = mock(IsWidget.class);
        presenter.put("hello", isWidget);

        verify(view).setKey("hello");
        verify(view).setValueWidget(isWidget);
    }
}