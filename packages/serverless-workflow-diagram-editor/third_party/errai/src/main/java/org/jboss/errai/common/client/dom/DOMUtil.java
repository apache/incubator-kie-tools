package org.jboss.errai.common.client.dom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import jsinterop.base.Js;

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
    public static Optional<HTMLElement> getFirstChildElement(final HTMLElement element) {

        DomGlobal.console.warn("getFirstChildElement " + element + " " + element.childElementCount);

        for (final Node child : nodeIterable(element.childNodes)) {
            if (isElement(child)) {
                return Optional.ofNullable(Js.uncheckedCast(child));
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
    public static Optional<HTMLElement> getLastChildElement(final HTMLElement element) {
        final NodeList children = element.childNodes;
        for (int i = children.getLength()-1; i > -1; i--) {
            if (isElement(Js.uncheckedCast(children.item(i)))) {
                return Optional.ofNullable((HTMLElement) children.item(i));
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
        return node.nodeType == Node.ELEMENT_NODE;
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
                    return (Node) nodeList.item(index++);
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
    public static Iterable<HTMLElement> elementIterable(final NodeList nodeList) {
        return () -> elementIterator(nodeList);
    }

    /**
     * @param nodeList
     *          Must not be null.
     * @return An iterator for the given node list that ignores non-element nodes.
     */
    public static Iterator<HTMLElement> elementIterator(final NodeList nodeList) {
        return new Iterator<HTMLElement>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                while (i < nodeList.getLength() && !isElement(Js.uncheckedCast(nodeList.item(i)))) {
                    i++;
                }
                return i < nodeList.getLength();
            }

            @Override
            public HTMLElement next() {
                if (hasNext()) {
                    return (HTMLElement) nodeList.item(i++);
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
    public static boolean removeFromParent(final HTMLElement element) {
        if (element.parentElement != null) {
            element.parentElement.removeChild(element);

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
        if (node == null || node.lastChild == null) {
            return false;
        }

        final boolean hadChildren = node.lastChild != null;
        while (node.lastChild != null) {
            node.removeChild(node.lastChild);
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
        for (final HTMLElement child : elementIterable(node.childNodes)) {
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
            element.classList.remove(className);

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
            element.classList.add(className);

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
        return element.classList.contains(className);
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
                .stream(styleDeclaration.cssText != null
                        ? styleDeclaration.cssText.split(";") : new String[0])
                .map(style -> style.split(":", 2)[0].trim())
                .filter(propertyName -> !propertyName.isEmpty());
    }

    public static int getAbsoluteLeft(HTMLElement elem) {
        int left = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null) {
            left -= curr.scrollLeft;
            curr = (HTMLElement) curr.parentNode;
        }
        while (elem != null) {
            left += elem.offsetLeft;
            elem = (HTMLElement) elem.offsetParent;
        }
        return left;
    }

    public static int getAbsoluteTop(HTMLElement elem) {
        int top = 0;
        HTMLElement curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent != null) {
            top -= curr.scrollTop;
            curr = (HTMLElement) curr.parentNode;
        }
        while (elem != null) {
            top += elem.offsetTop;
            elem = (HTMLElement) elem.offsetParent;
        }
        return top;
    }

}
