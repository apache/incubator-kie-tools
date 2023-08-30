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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

public class DefaultCanvasShapeListener implements CanvasShapeListener {

    private final Iterable<CanvasControl<AbstractCanvas>> canvasControls;

    public DefaultCanvasShapeListener(final Iterable<CanvasControl<AbstractCanvas>> canvasControls) {
        this.canvasControls = canvasControls;
    }

    @Override
    public void register(final Shape item) {
        onRegisterShape(item);
    }

    @Override
    public void deregister(final Shape item) {
        onDeregisterShape(item);
    }

    @Override
    public void clear() {
        onClear();
    }

    public Iterable<CanvasControl<AbstractCanvas>> getCanvasControls() {
        return canvasControls;
    }

    private void onRegisterShape(final Shape shape) {
        onShapeRegistration(shape,
                            true);
    }

    private void onDeregisterShape(final Shape shape) {
        onShapeRegistration(shape,
                            false);
    }

    private void onShapeRegistration(final Shape shape,
                                     final boolean add) {
        canvasControls.forEach(c -> fireRegistrationListeners(c,
                                                              shape,
                                                              add));
    }

    private void fireRegistrationListeners(final CanvasControl<AbstractCanvas> control,
                                           final Shape shape,
                                           final boolean add) {
        if (null != shape && control instanceof CanvasRegistrationControl) {
            final CanvasRegistrationControl<AbstractCanvas, Shape> registrationControl =
                    (CanvasRegistrationControl<AbstractCanvas, Shape>) control;
            if (add) {
                registrationControl.register(shape);
            } else {
                registrationControl.deregister(shape);
            }
        }
    }

    private void onClear() {
        canvasControls.forEach(this::fireRegistrationClearListeners);
    }

    private void fireRegistrationClearListeners(final CanvasControl<AbstractCanvas> control) {
        if (control instanceof CanvasRegistrationControl) {
            final CanvasRegistrationControl registrationControl =
                    (CanvasRegistrationControl) control;
            registrationControl.clear();
        }
    }
}
