/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.dom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides utility methods for interacting with the DOM.
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Deprecated
public abstract class DOMUtil {
  private DOMUtil() {}

  /**
   * @param element
   *          Must not be null.
   * @return If the given element has any child elements, return an optional containing the first child element.
   *         Otherwise return an empty optional.
   */
  public static Optional<Element> getFirstChildElement(final Element element) {
    for (final Node child : nodeIterable(element.getChildNodes())) {
      if (isElement(child)) {
        return Optional.ofNullable((Element) child);
      }
    }

    return Optional.empty();
  }

  /**
   * @param element
   *          Must not be null.
   * @return If the given element has any child elements, return an optional containing the last child element.
   *         Otherwise return an empty optional.
   */
  public static Optional<Element> getLastChildElement(final Element element) {
    final NodeList children = element.getChildNodes();
    for (int i = children.getLength()-1; i > -1; i--) {
      if (isElement(children.item(i))) {
        return Optional.ofNullable((Element) children.item(i));
      }
    }

    return Optional.empty();
  }

  /**
   * @param node
   *          Must not be null.
   * @return True iff the given node is an element.
   */
  public static boolean isElement(final Node node) {
    return node.getNodeType() == Node.ELEMENT_NODE;
  }

  /**
   * @param nodeList
   *          Must not be null.
   * @return An iterable for the given node list.
   */
  public static Iterable<Node> nodeIterable(final NodeList nodeList) {
    return () -> DOMUtil.nodeIterator(nodeList);
  }

  /**
   * @param nodeList
   *          Must not be null.
   * @return An iterator for the given node list.
   */
  public static Iterator<Node> nodeIterator(final NodeList nodeList) {
    return new Iterator<Node>() {
      int index = 0;

      @Override
      public boolean hasNext() {
        return index < nodeList.getLength();
      }

      @Override
      public Node next() {
        if (hasNext()) {
          return nodeList.item(index++);
        }
        else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * @param nodeList
   *          Must not be null.
   * @return An iterable for the given node list that ignores non-element nodes.
   */
  public static Iterable<Element> elementIterable(final NodeList nodeList) {
    return () -> elementIterator(nodeList);
  }

  /**
   * @param nodeList
   *          Must not be null.
   * @return An iterator for the given node list that ignores non-element nodes.
   */
  public static Iterator<Element> elementIterator(final NodeList nodeList) {
    return new Iterator<Element>() {

      int i = 0;

      @Override
      public boolean hasNext() {
        while (i < nodeList.getLength() && !isElement(nodeList.item(i))) {
          i++;
        }
        return i < nodeList.getLength();
      }

      @Override
      public Element next() {
        if (hasNext()) {
          return (Element) nodeList.item(i++);
        }
        else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * Detaches an element from its parent.
   *
   * @param element
   *          Must not be null.
   * @return True if calling this method detaches the given element from a parent node. False if there is no parent to
   *         be removed from.
   */
  public static boolean removeFromParent(final Element element) {
    if (element.getParentElement() != null) {
      element.getParentElement().removeChild(element);

      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Detaches all children from a node.
   *
   * @param node
   *          Must not be null.
   * @return True iff any children were detached by this call.
   */
  public static boolean removeAllChildren(final Node node) {
    final boolean hadChildren = node.getLastChild() != null;
    while (node.getLastChild() != null) {
      node.removeChild(node.getLastChild());
    }

    return hadChildren;
  }

  /**
   * Detaches all element children from a node.
   *
   * @param node
   *          Must not be null.
   * @return True iff any element children were detached by this call.
   */
  public static boolean removeAllElementChildren(final Node node) {
    boolean elementRemoved = false;
    for (final Element child : elementIterable(node.getChildNodes())) {
      node.removeChild(child);
      elementRemoved = true;
    }

    return elementRemoved;
  }

  /**
   * Removes a CSS class from an element's class list.
   *
   * @param element
   *          Must not be null.
   * @param className
   *          The name of a CSS class. Must not be null.
   * @return True if the given class was removed from the given element. False if the given element did not have the
   *         given class as part of its class list.
   */
  public static boolean removeCSSClass(final HTMLElement element, final String className) {
    if (hasCSSClass(element, className)) {
      element.getClassList().remove(className);

      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Adds a CSS class to an element's class list.
   *
   * @param element
   *          Must not be null.
   * @param className
   *          The name of a CSS class. Must not be null.
   * @return True if the given class was added to the given element. False if the given element already had the
   *         given class as part of its class list.
   */
  public static boolean addCSSClass(final HTMLElement element, final String className) {
    if (hasCSSClass(element, className)) {
      return false;
    }
    else {
      element.getClassList().add(className);

      return true;
    }
  }

  /**
   * @param element
   *          Must not be null.
   * @param className
   *          The name of a CSS class. Must not be null.
   * @return True iff the given element has the given CSS class as part of its class list.
   */
  public static boolean hasCSSClass(final HTMLElement element, final String className) {
    return element.getClassList().contains(className);
  }

  /**
   * @param tokenList
   *          Must not be null.
   * @return A sequential, ordered {@link Stream} of tokens from the given {@link DOMTokenList}.
   */
  public static Stream<String> tokenStream(final DOMTokenList tokenList) {
    return Stream
      .iterate(0, n -> n + 1)
      .limit(tokenList.getLength())
      .map(i -> tokenList.item(i));
  }

  /**
   * @param styleDeclaration
   *          Must not be null.
   * @return A stream of property names from the given style declaration.
   */
  public static Stream<String> cssPropertyNameStream(final CSSStyleDeclaration styleDeclaration) {
    return Arrays
            .stream(styleDeclaration.getCssText() != null
                    ? styleDeclaration.getCssText().split(";") : new String[0])
            .map(style -> style.split(":", 2)[0].trim())
            .filter(propertyName -> !propertyName.isEmpty());
  }

  /**
   * Appends the underlying {@link HTMLElement} of a {@link Widget} to another {@link HTMLElement}, in a way that does
   * not break GWT Widget events.
   *
   * @param parent
   *          The parent element that is appended to. Must not be null.
   * @param child
   *          The child Widget, whose underlying HTML element will be appended to the parent. Must not be null.
   */
  public static void appendWidgetToElement(final HTMLElement parent, final IsWidget child) {
    appendWidgetToElement(parent, child.asWidget());
  }

  /**
   * Appends the underlying {@link HTMLElement} of a {@link Widget} to another {@link HTMLElement}, in a way that does
   * not break GWT Widget events.
   *
   * @param parent
   *          The parent element that is appended to. Must not be null.
   * @param child
   *          The child Widget, whose underlying HTML element will be appended to the parent. Must not be null.
   */
  public static void appendWidgetToElement(final HTMLElement parent, final Widget child) {
    if (child.isAttached()) {
      child.removeFromParent();
    }
    RootPanel.detachOnWindowClose(child);
    parent.appendChild(nativeCast(child.getElement()));
    onAttach(child);
  }

  /**
   * <p>
   * Remove a {@link Widget} from its parent. Use this to undo calls to
   * {@link #appendWidgetToElement(HTMLElement, IsWidget)}.
   *
   * <p>
   * This works like calling {@code child.asWidget().removeFromParent()} except that if {@code child} is in the
   * {@link RootPanel#isInDetachList(Widget) dettach list} its underlying HTML element will also be removed from its
   * parent.
   *
   * @param child
   *          The child Widget, whose underlying HTML element will be removed from the parent. Must not be null.
   */
  public static void removeFromParent(final IsWidget child) {
    removeFromParent(child.asWidget());
  }

  /**
   * <p>
   * Remove a {@link Widget} from its parent. Use this to undo calls to
   * {@link #appendWidgetToElement(HTMLElement, Widget)}.
   *
   * <p>
   * This works like calling {@code child.removeFromParent()} except that if {@code child} is in the
   * {@link RootPanel#isInDetachList(Widget) dettach list} its underlying HTML element will also be removed from its
   * parent.
   *
   * @param child
   *          The child Widget, whose underlying HTML element will be removed from the parent. Must not be null.
   */
  public static void removeFromParent(final Widget child) {
    final boolean wasInDettachList = RootPanel.isInDetachList(child);
    child.removeFromParent();
    if (wasInDettachList) {
      child.getElement().removeFromParent();
    }
  }

  /**
   * Adds a unique enumerated CSS class to an element's class list removing all others
   * in the enumerated {@link Style.HasCssName}. Other CSS classes not in the enumerated
   * {@link Style.HasCssName} are preserved.
   *
   * @param element
   *          Must not be null.
   * @param enumClass
   *          The {@link Style.HasCssName} class. Must not be null.
   * @param style
   *          The enumerated {@link Style.HasCssName} element. Must not be null.
   */
  public static <E extends Style.HasCssName, F extends Enum<? extends Style.HasCssName>> void addUniqueEnumStyleName(final HTMLElement element,
                                                                                                                     final Class<F> enumClass,
                                                                                                                     final E style) {
    removeEnumStyleNames(element,
                         enumClass);
    addEnumStyleName(element,
                     style);
  }

  /**
   * Removes all of the enumerated CSS classes on an element's class list.
   *
   * @param element
   *          Must not be null.
   * @param enumClass
   *          The {@link Style.HasCssName} class. Must not be null.
   */
  public static <E extends Enum<? extends Style.HasCssName>> void removeEnumStyleNames(final HTMLElement element,
                                                                                       final Class<E> enumClass) {
    for (final Enum<? extends Style.HasCssName> constant : enumClass.getEnumConstants()) {
      final String cssClass = ((Style.HasCssName) constant).getCssName();

      if (cssClass != null && !cssClass.isEmpty()) {
        removeCSSClass(element,
                       cssClass);
      }
    }
  }

  /**
   * Adds an enumerated CSS class to an element's class list.
   *
   * @param element
   *          Must not be null.
   * @param style
   *          The enumerated {@link Style.HasCssName} element. Must not be null.
   */
  public static <E extends Style.HasCssName> void addEnumStyleName(final HTMLElement element,
                                                                   final E style) {
    if (style != null && style.getCssName() != null && !style.getCssName().isEmpty()) {
      addCSSClass(element,
                  style.getCssName());
    }
  }

  /**
   * Removes the enumerated CSS class from an element's class list.
   *
   * @param element
   *          Must not be null.
   * @param style
   *          The enumerated {@link Style.HasCssName} element. Must not be null.
   */
  public static <E extends Style.HasCssName> void removeEnumStyleName(final HTMLElement element,
                                                                      final E style) {
    if (style != null && style.getCssName() != null && !style.getCssName().isEmpty()) {
      removeCSSClass(element,
                     style.getCssName());
    }
  }

  private static native void onAttach(Widget w)/*-{
    w.@com.google.gwt.user.client.ui.Widget::onAttach()();
  }-*/;

  private static native HTMLElement nativeCast(com.google.gwt.dom.client.Element gwtElement)/*-{
    return gwtElement;
  }-*/;
}
