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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.DOWN_ARROW_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.FOCUSED_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.RIGHT_ARROW_CSS_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListItemViewCssHelperTest {

    @Test
    public void testAsRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        ListItemViewCssHelper.asRightArrow(element);

        verify(classList).add(RIGHT_ARROW_CSS_CLASS);
        verify(classList).remove(DOWN_ARROW_CSS_CLASS);
    }

    @Test
    public void testAsDownArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        ListItemViewCssHelper.asDownArrow(element);

        verify(classList).add(DOWN_ARROW_CSS_CLASS);
        verify(classList).remove(RIGHT_ARROW_CSS_CLASS);
    }

    @Test
    public void testIsRightArrowWhenItIsRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(true);

        assertTrue(ListItemViewCssHelper.isRightArrow(element));
    }

    @Test
    public void testIsRightArrowWhenItIsNotRightArrow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(RIGHT_ARROW_CSS_CLASS)).thenReturn(false);

        assertFalse(ListItemViewCssHelper.isRightArrow(element));
    }

    @Test
    public void testAsFocusedDataType() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        ListItemViewCssHelper.asFocusedDataType(element);

        verify(classList).add(FOCUSED_CSS_CLASS);
    }

    @Test
    public void testAsNonFocusedDataType() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        ListItemViewCssHelper.asNonFocusedDataType(element);

        verify(classList).remove(FOCUSED_CSS_CLASS);
    }

    @Test
    public void testIsFocusedDataTypeWhenItIsFocused() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(FOCUSED_CSS_CLASS)).thenReturn(true);

        assertTrue(ListItemViewCssHelper.isFocusedDataType(element));
    }

    @Test
    public void testIsFocusedDataTypeWhenItIsNotFocused() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(FOCUSED_CSS_CLASS)).thenReturn(false);

        assertFalse(ListItemViewCssHelper.isFocusedDataType(element));
    }
}
