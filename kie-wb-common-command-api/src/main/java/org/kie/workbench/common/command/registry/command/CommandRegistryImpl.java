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

package org.kie.workbench.common.command.registry.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.command.Command;

/**
 * The default generic implementation for the CommandRegistry type.
 * It's implemented for achieving an in-memory and lightweight registry approach, don't do an overuse of it.
 * Note: The Stack class behavior when using the iterator is not the expected one, so used
 * ArrayDeque instead of an Stack to provide right iteration order.
 */
public class CommandRegistryImpl<C extends Command> implements CommandRegistry<C> {

    private final Deque<C> commands = new ArrayDeque<>();
    private int maxStackSize = 200;

    @Override
    public void setMaxSize(final int size) {
        this.maxStackSize = size;
    }

    @Override
    public void register(final C command) {
        addIntoStack(command);
    }

    @Override
    public boolean remove(final C command) {
        throw new UnsupportedOperationException("Remove not implemented yet.");
    }

    @Override
    public void clear() {
        commands.clear();
    }

    @Override
    public boolean contains(final C item) {
        throw new UnsupportedOperationException("Contains not implemented yet.");
    }

    @Override
    public boolean isEmpty() {
        return commands.isEmpty();
    }

    @Override
    public List<C> getCommandHistory() {
        return new ArrayList<>(commands);
    }

    @Override
    public C peek() {
        return commands.peek();
    }

    @Override
    public C pop() {
        return commands.pop();
    }

    private void addIntoStack(final C command) {
        if (null != command) {
            if ((commands.size() + 1) > maxStackSize) {
                commands.removeLast();
            }
            commands.push(command);
        }
    }
}
