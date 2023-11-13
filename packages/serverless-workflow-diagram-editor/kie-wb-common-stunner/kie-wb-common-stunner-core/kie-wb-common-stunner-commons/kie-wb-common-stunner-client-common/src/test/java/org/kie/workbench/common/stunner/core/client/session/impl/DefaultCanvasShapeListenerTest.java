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
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasListener;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCanvasShapeListenerTest extends AbstractCanvasListenerTest<AbstractCanvas, Canvas, Shape> {

    private DefaultCanvasShapeListener defaultCanvasShapeListener;

    private List<CanvasRegistrationControlStub> registrationControls;

    private List<CanvasRegistrationControl<AbstractCanvas, Shape>> allRegistrationControls;

    private List<CanvasControl<AbstractCanvas>> nonRegistrationControls;

    private List<CanvasControl<AbstractCanvas>> allControls;

    @Override
    protected CanvasListener<Canvas, Shape> createCanvasListener() {
        defaultCanvasShapeListener = new DefaultCanvasShapeListener(allControls);
        return defaultCanvasShapeListener;
    }

    @Override
    protected List<CanvasRegistrationControl<AbstractCanvas, Shape>> createRegistrationControls() {
        return allRegistrationControls;
    }

    @Override
    protected Shape mockElement() {
        return mock(Shape.class);
    }

    @Override
    @Before
    public void setUp() {
        registrationControls = new ArrayList<>(mockList(CanvasRegistrationControlStub.class,
                                                        CONTROLS));
        allRegistrationControls = new ArrayList<>();
        allRegistrationControls.addAll(registrationControls);

        nonRegistrationControls = new ArrayList<>(mockList(CanvasControlStub.class,
                                                           CONTROLS));
        allControls = new ArrayList<>();
        allControls.addAll(allRegistrationControls);
        allControls.addAll(nonRegistrationControls);

        super.setUp();
    }

    interface CanvasRegistrationControlStub extends CanvasRegistrationControl<AbstractCanvas, Shape> {

    }

    interface CanvasControlStub extends CanvasControl<AbstractCanvas> {

    }
}
