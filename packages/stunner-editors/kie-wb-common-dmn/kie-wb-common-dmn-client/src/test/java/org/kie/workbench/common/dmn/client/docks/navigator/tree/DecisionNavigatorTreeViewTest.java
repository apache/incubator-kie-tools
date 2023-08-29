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

package org.kie.workbench.common.dmn.client.docks.navigator.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Node;
import elemental2.dom.Text;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemBuilder;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.Mock;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.CONTEXT;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.DECISION;
import static org.mockito.ArgumentMatchers.any;
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
    private elemental2.dom.HTMLElement icon;

    @Mock
    private elemental2.dom.HTMLElement textContent;

    @Mock
    private HTMLInputElement inputText;

    @Mock
    private elemental2.dom.HTMLElement save;

    @Mock
    private elemental2.dom.HTMLElement edit;

    @Mock
    private elemental2.dom.HTMLElement remove;

    @Mock
    private HTMLUListElement subItems;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Mock
    private EventSourceMock<LockRequiredEvent> locker;

    private DecisionNavigatorTreeView treeView;

    private DecisionNavigatorTreeView.TreeItem treeItem;

    @Before
    public void setup() {
        treeView = spy(new DecisionNavigatorTreeView(view, items, managedInstance, util));
        treeItem = spy(new DecisionNavigatorTreeView.TreeItem(textContent, inputText, icon, subItems, save, edit, remove, locker, readOnlyProvider));
    }

    @Test
    public void testClean() {

        final Element element = mock(Element.class);
        items.firstChild = element;

        when(items.removeChild(element)).then(a -> {
            items.firstChild = null;
            return element;
        });

        treeView.clean();

        verify(items).removeChild(element);
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
    public void testTreeItemOnTextContentClick() {

        final ClickEvent event = mock(ClickEvent.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(item).when(treeItem).getItem();

        treeItem.onTextContentClick(event);

        verify(item).onClick();
    }

    @Test
    public void testTreeItemOnInputTextKeyPressWhenKeyIsEnter() {

        final KeyDownEvent event = mock(KeyDownEvent.class);
        final NativeEvent nativeEvent = mock(NativeEvent.class);

        doNothing().when(treeItem).save();
        when(event.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getKeyCode()).thenReturn(13);

        treeItem.onInputTextKeyPress(event);

        verify(treeItem).save();
    }

    @Test
    public void testTreeItemOnInputTextKeyPressWhenKeyIsNotEnter() {

        final KeyDownEvent event = mock(KeyDownEvent.class);
        final NativeEvent nativeEvent = mock(NativeEvent.class);

        when(event.getNativeEvent()).thenReturn(nativeEvent);
        when(nativeEvent.getKeyCode()).thenReturn(99);

        treeItem.onInputTextKeyPress(event);

        verify(treeItem, never()).save();
    }

    @Test
    public void testOnInputTextBlur() {
        final BlurEvent event = mock(BlurEvent.class);
        doNothing().when(treeItem).save();
        treeItem.onInputTextBlur(event);
        verify(treeItem).save();
    }

    @Test
    public void testOnSaveClick() {
        final ClickEvent event = mock(ClickEvent.class);
        doNothing().when(treeItem).save();
        treeItem.onSaveClick(event);
        verify(treeItem).save();
    }

    @Test
    public void testOnEditClick() {

        final HTMLElement element = mock(HTMLElement.class);
        final ClickEvent event = mock(ClickEvent.class);
        final org.jboss.errai.common.client.dom.DOMTokenList tokenList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);

        doReturn(element).when(treeItem).getElement();
        when(element.getClassList()).thenReturn(tokenList);

        treeItem.onEditClick(event);

        verify(tokenList).add("editing");
    }

    @Test
    public void testOnRemoveClick() {

        final HTMLElement element = mock(HTMLElement.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final ClickEvent event = mock(ClickEvent.class);
        final org.jboss.errai.common.client.dom.DOMTokenList tokenList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);

        doReturn(element).when(treeItem).getElement();
        doReturn(item).when(treeItem).getItem();
        when(element.getClassList()).thenReturn(tokenList);

        treeItem.onRemoveClick(event);

        verify(item).onRemove();
        verify(locker).fire(any());
    }

    @Test
    public void testSave() {

        final HTMLElement element = mock(HTMLElement.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final org.jboss.errai.common.client.dom.DOMTokenList tokenList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);

        doNothing().when(treeItem).updateLabel();
        doReturn(element).when(treeItem).getElement();
        doReturn(item).when(treeItem).getItem();
        when(element.getClassList()).thenReturn(tokenList);

        treeItem.save();

        verify(item).setLabel(inputText.value);
        verify(tokenList).remove("editing");
        verify(treeItem).updateLabel();
        verify(item).onUpdate();
        verify(locker).fire(any());
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
        doNothing().when(treeItem).initOuiaComponentAttributes();

        final DecisionNavigatorTreeView.TreeItem actualTreeItem = treeItem.setup(expectedItem, children);
        final DecisionNavigatorItem actualItem = treeItem.getItem();

        verify(treeItem).updateDataUUID();
        verify(treeItem).updateTitle();
        verify(treeItem).updateCSSClass();
        verify(treeItem).updateLabel();
        verify(treeItem).updateSubItems(children);
        verify(treeItem).initOuiaComponentAttributes();

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
        when(item.isEditable()).thenReturn(true);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList).add("parent-node");
        verify(classList).add("editable");
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
        when(item.isEditable()).thenReturn(false);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList, never()).add("parent-node");
        verify(classList, never()).add("editable");
    }

    @Test
    public void testTreeItemUpdateCSSClassWhenItemHasChildrenAndIsReadOnly() {

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
        when(item.isEditable()).thenReturn(true);
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList).add("parent-node");
        verify(classList, never()).add("editable");
    }

    @Test
    public void testTreeItemUpdateCSSClassWhenItemDoesNotHaveChildrenAndIsReadOnly() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final org.jboss.errai.common.client.dom.DOMTokenList classList = mock(org.jboss.errai.common.client.dom.DOMTokenList.class);
        final HTMLElement element = mock(HTMLElement.class);
        final String cssClass = "css-class";

        doReturn(item).when(treeItem).getItem();
        doReturn(element).when(treeItem).getElement();
        doReturn(cssClass).when(treeItem).getCSSClass(item);
        when(element.getClassList()).thenReturn(classList);
        when(item.isEditable()).thenReturn(false);
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        treeItem.updateCSSClass();

        verify(classList).add(cssClass);
        verify(classList, never()).add("parent-node");
        verify(classList, never()).add("editable");
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

        assertEquals(label, inputText.value);
        verify(textContent).appendChild(textNode);
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
    public void testTreeItemInitOuiaAttributes() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final Text textNode = mock(Text.class);
        final String label = "label";
        final HTMLElement element = mock(HTMLElement.class);

        doReturn(element).when(treeItem).getElement();
        doReturn(item).when(treeItem).getItem();
        doReturn(DECISION).when(item).getType();
        doReturn(textNode).when(treeItem).getTextNode(label);

        when(item.getLabel()).thenReturn(label);

        treeItem.initOuiaComponentAttributes();

        verify(element).setAttribute(OuiaComponentTypeAttribute.COMPONENT_TYPE, "dmn-graph-navigator-decision");
        verify(element).setAttribute(OuiaComponentIdAttribute.COMPONENT_ID, "dmn-graph-navigator-decision-label");
    }

    @Test
    public void testTreeItemInitOuiaAttributesIllegalState() {
        doReturn(null).when(treeItem).getItem();

        assertThatThrownBy(() -> treeItem.initOuiaComponentAttributes()).hasMessage("Decision Navigator item can not be null");
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
        final Command onClick = () -> {/* Nothing. */};
        final String expectedCSSClass = "kie-context";
        final DecisionNavigatorItem item = new DecisionNavigatorItemBuilder().withUUID(uuid).withLabel(label).withType(subItem).withOnClick(onClick).build();
        final String actualCSSClass = treeItem.getCSSClass(item);

        assertEquals(expectedCSSClass, actualCSSClass);
    }

    private DecisionNavigatorItem makeItem(final String uuid) {
        return makeItem(uuid, null);
    }

    private DecisionNavigatorItem makeItem(final String uuid,
                                           final String parentUUID) {
        return new DecisionNavigatorItemBuilder().withUUID(uuid).withParentUUID(parentUUID).build();
    }
}
