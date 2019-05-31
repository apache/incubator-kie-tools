/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class ManagedClientSessionCommands {

    private static final int EXPECTED_COMMANDS_SIZE = 20;

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<ClientSessionCommand> sessionCommands;
    private final List<Class<? extends ClientSessionCommand>> types;
    private final List<ClientSessionCommand> commands;

    @Inject
    public ManagedClientSessionCommands(final DefinitionUtils definitionUtils,
                                        final @Any ManagedInstance<ClientSessionCommand> sessionCommands) {
        this(definitionUtils,
             sessionCommands,
             EXPECTED_COMMANDS_SIZE);
    }

    public ManagedClientSessionCommands(final DefinitionUtils definitionUtils,
                                        final @Any ManagedInstance<ClientSessionCommand> sessionCommands,
                                        final int size) {
        this.definitionUtils = definitionUtils;
        this.sessionCommands = sessionCommands;
        this.types = new ArrayList<>(size);
        this.commands = new ArrayList<>(size);
    }

    public ManagedClientSessionCommands register(final Class<? extends ClientSessionCommand> type) {
        types.add(type);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void bind(final ClientSession session) {
        clearCommands();
        final String id =
                session.getCanvasHandler().getDiagram().getMetadata().getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(id);
        final List<ClientSessionCommand> instances =
                types.stream()
                        .map(type -> InstanceUtils.lookup(sessionCommands,
                                                          type,
                                                          qualifier))
                        .collect(Collectors.toList());
        commands.addAll(instances);
        commands.forEach(c -> safeBind(c, session));
    }

    @SuppressWarnings("unchecked")
    public <S extends ClientSessionCommand> S get(final int index) {
        return (S) commands.get(index);
    }

    @SuppressWarnings("unchecked")
    public <S extends ClientSessionCommand> void visit(final BiConsumer<Class<S>, S> visitor) {
        for (int i = 0; i < commands.size(); i++) {
            final S command = (S) commands.get(i);
            final Class<S> type = (Class<S>) this.types.get(i);
            visitor.accept(type, command);
        }
    }

    @PreDestroy
    public void destroy() {
        clearCommands();
        types.clear();
        sessionCommands.destroyAll();
    }

    public void clearCommands() {
        commands.forEach(command -> InstanceUtils.destroy(sessionCommands, command, ClientSessionCommand::destroy));
        commands.clear();
    }

    @SuppressWarnings("unchecked")
    private void safeBind(final ClientSessionCommand command,
                          final ClientSession session) {
        if (command instanceof AbstractClientSessionCommand) {
            final AbstractClientSessionCommand abstractCommand = (AbstractClientSessionCommand) command;
            if (abstractCommand.accepts(session)) {
                abstractCommand.bind(session);
            } else {
                abstractCommand.enable(false);
            }
        } else {
            command.bind(session);
        }
    }
}