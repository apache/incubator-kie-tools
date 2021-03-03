/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;

@Dependent
public class EditorSessionCommands {

    private final ManagedClientSessionCommands commands;

    @Inject
    public EditorSessionCommands(final ManagedClientSessionCommands commands) {
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        registerCommands();
    }

    protected void registerCommands() {
        commands.register(SwitchGridSessionCommand.class);
    }

    public EditorSessionCommands bind(final ClientSession session) {
        commands.bind(session);
        return this;
    }

    public ManagedClientSessionCommands getCommands() {
        return commands;
    }

    public SwitchGridSessionCommand getSwitchGridSessionCommand() {
        return commands.get(SwitchGridSessionCommand.class);
    }

    public <S extends ClientSessionCommand> S get(final Class<? extends ClientSessionCommand> type) {
        return commands.get(type);
    }
}