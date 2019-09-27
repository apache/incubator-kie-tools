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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

public class DMNSafeDeleteNodeCommand extends SafeDeleteNodeCommand {

    public DMNSafeDeleteNodeCommand(final Node<?, Edge> node,
                                    final SafeDeleteNodeCommandCallback safeDeleteCallback,
                                    final Options options) {
        super(node, safeDeleteCallback, options);
    }

    @Override
    public boolean shouldKeepChildren(final Node<Definition<?>, Edge> candidate) {
        return candidate.getContent().getDefinition() instanceof DecisionService;
    }
}