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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.appformer.client.context.Channel;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.jsinterop.JavaClass;
import org.kie.workbench.common.dmn.client.editors.types.jsinterop.JavaField;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListTest {

    @Mock
    private DataTypeList.View view;

    @Mock
    private ManagedInstance<DataTypeListItem> listItems;

    @Mock
    private DataTypeListItem treeGridItem;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeSearchBar searchBar;

    @Mock
    private Consumer<DataTypeListItem> listItemConsumer;

    @Mock
    private DNDListComponent dndListComponent;

    @Mock
    private DNDDataTypesHandler dndDataTypesHandler;

    @Mock
    private DataTypeListHighlightHelper highlightHelper;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private KogitoChannelHelper kogitoChannelHelperMock;

    private DataTypeStore dataTypeStore;

    private DataTypeStackHash dataTypeStackHash;

    @Captor
    private ArgumentCaptor<List<DataTypeListItem>> listItemsCaptor;

    @Captor
    private ArgumentCaptor<List<HTMLElement>> htmlElementsCaptor;

    @Captor
    private ArgumentCaptor<Supplier<CommandResult>> supplierCaptor;

    private DataTypeList dataTypeList;

    @Before
    public void setup() {
        dataTypeStore = new DataTypeStore();
        dataTypeStackHash = new DataTypeStackHash(dataTypeStore);
        dataTypeList = spy(new DataTypeList(view,
                                            listItems,
                                            dataTypeManager,
                                            searchBar,
                                            dndListComponent,
                                            dataTypeStackHash,
                                            dndDataTypesHandler,
                                            highlightHelper,
                                            commandManager,
                                            sessionManager,
                                            kogitoChannelHelperMock));

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(listItems.get()).thenReturn(treeGridItem);
    }

    @Test
    public void testSetup() {

        final BiConsumer<Element, Element> consumer = (a, b) -> {/* Nothing. */};

        doReturn(consumer).when(dataTypeList).getOnDropDataType();

        dataTypeList.setup();

        verify(view).init(dataTypeList);
        verify(highlightHelper).init(dataTypeList);
        verify(dndDataTypesHandler).init(dataTypeList);
        verify(dndListComponent).setOnDropItem(consumer);
    }

    @Test
    public void activateReactComponentsOnVsCodeDesktop() {
        // Included channel
        when(kogitoChannelHelperMock.isCurrentChannelEnabled(Channel.VSCODE_DESKTOP)).thenReturn(true);

        dataTypeList.activateReactComponents();

        verify(kogitoChannelHelperMock, times(1)).isCurrentChannelEnabled(Channel.VSCODE_DESKTOP);
        verify(view, times(1)).renderImportJavaClasses();

        // Excluded channel
        reset(kogitoChannelHelperMock, view);
        when(kogitoChannelHelperMock.isCurrentChannelEnabled(Channel.VSCODE_DESKTOP)).thenReturn(false);

        dataTypeList.activateReactComponents();

        verify(kogitoChannelHelperMock, times(1)).isCurrentChannelEnabled(Channel.VSCODE_DESKTOP);
        verify(view, never()).renderImportJavaClasses();
    }

    @Test
    public void activateReactComponentsOnVsCodeWeb() {
        // Included channel
        when(kogitoChannelHelperMock.isCurrentChannelEnabled(Channel.VSCODE_WEB)).thenReturn(true);

        dataTypeList.activateReactComponents();

        verify(kogitoChannelHelperMock, times(1)).isCurrentChannelEnabled(Channel.VSCODE_WEB);
        verify(view, times(1)).renderImportJavaClasses();

        // Excluded channel
        reset(kogitoChannelHelperMock, view);
        when(kogitoChannelHelperMock.isCurrentChannelEnabled(Channel.VSCODE_WEB)).thenReturn(false);

        dataTypeList.activateReactComponents();

        verify(kogitoChannelHelperMock, times(1)).isCurrentChannelEnabled(Channel.VSCODE_WEB);
        verify(view, never()).renderImportJavaClasses();
    }

    @Test
    public void testGetOnDropDataType() {

        final Element e1 = mock(Element.class);
        final Element e2 = mock(Element.class);

        dataTypeList.getOnDropDataType().accept(e1, e2);

        verify(dndDataTypesHandler).onDropDataType(e1, e2);
    }

    @Test
    public void testGetElement() {

        final HTMLElement htmlElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(htmlElement);

        assertEquals(htmlElement, dataTypeList.getElement());
    }

    @Test
    public void testSetupItems() {

        final DataType dataType1 = makeDataType("item", "iITem");
        final DataType dataType2 = makeDataType("item", "iITem");
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final List<DataType> dataTypes = asList(dataType1, dataType2);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2);

        doReturn(listItems).when(dataTypeList).makeDataTypeListItems(dataTypes);

        dataTypeList.setupItems(dataTypes);

        final InOrder inOrder = Mockito.inOrder(dndListComponent, dataTypeList, view);

        inOrder.verify(dndListComponent).clear();
        inOrder.verify(dataTypeList).makeDataTypeListItems(dataTypes);
        inOrder.verify(dndListComponent).refreshItemsPosition();
        inOrder.verify(view).showOrHideNoCustomItemsMessage();
        inOrder.verify(view).showReadOnlyMessage(false);
        inOrder.verify(dataTypeList).collapseItemsInTheFirstLevel();

        assertEquals(listItems, dataTypeList.getItems());
    }

    @Test
    public void testCollapseItemsInTheFirstLevel() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem4 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3, listItem4);

        when(listItem1.getLevel()).thenReturn(1);
        when(listItem2.getLevel()).thenReturn(2);
        when(listItem3.getLevel()).thenReturn(1);
        when(listItem4.getLevel()).thenReturn(2);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseItemsInTheFirstLevel();

        verify(listItem1).collapse();
        verify(listItem2, never()).collapse();
        verify(listItem3).collapse();
        verify(listItem4, never()).collapse();
    }

    @Test
    public void testExpandAllWhenSearchBarIsEnabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(true);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.expandAll();

        verify(listItem1, never()).expand();
        verify(listItem2, never()).expand();
        verify(listItem3, never()).expand();
    }

    @Test
    public void testExpandAllWhenSearchBarIsDisabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(false);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.expandAll();

        verify(listItem1).expand();
        verify(listItem2).expand();
        verify(listItem3).expand();
    }

    @Test
    public void testCollapseAllWhenSearchBarIsEnabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(true);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseAll();

        verify(listItem1, never()).collapse();
        verify(listItem2, never()).collapse();
        verify(listItem3, never()).collapse();
    }

    @Test
    public void testCollapseAllWhenSearchBarIsDisabledEnabled() {

        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(listItem1, listItem2, listItem3);

        when(searchBar.isEnabled()).thenReturn(false);
        doReturn(listItems).when(dataTypeList).getItems();

        dataTypeList.collapseAll();

        verify(listItem1).collapse();
        verify(listItem2).collapse();
        verify(listItem3).collapse();
    }

    @Test
    public void testMakeDataTypeListItemsWithoutSubItems() {

        final DataType dataType1 = makeDataType("item", "iITem");
        final DataType dataType2 = makeDataType("item", "iITem");
        final List<DataType> dataTypes = asList(dataType1, dataType2);

        dataTypeList.makeDataTypeListItems(dataTypes);

        verify(dataTypeList).makeTreeListItems(eq(dataType1), eq(1));
        verify(dataTypeList).makeTreeListItems(eq(dataType2), eq(1));
        verify(dataTypeList, times(2)).makeTreeListItems(any(), anyInt());
    }

    @Test
    public void testMakeDataTypeListItemsWithSubItems() {

        final DataType subDataType3 = makeDataType("subItem3", "subItemType3");
        final DataType subDataType1 = makeDataType("subItem1", "subItemType1");
        final DataType subDataType2 = makeDataType("subItem2", "subItemType2", subDataType3);
        final DataType dataType = makeDataType("item", "iITem", subDataType1, subDataType2);
        final List<DataType> dataTypes = singletonList(dataType);

        dataTypeList.makeDataTypeListItems(dataTypes);

        verify(dataTypeList).makeTreeListItems(eq(dataType), eq(1));
        verify(dataTypeList).makeTreeListItems(eq(subDataType1), eq(2));
        verify(dataTypeList).makeTreeListItems(eq(subDataType2), eq(2));
        verify(dataTypeList).makeTreeListItems(eq(subDataType3), eq(3));
        verify(dataTypeList, times(4)).makeTreeListItems(any(), anyInt());
    }

    @Test
    public void testMakeTreeListItems() {

        final DataType item1 = makeDataType("item1", "iITem1");
        final DataType item2 = makeDataType("item2", "iITem2");
        final DataType item3 = makeDataType("item", "iITem", item1, item2);

        final List<DataTypeListItem> listItems = dataTypeList.makeTreeListItems(item3, 1);

        verify(dataTypeList).makeTreeListItems(item3, 1);
        verify(dataTypeList).makeTreeListItems(item1, 2);
        verify(dataTypeList).makeTreeListItems(item2, 2);
        assertEquals(3, listItems.size());
    }

    @Test
    public void testRefreshSubItems() {

        final DataTypeListItem listItem0 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeManager dataTypeManager1 = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManager2 = mock(DataTypeManager.class);
        final DataTypeManager dataTypeManager3 = mock(DataTypeManager.class);
        final ArrayList<Object> items = new ArrayList<>();
        final int level = 1;
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final HTMLElement element3 = mock(HTMLElement.class);

        when(listItem0.getLevel()).thenReturn(level);
        when(listItem0.getDataType()).thenReturn(dataType0);
        when(listItem0.getDragAndDropElement()).thenReturn(element0);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem1.getDragAndDropElement()).thenReturn(element1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem2.getDragAndDropElement()).thenReturn(element2);
        when(listItem3.getDataType()).thenReturn(dataType3);
        when(listItem3.getDragAndDropElement()).thenReturn(element3);
        when(dataTypeManager.from(dataType1)).thenReturn(dataTypeManager1);
        when(dataTypeManager.from(dataType2)).thenReturn(dataTypeManager2);
        when(dataTypeManager.from(dataType3)).thenReturn(dataTypeManager3);
        doReturn(singletonList(listItem1)).when(dataTypeList).makeTreeListItems(dataType1, level + 1);
        doReturn(singletonList(listItem2)).when(dataTypeList).makeTreeListItems(dataType2, level + 1);
        doReturn(singletonList(listItem3)).when(dataTypeList).makeTreeListItems(dataType3, level + 1);
        doReturn(items).when(dataTypeList).getItems();

        dataTypeList.refreshSubItemsFromListItem(listItem0, asList(dataType1, dataType2, dataType3));

        verify(view).cleanSubTypes(eq(dataType0));
        verify(view).addSubItems(eq(dataType0), listItemsCaptor.capture());
        verify(dataTypeManager1).withIndexedItemDefinition();
        verify(dataTypeManager2).withIndexedItemDefinition();
        verify(dataTypeManager3).withIndexedItemDefinition();
        verify(dndListComponent).setInitialPositionY(eq(element0), htmlElementsCaptor.capture());

        final List<HTMLElement> capturedElements = htmlElementsCaptor.getValue();
        final List<HTMLElement> expectedElements = asList(element1, element2, element3);
        assertEquals(expectedElements, capturedElements);

        final List<DataTypeListItem> actualItems = listItemsCaptor.getValue();
        final List<DataTypeListItem> expectedItems = asList(listItem1, listItem2, listItem3);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testMakeListItem() {

        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);

        doCallRealMethod().when(dataTypeList).makeListItem();
        when(listItems.get()).thenReturn(expectedListItem);

        final DataTypeListItem actualListItem = dataTypeList.makeListItem();

        verify(expectedListItem).init(eq(dataTypeList));
        assertEquals(expectedListItem, actualListItem);
    }

    @Test
    public void testRemoveItemByDataType() {

        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";

        doNothing().when(dataTypeList).removeItem(Mockito.<String>any());
        when(dataType.getUUID()).thenReturn(uuid);

        dataTypeList.removeItem(dataType);

        verify(dataTypeList).removeItem(uuid);
        verify(view).removeItem(dataType);
    }

    @Test
    public void testRemoveItemByUUID() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem0 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> items = new ArrayList<>(asList(dataTypeListItem0, dataTypeListItem1));

        when(dataType0.getUUID()).thenReturn("012");
        when(dataType1.getUUID()).thenReturn("345");
        when(dataTypeListItem0.getDataType()).thenReturn(dataType0);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeList.getItems()).thenReturn(items);

        dataTypeList.removeItem("012");

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        final List expected = singletonList(dataTypeListItem1);
        final List<DataTypeListItem> actual = dataTypeList.getItems();

        assertEquals(expected, actual);
    }

    @Test
    public void testFindItemWhenItemExists() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> existingItems = new ArrayList<>(asList(dataTypeListItem1, dataTypeListItem2));

        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeListItem2.getDataType()).thenReturn(dataType2);
        when(dataTypeList.getItems()).thenReturn(existingItems);

        final Optional<DataTypeListItem> item = dataTypeList.findItem(dataType1);

        assertEquals(dataTypeListItem1, item.get());
    }

    @Test
    public void testFindItemWhenItemDoesNotExist() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeListItem dataTypeListItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem dataTypeListItem2 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> existingItems = new ArrayList<>(asList(dataTypeListItem1, dataTypeListItem2));

        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);
        when(dataTypeListItem1.getDataType()).thenReturn(dataType1);
        when(dataTypeListItem2.getDataType()).thenReturn(dataType2);
        when(dataTypeList.getItems()).thenReturn(existingItems);

        final Optional<DataTypeListItem> item = dataTypeList.findItem(dataType3);

        assertFalse(item.isPresent());
    }

    @Test
    public void testRefreshItemsByUpdatedDataTypes() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final List<DataType> subDataTypes = asList(dataType2, dataType3);
        final List<DataType> existingItems = new ArrayList<>(asList(dataType1, dataType2, dataType3));

        doReturn(Optional.of(listItem)).when(dataTypeList).findItem(dataType1);
        doReturn(Optional.empty()).when(dataTypeList).findItem(dataType2);
        doReturn(Optional.empty()).when(dataTypeList).findItem(dataType3);
        doNothing().when(dataTypeList).refreshSubItemsFromListItem(any(), anyList());
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataType2.getUUID()).thenReturn(uuid2);
        when(dataType3.getUUID()).thenReturn(uuid3);
        when(dataType1.getSubDataTypes()).thenReturn(subDataTypes);

        dataTypeList.refreshItemsByUpdatedDataTypes(existingItems);

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(listItem).refresh();
        verify(dataTypeList).refreshSubItemsFromListItem(listItem, subDataTypes);
        verify(dndListComponent).consolidateYPosition();
        verify(dndListComponent).refreshItemsPosition();
        verify(searchBar).refresh();
    }

    @Test
    public void testAddDataType() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(dataType);
        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType();

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
        verify(listItem).enableEditMode();
    }

    @Test
    public void testAddDataTypeWithDefinedDataType() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType(dataType, false);

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem, never()).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
    }

    @Test
    public void testAddDataTypeWithDefinedDataTypeAndEditMode() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.addDataType(dataType, true);

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(searchBar).reset();
        verify(dataType).create();
        verify(view).showOrHideNoCustomItemsMessage();
        verify(listItem).refresh();
        verify(listItem).enableEditMode();
        verify(dndListComponent).refreshItemsCSSAndHTMLPosition();
    }

    @Test
    public void testInsertBelow() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);
        when(listItem.getDataType()).thenReturn(dataType);

        dataTypeList.insertBelow(dataType, reference);

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(view).insertBelow(listItem, reference);
        verify(dataTypeList).refreshItemsByUpdatedDataTypes(singletonList(dataType));
    }

    @Test
    public void testInsertAbove() {

        final DataType dataType = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(listItem).when(dataTypeList).makeListItem(dataType);

        dataTypeList.insertAbove(dataType, reference);

        verify(dataTypeList).executeInCommandManager(supplierCaptor.capture());

        final CommandResult commandResult = supplierCaptor.getValue().get();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, commandResult);

        verify(view).insertAbove(listItem, reference);
        verify(dndListComponent).consolidateYPosition();
        verify(dndListComponent).refreshItemsPosition();
    }

    @Test
    public void testMakeListItemWithDataType() {

        final DataType dataType = mock(DataType.class);
        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);

        doReturn(expectedListItem).when(dataTypeList).makeListItem();
        doReturn(new ArrayList<>()).when(dataTypeList).getItems();

        final DataTypeListItem actualListItem = dataTypeList.makeListItem(dataType);
        final List<DataTypeListItem> actualItems = dataTypeList.getItems();
        final List expectedItems = singletonList(expectedListItem);

        verify(expectedListItem).setupDataType(dataType, 1);
        assertEquals(expectedListItem, actualListItem);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testShowNoDataTypesFound() {
        dataTypeList.showNoDataTypesFound();

        verify(view).showNoDataTypesFound();
    }

    @Test
    public void testShowListItems() {
        dataTypeList.showListItems();

        verify(view).showOrHideNoCustomItemsMessage();
    }

    @Test
    public void testEnableEditMode() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.enableEditMode(dataTypeHash);

        verify(listItem).enableEditMode();
    }

    @Test
    public void testInsertNestedField() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.insertNestedField(dataTypeHash);

        verify(listItem).insertNestedField();
    }

    @Test
    public void testFireListItemUpdateCallbacks() {

        final String dataTypeHash = "tCity.name";
        final DataTypeListItem listItem = mock(DataTypeListItem.class);

        doReturn(Optional.of(listItem)).when(dataTypeList).findItemByDataTypeHash(dataTypeHash);

        dataTypeList.registerDataTypeListItemUpdateCallback(listItemConsumer);
        dataTypeList.fireOnDataTypeListItemUpdateCallback(dataTypeHash);

        verify(listItemConsumer).accept(listItem);
    }

    @Test
    public void testFindItemByDataTypeHashWhenListItemIsFound() {

        final DataTypeListItem tCity = listItem(makeDataType("001", "tCity", TOP_LEVEL_PARENT_UUID));
        final DataTypeListItem tCityId = listItem(makeDataType("002", "id", "001"));
        final DataTypeListItem tCityName = listItem(makeDataType("003", "name", "001"));

        doReturn(asList(tCity, tCityId, tCityName)).when(dataTypeList).getItems();

        final Optional<DataTypeListItem> item = dataTypeList.findItemByDataTypeHash("tCity.name");

        assertTrue(item.isPresent());
        assertEquals(item.get(), tCityName);
    }

    @Test
    public void testFindItemByDataTypeHashWhenListItemIsNotFound() {

        doReturn(emptyList()).when(dataTypeList).getItems();

        final Optional<DataTypeListItem> item = dataTypeList.findItemByDataTypeHash("tCity.name");

        assertFalse(item.isPresent());
    }

    @Test
    public void testOnDataTypeEditModeToggleStartEditing() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(true, currentEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(currentEditingItem, actual);
    }

    @Test
    public void testOnDataTypeEditModeToggleStopEditing() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(false, currentEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(null, actual);
    }

    @Test
    public void testOnDataTypeEditModeToggleChangedCurrentEditingItem() {

        final DataTypeListItem currentEditingItem = mock(DataTypeListItem.class);
        final DataTypeListItem previousEditingItem = mock(DataTypeListItem.class);
        final List<DataTypeListItem> listItems = asList(currentEditingItem, previousEditingItem);

        doReturn(listItems).when(dataTypeList).getItems();

        final DataTypeEditModeToggleEvent event = new DataTypeEditModeToggleEvent(true, currentEditingItem);

        dataTypeList.setCurrentEditingItem(previousEditingItem);

        dataTypeList.onDataTypeEditModeToggle(event);

        final DataTypeListItem actual = dataTypeList.getCurrentEditingItem();

        verify(searchBar).reset();
        assertEquals(currentEditingItem, actual);
        verify(previousEditingItem).disableEditMode();
    }

    @Test
    public void testGetListElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getListItems()).thenReturn(expectedElement);

        final HTMLElement actualElement = dataTypeList.getListItems();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testImportDataObjects_NullOrEmpty() {
        dataTypeList.importJavaClasses(null);
        dataTypeList.importJavaClasses(Collections.emptyList());

        verify(dataTypeList, never()).renameJavaClassToDMNName(any());
        verify(dataTypeList, never()).insert(any());
        verify(dataTypeList, never()).replace(any(), any());
        verify(dataTypeList, never()).insertFields(any(), any());
    }

    @Test
    public void testImportDataObjects() {
        List<JavaClass> javaClasses = new ArrayList<>();
        /* Author (org.kogito.test.Author) */
        List<JavaField> authorJavaFields = new ArrayList<>();
        JavaField nameJavaField = mockJavaField("name", "java.lang.String", "string", false);
        JavaField birthDateField = mockJavaField("birthDate", "java.time.LocalDate", "date", false);
        JavaField booksField = mockJavaField("books", "org.kogito.test.Book", "Book", true);
        JavaField anotherBookField = mockJavaField("anotherBook", "com.another.Book", "Book", false);
        authorJavaFields.add(nameJavaField);
        authorJavaFields.add(birthDateField);
        authorJavaFields.add(booksField);
        authorJavaFields.add(anotherBookField);
        JavaClass authorClass = mockJavaClass("org.kogito.test.Author", authorJavaFields);
        /* Book (org.kogito.test.Book) */
        List<JavaField> bookJavaFields = new ArrayList<>();
        JavaField titleJavaField = mockJavaField("title", "java.lang.String", "string", false);
        JavaField authorField = mockJavaField("author", "org.kogito.test.Author", "Author", false);
        bookJavaFields.add(titleJavaField);
        bookJavaFields.add(authorField);
        JavaClass bookClass = mockJavaClass("org.kogito.test.Book", bookJavaFields);
        /* Book (com.another.Book) another book class with different package and no fields */
        JavaClass anotherBookClass = mockJavaClass("com.another.Book", Collections.emptyList());

        javaClasses.add(authorClass);
        javaClasses.add(bookClass);
        javaClasses.add(anotherBookClass);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(any())).thenReturn(dataTypeManager);
        when(dataTypeManager.asList(anyBoolean())).thenReturn(dataTypeManager);

        /* Author (org.kogito.test.Author) */
        DataType authorDataType = new DataType(null);
        DataType authorNameDataType = new DataType(null);
        DataType authorBirthDateType = new DataType(null);
        DataType authorBooksDateType = new DataType(null);
        DataType authorAnotherBookDateType = new DataType(null);
        /* Book (org.kogito.test.Book) */
        DataType bookDataType = new DataType(null);
        DataType bookTitleDataType = new DataType(null);
        DataType bookAuthorDataType = new DataType(null);
        /* Book (com.another.Book) another book class with different package and no fields */
        DataType anotherBookDataType = new DataType(null);

        /* A previous Author exists */
        DataType oldAuthorDataType = new DataType(null);
        oldAuthorDataType.setName("Author");
        doReturn(Optional.of(oldAuthorDataType)).when(dataTypeList).findDataTypeByName("Author");

        DataTypeListItem authorDataTypeListItem = mock(DataTypeListItem.class);
        DataTypeListItem bookDataTypeListItem = mock(DataTypeListItem.class);

        when(dataTypeManager.get())
                .thenReturn(authorDataType, authorNameDataType, authorBirthDateType, authorBooksDateType, authorAnotherBookDateType,
                            bookDataType, bookTitleDataType, bookAuthorDataType, anotherBookDataType)
                .thenThrow(new IllegalStateException());
        when(dataTypeList.findItem(authorDataType)).thenReturn(Optional.of(authorDataTypeListItem));
        when(dataTypeList.findItem(bookDataType)).thenReturn(Optional.of(bookDataTypeListItem));

        dataTypeList.importJavaClasses(javaClasses);

        verify(dataTypeList, times(1)).renameJavaClassToDMNName(javaClasses);
        verify(dataTypeList, times(1)).replace(oldAuthorDataType, authorDataType);
        verify(dndDataTypesHandler, times(1)).deleteKeepingReferences(oldAuthorDataType);
        verify(dataTypeList, times(1)).insert(authorDataType);
        verify(dataTypeList, times(1)).insertFields(authorDataType, authorClass);
        verify(dataTypeList, times(1)).insert(bookDataType);
        verify(dataTypeList, times(1)).insertFields(bookDataType, bookClass);
        verify(dataTypeList, times(1)).insert(anotherBookDataType);

        assertEquals("Author", authorDataType.getName());
        assertEquals("name", authorNameDataType.getName());
        assertEquals("birthDate", authorBirthDateType.getName());
        verify(dataTypeManager, times(1)).withType("date");
        assertEquals("books", authorBooksDateType.getName());
        verify(dataTypeManager, times(1)).withType("Book");
        assertEquals("anotherBook", authorAnotherBookDateType.getName());
        verify(dataTypeManager, times(1)).withType("Book-1");
        assertEquals("Book", bookDataType.getName());
        assertEquals("title", bookTitleDataType.getName());
        verify(dataTypeManager, times(2)).withType("string");
        assertEquals("author", bookAuthorDataType.getName());
        verify(dataTypeManager, times(1)).withType("Author");
        assertEquals("Book-1", anotherBookDataType.getName());
        verify(dataTypeManager, times(5)).asList(false);
        verify(dataTypeManager, times(1)).asList(true);

        verify(authorDataTypeListItem, times(1)).insertNestedField(authorNameDataType);
        verify(authorDataTypeListItem, times(1)).insertNestedField(authorBirthDateType);
        verify(authorDataTypeListItem, times(1)).insertNestedField(authorBooksDateType);
        verify(authorDataTypeListItem, times(1)).insertNestedField(authorAnotherBookDateType);
        verify(bookDataTypeListItem, times(1)).insertNestedField(bookTitleDataType);
        verify(bookDataTypeListItem, times(1)).insertNestedField(bookAuthorDataType);
    }

    private JavaClass mockJavaClass(String name, List<JavaField> javaFields) {
        JavaClass javaClass = mock(JavaClass.class);
        when(javaClass.getName()).thenReturn(name);
        when(javaClass.getFields()).thenReturn(javaFields);
        doAnswer(invocation -> {
            when(javaClass.getName()).thenReturn(invocation.getArgument(0));
            return null;
        }).when(javaClass).setName(anyString());
        return javaClass;
    }

    private JavaField mockJavaField(String name, String type, String dmnRef, boolean isList) {
        JavaField javaField = mock(JavaField.class);
        when(javaField.getName()).thenReturn(name);
        when(javaField.getType()).thenReturn(type);
        when(javaField.isList()).thenReturn(isList);
        when(javaField.getDmnTypeRef()).thenReturn(dmnRef);
        doAnswer(invocation -> {
            when(javaField.getDmnTypeRef()).thenReturn(invocation.getArgument(0));
            return null;
        }).when(javaField).setDmnTypeRef(anyString());
        return javaField;
    }

    @Test
    public void testRemoveFullQualifiedNames() {
        final String do1Class = "something.class1";
        final String do2Class = "something.class2";
        final String do3Class = "something.class3";
        final String extractedName1 = "class1";
        final String extractedName2 = "class2";
        final String extractedName3 = "class3";
        final String builtName1 = "name1";
        final String builtName2 = "name2";
        final String builtName3 = "name3";
        final JavaClass do1 = mockJavaClass(do1Class, Collections.emptyList());
        final JavaClass do2 = mockJavaClass(do2Class, Collections.emptyList());
        final JavaClass do3 = mockJavaClass(do3Class, Collections.emptyList());

        final List<JavaClass> imported = Arrays.asList(do1, do2, do3);

        doReturn(builtName1).when(dataTypeList).buildName(eq(extractedName1), isA(Map.class));
        doReturn(builtName2).when(dataTypeList).buildName(eq(extractedName2), isA(Map.class));
        doReturn(builtName3).when(dataTypeList).buildName(eq(extractedName3), isA(Map.class));

        doNothing().when(dataTypeList).updatePropertiesReferences(eq(imported), isA(Map.class));

        dataTypeList.renameJavaClassToDMNName(imported);

        verify(dataTypeList).buildName(eq(extractedName1), isA(Map.class));
        verify(do1).setName(builtName1);

        verify(dataTypeList).buildName(eq(extractedName2), isA(Map.class));
        verify(do2).setName(builtName2);

        verify(dataTypeList).buildName(eq(extractedName3), isA(Map.class));
        verify(do3).setName(builtName3);

        ArgumentCaptor<Map<String, String>> renamedArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(dataTypeList).updatePropertiesReferences(eq(imported), renamedArgumentCaptor.capture());

        assertEquals(3, renamedArgumentCaptor.getValue().size());
        assertTrue(renamedArgumentCaptor.getValue().containsKey(do1Class));
        assertEquals(builtName1, renamedArgumentCaptor.getValue().get(do1Class));
        assertTrue(renamedArgumentCaptor.getValue().containsKey(do2Class));
        assertEquals(builtName2, renamedArgumentCaptor.getValue().get(do2Class));
        assertTrue(renamedArgumentCaptor.getValue().containsKey(do3Class));
        assertEquals(builtName3, renamedArgumentCaptor.getValue().get(do3Class));
    }

    @Test
    public void testBuildName() {

        final String name = "MyClass";
        final String differentName = "SomeOtherClass";
        final HashMap<String, Integer> namesCount = new HashMap<>();

        final String occurrence0 = dataTypeList.buildName(name, namesCount);
        assertEquals(name, occurrence0);

        final String occurrence1 = dataTypeList.buildName(name, namesCount);
        assertEquals(name + DataTypeList.NAME_SEPARATOR + "1", occurrence1);

        final String occurrence2 = dataTypeList.buildName(name, namesCount);
        assertEquals(name + DataTypeList.NAME_SEPARATOR + "2", occurrence2);

        final String differentOccurrence0 = dataTypeList.buildName(differentName, namesCount);
        assertEquals(differentName, differentOccurrence0);

        final String differentOccurrence1 = dataTypeList.buildName(differentName, namesCount);
        assertEquals(differentName + DataTypeList.NAME_SEPARATOR + "1", differentOccurrence1);
    }

    @Test
    public void testUpdatePropertiesReferences() {
        final List<JavaClass> javaClasses = new ArrayList<>();
        final HashMap<String, String> renamed = new HashMap<>();

        final String propertyType1 = "type";
        final String propertyNewType1 = "type-1";
        final String uniqueType = "uniqueType";

        renamed.put(propertyType1, propertyNewType1);

        final JavaField prop1 = mockJavaField("f1", propertyType1, "unkwown", false);
        final JavaField prop2 = mockJavaField("f2", uniqueType, "unkwown", false);
        final JavaClass javaClass = mockJavaClass("name", Arrays.asList(prop1, prop2));
        javaClasses.add(javaClass);

        dataTypeList.updatePropertiesReferences(javaClasses, renamed);

        verify(prop1, times(1)).setDmnTypeRef(propertyNewType1);
        verify(prop2, never()).setDmnTypeRef(any());
    }

    @Test
    public void testInsert() {

        final DataType newDataType = mock(DataType.class);

        doNothing().when(dataTypeList).addDataType(newDataType, false);

        dataTypeList.insert(newDataType);

        verify(dataTypeList).addDataType(newDataType, false);
    }

    @Test
    public void testReplace() {

        final DataType newDataType = mock(DataType.class);
        final DataType existing = mock(DataType.class);

        doNothing().when(dataTypeList).insert(newDataType);

        dataTypeList.replace(existing, newDataType);

        verify(dndDataTypesHandler).deleteKeepingReferences(existing);

        verify(dataTypeList).insert(newDataType);
    }

    @Test
    public void testCreateNewDataTypeFromJavaField() {
        final String propertyName = "name";
        final String propertyType = "type";
        final String dmnType = "type";
        final JavaField javaField = mockJavaField(propertyName, propertyType, dmnType, false);

        final DataType newType = mock(DataType.class);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.asList(anyBoolean())).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(propertyType)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(newType);

        final DataType actual = dataTypeList.createNewDataType(javaField);

        assertEquals(newType, actual);

        verify(dataTypeManager, times(1)).asList(false);
        verify(dataTypeManager, times(1)).withType(dmnType);
        verify(newType, times(1)).setName(propertyName);
    }

    @Test
    public void testCreateNewDataTypeFromJavaFieldWhenIsList() {
        final String propertyName = "name";
        final String propertyType = "type";
        final String dmnType = "type";
        final JavaField javaField = mockJavaField(propertyName, propertyType, dmnType, true);

        final DataType newType = mock(DataType.class);

        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.asList(anyBoolean())).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(propertyType)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(newType);

        final DataType actual = dataTypeList.createNewDataType(javaField);

        assertEquals(newType, actual);

        verify(dataTypeManager, times(1)).asList(true);
        verify(dataTypeManager, times(1)).withType(dmnType);
        verify(newType, times(1)).setName(propertyName);
    }

    @Test
    public void testCreateNewDataTypeFromJavaClass() {
        final String classType = "classType";
        final JavaClass javaClass = mockJavaClass(classType, Collections.emptyList());
        final DataType dataType = mock(DataType.class);
        final String structure = "structure";

        when(dataTypeManager.structure()).thenReturn(structure);
        when(dataTypeManager.fromNew()).thenReturn(dataTypeManager);
        when(dataTypeManager.withType(structure)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(dataType);

        final DataType actual = dataTypeList.createNewDataType(javaClass);
        assertEquals(dataType, actual);

        verify(dataType, times(1)).setName(classType);
        verify(dataTypeManager, times(1)).structure();
    }

    @Test
    public void testFindDataTypeByName() {

        final String name = "tName";

        final Optional<DataType> type = Optional.of(mock(DataType.class));
        when(dataTypeManager.getTopLevelDataTypeWithName(name)).thenReturn(type);

        final Optional<DataType> actual = dataTypeList.findDataTypeByName(name);

        verify(dataTypeManager).getTopLevelDataTypeWithName(name);
        assertEquals(type, actual);
    }

    @Test
    public void testDisableEditModeForChildren() {

        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        final DataType dataType = mock(DataType.class);
        final String uuid = "uuid";
        final String innerUuid = "inner";
        final String deepUuid = "deep";

        final DataType notChildDataType = mock(DataType.class);
        final DataTypeListItem notChildItem = mock(DataTypeListItem.class);
        final DataType childDataType1 = mock(DataType.class);
        final DataTypeListItem child1 = mock(DataTypeListItem.class);
        final DataType childDataType2 = mock(DataType.class);
        final DataTypeListItem child2 = mock(DataTypeListItem.class);
        final DataType innerDataType = mock(DataType.class);
        final DataTypeListItem innerDataTypeListItem = mock(DataTypeListItem.class);

        final DataType deepDataType = mock(DataType.class);
        final DataTypeListItem deepDataTypeListItem = mock(DataTypeListItem.class);
        when(deepDataType.getUUID()).thenReturn(deepUuid);
        when(deepDataType.getParentUUID()).thenReturn(innerUuid);
        when(deepDataTypeListItem.getDataType()).thenReturn(deepDataType);

        when(innerDataType.getUUID()).thenReturn(innerUuid);
        when(innerDataType.getParentUUID()).thenReturn(uuid);
        when(innerDataTypeListItem.getDataType()).thenReturn(innerDataType);
        when(notChildDataType.getParentUUID()).thenReturn("other_uuid");
        when(notChildItem.getDataType()).thenReturn(notChildDataType);
        when(child1.getDataType()).thenReturn(childDataType1);
        when(childDataType1.getParentUUID()).thenReturn(uuid);
        when(child2.getDataType()).thenReturn(childDataType2);
        when(childDataType2.getParentUUID()).thenReturn(uuid);
        when(dataType.getUUID()).thenReturn(uuid);
        when(dataTypeListItem.getDataType()).thenReturn(dataType);

        final List<DataTypeListItem> list = asList(child1, notChildItem, child2, innerDataTypeListItem, deepDataTypeListItem);

        doReturn(list).when(dataTypeList).getItems();

        dataTypeList.disableEditModeForChildren(dataTypeListItem);

        verify(child1).disableEditMode();
        verify(child2).disableEditMode();
        verify(innerDataTypeListItem).disableEditMode();
        verify(deepDataTypeListItem).disableEditMode();
        verify(notChildItem, never()).disableEditMode();
    }

    @Test
    public void testGetExistingDataTypeNames() {

        final String name1 = "name1";
        final String name2 = "name2";
        final String name3 = "name3";
        final DataType dataType1 = makeDataType(name1, "whatever");
        final DataType dataType2 = makeDataType(name2, "whatever");
        final DataType dataType3 = makeDataType(name3, "whatever");
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final List<DataTypeListItem> items = Arrays.asList(listItem1, listItem2, listItem3);

        when(dataType1.isTopLevel()).thenReturn(true);
        when(dataType2.isTopLevel()).thenReturn(false);
        when(dataType3.isTopLevel()).thenReturn(true);

        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem3.getDataType()).thenReturn(dataType3);

        doReturn(items).when(dataTypeList).getItems();

        final List<String> names = dataTypeList.getExistingDataTypesNames();

        assertEquals(2, names.size());
        assertTrue(names.contains(name1));
        assertFalse(names.contains(name2));
        assertTrue(names.contains(name3));
    }

    @Test
    public void testHighlightLevelWithDataType() {
        final DataType dataType = mock(DataType.class);
        dataTypeList.highlightLevel(dataType);
        verify(highlightHelper).highlightLevel(dataType);
    }

    @Test
    public void testHighlightLevelWithElement() {
        final Element element = mock(Element.class);
        dataTypeList.highlightLevel(element);
        verify(highlightHelper).highlightLevel(element);
    }

    @Test
    public void testHighlight() {
        final Element element = mock(Element.class);
        dataTypeList.highlight(element);
        verify(highlightHelper).highlight(element);
    }

    @Test
    public void testCleanLevelHighlightClass() {
        dataTypeList.cleanLevelHighlightClass();
        verify(highlightHelper).cleanLevelHighlightClass();
    }

    @Test
    public void testCleanHighlightClass() {
        dataTypeList.cleanHighlightClass();
        verify(highlightHelper).cleanHighlightClass();
    }

    private DataTypeListItem listItem(final DataType dataType) {
        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        when(listItem.getDataType()).thenReturn(dataType);
        return listItem;
    }

    private DataType makeDataType(final String name,
                                  final String type,
                                  final DataType... subDataTypes) {

        final DataType dataType = makeDataType("default", name, TOP_LEVEL_PARENT_UUID);

        when(dataType.getType()).thenReturn(type);
        when(dataType.getSubDataTypes()).thenReturn(asList(subDataTypes));

        return dataType;
    }

    private DataType makeDataType(final String uuid,
                                  final String name,
                                  final String parentUUID) {

        final DataType dataType = mock(DataType.class);

        when(dataType.getUUID()).thenReturn(uuid);
        when(dataType.getName()).thenReturn(name);
        when(dataType.getParentUUID()).thenReturn(parentUUID);

        dataTypeStore.index(dataType.getUUID(), dataType);

        return dataType;
    }
}
