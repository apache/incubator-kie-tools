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


package org.appformer.kogito.bridge.client.stateControl.registry.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.appformer.kogito.bridge.client.stateControl.registry.interop.KogitoJSCommandRegistry;

/**
 * Implementation of {@link Registry} to register commands on the State Control engine.
 *
 * @param <C>
 */
public class KogitoCommandRegistry<C> implements Registry<C> {

    private KogitoJSCommandRegistry<C> wrapped;
    private RegistryChangeListener registryChangeListener;

    public KogitoCommandRegistry() {
        this(WindowRef::isEnvelopeAvailable, () -> StateControl.get().getCommandRegistry());
    }

    KogitoCommandRegistry(Supplier<Boolean> envelopeEnabledSupplier, Supplier<KogitoJSCommandRegistry<C>> kogitoJSCommandRegistrySupplier) {
        if (!envelopeEnabledSupplier.get()) {
            throw new RuntimeException("Envelope isn't present, we shouldn't be here!");
        }
        wrapped = kogitoJSCommandRegistrySupplier.get();
    }

    @Override
    public void register(C item) {
        wrapped.register(String.valueOf(item.hashCode()), item);
        notifyRegistryChange();
    }

    @Override
    public C peek() {
        return wrapped.peek();
    }

    @Override
    public C pop() {
        Optional<C> optional = Optional.ofNullable(wrapped.pop());
        if (optional.isPresent()) {
            notifyRegistryChange();
        }
        return optional.orElse(null);
    }

    @Override
    public List<C> getHistory() {
        return Stream.of(wrapped.getCommands())
                .collect(Collectors.toList());
    }

    @Override
    public void setMaxSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("The registry size should be a positive number");
        }
        wrapped.setMaxSize(size);
    }

    @Override
    public void clear() {
        wrapped.clear();
        notifyRegistryChange();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public void setRegistryChangeListener(final RegistryChangeListener registryChangeListener) {
        this.registryChangeListener = registryChangeListener;
    }

    private void notifyRegistryChange() {
        if (registryChangeListener != null) {
            registryChangeListener.notifyRegistryChange();
        }
    }
}
