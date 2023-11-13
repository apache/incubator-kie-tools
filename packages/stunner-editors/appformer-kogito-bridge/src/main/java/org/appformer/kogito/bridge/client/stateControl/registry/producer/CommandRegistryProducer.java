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


package org.appformer.kogito.bridge.client.stateControl.registry.producer;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.stateControl.registry.impl.KogitoCommandRegistry;

public class CommandRegistryProducer {

    private final Supplier<Boolean> envelopeEnabledSupplier;
    private final Supplier<KogitoCommandRegistry<?>> kogitoCommandRegistrySupplier;

    public CommandRegistryProducer() {
        this(WindowRef::isEnvelopeAvailable, KogitoCommandRegistry::new);
    }

    CommandRegistryProducer(Supplier<Boolean> envelopeEnabledSupplier, final Supplier<KogitoCommandRegistry<?>> kogitoCommandRegistrySupplier) {
        this.envelopeEnabledSupplier = envelopeEnabledSupplier;
        this.kogitoCommandRegistrySupplier = kogitoCommandRegistrySupplier;
    }

    @Produces
    @Dependent
    public Registry lookup() {
        if (isEnvelopeEnabled()) {
            return kogitoCommandRegistrySupplier.get();
        }
        return new DefaultRegistryImpl();
    }

    boolean isEnvelopeEnabled() {
        return envelopeEnabledSupplier.get();
    }
}
