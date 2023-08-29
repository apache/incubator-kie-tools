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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.graph.Element;

public class DefaultCanvasElementListener implements CanvasElementListener {

    private final Iterable<CanvasControl<AbstractCanvasHandler>> canvasControls;

    public DefaultCanvasElementListener(final Iterable<CanvasControl<AbstractCanvasHandler>> canvasControls) {
        this.canvasControls = canvasControls;
    }

    @Override
    public void update(final Element item) {
        onElementRegistration(item,
                              false,
                              true);
    }

    @Override
    public void register(final Element item) {
        onRegisterElement(item);
    }

    @Override
    public void deregister(final Element item) {
        onDeregisterElement(item);
    }

    @Override
    public void clear() {
        onClear();
    }

    public Iterable<CanvasControl<AbstractCanvasHandler>> getCanvasControls() {
        return canvasControls;
    }

    private void onRegisterElement(final Element element) {
        onElementRegistration(element,
                              true,
                              false);
    }

    private void onDeregisterElement(final Element element) {
        onElementRegistration(element,
                              false,
                              false);
    }

    private void onElementRegistration(final Element element,
                                       final boolean add,
                                       final boolean update) {
        if (update) {
            canvasControls.forEach(c -> fireRegistrationUpdateListeners(c,
                                                                        element));
        } else {
            canvasControls.forEach(c -> fireRegistrationListeners(c,
                                                                  element,
                                                                  add));
        }
    }

    private void onClear() {
        canvasControls.forEach(this::fireRegistrationClearListeners);
    }

    private void fireRegistrationListeners(final CanvasControl<AbstractCanvasHandler> control,
                                           final Element element,
                                           final boolean add) {
        if (null != element && control instanceof CanvasRegistrationControl) {
            final CanvasRegistrationControl<AbstractCanvasHandler, Element> registrationControl =
                    (CanvasRegistrationControl<AbstractCanvasHandler, Element>) control;
            if (add) {
                registrationControl.register(element);
            } else {
                registrationControl.deregister(element);
            }
        }
    }

    private void fireRegistrationUpdateListeners(final CanvasControl<AbstractCanvasHandler> control,
                                                 final Element element) {
        if (null != element && control instanceof AbstractCanvasHandlerRegistrationControl) {
            final AbstractCanvasHandlerRegistrationControl registrationControl =
                    (AbstractCanvasHandlerRegistrationControl) control;
            registrationControl.update(element);
        }
    }

    private void fireRegistrationClearListeners(final CanvasControl<AbstractCanvasHandler> control) {
        if (control instanceof CanvasRegistrationControl) {
            final CanvasRegistrationControl registrationControl =
                    (CanvasRegistrationControl) control;
            registrationControl.clear();
        }
    }
}
