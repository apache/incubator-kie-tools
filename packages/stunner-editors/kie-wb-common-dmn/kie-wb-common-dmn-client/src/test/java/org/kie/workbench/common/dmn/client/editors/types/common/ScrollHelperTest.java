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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.selectpicker.JQuery;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollHelperTest {

    private ScrollHelper scrollHelper;

    @Before
    public void setup() {
        scrollHelper = spy(new ScrollHelper());
    }

    @Test
    public void testScrollToBottom() {

        final Element element = mock(Element.class);

        doNothing().when(scrollHelper).scrollTo(element, element);

        scrollHelper.scrollToBottom(element);

        verify(scrollHelper).scrollTo(element, element);
    }

    @Test
    public void testScrollToWithTwoArguments() {

        final Element target = mock(Element.class);
        final Element container = mock(Element.class);

        doNothing().when(scrollHelper).scrollTo(any(), any(), anyInt());

        scrollHelper.scrollTo(target, container);

        verify(scrollHelper).scrollTo(target, container, 0);
    }

    @Test
    public void testScrollToWithThreeArguments() {

        final HTMLElement target = mock(HTMLElement.class);
        final HTMLElement container = mock(HTMLElement.class);
        target.offsetTop = 8;
        container.offsetTop = 4;

        scrollHelper.scrollTo(target, container, 2);

        final Double expectedScrollTop = 2d;
        final Double actualScrollTop = container.scrollTop;

        assertEquals(expectedScrollTop, actualScrollTop);
    }

    @Test
    public void testAnimatedScrollToBottomWithOneArgument() {

        final Element element = mock(Element.class);
        final int scrollHeight = 123;

        element.scrollHeight = scrollHeight;
        doNothing().when(scrollHelper).animatedScrollToBottom(any(), anyDouble());

        scrollHelper.animatedScrollToBottom(element);

        verify(scrollHelper).animatedScrollToBottom(element, scrollHeight);
    }

    @Test
    public void testAnimatedScrollToBottomWithTwoArguments() {

        final Element element = mock(Element.class);
        final JQuery jQuery = mock(JQuery.class);
        final JavaScriptObject javaScriptObject = mock(JavaScriptObject.class);
        final double scrollHeight = 123;

        when(scrollHelper.elementJQuery(element)).thenReturn(jQuery);
        doReturn(javaScriptObject).when(scrollHelper).property("scrollTop", scrollHeight);

        scrollHelper.animatedScrollToBottom(element, scrollHeight);

        verify(jQuery).animate(javaScriptObject, 800);
    }
}
