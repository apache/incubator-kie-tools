/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.menu.dev.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
public class LogNodeEdgesDevCommand extends AbstractSelectedNodeDevCommand {

    private static Logger LOGGER = Logger.getLogger(LogNodeEdgesDevCommand.class.getName());

    protected LogNodeEdgesDevCommand() {
        this(null);
    }

    @Inject
    public LogNodeEdgesDevCommand(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getText() {
        return "Log Edges";
    }

    @Override
    protected void execute(final Node<? extends View<?>, Edge> node) {
        final String uuid = node.getUUID();
        try {
            final List<Edge> inEdges = node.getInEdges();
            log(uuid,
                inEdges,
                true);
            final List<Edge> outEdges = node.getOutEdges();
            log(uuid,
                outEdges,
                false);
        } catch (final ClassCastException e) {
            log("Item [" + uuid + "is not a Node");
        }
    }

    private void log(final String uuid,
                     final List<Edge> edges,
                     final boolean in) {
        final String eType = in ? "incoming" : "outgoing";
        if (null != edges && !edges.isEmpty()) {
            log("************ " + eType + " edges for [" + uuid + "] *********************");
            edges.stream().forEach(this::log);
            log("************ End of " + eType + " edges for [" + uuid + "] *********************");
        } else {
            log("Item [" + uuid + "] has not " + eType + " edges.");
        }
    }

    private void log(final Edge edge) {
        if (null != edge) {
            final String uuid = edge.getUUID();
            final Object content = edge.getContent();
            final Node source = edge.getSourceNode();
            final String sId = null != source ? source.getUUID() : "null";
            final Node target = edge.getTargetNode();
            final String tId = null != target ? target.getUUID() : "null";
            log("-- Edge [uuid=" + uuid + ", content=" + content.getClass().getName()
                        + ", source=" + sId + ", target=" + tId + "]");
        } else {
            log("Edge is null...");
        }
    }

    private void log(final String s) {
        LOGGER.log(Level.FINE,
                   s);
    }
}
