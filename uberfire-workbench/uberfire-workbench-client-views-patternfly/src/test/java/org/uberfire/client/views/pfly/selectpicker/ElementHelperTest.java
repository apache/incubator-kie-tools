/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.selectpicker;

import elemental2.dom.Node;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ElementHelperTest {

    @Test
    public void testInsertAfter() {

        final Node newNode = mock(Node.class);
        final Node referenceNode = mock(Node.class);
        final Node parentNode = mock(Node.class);
        final Node nextSibling = mock(Node.class);

        referenceNode.parentNode = parentNode;
        referenceNode.nextSibling = nextSibling;

        ElementHelper.insertAfter(newNode, referenceNode);

        verify(parentNode).insertBefore(newNode, nextSibling);
    }

    @Test
    public void testInsertBefore() {

        final Node newNode = mock(Node.class);
        final Node referenceNode = mock(Node.class);
        final Node parentNode = mock(Node.class);

        referenceNode.parentNode = parentNode;

        ElementHelper.insertBefore(newNode, referenceNode);

        verify(parentNode).insertBefore(newNode, referenceNode);
    }

    @Test
    public void testRemove() {

        final Node node = mock(Node.class);
        final Node parentNode = mock(Node.class);

        node.parentNode = parentNode;

        ElementHelper.remove(node);

        verify(parentNode).removeChild(node);
    }
}
