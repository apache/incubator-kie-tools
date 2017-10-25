/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.marshalling.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guvnor.ala.marshalling.Marshaller;
import org.guvnor.ala.marshalling.MarshallerRegistry;

/**
 * Base implementation of a MarshallerRegistry.
 */
public abstract class BaseMarshallerRegistry
        implements MarshallerRegistry {

    protected Map<Class, Marshaller<?>> marshallerMap = new ConcurrentHashMap<>();

    public BaseMarshallerRegistry() {
    }

    @Override
    public void register(final Class clazz,
                         final Marshaller marshaller) {
        marshallerMap.put(clazz,
                          marshaller);
    }

    @Override
    public void deregister(final Class clazz) {
        marshallerMap.remove(clazz);
    }

    @Override
    public Marshaller get(final Class clazz) {
        return marshallerMap.get(clazz);
    }
}
