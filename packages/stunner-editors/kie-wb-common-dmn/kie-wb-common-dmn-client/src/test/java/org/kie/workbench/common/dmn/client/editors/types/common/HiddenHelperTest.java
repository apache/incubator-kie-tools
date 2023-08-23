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

package org.kie.workbench.common.dmn.client.editors.types.common;

import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HiddenHelperTest {

    @Test
    public void testHide() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        HiddenHelper.hide(element);

        verify(classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShow() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;

        HiddenHelper.show(element);

        verify(classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testIsHiddenWhenElementIsHidden() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(HIDDEN_CSS_CLASS)).thenReturn(true);

        assertTrue(HiddenHelper.isHidden(element));
    }

    @Test
    public void testIsHiddenWhenElementIsNotHidden() {

        final Element element = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.classList = classList;
        when(classList.contains(HIDDEN_CSS_CLASS)).thenReturn(false);

        assertFalse(HiddenHelper.isHidden(element));
    }
}
