/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;

public abstract class AbstractCanvasFactory<T extends AbstractCanvasFactory> implements CanvasFactory<AbstractCanvas, AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(AbstractCanvasFactory.class.getName());

    private final Map<Class<? extends CanvasControl>, ManagedInstance> controls = new HashMap<>(15);

    public T register(final Class<? extends CanvasControl> controlType,
                      final ManagedInstance instances) {
        controls.put(controlType,
                     instances);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends CanvasControl> A newControl(final Class<A> type) {
        if (controls.containsKey(type)) {
            final ManagedInstance<A> mi = controls.get(type);
            if (!mi.isUnsatisfied()) {
                return mi.get();
            } else {
                LOGGER.log(Level.SEVERE,
                           "Canvas Control for type [" + type.getName() + "] is cannot be resolved by " +
                                   "this canvas factory [" + this.getClass().getName() + "]");
            }
        } else {
            LOGGER.log(Level.WARNING,
                       "Canvas Control for type [" + type.getName() + "] is not supported by " +
                               "this canvas factory [" + this.getClass().getName() + "]");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
