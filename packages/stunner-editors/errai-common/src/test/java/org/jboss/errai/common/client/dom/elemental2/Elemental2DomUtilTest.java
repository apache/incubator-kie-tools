/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.dom.elemental2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Testing Elemental2DomUtil API.
 * @author Guilherme Carreiro <ggomes@redhat.com>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Elemental2DomUtil.class, RootPanel.class, Js.class })
public class Elemental2DomUtilTest {

  private Elemental2DomUtil elemental2DomUtil;

  @BeforeClass
  public static void setupPreferences() {
    // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
    GWTMockUtilities.disarm();
  }

  @Before
  public void setup() {
    elemental2DomUtil = spy(new Elemental2DomUtil() {

      void onAttach(Widget w) {
        // fake native
      }
    });
  }

  @Test
  public void testRemoveAllElementChildrenWhenNodeDoesNotHaveAnyChildren() {

    final Node node = Mockito.spy(makeNode());

    final boolean hadChildren = elemental2DomUtil.removeAllElementChildren(node);

    Mockito.verify(node, Mockito.never()).removeChild(any());

    assertFalse(hadChildren);
  }

  @Test
  public void testRemoveAllElementChildrenWhenNodeHasChildren() {

    final Node child1 = mock(Node.class);
    final Node child2 = mock(Node.class);
    final Node node = Mockito.spy(makeNode(child1, child2));

    final boolean hadChildren = elemental2DomUtil.removeAllElementChildren(node);

    Mockito.verify(node).removeChild(child1);
    Mockito.verify(node).removeChild(child2);

    assertTrue(hadChildren);
  }

  @Test
  public void testAppendWidgetToElementWhenChildIsAttached() throws Exception {

    final HTMLElement parent = mock(HTMLElement.class);
    final HTMLElement widgetElement = mock(HTMLElement.class);
    final Widget child = mock(Widget.class);
    final Element element = mock(Element.class);

    doReturn(true).when(child).isAttached();
    doReturn(element).when(child).getElement();
    doReturn(widgetElement).when(elemental2DomUtil).asHTMLElement(element);

    mockRootPanel();

    elemental2DomUtil.appendWidgetToElement(parent, child);

    verify(child).removeFromParent();
    verify(parent).appendChild(widgetElement);
    verify(elemental2DomUtil).onAttach(child);

    verifyStatic(RootPanel.class);
    RootPanel.detachOnWindowClose(child);
  }

  @Test
  public void testAppendWidgetToElementWhenChildIsNotAttached() throws Exception {

    final HTMLElement parent = mock(HTMLElement.class);
    final HTMLElement widgetElement = mock(HTMLElement.class);
    final Widget child = mock(Widget.class);
    final Element element = mock(Element.class);

    doReturn(false).when(child).isAttached();
    doReturn(element).when(child).getElement();
    doReturn(widgetElement).when(elemental2DomUtil).asHTMLElement(element);

    mockRootPanel();

    elemental2DomUtil.appendWidgetToElement(parent, child);

    verify(child, never()).removeFromParent();
    verify(parent).appendChild(widgetElement);
    verify(elemental2DomUtil).onAttach(child);

    verifyStatic(RootPanel.class);
    RootPanel.detachOnWindowClose(child);
  }

  @Test
  public void testAsHTMLElementForGWTElement() throws Exception {

    final com.google.gwt.dom.client.Element gwtElement = mock(com.google.gwt.dom.client.Element.class);
    final HTMLElement expectedElement = mock(HTMLElement.class);

    mockJsCast(expectedElement, gwtElement);

    final HTMLElement actualElement = elemental2DomUtil.asHTMLElement(gwtElement);

    assertSame(expectedElement, actualElement);
  }

  @Test
  public void testAsHTMLElementForErraiHTMLElement() throws Exception {

    final org.jboss.errai.common.client.dom.HTMLElement htmlElement = mock(
        org.jboss.errai.common.client.dom.HTMLElement.class);
    final HTMLElement expectedElement = mock(HTMLElement.class);

    mockJsCast(expectedElement, htmlElement);

    final HTMLElement actualElement = elemental2DomUtil.asHTMLElement(htmlElement);

    assertSame(expectedElement, actualElement);
  }

  @Test
  public void testAsHTMLElementForHTMLElement() throws Exception {

    final org.jboss.errai.common.client.dom.HTMLElement deprecatedElement = mock(
            org.jboss.errai.common.client.dom.HTMLElement.class);
    final HTMLElement htmlElement = mock(HTMLElement.class);

    mockJsCast(deprecatedElement, htmlElement);

    org.jboss.errai.common.client.dom.HTMLElement actualElement = elemental2DomUtil.asHTMLElement(htmlElement);

    assertSame(deprecatedElement, actualElement);
  }

  private void mockJsCast(final HTMLElement htmlElement, final Object obj) {
    PowerMockito.spy(Js.class);
    PowerMockito.doReturn(htmlElement).when(Js.class);
    Js.cast(obj);
  }

  private void mockJsCast(final org.jboss.errai.common.client.dom.HTMLElement htmlElement, final Object obj) {
    PowerMockito.spy(Js.class);
    PowerMockito.doReturn(htmlElement).when(Js.class);
    Js.cast(obj);
  }

  private void mockRootPanel() {
    PowerMockito.mockStatic(RootPanel.class);
  }

  private Node makeNode(final Node... nodes) {

    final List<Node> nodeList = asArrayList(nodes);

    return new Node() {
      {
        lastChild = last(nodeList);
      }

      public Node removeChild(final Node oldChild) {

        nodeList.remove(oldChild);
        lastChild = last(nodeList);

        return oldChild;
      }
    };
  }

  private Node last(final List<Node> nodeList) {
    return nodeList.isEmpty() ? null : nodeList.get(nodeList.size() - 1);
  }

  private ArrayList<Node> asArrayList(final Node[] nodes) {

    final List<Node> nodeList = Arrays.asList(nodes);

    return new ArrayList<>(nodeList);
  }
}
