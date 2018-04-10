/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Node;
import elemental2.dom.Text;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.CONTEXT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorTreeViewTest {

    @Mock
    private HTMLDivElement view;

    @Mock
    private HTMLDivElement items;

    @Mock
    private ManagedInstance<DecisionNavigatorTreeView.TreeItem> managedInstance;

    @Mock
    private Elemental2DomUtil util;

    @Mock
    private HTMLDivElement text;

    @Mock
    private elemental2.dom.HTMLElement icon;

    @Mock
    private HTMLUListElement subItems;

    private DecisionNavigatorTreeView treeView;

    private DecisionNavigatorTreeView.TreeItem treeItem;

    @Before
    public void setup() {
        treeView = spy(new DecisionNavigatorTreeView(view, items, managedInstance, util));
        treeItem = spy(new DecisionNavigatorTreeView.TreeItem(text, icon, subItems));
    }

    @Test
    public void testClean() {

        items.innerHTML = "123";

        treeView.clean();

        assertEquals("", items.innerHTML);
    }

    @Test
    public void testSetup() {

        final List<DecisionNavigatorItem> items = new ArrayList<>();
        final Element element = mock(Element.class);

        doReturn(element).when(treeView).makeTree(items);

        treeView.setup(items);

        verify(this.items).appendChild(element);
    }

    @Test
    public void testAddItemWhenElementExists() {

        final String itemUUID = "itemUUID";
        final String nextItemUUID = "nextItemUUID";
        final String parentUUID = "parentUUID";
        final DecisionNavigatorItem item = makeItem(itemUUID, parentUUID);
        final DecisionNavigatorItem nextItem = makeItem(nextItemUUID, parentUUID);
        final Element parentElement = mock(Element.class);
        final Element parentChildrenElement = mock(Element.class);
        final Element newChild = mock(Element.class);
        final Element refChild = mock(Element.class);
        final DOMTokenList domTokenList = mock(DOMTokenList.class);

        parentElement.classList = domTokenList;

        doReturn(parentElement).when(treeView).findTreeItemElement(parentUUID);
        doReturn(parentChildrenElement).when(treeView).findTreeItemChildrenElement(parentUUID);
        doReturn(newChild).when(treeView).makeTreeItemElement(item);
        doReturn(refChild).when(treeView).findItem(nextItem);

        treeView.addItem(item, nextItem);

        verify(domTokenList).add("parent-node");
        verify(parentChildrenElement).insertBefore(newChild, refChild);
    }

    @Test
    public void testUpdate() {

        final String itemUUID = "itemUUID";
        final String nextItemUUID = "nextItemUUID";
        final String parentUUID = "parentUUID";
        final DecisionNavigatorItem item = makeItem(itemUUID, parentUUID);
        final DecisionNavigatorItem nextItem = makeItem(nextItemUUID, parentUUID);
        final Element parentElement = mock(Element.class);
        final Element oldChild = mock(Element.class);
        final Element newChild = mock(Element.class);
        final Element refChild = mock(Element.class);

        doReturn(parentElement).when(treeView).findTreeItemChildrenElement(parentUUID);
        doReturn(oldChild).when(treeView).findItem(item);
        doReturn(newChild).when(treeView).makeTreeItemElement(item);
        doReturn(refChild).when(treeView).findItem(nextItem);

        treeView.update(item, nextItem);

        verify(oldChild).remove();
        verify(parentElement).insertBefore(newChild, refChild);
    }

    @Test
    public void testMakeTree() {

        final DecisionNavigatorItem item = makeItem("uuid");
        final List<DecisionNavigatorItem> items = Collections.singletonList(item);
        final Element expectedTreeElement = mock(Element.class);
        final Element treeItemElement = mock(Element.class);

        doReturn(expectedTreeElement).when(treeView).createElement("ul");
        doReturn(treeItemElement).when(treeView).makeTreeItemElement(item);

        final Element actualTreeElement = treeView.makeTree(items);

        verify(expectedTreeElement).appendChild(treeItemElement);
        assertEquals(expectedTreeElement, actualTreeElement);
    }

    @Test
    public void testFindTreeItemTextElement() {
        treeView.findTreeItemTextElement("123");

        verify(treeView).itemsQuerySelector("[data-uuid=\"123\"] div");
    }

    @Test
    public void testFindTreeItemChildrenElement() {
        treeView.findTreeItemChildrenElement("123");

        verify(treeView).itemsQuerySelector("[data-uuid=\"123\"] ul");
    }

    @Test
    public void testFindTreeItemElement() {
        treeView.findTreeItemElement("123");

        verify(treeView).itemsQuerySelector("[data-uuid=\"123\"]");
    }

    @Test
    public void testItemsQuerySelector() {

        final String selector = "selector";

        treeView.itemsQuerySelector(selector);

        verify(items).querySelector(selector);
    }

    @Test
    public void testMakeTreeItemElement() {

        final DecisionNavigatorItem item = makeItem("uuid");
        final DecisionNavigatorTreeView.TreeItem newTreeItem = mock(DecisionNavigatorTreeView.TreeItem.class);
        final DecisionNavigatorTreeView.TreeItem treeItem = mock(DecisionNavigatorTreeView.TreeItem.class);
        final Element childrenTree = mock(Element.class);
        final HTMLElement htmlElement = mock(HTMLElement.class);
        final elemental2.dom.HTMLElement expectedHtmlElement = mock(elemental2.dom.HTMLElement.class);

        doReturn(childrenTree).when(treeView).makeTree(item.getChildren());
        when(managedInstance.get()).thenReturn(newTreeItem);
        when(newTreeItem.setup(item, childrenTree)).thenReturn(treeItem);
        when(treeItem.getElement()).thenReturn(htmlElement);
        when(util.asHTMLElement(htmlElement)).thenReturn(expectedHtmlElement);

        final Element actualHtmlElement = treeView.makeTreeItemElement(item);

        assertEquals(expectedHtmlElement, actualHtmlElement);
    }

    @Test
    public void testFindItem() {

        final String uuid = "uuid";
        final DecisionNavigatorItem item = makeItem(uuid);
        final Element expectedElement = mock(Element.class);

        doReturn(expectedElement).when(treeView).findTreeItemElement(uuid);

        final Element actualElement = treeView.findItem(item);

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testHasItemWhenItemExists() {

        final String uuid = "uuid";
        final DecisionNavigatorItem item = makeItem(uuid);
        final Element element = mock(Element.class);

        doReturn(element).when(treeView).findItem(item);

        assertTrue(treeView.hasItem(item));
    }

    @Test
    public void testHasItemWhenItemDoesNotExist() {

        final String uuid = "uuid";
        final DecisionNavigatorItem item = makeItem(uuid);

        assertFalse(treeView.hasItem(item));
    }

    @Test
    public void testRemove() {

        final DecisionNavigatorItem item = makeItem("uuid");
        final Element element = mock(Element.class);

        doReturn(element).when(treeView).findItem(item);

        treeView.remove(item);

        verify(element).remove();
    }

    @Test
    public void testSelect() {

        final String uuid = "uuid";
        final Element newElement = mock(Element.class);
        final Element oldElement = mock(Element.class);
        final DOMTokenList newDomTokenList = mock(DOMTokenList.class);
        final DOMTokenList oldDomTokenList = mock(DOMTokenList.class);

        newElement.classList = newDomTokenList;
        oldElement.classList = oldDomTokenList;
        doReturn(newElement).when(treeView).findTreeItemTextElement(uuid);
        doReturn(oldElement).when(treeView).getSelectedElement();

        treeView.select(uuid);

        verify(oldDomTokenList).remove("selected");
        verify(newDomTokenList).add("selected");
        verify(treeView).deselect(oldElement);
        verify(treeView).select(newElement);
        verify(treeView).setSelectedElement(newElement);
    }

    @Test
    public void testDeselect() {

        final Element element = mock(Element.class);
        final DOMTokenList domTokenList = mock(DOMTokenList.class);

        element.classList = domTokenList;
        doReturn(element).when(treeView).getSelectedElement();

        treeView.deselect();

        verify(domTokenList).remove("selected");
        verify(treeView).deselect(element);
    }

    @Test
    public void testTreeItemOnIconClick() {

        final ClickEvent event = mock(ClickEvent.class);

        doNothing().when(treeItem).toggle();

        treeItem.onIconClick(event);

        verify(treeItem).toggle();
        verify(event).stopPropagation();
    }

    @Test
    public void testTreeItemOnTextClick() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final ClickEvent event = mock(ClickEvent.class);

        doReturn(item).when(treeItem).getItem();

        treeItem.onTextClick(event);

        verify(item).onClick();
    }

    @Test
    public void testTreeItemSetup() {

        final DecisionNavigatorItem expectedItem = mock(DecisionNavigatorItem.class);
        final Element children = mock(Element.class);

        doNothing().when(treeItem).updateDataUUID();
        doNothing().when(treeItem).updateTitle();
        doNothing().when(treeItem).updateCSSClass();
        doNothing().when(treeItem).updateLabel();
        doNothing().when(treeItem).updateSubItems(children);

        final DecisionNavigatorTreeView.TreeItem actualTreeItem = treeItem.setup(expectedItem, children);
        final DecisionNavigatorItem actualItem = treeItem.getItem();

        verify(treeItem).updateDataUUID();
        verify(treeItem).updateTitle();
        verify(treeItem).updateCSSClass();
        verify(treeItem).updateLabel();
        verify(treeItem).updateSubItems(children);

        assertEquals(treeItem, actualTreeItem);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testTreeItemUpdateDataUUID() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final HTMLElement element = mock(HTMLElement.class);
        final String uuid = "uuid";

        doReturn(item).when(treeItem).getItem();
        doReturn(element).when(treeItem).getElement();
        when(item.getUUID()).thenReturn(uuid);

        treeItem.updateDataUUID();

        verify(element).setAttribute("data-uuid", uuid);
    }

    @Test
    public void testTreeItemUpdateTitle() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final HTMLElement element = mock(HTMLElement.class);
        final String label = "label";

        doReturn(item).when(treeItem).getItem();
        doReturn(element).when(treeItem).getElement();
        when(item.getLabel()).thenReturn(label);

        treeItem.updateTitle();

        verify(element).setAttribute("title", label);
    }

    @Test
    public void testTreeItemUpdateCSSClassWhenItemHasChildren() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem child = mock(DecisionNavigatorItem.class);
        final TreeSet<DecisionNavigatorItem> children = new TreeSet<DecisionNavigatorItem>() {{
            add(child);
        }};
        final org.jboss.errai.common.client.dom.DOMTokenList classList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);
        final HTMLElement element = mock(HTMLElement.class);
        final String cssClass = "css-class";

        doReturn(item).when(treeItem).getItem();
        doReturn(element).when(treeItem).getElement();
        doReturn(cssClass).when(treeItem).getCSSClass(item);
        when(element.getClassList()).thenReturn(classList);
        when(item.getChildren()).thenReturn(children);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList).add("parent-node");
    }

    @Test
    public void testTreeItemUpdateCSSClassWhenItemDoesNotHaveChildren() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final org.jboss.errai.common.client.dom.DOMTokenList classList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);
        final HTMLElement element = mock(HTMLElement.class);
        final String cssClass = "css-class";

        doReturn(item).when(treeItem).getItem();
        doReturn(element).when(treeItem).getElement();
        doReturn(cssClass).when(treeItem).getCSSClass(item);
        when(element.getClassList()).thenReturn(classList);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList, never()).add("parent-node");
    }

    @Test
    public void testTreeItemUpdateLabel() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final Text textNode = mock(Text.class);
        final String label = "label";

        doReturn(item).when(treeItem).getItem();
        doReturn(textNode).when(treeItem).getTextNode(label);
        when(item.getLabel()).thenReturn(label);

        treeItem.updateLabel();

        verify(text).appendChild(textNode);
    }

    @Test
    public void testTreeItemUpdateSubItems() {

        final Element children = mock(Element.class);
        final Node parentNode = mock(Node.class);

        subItems.parentNode = parentNode;

        treeItem.updateSubItems(children);

        verify(parentNode).replaceChild(children, subItems);
    }

    @Test
    public void testTreeItemToggle() {

        final HTMLElement element = mock(HTMLElement.class);
        final org.jboss.errai.common.client.dom.DOMTokenList classList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);

        doReturn(element).when(treeItem).getElement();
        doReturn(classList).when(element).getClassList();

        treeItem.toggle();

        verify(classList).toggle("closed");
    }

    @Test
    public void testTreeItemGetCSSClass() {

        final String uuid = "uuid";
        final String label = "label";
        final DecisionNavigatorItem.Type subItem = CONTEXT;
        final Command onClick = () -> {
        };
        final String expectedCSSClass = "kie-context";
        final String actualCSSClass = treeItem.getCSSClass(new DecisionNavigatorItem(uuid, label, subItem, onClick, null));

        assertEquals(expectedCSSClass, actualCSSClass);
    }

    private DecisionNavigatorItem makeItem(final String uuid) {
        return makeItem(uuid, null);
    }

    private DecisionNavigatorItem makeItem(final String uuid,
                                           final String parentUUID) {
        return new DecisionNavigatorItem(uuid, null, null, null, parentUUID);
    }
}
