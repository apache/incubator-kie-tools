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


package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class AbstractCanvasListenerTest<C, D, E> {

    public static final int CONTROLS = 10;

    protected CanvasListener<D, E> canvasListener;

    protected List<CanvasRegistrationControl<C, E>> registrationControls;

    protected abstract CanvasListener<D, E> createCanvasListener();

    protected abstract List<CanvasRegistrationControl<C, E>> createRegistrationControls();

    protected abstract E mockElement();

    @Before
    public void setUp() {
        registrationControls = createRegistrationControls();
        canvasListener = createCanvasListener();
    }

    @Test
    public void testRegister() {
        final E element = mockElement();
        canvasListener.register(element);
        registrationControls.forEach(control -> verify(control).register(element));
    }

    @Test
    public void testDeregister() {
        final E element = mockElement();
        canvasListener.deregister(element);
        registrationControls.forEach(control -> verify(control).deregister(element));
    }

    @Test
    public void testClear() {
        canvasListener.clear();
        registrationControls.forEach(control -> verify(control).clear());
    }

    protected <T> List<T> mockList(Class<T> clazz,
                                   int size) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            list.add(mock(clazz));
        }
        return list;
    }
}
