/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TreeListSubItemTest {

    @Mock
    private TreeListSubItem.View view;

    @Mock
    private HTMLElement element;

    private TreeListSubItem subItem;

    @Before
    public void setup() {
        subItem = new TreeListSubItem(view);

        when(view.getElement()).thenReturn(element);
    }

    @Test
    public void testSetGetDescription() {

        final String description = "This is a great description!";

        subItem.setDescription(description);

        verify(view).setDescription(description);

        final String actual = subItem.getDescription();
        assertEquals(description, actual);
    }

    @Test
    public void testSetGetDetails() {

        final String details = "Details";

        subItem.setDetails(details);

        verify(view).setDetails(details);

        final String actual = subItem.getDetails();
        assertEquals(details, actual);
    }

    @Test
    public void testGetElement() {

        final Node actual = subItem.getElement();
        assertEquals(element, actual);
    }
}