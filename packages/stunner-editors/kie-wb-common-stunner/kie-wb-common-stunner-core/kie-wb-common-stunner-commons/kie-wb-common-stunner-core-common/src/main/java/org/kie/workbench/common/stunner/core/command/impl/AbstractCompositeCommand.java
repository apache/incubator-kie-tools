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


package org.kie.workbench.common.stunner.core.command.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.command.AbstractGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;

public abstract class AbstractCompositeCommand<T, V> implements Command<T, V> {

    protected final List<Command<T, V>> commands = new LinkedList<>();
    private boolean initialized = false;

    public AbstractCompositeCommand<T, V> addCommand(final Command<T, V> command) {
        commands.add(command);
        return this;
    }

    protected CommandResult<V> doAllow(final T context,
                                       final Command<T, V> command) {
        return command.allow(context);
    }

    protected abstract CommandResult<V> doExecute(final T context,
                                                  final Command<T, V> command);

    protected abstract CommandResult<V> doUndo(final T context,
                                               final Command<T, V> command);

    @Override
    public CommandResult<V> allow(final T context) {
        ensureInitialized(context);
        final List<CommandResult<V>> results = new LinkedList<>();
        for (final Command<T, V> command : commands) {
            final CommandResult<V> result = doAllow(context,
                                                    command);
            results.add(result);
            if (CommandUtils.isError(result)) {
                break;
            }
        }
        return buildResult(results);
    }

    @Override
    public CommandResult<V> execute(final T context) {
        ensureInitialized(context);
        return executeCommands(context);
    }

    protected CommandResult<V> executeCommands(final T context) {
        return processMultipleCommands(commands,
                                       command -> doExecute(context, command),
                                       command -> doUndo(context, command));
    }

    @Override
    public CommandResult<V> undo(final T context) {
        return undo(context,
                    isUndoReverse());
    }

    public int size() {
        return commands.size();
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    public List<Command<T, V>> getCommands() {
        return commands;
    }

    protected AbstractCompositeCommand<T, V> initialize(final T context) {
        // Nothing to do by default. Implementation can add commands here.
        return this;
    }

    public boolean isUndoReverse() {
        return true;
    }

    protected CommandResult<V> undo(final T context,
                                    final boolean reverse) {
        final List<Command<T, V>> collected = reverse ?
                commands.stream().collect(reverse()) : commands.stream().collect(forward());
        return processMultipleCommands(collected,
                                       command -> doUndo(context, command),
                                       command -> doExecute(context, command));
    }

    protected Collection<RuleViolation> evaluate(final GraphCommandExecutionContext context,
                                                 final Function<RuleEvaluationContextBuilder.GraphContextBuilder, GraphEvaluationContext> contextBuilder) {
        return (Collection<RuleViolation>) ((AbstractGraphCommandExecutionContext) context).evaluate(contextBuilder).violations();
    }

    protected boolean isInitialized() {
        return initialized;
    }

    protected void ensureInitialized(final T context) {
        if (!isInitialized()) {
            initialize(context);
            initialized = true;
        }
    }

    protected CommandResult<V> processMultipleFunctions(final Iterable<Command<T, V>> commands,
                                                        final Function<Command<T, V>, CommandResult<V>> function,
                                                        final Consumer<Iterable<Command<T, V>>> revertCandidates) {
        final Stack<Command<T, V>> executedCommands = new Stack<>();
        final List<CommandResult<V>> results = new LinkedList<>();
        for (final Command<T, V> command : commands) {
            final CommandResult<V> violations = function.apply(command);
            results.add(violations);
            if (CommandResult.Type.ERROR.equals(violations.getType())) {
                revertCandidates.accept(executedCommands);
                break;
            }
            executedCommands.push(command);
        }
        return buildResult(results);
    }

    protected CommandResult<V> processMultipleCommands(final Iterable<Command<T, V>> commands,
                                                       final Function<Command<T, V>, CommandResult<V>> executorFunction,
                                                       final Function<Command<T, V>, CommandResult<V>> revertFunction) {
        return processMultipleFunctions(commands,
                                        executorFunction,
                                        revertCandidates -> {
                                            processMultipleFunctions(revertCandidates,
                                                                     revertFunction,
                                                                     c -> {
                                                                     });
                                        });
    }

    protected CommandResult<V> buildResult(final List<CommandResult<V>> results) {
        final CommandResult.Type[] type = {CommandResult.Type.INFO};
        final List<V> violations = new LinkedList<>();
        results.stream()
                .filter(Objects::nonNull)
                .forEach(rr -> {
                    if (hasMoreSeverity(rr.getType(),
                                        type[0])) {
                        type[0] = rr.getType();
                    }
                    final Iterable<V> rrIter = rr.getViolations();
                    if (null != rrIter) {
                        rrIter.forEach(violations::add);
                    }
                });
        return new CommandResultImpl<>(type[0],
                                       violations);
    }

    private boolean hasMoreSeverity(final CommandResult.Type type,
                                    final CommandResult.Type reference) {
        return type.getSeverity() > reference.getSeverity();
    }

    private static <T> Collector<T, ?, List<T>> forward() {
        return Collectors.toList();
    }

    private static <T> Collector<T, ?, List<T>> reverse() {
        return Collectors.collectingAndThen(Collectors.toList(),
                                            l -> {
                                                Collections.reverse(l);
                                                return l;
                                            });
    }

    @Override
    public String toString() {
        String s = "[" + getClass().getSimpleName() + "]";
        for (int x = 0; x < commands.size(); x++) {
            s += " {(" + x + ")[" + commands.get(x) + "]}\n";
        }
        return s;
    }
}
