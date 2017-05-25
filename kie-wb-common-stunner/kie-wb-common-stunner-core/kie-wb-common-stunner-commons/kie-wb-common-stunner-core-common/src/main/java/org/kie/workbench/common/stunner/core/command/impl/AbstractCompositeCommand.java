/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractCompositeCommand<T, V> implements CompositeCommand<T, V> {

    private static Logger LOGGER = Logger.getLogger(AbstractCompositeCommand.class.getName());

    protected final List<Command<T, V>> commands = new LinkedList<>();
    private boolean initialized = false;

    public AbstractCompositeCommand<T, V> addCommand(final Command<T, V> command) {
        commands.add(command);
        return this;
    }

    protected abstract CommandResult<V> doAllow(final T context,
                                                final Command<T, V> command);

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
        final CommandResult<V> allowResult = this.allow(context);
        if (!CommandUtils.isError(allowResult)) {
            final Stack<Command<T, V>> executedCommands = new Stack<>();
            final List<CommandResult<V>> results = new LinkedList<>();
            for (final Command<T, V> command : commands) {
                final CommandResult<V> violations = doExecute(context,
                                                              command);
                LOGGER.log(Level.FINEST,
                           "Execution of command [" + command + "] finished - Violations [" + violations + "]");
                results.add(violations);
                if (CommandResult.Type.ERROR.equals(violations.getType())) {
                    undoMultipleExecutedCommands(context,
                                                 executedCommands);
                    break;
                }
            }
            return buildResult(results);
        }
        return allowResult;
    }

    @Override
    public CommandResult<V> undo(final T context) {
        return undo(context,
                    isUndoReverse());
    }

    @Override
    public int size() {
        return commands.size();
    }

    public List<Command<T, V>> getCommands() {
        return commands;
    }

    protected AbstractCompositeCommand<T, V> initialize(final T context) {
        // Nothing to do by default. Implementation can add commands here.
        return this;
    }

    protected boolean isUndoReverse() {
        return true;
    }

    protected CommandResult<V> undo(final T context,
                                    final boolean reverse) {
        final List<CommandResult<V>> results = new LinkedList<>();
        final List<Command<T, V>> collected = reverse ?
                commands.stream().collect(reverse()) : commands.stream().collect(forward());
        collected.forEach(command -> {
            final CommandResult<V> violations = doUndo(context,
                                                       command);
            LOGGER.log(Level.FINEST,
                       "Undo of command [" + command + "] finished - Violations [" + violations + "]");
            results.add(violations);
        });
        return buildResult(results);
    }

    @SuppressWarnings("unchecked")
    protected Collection<RuleViolation> doEvaluate(final GraphCommandExecutionContext context,
                                                   final RuleEvaluationContext ruleEvaluationContext) {
        final RuleSet ruleSet = context.getRuleSet();
        return (Collection<RuleViolation>) context.getRuleManager().evaluate(ruleSet,
                                                                             ruleEvaluationContext)
                .violations();
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

    private CommandResult<V> buildResult(final List<CommandResult<V>> results) {
        final CommandResult.Type[] type = {CommandResult.Type.INFO};
        final List<V> violations = new LinkedList<>();
        results.stream().forEach(rr -> {
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

    private CommandResult<V> undoMultipleExecutedCommands(final T context,
                                                          final List<Command<T, V>> commandStack) {
        final List<CommandResult<V>> results = new LinkedList<>();
        commandStack.stream().forEach(command -> results.add(doUndo(context,
                                                                    command)));
        return buildResult(results);
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
        String s = "[" + getClass().getName() + "]";
        for (int x = 0; x < commands.size(); x++) {
            s += " {(" + x + ") [" + commands.get(x) + "]} ";
        }
        return s;
    }
}
