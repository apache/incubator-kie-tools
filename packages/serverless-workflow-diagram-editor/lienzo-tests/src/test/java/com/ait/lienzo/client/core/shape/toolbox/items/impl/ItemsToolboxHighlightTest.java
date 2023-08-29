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


package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.toolbox.items.ActionItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ItemsToolbox;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.BoxDecorator;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.DecoratorsFactory;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ItemsToolboxHighlightTest {

    @Mock
    private ItemsToolbox toolbox;

    private ItemsToolboxHighlight tested;
    private ActionItem item;
    private ActionItem item1;
    private ActionItem item2;
    private ButtonItemImpl button;
    private AbstractFocusableGroupItem buttonWrappedItem;
    private MultiPath decoratorPrimitive;
    private Layer layer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        layer = spy(new Layer());
        decoratorPrimitive = mock(MultiPath.class);
        when(decoratorPrimitive.getLayer()).thenReturn(layer);
        BoxDecorator decorator = spy(DecoratorsFactory.INSTANCE.box());
        when(decorator.asPrimitive()).thenReturn(decoratorPrimitive);
        BoxDecorator d1 = spy(DecoratorsFactory.INSTANCE.box());
        doReturn(decorator).when(d1).copy();
        buttonWrappedItem = mock(AbstractFocusableGroupItem.class);
        button = mock(ButtonItemImpl.class);
        Group buttonGroup = spy(new Group());
        when(button.getWrapped()).thenReturn(buttonWrappedItem);
        when(buttonWrappedItem.getDecorator()).thenReturn(d1);
        when(buttonWrappedItem.asPrimitive()).thenReturn(buttonGroup);
        when(buttonGroup.getLayer()).thenReturn(layer);
        item = mock(ActionItem.class);
        item1 = mock(ActionItem.class);
        item2 = mock(ActionItem.class);
        List items = Arrays.asList(item, item1, item2, button);
        when(toolbox.iterator()).thenReturn(items.iterator());
        tested = new ItemsToolboxHighlight(toolbox);
    }

    @Test
    public void testHighlight() {
        tested.highlight(item);
        verify(item, times(1)).enable();
        verify(item1, times(1)).disable();
        verify(item2, times(1)).disable();
        verify(button, times(1)).disable();
        verify(item, never()).disable();
        verify(item1, never()).enable();
        verify(item2, never()).enable();
        verify(button, never()).enable();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHighlightButton() {
        tested.highlight(button);
        verify(buttonWrappedItem, times(1)).add(eq(decoratorPrimitive));
        verify(layer, atLeastOnce()).batch();

        verify(button, times(1)).enable();
        verify(item1, times(1)).disable();
        verify(item2, times(1)).disable();
        verify(item, times(1)).disable();
        verify(button, never()).disable();
        verify(item1, never()).enable();
        verify(item2, never()).enable();
        verify(item, never()).enable();
    }

    @Test
    public void testRestore() {
        tested.restore();
        verify(item, times(1)).enable();
        verify(item1, times(1)).enable();
        verify(item2, times(1)).enable();
        verify(button, times(1)).enable();
        verify(item, never()).disable();
        verify(item1, never()).disable();
        verify(item2, never()).disable();
        verify(button, never()).disable();
    }
}
