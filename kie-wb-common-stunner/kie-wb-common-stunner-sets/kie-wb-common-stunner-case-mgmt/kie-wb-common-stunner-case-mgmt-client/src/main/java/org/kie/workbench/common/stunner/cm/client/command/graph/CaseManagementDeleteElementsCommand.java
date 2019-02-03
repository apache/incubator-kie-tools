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
package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.Collection;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;

@Portable
public class CaseManagementDeleteElementsCommand extends DeleteElementsCommand {

    public CaseManagementDeleteElementsCommand(@MapsTo("uuids") Collection<String> uuids) {
        super(uuids);
    }

    public CaseManagementDeleteElementsCommand(Supplier<Collection<Element>> elements) {
        super(elements);
    }

    public CaseManagementDeleteElementsCommand(Supplier<Collection<Element>> elements,
                                               DeleteCallback callback) {
        super(elements, callback);
    }

    @Override
    protected CaseManagementSafeDeleteNodeCommand createSafeDeleteNodeCommand(final Node<?, Edge> node,
                                                                              final SafeDeleteNodeCommand.Options options,
                                                                              final DeleteCallback callback) {
        return new CaseManagementSafeDeleteNodeCommand(node,
                                                       callback.onDeleteNode(node, options),
                                                       options);
    }
}
