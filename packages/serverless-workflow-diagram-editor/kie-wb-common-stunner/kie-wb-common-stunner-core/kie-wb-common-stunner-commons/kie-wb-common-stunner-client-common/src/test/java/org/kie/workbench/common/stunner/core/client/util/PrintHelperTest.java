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


package org.kie.workbench.common.stunner.core.client.util;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import elemental2.dom.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.client.util.PrintHelper.PREVIEW_SCREEN_CSS_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PrintHelperTest {

    private PrintHelper helper;

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    @Captor
    private ArgumentCaptor<Element> elementArgumentCaptor;

    @Before
    public void setup() {
        helper = spy(new PrintHelper());
    }

    @Test
    public void testPrint() {

        final HTMLElement element = mock(HTMLElement.class);
        final HTMLDocument printDocument = mock(HTMLDocument.class);
        final Window globalWindow = mock(Window.class);
        final Window printWindow = mock(Window.class);

        doReturn(globalWindow).when(helper).getGlobalWindow();
        doReturn(printDocument).when(helper).getWindowDocument(printWindow);
        doNothing().when(helper).writeElementIntoDocument(any(), any());
        doNothing().when(helper).changeMediaAttributesToAll(any());
        doNothing().when(helper).copyStylesFromWindow(any(), any());
        doNothing().when(helper).setupPrintCommandOnPageLoad(any(), any());
        when(globalWindow.open("", "_blank")).thenReturn(printWindow);

        helper.print(element);

        verify(helper).writeElementIntoDocument(element, printDocument);
        verify(helper).changeMediaAttributesToAll(printDocument);
        verify(helper).copyStylesFromWindow(printDocument, globalWindow);
        verify(helper).setupPrintCommandOnPageLoad(printDocument, printWindow);
    }

    @Test
    public void testWriteElementIntoDocument() {

        final HTMLElement element = mock(HTMLElement.class);
        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLBodyElement body = mock(HTMLBodyElement.class);
        final String elementHTML = "<html />";
        final DOMTokenList classList = mock(DOMTokenList.class);

        element.innerHTML = elementHTML;
        document.body = body;
        body.classList = classList;

        helper.writeElementIntoDocument(element, document);

        verify(document).open();
        verify(document).write(elementHTML);
        verify(document).close();
        verify(classList).add(PREVIEW_SCREEN_CSS_CLASS);
    }

    @Test
    public void testChangeMediaAttributesToAll() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final Element element = mock(Element.class);
        final NodeList<Element> links = spy(new NodeList<>());
        final String media = "media";

        links.length = 1;

        doReturn(element).when(links).item(0);
        doReturn(element).when(helper).asElement(element);
        when(document.querySelectorAll("link")).thenReturn(links);
        when(element.getAttribute(media)).thenReturn("print");

        helper.changeMediaAttributesToAll(document);

        verify(element).setAttribute(media, "all");
    }

    @Test
    public void testCopyStylesFromWindow() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLDocument topDocument = mock(HTMLDocument.class);
        final Window window = mock(Window.class);
        final Window topWindow = mock(Window.class);
        final Element element = mock(Element.class);
        final Element head = mock(Element.class);
        final NodeList<Element> parentStyles = spy(new NodeList<>());

        parentStyles.length = 1;
        window.top = topWindow;
        element.innerHTML = ".page { background: red }";

        doReturn(element).when(parentStyles).item(0);
        doReturn(topDocument).when(helper).getWindowDocument(topWindow);
        doReturn(element).when(helper).asElement(element);
        doReturn(head).when(helper).asElement(head);
        doReturn(mock(Element.class)).when(helper).createElement("style");
        when(document.querySelector("head")).thenReturn(head);
        when(topDocument.querySelectorAll("style")).thenReturn(parentStyles);

        helper.copyStylesFromWindow(document, window);

        verify(head).appendChild(elementArgumentCaptor.capture());

        final Element copiedStyle = elementArgumentCaptor.getValue();

        assertEquals(".page { background: red }", copiedStyle.innerHTML);
    }

    @Test
    public void testSetupPrintCommandOnPageLoad() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final Window window = mock(Window.class);

        document.body = mock(HTMLBodyElement.class);
        doNothing().when(helper).setTimeout(any(), anyInt());

        helper.setupPrintCommandOnPageLoad(document, window);
        document.body.onload.onInvoke(mock(Event.class));

        verify(helper).setTimeout(commandArgumentCaptor.capture(), eq(10));
        commandArgumentCaptor.getValue().execute();

        verify(window).focus();
        verify(window).print();
        verify(window).close();
    }
}
