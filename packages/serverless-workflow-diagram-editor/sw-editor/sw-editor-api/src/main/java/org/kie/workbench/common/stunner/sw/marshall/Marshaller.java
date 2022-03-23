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

package org.kie.workbench.common.stunner.sw.marshall;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.Global;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.AutoLayout;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.EVENT_STATE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.EVENT_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.STATE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.ERROR_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.ERROR_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.START_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.START_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.START_NODE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.WORKFLOW_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.WORKFLOW_UNMARSHALLER;

@ApplicationScoped
public class Marshaller {

    public static final String WORKFLOW_UUID = "workflowRoot";
    public static final String STATE_START = "startState";
    public static final String STATE_END = "endState";
    public static final String EDGE_START = "startEdge";

    @Inject
    private DefinitionManager definitionManager;
    @Inject
    private FactoryManager factoryManager;
    @Inject
    private Parser parser;
    @Inject
    private Promises promises;

    private Context context;

    @SuppressWarnings("all")
    public Promise<Graph> unmarshall(String raw) {
        final Object root = parse(raw);
        final Workflow workflow = parser.parse(Js.uncheckedCast(root));

        // TODO: Use dedicated factory instead.
        final GraphImpl<Object> graph = GraphImpl.build(workflow.id);
        context = new Context(graph);
        final BuilderContext builderContext = new BuilderContext(context, factoryManager);

        // Workflow root node.
        unmarshallNode(builderContext, workflow);

        // Graph building.
        final Index index = new MapIndexBuilder().build(graph);
        final DirectGraphCommandExecutionContext executionContext =
                new DirectGraphCommandExecutionContext(definitionManager,
                                                       factoryManager,
                                                       index);
        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> commands = builderContext.commands();
        commands.execute(executionContext);

        // Perform automatic layout.
        return AutoLayout.applyLayout(graph, promises, executionContext, context.getWorkflowRootNode().getUUID());
    }

    @SuppressWarnings("all")
    public Promise<String> marshall(Graph graph) {
        // Marshall from the workflow root node.
        Workflow workflow = marshallNode(context, context.getWorkflowRootNode());
        // Stringify the workflow js type.
        String raw = stringify(workflow);
        return promises.resolve(raw);
    }

    public Context getContext() {
        return context;
    }

    public static Object parse(String raw) {
        return Global.JSON.parse(raw);
    }

    public static String stringify(Object jsonObj) {
        return Global.JSON.stringify(jsonObj, (key, value) -> {
            if (null == value) {
                return Global.undefined;
            }
            if (key.contains("hashCode") ||
                    key.contains("host") ||
                    key.contains("labels") ||
                    key.startsWith("$")) {
                return Global.undefined;
            }
            return value;
        });
    }

    /* +++++++++++++++++ UN-MARSHALLING +++++++++++++++++ */

    @FunctionalInterface
    public interface NodeUnmarshaller<T> {

        Node<? extends Definition<?>, Edge> unmarshall(BuilderContext context, T bean);
    }

    @FunctionalInterface
    public interface EdgeUnmarshaller<T> {

        Edge<ViewConnector<T>, Node> unmarshall(BuilderContext context, T domainBean);
    }

    public static Node unmarshallNode(BuilderContext context, Object bean) {
        return getNodeUnmarshaller(bean).unmarshall(context, bean);
    }

    public static Edge<ViewConnector<Object>, Node> unmarshallEdge(BuilderContext context, Object bean) {
        return getEdgeUnmarshaller(bean).unmarshall(context, bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> NodeUnmarshaller<T> getNodeUnmarshaller(Object bean) {
        final Class<?> type = bean.getClass();
        if (Workflow.class.equals(type)) {
            return (NodeUnmarshaller<T>) WORKFLOW_UNMARSHALLER;
        } else if (EventState.class.equals(type)) {
            return (NodeUnmarshaller<T>) EVENT_STATE_UNMARSHALLER;
        } else if (InjectState.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        } else if (SwitchState.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        }
        throw new UnsupportedOperationException("No NodeUnmarshaller found for " + type.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T> EdgeUnmarshaller<T> getEdgeUnmarshaller(Object bean) {
        final Class<?> type = bean.getClass();
        if (StartTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) START_TRANSITION_UNMARSHALLER;
        }
        if (Transition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) TRANSITION_UNMARSHALLER;
        }
        if (ErrorTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) ERROR_TRANSITION_UNMARSHALLER;
        }
        throw new UnsupportedOperationException("No EdgeUnmarshaller found for " + type.getName());
    }


    /* +++++++++++++++++ MARSHALLING +++++++++++++++++ */

    @FunctionalInterface
    public interface NodeMarshaller<T> {

        T marshall(Context context, Node<? extends Definition<T>, Edge> node);
    }

    @FunctionalInterface
    public interface EdgeMarshaller<T> {

        Edge<ViewConnector<T>, Node> marshall(Context context, Edge<ViewConnector<T>, Node> edge);
    }

    @SuppressWarnings("all")
    public static <T> T marshallNode(Context context, Node node) {
        return (T) getNodeMarshaller(node).marshall(context, node);
    }

    @SuppressWarnings("all")
    public static Edge<ViewConnector<Object>, Node> marshallEdge(Context context, Edge edge) {
        return getEdgeMarshaller(edge).marshall(context, edge);
    }

    @SuppressWarnings("all")
    public static <T> NodeMarshaller<T> getNodeMarshaller(Node node) {
        Object bean = ((Definition) node.getContent()).getDefinition();
        return getNodeMarshallerForBean(bean);
    }

    @SuppressWarnings("all")
    public static <T> EdgeMarshaller<T> getEdgeMarshaller(Edge edge) {
        Object bean = ((Definition) edge.getContent()).getDefinition();
        return getEdgeMarshallerForBean(bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> NodeMarshaller<T> getNodeMarshallerForBean(Object bean) {
        final Class<?> type = bean.getClass();
        if (Workflow.class.equals(type)) {
            return (NodeMarshaller<T>) WORKFLOW_MARSHALLER;
        } else if (Start.class.equals(type)) {
            return (NodeMarshaller<T>) START_NODE_MARSHALLER;
        } else if (EventState.class.equals(type)) {
            return (NodeMarshaller<T>) EVENT_STATE_MARSHALLER;
        } else if (InjectState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (SwitchState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        }
        throw new UnsupportedOperationException("No NodeMarshaller found for " + type.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T> EdgeMarshaller<T> getEdgeMarshallerForBean(Object bean) {
        final Class<?> type = bean.getClass();
        if (StartTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) START_TRANSITION_MARSHALLER;
        }
        if (Transition.class.equals(type)) {
            return (EdgeMarshaller<T>) TRANSITION_MARSHALLER;
        }
        if (ErrorTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) ERROR_TRANSITION_MARSHALLER;
        }
        throw new UnsupportedOperationException("No EdgeMarshaller found for " + type.getName());
    }

    public static boolean isStartState(Node node) {
        return ((View<?>) node.getContent()).getDefinition() instanceof Start;
    }

    public static boolean isEndState(Node node) {
        return ((View<?>) node.getContent()).getDefinition() instanceof End;
    }
}
