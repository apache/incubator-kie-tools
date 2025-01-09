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

package org.uberfire.client.views.pfly.widgets;

import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KebabMenuTest {

    @Mock
    HTMLUListElement dropdownMenu;

    @Mock
    HTMLDocument document;

    @Mock
    HTMLDivElement kebab;

    @InjectMocks
    KebabMenu kebabMenu;

    @Before
    public void setup() {
        final HTMLLIElement element = mock(HTMLLIElement.class);
        element.classList = mock(DOMTokenList.class);
        when(document.createElement("li")).thenReturn(element);
        kebab.classList = mock(DOMTokenList.class);
    }

    @Test
    public void testAddSeparator() {
        kebabMenu.addSeparator();

        verify(dropdownMenu).appendChild(any(HTMLLIElement.class));
    }

    @Test
    public void testDropPositionUp() {
        kebabMenu.setDropPosition(KebabMenu.DropPosition.UP);

        verify(kebab.classList).add("dropup");
        verify(kebab.classList).remove("dropdown");
        verifyNoMoreInteractions(kebab.classList);
    }

    @Test
    public void testDropPositionDown() {
        kebabMenu.setDropPosition(KebabMenu.DropPosition.DOWN);

        verify(kebab.classList).remove("dropup");
        verify(kebab.classList).add("dropdown");
        verifyNoMoreInteractions(kebab.classList);
    }
}
