/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour.observers;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Attr;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Event;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NamedNodeMap;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GlobalHTMLObserverTest {

    @Mock
    private Disposer<GlobalHTMLObserver> selfDisposer;

    @Mock
    private HTMLDocument document;

    @Mock
    private GuidedTourService service;

    @Mock
    private GlobalHTMLObserver observer;

    @Captor
    private ArgumentCaptor<UserInteraction> userInteractionArgumentCaptor;

    private GuidedTourBridge bridge;

    private GlobalHTMLObserver htmlObserver;

    @Before
    public void setup() {
        bridge = spy(new GuidedTourBridge(service, observer));
        htmlObserver = spy(new GlobalHTMLObserver(selfDisposer));

        doReturn(document).when(htmlObserver).document();

        htmlObserver.init();
        bridge.registerObserver(htmlObserver);
    }

    @Test
    public void testInit() {
        // 'init' is called on setup
        document.addEventListener(CLICK, htmlObserver.CLICK_LISTENER);
    }

    @Test
    public void testDispose() {
        htmlObserver.dispose();
        document.removeEventListener(CLICK, htmlObserver.CLICK_LISTENER);
    }

    @Test
    public void testOnHTMLElementEvent() {

        final Event event = mock(Event.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final DOMTokenList classList = mock(DOMTokenList.class);
        final UserInteraction expected = mock(UserInteraction.class);
        final List<String> classStringList = asList("pf-label", "pf-label--red", "pf-label--xl");
        final NamedNodeMap<Attr> attrNamedNodeMap = spy(new NamedNodeMap<>());
        final Attr node1 = new Attr();
        final Attr node2 = new Attr();

        htmlElement.id = "user-name-label";
        htmlElement.tagName = "div";
        htmlElement.classList = classList;
        htmlElement.attributes = attrNamedNodeMap;

        event.target = htmlElement;
        classList.length = classStringList.size();

        attrNamedNodeMap.length = 2;
        node1.nodeName = "data-field";
        node1.nodeValue = "username";
        node2.nodeName = "data-key";
        node2.nodeValue = "123";

        when(classList.asList()).thenReturn(classStringList);
        doReturn(node1).when(attrNamedNodeMap).item(0);
        doReturn(node2).when(attrNamedNodeMap).item(1);
        doReturn(expected).when(htmlObserver).makeUserInteraction("CLICK", "div[data-field=\"username\"][data-key=\"123\"]#user-name-label.pf-label.pf-label--red.pf-label--xl");

        htmlObserver.onHTMLElementEvent(event);

        verify(bridge).refresh(userInteractionArgumentCaptor.capture());
        final UserInteraction actual = userInteractionArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }
}
