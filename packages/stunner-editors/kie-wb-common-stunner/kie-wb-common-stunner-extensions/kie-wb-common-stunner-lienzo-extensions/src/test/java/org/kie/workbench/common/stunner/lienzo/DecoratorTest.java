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


package org.kie.workbench.common.stunner.lienzo;

import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.GroupOf;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.Decorator.ItemCallback;
import org.mockito.Mock;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecoratorTest {

    @Mock
    private ItemCallback callback;
    @Mock
    private Timer timer;
    @Mock
    private Rectangle rectangle;
    @Mock
    private IAnimation animation;
    @Mock
    private IAnimationHandle animationHandle;
    @Mock
    private IPrimitive primitive;
    @Mock
    private Group group;

    private Decorator decorator;

    @Before
    public void setUp() {
        decorator = spy(new Decorator(callback));
        doReturn(group).when((GroupOf) decorator).add(anyObject());
        doReturn(rectangle).when(decorator).createRectangle(anyDouble(),
                                                            anyDouble());
        doReturn(rectangle).when(rectangle).setDraggable(anyBoolean());
    }

    @Test
    public void testBuild() {
        decorator.build(primitive,
                        5.0,
                        8.0);
        verify(decorator).createRectangle(5.0,
                                          8.0);
        verify(decorator).add(primitive);
        verify(decorator).add(rectangle);
        verify(rectangle).addNodeMouseEnterHandler(anyObject());
        verify(rectangle).addNodeMouseExitHandler(anyObject());
        verify(rectangle).addNodeMouseMoveHandler(anyObject());
        verify(rectangle).setDraggable(false);
        verify(rectangle).moveToTop();
    }

    @Test
    public void testFireHide() {
        decorator.fireHide();
        verify(callback).onHide();
    }

    @Test
    public void testFireHideOnNull() {
        decorator.setItemCallback(null);
        decorator.fireHide();
        verify(callback,
               never()).onHide();
    }

    @Test
    public void testFireShow() {
        decorator.fireShow(3.0,
                           5.0);
        verify(callback).onShow(3.0,
                                5.0);
    }

    @Test
    public void testFireShowOnNull() {
        decorator.setItemCallback(null);
        decorator.fireShow(3.0,
                           5.0);
        verify(callback,
               never()).onShow(anyDouble(),
                               anyDouble());
    }

    @Test
    public void testHide() {
        decorator.build(primitive,
                        6.0,
                        0.6);
        doReturn(animationHandle).when((Node) rectangle).animate(anyObject(),
                                                                 anyObject(),
                                                                 anyDouble(),
                                                                 anyObject());
        decorator.resetTimer(timer);
        when(timer.isRunning()).thenReturn(true);
        decorator.hide();
        verify(rectangle,
               never()).animate(anyObject(),
                                anyObject(),
                                anyDouble(),
                                anyObject());
        when(timer.isRunning()).thenReturn(false);
        decorator.hide();
        verify(decorator).createHideAnimationCallback();
        verify(rectangle).animate(anyObject(),
                                  anyObject(),
                                  anyDouble(),
                                  anyObject());
    }

    @Test
    public void testCreateHideAnimationCallback() {
        decorator.createHideAnimationCallback().onClose(animation,
                                                        animationHandle);
        verify(decorator).fireHide();
    }

    @Test
    public void testTimer() {
        decorator.build(primitive,
                        1.0,
                        2.0);
        decorator.createTimer().run();
        verify(decorator).hide();
    }

    @Test
    public void testShow() {
        decorator.build(primitive,
                        6.0,
                        0.6);
        doReturn(animationHandle).when((Node) rectangle).animate(anyObject(),
                                                                 anyObject(),
                                                                 anyDouble(),
                                                                 anyObject());
        decorator.resetTimer(timer);
        when(timer.isRunning()).thenReturn(true);
        decorator.show(4,
                       5);
        verify(rectangle,
               never()).animate(anyObject(),
                                anyObject(),
                                anyDouble(),
                                anyObject());
        when(timer.isRunning()).thenReturn(false);
        decorator.show(0.1,
                       0);
        verify(decorator).createShowAnimationCallback(0.1,
                                                      0);
        verify(rectangle).animate(anyObject(),
                                  anyObject(),
                                  anyDouble(),
                                  anyObject());
        verify(timer).schedule(anyInt());
    }

    @Test
    public void testCreateShowAnimationCallback() {
        decorator.createShowAnimationCallback(0.9,
                                              1.1).onClose(animation,
                                                           animationHandle);
        verify(decorator).fireShow(0.9,
                                   1.1);
    }
}
