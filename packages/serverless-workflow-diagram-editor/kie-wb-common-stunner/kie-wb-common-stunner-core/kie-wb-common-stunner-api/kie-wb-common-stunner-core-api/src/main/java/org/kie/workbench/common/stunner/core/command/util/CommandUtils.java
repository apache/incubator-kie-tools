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

package org.kie.workbench.common.stunner.core.command.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CommandUtils {

    public static String[] toUUIDs(final Collection<Node<?, Edge>> nodes) {
        return nodes.stream().map(Element::getUUID).toArray(String[]::new);
    }

    @SuppressWarnings("unchecked")
    public static Collection<Node<?, Edge>> getCandidates(final GraphCommandExecutionContext context,
                                                          final String[] candidateUUIDs) {
        final Index<Node<?, Edge>, ?> graphIndex = (Index<Node<?, Edge>, ?>) context.getGraphIndex();
        return Stream.of(candidateUUIDs)
                .map(graphIndex::getNode)
                .collect(Collectors.toSet());
    }

    public static boolean isError(final CommandResult<?> result) {
        return isCommandResultError(result);
    }

    public static boolean isWarn(final CommandResult<?> result) {
        return isCommandResultWarn(result);
    }

    public static CommandResult.Type getType(final RuleViolation violation) {
        switch (violation.getViolationType()) {
            case ERROR:
                return CommandResult.Type.ERROR;
            case WARNING:
                return CommandResult.Type.WARNING;
        }
        return CommandResult.Type.INFO;
    }

    public static <V> List<V> toList(final Iterable<V> iterable) {
        final List<V> result = new LinkedList<>();
        iterable.iterator().forEachRemaining(result::add);
        return result;
    }

    private static boolean isCommandResultError(final CommandResult<?> result) {
        return result != null && CommandResult.Type.ERROR.equals(result.getType());
    }

    private static boolean isCommandResultWarn(final CommandResult<?> result) {
        return result != null && CommandResult.Type.WARNING.equals(result.getType());
    }
}
