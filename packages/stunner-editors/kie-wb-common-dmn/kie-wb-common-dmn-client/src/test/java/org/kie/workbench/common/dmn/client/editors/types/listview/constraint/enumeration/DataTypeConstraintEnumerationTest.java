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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintEnumerationTest {

    @Mock
    private DataTypeConstraintEnumeration.View view;

    @Mock
    private DMNClientServicesProxy clientServicesProxy;

    @Mock
    private ScrollHelper scrollHelper;

    @Mock
    private EventSourceMock<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    @Mock
    private ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances;

    @Captor
    private ArgumentCaptor<ScheduledCommand> scheduledCommandArgumentCaptor;

    private DataTypeConstraintEnumeration constraintEnumeration;

    @Before
    public void setup() {
        constraintEnumeration = spy(new DataTypeConstraintEnumeration(view,
                                                                      clientServicesProxy,
                                                                      scrollHelper,
                                                                      parserWarningEvent,
                                                                      enumerationItemInstances));

        doNothing().when(constraintEnumeration).scheduleRender(any(ScheduledCommand.class));
    }

    @Test
    public void testSetup() {
        constraintEnumeration.setup();

        verify(view).init(constraintEnumeration);
    }

    @Test
    public void testGetValue() {
        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item3 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item4 = mock(DataTypeConstraintEnumerationItem.class);

        when(item1.getValue()).thenReturn("123");
        when(item2.getValue()).thenReturn("456");
        when(item3.getValue()).thenReturn("");
        when(item4.getValue()).thenReturn("123");

        doReturn(asList(item1, item2, item3, item4)).when(constraintEnumeration).getEnumerationItems();

        final String actualValue = constraintEnumeration.getValue();
        final String expectedValue = "123, 456";

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetValue() {
        final String value = "value";

        constraintEnumeration.setValue(value);

        verify(clientServicesProxy).parseFEELList(eq(value),
                                                  any(ServiceCallback.class));
    }

    @Test
    public void testRefreshView() {
        final String value = "1, 2, 3";

        doNothing().when(constraintEnumeration).setValue(Mockito.<String>any());
        doReturn(value).when(constraintEnumeration).getValue();

        constraintEnumeration.refreshView();

        verify(constraintEnumeration).setValue(value);
    }

    @Test
    public void testRefreshViewWithOnCompleteCallback() {
        final String value = "1, 2, 3";
        final Command command = mock(Command.class);

        doNothing().when(constraintEnumeration).setValue(Mockito.<String>any());
        doReturn(value).when(constraintEnumeration).getValue();

        constraintEnumeration.refreshView(command);

        verify(constraintEnumeration).registerOnCompleteCallback(command);
        verify(constraintEnumeration).setValue(value);
    }

    @Test
    public void testExecuteOnCompleteCallback() {
        final Command defaultCommand = mock(Command.class);
        final Command customCommand = mock(Command.class);

        when(constraintEnumeration.defaultOnCompleteCallback()).thenReturn(defaultCommand);

        constraintEnumeration.registerOnCompleteCallback(customCommand);

        constraintEnumeration.executeOnCompleteCallback();
        constraintEnumeration.executeOnCompleteCallback();
        constraintEnumeration.executeOnCompleteCallback();

        verify(customCommand).execute();
        verify(defaultCommand, times(2)).execute();
    }

    @Test
    public void testScrollToBottom() {
        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item3 = mock(DataTypeConstraintEnumerationItem.class);

        doReturn(asList(item1, item2, item3)).when(constraintEnumeration).getEnumerationItems();
        doNothing().when(constraintEnumeration).scrollToPosition(anyInt());
        when(item1.getOrder()).thenReturn(1);
        when(item2.getOrder()).thenReturn(2);
        when(item3.getOrder()).thenReturn(3);

        constraintEnumeration.scrollToBottom();

        verify(constraintEnumeration).scrollToPosition(3);
    }

    @Test
    public void testScrollToPosition() {
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement itemElement = mock(HTMLElement.class);

        when(constraintEnumeration.getElement()).thenReturn(element);
        when(element.querySelector("[data-position=\"2\"")).thenReturn(itemElement);

        constraintEnumeration.scrollToPosition(2);

        verify(scrollHelper).scrollTo(itemElement, element);
    }

    @Test
    public void testGetElement() {
        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final Element actualElement = constraintEnumeration.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRender() {
        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);

        final InOrder inorder = Mockito.inOrder(view);

        when(item1.getOrder()).thenReturn(1);
        when(item2.getOrder()).thenReturn(0);

        when(item1.getElement()).thenReturn(element1);
        when(item2.getElement()).thenReturn(element2);
        doReturn(asList(item1, item2)).when(constraintEnumeration).getEnumerationItems();

        constraintEnumeration.render();

        verify(constraintEnumeration).scheduleRender(scheduledCommandArgumentCaptor.capture());
        scheduledCommandArgumentCaptor.getValue().execute();

        verify(view).clear();
        inorder.verify(view).addItem(element2);
        inorder.verify(view).addItem(element1);
    }

    @Test
    public void testAddEnumerationItem() {
        final DataTypeConstraintEnumerationItem item = mock(DataTypeConstraintEnumerationItem.class);
        final List<DataTypeConstraintEnumerationItem> items = spy(new ArrayList<>());
        final Element element = mock(Element.class);

        doReturn(items).when(constraintEnumeration).getEnumerationItems();
        doReturn(item).when(constraintEnumeration).makeEnumerationItem("");
        doReturn(element).when(constraintEnumeration).getElement();
        doNothing().when(constraintEnumeration).refreshEnumerationItemsOrder();

        constraintEnumeration.addEnumerationItem();

        verify(items).add(item);
        verify(constraintEnumeration).refreshEnumerationItemsOrder();
        verify(constraintEnumeration).render();
        verify(constraintEnumeration).scrollToBottom();
        verify(item).enableEditMode();
        verify(item).setOrder(items.size() - 1);
    }

    @Test
    public void testMakeEnumerationItem() {
        final String value = "123";
        final String constraintValueType = "string";
        final DataTypeConstraintEnumerationItem expectedItem = mock(DataTypeConstraintEnumerationItem.class);

        when(enumerationItemInstances.get()).thenReturn(expectedItem);
        doReturn(constraintValueType).when(constraintEnumeration).getConstraintValueType();

        final DataTypeConstraintEnumerationItem actualItem = constraintEnumeration.makeEnumerationItem(value);

        verify(expectedItem).setValue(value);
        verify(expectedItem).setConstraintValueType(constraintValueType);
        verify(expectedItem).setDataTypeConstraintEnumeration(constraintEnumeration);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testGetValueOrdered() {
        final DataTypeConstraintEnumerationItem item1 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item2 = mock(DataTypeConstraintEnumerationItem.class);
        final DataTypeConstraintEnumerationItem item3 = mock(DataTypeConstraintEnumerationItem.class);

        when(item1.getValue()).thenReturn("123");
        when(item2.getValue()).thenReturn("456");
        when(item3.getValue()).thenReturn("789");

        when(item3.getOrder()).thenReturn(0);
        when(item2.getOrder()).thenReturn(1);
        when(item1.getOrder()).thenReturn(2);

        constraintEnumeration.setEnumerationItems(asList(item1, item2, item3));

        final String actualValue = constraintEnumeration.getValue();
        final String expectedValue = "789, 456, 123";

        assertEquals(expectedValue, actualValue);
    }
}
