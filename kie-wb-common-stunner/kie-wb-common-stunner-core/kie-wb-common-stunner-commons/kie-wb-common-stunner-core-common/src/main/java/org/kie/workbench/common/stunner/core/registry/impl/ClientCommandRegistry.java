/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.registry.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.command.Command;

/**
 * The client implementation for the CommandRegistry type.
 * Note: The Stack class behavior when using the iterator is not the expected one, so used
 * ArrayDeque instead of an Stack to provide right iteration order.
 */
@Dependent
public class ClientCommandRegistry<C extends Command> extends CommandRegistryImpl<C> {

    private Event<RegisterChangedEvent> registerChangedEvent;

    @Inject
    public ClientCommandRegistry(Event<RegisterChangedEvent> registerChangedEvent) {
        this.registerChangedEvent = registerChangedEvent;
    }

    public ClientCommandRegistry() {
    }

    @Override
    public void register(final C command) {
        super.register(command);
        registerChangedEvent.fire(new RegisterChangedEvent());
    }

    @Override
    public void clear() {
        super.clear();
        registerChangedEvent.fire(new RegisterChangedEvent());
    }

    @Override
    public C pop() {
        C command = super.pop();
        registerChangedEvent.fire(new RegisterChangedEvent());
        return command;
    }
}
