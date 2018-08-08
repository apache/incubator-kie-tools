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

package org.kie.workbench.common.dmn.client.editors.types.treegrid.common;

import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeGridItemCssHelperTest {

    @Test
    public void testAsRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        TreeGridItemCssHelper.asRightArrow(element);

        verify(classList).add("fa-angle-right");
        verify(classList).remove("fa-angle-down");
    }

    @Test
    public void testAsDownArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        TreeGridItemCssHelper.asDownArrow(element);

        verify(classList).add("fa-angle-down");
        verify(classList).remove("fa-angle-right");
    }

    @Test
    public void testIsRightArrowWhenItIsRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(true);

        assertTrue(TreeGridItemCssHelper.isRightArrow(element));
    }

    @Test
    public void testIsRightArrowWhenItIsNotRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(false);

        assertFalse(TreeGridItemCssHelper.isRightArrow(element));
    }
}
