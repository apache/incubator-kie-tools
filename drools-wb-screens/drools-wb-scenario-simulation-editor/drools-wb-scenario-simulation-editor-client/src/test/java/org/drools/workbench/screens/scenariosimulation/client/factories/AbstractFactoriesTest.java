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
package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.fakes.FakeProvider;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.mockito.Mockito.when;

public abstract class AbstractFactoriesTest extends AbstractScenarioSimulationTest {

    @Mock
    protected TextArea textAreaMock;
    @Mock
    protected GridBodyCellRenderContext contextMock;
    @Mock
    protected Element elementMock;
    @Mock
    protected Style styleMock;
    @Captor
    protected ArgumentCaptor<KeyDownHandler> keyDownHandlerArgumentCaptor;



    @Before
    public void setup() {
        super.setup();
        when(elementMock.getStyle()).thenReturn(styleMock);
        when(textAreaMock.getElement()).thenReturn(elementMock);
        when(contextMock.getRowIndex()).thenReturn(ROW_INDEX);
        when(contextMock.getColumnIndex()).thenReturn(COLUMN_INDEX);

        GwtMockito.useProviderForType(TextArea.class, (FakeProvider<TextArea>) aClass -> textAreaMock);
    }
}
