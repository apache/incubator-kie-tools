/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMRect;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class HTMLElementsPositionProviderFactoryTest {

    private HTMLElementsPositionProviderFactory utils;

    @Mock
    private HTMLDocument document;

    @Before
    public void init() {
        utils = spy(new HTMLElementsPositionProviderFactory());
        doReturn(document).when(utils).document();
    }

    @Test
    public void testGetPositionProviderFunction() {

        final String selector = ".my-button--inside-of-iframe";
        final Element element = mock(Element.class);
        final DOMRect clientRect = mock(DOMRect.class);
        final Rect expected = mock(Rect.class);

        clientRect.bottom = 60;
        clientRect.top = 10;
        clientRect.left = 10;
        clientRect.right = 110;
        clientRect.height = 50;
        clientRect.width = 100;

        when(document.querySelector(selector)).thenReturn(element);
        when(element.getBoundingClientRect()).thenReturn(clientRect);

        doReturn(expected).when(utils).makeRect(60, 10, 10, 110, 50, 100);

        final Rect actual = utils.createPositionProvider().call(selector);

        assertEquals(expected, actual);
    }
}
