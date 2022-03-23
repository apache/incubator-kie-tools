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

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import jsinterop.base.Js;

/**
 * Provides utility methods for interacting with the DOM.
 * @author Guilherme Carreiro <ggomes@redhat.com>
 */
public class Elemental2DomUtil {

  /**
   * Detaches all element children from a node.
   * @param node Must not be null.
   * @return True iff any element children were detached by this call.
   */
  public boolean removeAllElementChildren(final Node node) {

    final boolean hadChildren = node.lastChild != null;

    while (node.lastChild != null) {
      node.removeChild(node.lastChild);
    }

    return hadChildren;
  }

  /**
   * Appends the underlying {@link HTMLElement} of a {@link Widget} to another {@link HTMLElement},
   * in a way that does not break GWT Widget events.
   * @param parent The parent element that is appended to. Must not be null.
   * @param child The child Widget, whose underlying HTML element will be
   * appended to the parent. Must not be null.
   */
  public void appendWidgetToElement(final HTMLElement parent, final Widget child) {

    if (child.isAttached()) {
      child.removeFromParent();
    }

    RootPanel.detachOnWindowClose(child);

    HTMLElement newChild = asHTMLElement(child.getElement());
    parent.appendChild(newChild);

    onAttach(child);
  }

  /**
   * Converts from {@link com.google.gwt.dom.client.Element} to {@link HTMLElement}.
   * @param gwtElement The deprecated GWT Element
   * @return The GWT Elemental2 HTMLElement
   */
  public HTMLElement asHTMLElement(final com.google.gwt.dom.client.Element gwtElement) {
    return Js.cast(gwtElement);
  }

  /**
   * Converts from {@link org.jboss.errai.common.client.dom.HTMLElement} to {@link HTMLElement}.
   * @param htmlElement The deprecated Errai HTMLElement
   * @return The GWT Elemental2 HTMLElement
   */
  public HTMLElement asHTMLElement(final org.jboss.errai.common.client.dom.HTMLElement htmlElement) {
    return Js.cast(htmlElement);
  }

  /**
   * Converts from {@link HTMLElement} to {@link org.jboss.errai.common.client.dom.HTMLElement}. This is just in case you
   * need to use your new elements with deprecated Errai HTMLElement.
   * @param htmlElement The deprecated Errai HTMLElement
   * @return The GWT Elemental2 HTMLElement
   */
  public org.jboss.errai.common.client.dom.HTMLElement asHTMLElement(final HTMLElement htmlElement) {
    return Js.cast(htmlElement);
  }

  native void onAttach(Widget w)/*-{
    w.@com.google.gwt.user.client.ui.Widget::onAttach()();
  }-*/;
}
