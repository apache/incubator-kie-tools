/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.cards;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class CardsGridComponentViewTest {

    @Mock
    private HTMLDivElement cardGrid;

    private CardsGridComponentView view;

    @Before
    public void setup() {
        view = new CardsGridComponentView(cardGrid);
    }

    @Test
    public void testClearGrid() {
        cardGrid.innerHTML = "something";

        view.clearGrid();

        final String expected = "";
        final String actual = cardGrid.innerHTML;

        assertEquals(expected, actual);
    }

    @Test
    public void testAppendCard() {

        final HTMLElement cardElement = mock(HTMLElement.class);

        view.appendCard(cardElement);

        verify(cardGrid).appendChild(cardElement);
    }
}
