/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.lienzo.palette;

import java.util.Iterator;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.Decorator;
import org.kie.workbench.common.stunner.lienzo.Decorator.ItemCallback;
import org.kie.workbench.common.stunner.lienzo.grid.Grid;
import org.kie.workbench.common.stunner.lienzo.grid.Grid.Point;
import org.kie.workbench.common.stunner.lienzo.palette.AbstractPalette.Callback;
import org.mockito.Mock;

import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PaletteTest {

    @Mock
    private Callback callback;
    @Mock
    private IPrimitive firstPrimitive;
    @Mock
    private IPrimitive secondPrimitive;
    @Mock
    private Decorator decorator;
    @Mock
    private Group group;
    @Mock
    private Group itemsGroup;
    @Mock
    private Rectangle rectangle;

    private static final int ICON_SIZE = 10;
    private static final int PADDING = 5;
    private static final int X = 11;
    private static final int Y = 12;

    private IPrimitive[] arrayOfPrimitives;
    private Iterator<Point> pointIterator;
    private Palette miniPalette;
    private Grid grid;

    @Before
    public void setUp() {
        arrayOfPrimitives = new IPrimitive[]{firstPrimitive, secondPrimitive};
        miniPalette = new Palette()
                .setIconSize(ICON_SIZE)
                .setItemCallback(callback)
                .setPadding(PADDING)
                .setX(X)
                .setY(Y);
        grid = spy(miniPalette.createGrid(arrayOfPrimitives.length));
        pointIterator = spy(grid.iterator());
        doReturn(pointIterator).when(grid).iterator();
        miniPalette = spy(miniPalette);
        doReturn(miniPalette).when(miniPalette).add(anyObject());
        when(itemsGroup.add(anyObject())).thenReturn(itemsGroup);
        doReturn(decorator).when(miniPalette).createDecorator(anyInt(),
                                                              anyDouble(),
                                                              anyDouble());
        doReturn(grid).when(miniPalette).createGrid(anyInt());
        doReturn(decorator).when(decorator).build(anyObject(),
                                                  anyDouble(),
                                                  anyDouble());
        doReturn(decorator).when(decorator).setX(anyDouble());
        doReturn(decorator).when(decorator).setY(anyDouble());
        doReturn(group).when(decorator).add(anyObject());
        when(group.getChildNodes()).thenReturn(new NFastArrayList<>());
        when(itemsGroup.getChildNodes()).thenReturn(new NFastArrayList<>());
        miniPalette.itemsGroup = itemsGroup;
    }

    @Test
    public void testBuild() {
        miniPalette.build(new AbstractPalette.Item(firstPrimitive,
                                                   AbstractPalette.ItemDecorator.DEFAULT),
                          new AbstractPalette.Item(secondPrimitive,
                                                   AbstractPalette.ItemDecorator.DEFAULT));
        verify(miniPalette).clear();
        verify(miniPalette).createGrid(arrayOfPrimitives.length);
        verify(itemsGroup,
               times(2)).add(decorator);
        verify(pointIterator,
               times(2)).next();
        verify(decorator).build(firstPrimitive,
                                (double) ICON_SIZE,
                                (double) ICON_SIZE);
        verify(decorator).build(secondPrimitive,
                                (double) ICON_SIZE,
                                (double) ICON_SIZE);
        verify(decorator,
               times(2)).setX(anyDouble());
        verify(decorator,
               times(2)).setY(anyDouble());
        verify(decorator,
               times(2)).addNodeMouseDownHandler(anyObject());
        verify(decorator,
               times(2)).addNodeMouseClickHandler(anyObject());
    }

    @Test
    public void testCreateDecoratorCallback() {
        arrayOfPrimitives = new IPrimitive[]{firstPrimitive, secondPrimitive};
        miniPalette = spy(new Palette()
                                  .setIconSize(ICON_SIZE)
                                  .setItemCallback(callback)
                                  .setPadding(PADDING)
                                  .setX(X)
                                  .setY(Y));
        ItemCallback callback = spy(miniPalette.createDecoratorCallback(1,
                                                                        0,
                                                                        0));
        callback.onShow(6.0,
                        4.0);
        verify(miniPalette).doShowItem(1,
                                       6.0,
                                       4.0,
                                       0,
                                       0);
        callback.onHide();
        verify(miniPalette).doItemOut(1);
    }

    @Test
    public void testClear() {
        IPrimitive<?> p = mock(IPrimitive.class);
        NFastArrayList<IPrimitive<?>> children = new NFastArrayList<>();
        children.add(p);
        when(itemsGroup.getChildNodes()).thenReturn(children);
        miniPalette.clear();
        verify(p).removeFromParent();
    }

    @Test
    public void testNullCallback() {
        miniPalette.setItemCallback(null);
        miniPalette.doShowItem(1,
                               4,
                               3.2,
                               0,
                               0);
        miniPalette.doItemOut(1);
        miniPalette.onItemMouseDown(1,
                                    4,
                                    7,
                                    0,
                                    0);
        miniPalette.onItemClick(1,
                                2,
                                3,
                                0,
                                0);
    }

    @Test
    public void testCallback() {
        miniPalette.setItemCallback(callback);
        miniPalette.doShowItem(1,
                               4,
                               3.2,
                               0,
                               0);
        verify(callback).onItemHover(1,
                                     4.0,
                                     3.2,
                                     0,
                                     0);
        miniPalette.doItemOut(1);
        verify(callback).onItemOut(1);
        miniPalette.onItemMouseDown(1,
                                    4,
                                    7,
                                    0,
                                    0);
        verify(callback).onItemMouseDown(1,
                                         4,
                                         7,
                                         0,
                                         0);
        miniPalette.onItemClick(1,
                                2,
                                3,
                                0,
                                0);
        verify(miniPalette).onItemClick(1,
                                        2,
                                        3,
                                        0,
                                        0);
    }
}
