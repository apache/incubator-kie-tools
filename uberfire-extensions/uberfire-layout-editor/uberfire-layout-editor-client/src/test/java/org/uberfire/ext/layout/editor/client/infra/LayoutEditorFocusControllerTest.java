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

package org.uberfire.ext.layout.editor.client.infra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;

@RunWith(MockitoJUnitRunner.class)
public class LayoutEditorFocusControllerTest {
    
    LayoutEditorFocusController controller = new LayoutEditorFocusController();

    @Test
    public void noScrollableParentTest() {
        HTMLElement element = mock(HTMLElement.class);
        element.style = mock(CSSStyleDeclaration.class);
        
        HTMLElement foundScrollParent = controller.findScrollableParent(element);
        assertNull(foundScrollParent);
    }
    
    @Test
    public void withScrollableParentTest() {
        HTMLElement scrollParent = mock(HTMLElement.class);
        HTMLElement element = mock(HTMLElement.class);
        CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        
        style.overflow = "auto";
        scrollParent.style = style;
        element.parentNode = scrollParent;
        element.style = mock(CSSStyleDeclaration.class);
        
        HTMLElement foundScrollParent = controller.findScrollableParent(element);
        assertEquals(scrollParent, foundScrollParent);
    }
    
    @Test
    public void scrollRecoveryTest() {
        final int initialScrollLeft = -1;
        final int initialScrollTop = -5;
        HTMLElement element = mock(HTMLElement.class);
        
        controller.setScrollableElement(element);
        element.scrollLeft = initialScrollLeft;
        element.scrollTop = initialScrollTop;
        
        controller.recordFocus();
        assertTrue(controller.isDirty());
        
        element.scrollLeft = 0;
        element.scrollTop = 0;

        controller.restoreFocus();
        assertFalse(controller.isDirty());
        assertEquals(initialScrollLeft, element.scrollLeft, 0.001);
        assertEquals(initialScrollTop, element.scrollTop, 0.001);
    }
    
}