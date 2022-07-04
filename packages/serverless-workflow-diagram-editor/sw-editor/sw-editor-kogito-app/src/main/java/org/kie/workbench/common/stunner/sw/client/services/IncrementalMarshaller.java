/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.client.editor.EditorWindow;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.Context;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;
import org.kie.workbench.common.stunner.sw.marshall.MarshallerUtils;

@ApplicationScoped
public class IncrementalMarshaller {

    private final CommandRegistryListener commandListener;
    private Marshaller marshaller;

    @Inject
    public IncrementalMarshaller(CommandRegistryListener commandListener) {
        this.commandListener = commandListener;
    }

    public void run(Marshaller marshaller) {
        this.marshaller = marshaller;
        commandListener.setProvider(this::applyCommand);
    }

    @SuppressWarnings("all")
    private void applyCommand(AbstractCanvasHandler canvasHandler,
                              Command<AbstractCanvasHandler, CanvasViolation> command) {

        // TODO: Not  behaving incremental yet. Just changing the whole content.
        if (true) {
            Promise<String> contentPromise = marshaller.marshallGraph(canvasHandler.getDiagram().getGraph());
            contentPromise.then(new IThenable.ThenOnFulfilledCallbackFn<String, Object>() {
                @Override
                public IThenable<Object> onInvoke(String content) {
                    EditorWindow.updateContent(content);
                    return null;
                }
            });
            return;
        }

        if (command instanceof AddNodeCommand) {
            addNode(((AddNodeCommand) command).getCandidate());
        }
        if (command instanceof AddChildNodeCommand) {
            AddChildNodeCommand c = (AddChildNodeCommand) command;
            Node<View<?>, Edge> parent = c.getParent();
            if (parent.getContent().getDefinition() instanceof Workflow) {
                addNode(c.getCandidate());
            } else {
                // TODO
            }
        }
    }

    @SuppressWarnings("all")
    private void addNode(Node candidate) {
        Object def = MarshallerUtils.getElementDefinition(candidate);
        if (def instanceof State) {
            Context context = marshaller.getContext();
            // Get specific state marshaller.
            Marshaller.NodeMarshaller stateNodeUnmarshaller = Marshaller.getNodeMarshallerForBean(def);
            if (null != stateNodeUnmarshaller) {
                // Update the js type definition.
                stateNodeUnmarshaller.marshall(context, candidate);
                // TODO: Update workflow's state array (push new def)

            }
        }
    }
}
