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
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasListener;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCanvasElementListenerTest extends AbstractCanvasListenerTest<AbstractCanvasHandler, CanvasHandler, Element> {

    private DefaultCanvasElementListener defaultCanvasElementListener;

    private List<CanvasRegistrationControlStub> registrationControls;

    private List<AbstractCanvasHandlerRegistrationControlStub> updatableRegistrationControls;

    private List<CanvasRegistrationControl<AbstractCanvasHandler, Element>> allRegistrationControls;

    private List<CanvasControl<AbstractCanvasHandler>> nonRegistrationControls;

    private List<CanvasControl<AbstractCanvasHandler>> allControls;

    @Override
    protected CanvasListener<CanvasHandler, Element> createCanvasListener() {
        defaultCanvasElementListener = new DefaultCanvasElementListener(allControls);
        return defaultCanvasElementListener;
    }

    @Override
    protected List<CanvasRegistrationControl<AbstractCanvasHandler, Element>> createRegistrationControls() {
        return allRegistrationControls;
    }

    @Override
    protected Element mockElement() {
        return mock(Element.class);
    }

    @Override
    @Before
    public void setUp() {
        registrationControls = new ArrayList<>(mockList(CanvasRegistrationControlStub.class,
                                                        CONTROLS));
        updatableRegistrationControls = new ArrayList<>(mockList(AbstractCanvasHandlerRegistrationControlStub.class,
                                                                 CONTROLS));

        allRegistrationControls = new ArrayList<>();
        allRegistrationControls.addAll(registrationControls);
        allRegistrationControls.addAll(updatableRegistrationControls);

        nonRegistrationControls = new ArrayList<>(mockList(CanvasControlStub.class,
                                                           CONTROLS));

        allControls = new ArrayList<>();
        allControls.addAll(allRegistrationControls);
        allControls.addAll(nonRegistrationControls);

        super.setUp();
    }

    @Test
    public void testUpdate() {
        Element element = mock(Element.class);
        defaultCanvasElementListener.update(element);
        updatableRegistrationControls.forEach(control -> verify(control).update(element));
    }

    interface CanvasRegistrationControlStub extends CanvasRegistrationControl<AbstractCanvasHandler, Element> {

    }

    abstract class AbstractCanvasHandlerRegistrationControlStub extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler> {

    }

    interface CanvasControlStub extends CanvasControl<AbstractCanvasHandler> {

    }
}