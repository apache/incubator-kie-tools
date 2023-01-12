/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.ITextWrapper;
import com.ait.lienzo.client.core.shape.ITextWrapperWithBoundaries;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.layout.label.LabelContainerLayout;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresTextDecoratorTest {

    public static final BoundingBox NEW_SIZE = BoundingBox.fromDoubles(0, 0, 10, 10);

    @Mock
    private Supplier<ViewEventHandlerManager> eventHandlerManager;

    @Mock
    private ViewEventHandlerManager manager;

    @Mock
    private ITextWrapperWithBoundaries textWrapperWithBoundaries;

    private final BoundingBox bb = BoundingBox.fromArrayOfPoint2D(new Point2D(0, 0),
                                                                  new Point2D(100, 100));

    @Mock
    private WiresShapeViewExt shape;

    @Mock
    private MultiPath path;

    @Mock
    private LabelContainerLayout layout;

    private WiresTextDecorator decorator;

    @Mock
    private WiresShape child1;

    @Mock
    private WiresShape child2;

    @Before
    public void setup() {
        when(eventHandlerManager.get()).thenReturn(manager);
        when(shape.getPath()).thenReturn(path);
        when(shape.getLabelContainerLayout()).thenReturn(Optional.of(layout));
        when(path.getBoundingBox()).thenReturn(bb);
        when(layout.getMaxSize(any())).thenReturn(bb);

        decorator = spy(new WiresTextDecorator(eventHandlerManager, shape));
    }

    @Test
    public void ensureThatWrapBoundariesAreSet() {
        assertBoundaries(bb.getWidth(), bb.getHeight());
    }

    private void assertBoundaries(double width, double height) {
        final Text text = decorator.getView();
        final BoundingBox wrapBoundaries = ((TextBoundsWrap) text.getWrapper()).getWrapBoundaries();
        assertEquals(width, wrapBoundaries.getWidth(), 0d);
        assertEquals(height, wrapBoundaries.getHeight(), 0d);
        assertNotEquals(wrapBoundaries.getWidth(), 0d, 0.d);
        assertNotEquals(wrapBoundaries.getHeight(), 0d, 0d);
    }

    @Test
    public void ensureResizeUpdatesTheNode() {
        when(layout.getMaxSize(any())).thenReturn(NEW_SIZE);
        decorator.update();
        verify(layout, atLeastOnce()).execute();
        assertBoundaries(NEW_SIZE.getWidth(), NEW_SIZE.getHeight());
    }

    @Test
    public void assertWidthHeightWhenMaxSizeIsNull() {
        when(layout.getMaxSize(any())).thenReturn(null);
        decorator.setTitleBoundaries(30, 30);
        verify(layout, atLeastOnce()).execute();
        assertBoundaries(30, 30);
    }

    @Test
    public void ensureThatUpdateRefreshTextBoundaries() {
        decorator.update();
        verify(decorator).setTextBoundaries(any(BoundingBox.class));
    }

    @Test
    public void testSetTextWrapperBounds() {
        testSetTextWrapperStrategy(TextWrapperStrategy.BOUNDS);
    }

    @Test
    public void testSetTextWrapperBoundsAndLineBreaks() {
        testSetTextWrapperStrategy(TextWrapperStrategy.BOUNDS_AND_LINE_BREAKS);
    }

    @Test
    public void testSetTextWrapperLineBreak() {
        testSetTextWrapperStrategy(TextWrapperStrategy.LINE_BREAK);
    }

    @Test
    public void testSetTextWrapperNoWrap() {
        testSetTextWrapperStrategy(TextWrapperStrategy.NO_WRAP);
    }

    @Test
    public void testSetTextWrapperTruncate() {
        testSetTextWrapperStrategy(TextWrapperStrategy.TRUNCATE);
    }

    @Test
    public void testSetTextWrapperTruncateWithLineBreak() {
        testSetTextWrapperStrategy(TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
    }

    private void testSetTextWrapperStrategy(final TextWrapperStrategy wrapperStrategy) {
        final Text text = decorator.getView();
        final ITextWrapper expectedWrapper = TextWrapperProvider.get(wrapperStrategy, text);

        decorator.setTitleWrapper(wrapperStrategy);

        verify(decorator).setTextBoundaries(any(BoundingBox.class));
        assertEquals(expectedWrapper.getClass(), text.getWrapper().getClass());
    }

    @Test
    public void ensureSetWrapBoundariesIsCalled() {
        doReturn(textWrapperWithBoundaries).when(decorator).getTextWrapper(any());

        decorator.setTitleWrapper(any());

        verify(textWrapperWithBoundaries).setWrapBoundaries(any());
    }

    @Test
    public void testMoveTitleToFront() throws NoSuchFieldException, IllegalAccessException {
        final Text text = spy(new Text(""));
        final WiresTextDecorator localDecorator = new WiresTextDecorator(eventHandlerManager, shape);
        final NFastArrayList<WiresShape> children = new NFastArrayList<>();
        children.add(child1);
        children.add(child2);
        when(child1.getGroup()).thenReturn(mock(Group.class));
        when(child2.getGroup()).thenReturn(mock(Group.class));
        when(shape.getChildShapes()).thenReturn(children);

        final Field field = localDecorator.getClass().getDeclaredField("text");
        field.setAccessible(true);
        field.set(localDecorator, text);

        localDecorator.moveTitleToTop();

        InOrder order = inOrder(text, child1.getGroup(), child2.getGroup());
        order.verify(text).moveToTop();
        order.verify(child1.getGroup()).moveToTop();
        order.verify(child2.getGroup()).moveToTop();
    }

    @Test
    public void testGetTitleFontFamily() {
        assertEquals("Verdana", decorator.getTitleFontFamily());
    }

    @Test
    public void testGetTitleFontSize() {
        assertEquals(10.0, decorator.getTitleFontSize(), 0.001);
    }

    @Test
    public void testGetTitlePosition() {
        assertEquals("INSIDE", decorator.getTitlePosition());
    }

    @Test
    public void testGetOrientation() {
        assertEquals("HORIZONTAL", decorator.getOrientation());
    }

    @Test
    public void testGetMarginX() {
        assertEquals(0.0, decorator.getMarginX(), 0.001);
    }

    @Test
    public void testGetFontPosition() {
        assertEquals("INSIDE", decorator.getFontPosition());
    }

    @Test
    public void testGetFontAlignment() {
        assertEquals("MIDDLE", decorator.getFontAlignment());
    }

    @Test
    public void testBatch() throws IllegalAccessException, NoSuchFieldException {
        Text text = spy(new Text(""));

        final WiresTextDecorator localDecorator = new WiresTextDecorator(eventHandlerManager, shape);
        final Field field = localDecorator.getClass().getDeclaredField("text");
        field.setAccessible(true);
        field.set(localDecorator, text);

        localDecorator.batch();

        verify(text).batch();
    }
}
