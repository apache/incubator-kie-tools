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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * Support for a basic single process hierarchy
 */
public class BPMNGraphGenerator extends JsonGenerator {

    private final GraphObjectBuilderFactory bpmnGraphBuilderFactory;
    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final DefinitionsCacheRegistry definitionsCacheRegistry;
    private final RuleManager ruleManager;
    private final OryxManager oryxManager;
    private final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager;
    private final GraphCommandFactory commandFactory;
    private final GraphIndexBuilder<?> indexBuilder;
    private final Class<?> diagramDefinitionSetClass;
    private final Class<? extends BPMNDefinition> diagramDefinitionClass;
    private final Stack<GraphObjectBuilder> nodeBuilders = new Stack<>();
    private final Stack<GraphObjectParser> parsers = new Stack<GraphObjectParser>();
    private final Collection<GraphObjectBuilder<?, ?>> builders = new LinkedList<GraphObjectBuilder<?, ?>>();
    Graph<DefinitionSet, Node> graph;
    boolean isClosed;

    public BPMNGraphGenerator(final GraphObjectBuilderFactory bpmnGraphBuilderFactory,
                              final DefinitionManager definitionManager,
                              final FactoryManager factoryManager,
                              final DefinitionsCacheRegistry definitionsCacheRegistry,
                              final RuleManager ruleManager,
                              final OryxManager oryxManager,
                              final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
                              final GraphCommandFactory commandFactory,
                              final GraphIndexBuilder<?> indexBuilder,
                              final Class<?> diagramDefinitionSetClass,
                              final Class<? extends BPMNDiagram> diagramDefinitionClass) {
        this.bpmnGraphBuilderFactory = bpmnGraphBuilderFactory;
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.definitionsCacheRegistry = definitionsCacheRegistry;
        this.ruleManager = ruleManager;
        this.oryxManager = oryxManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.indexBuilder = indexBuilder;
        this.diagramDefinitionSetClass = diagramDefinitionSetClass;
        this.diagramDefinitionClass = diagramDefinitionClass;
        this.parsers.push(new RootObjectParser(null));
        this.isClosed = false;
    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        parsers.peek().writeStartObject();
    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        parsers.peek().writeEndObject();
    }

    @Override
    public void writeFieldName(final String s) throws IOException, JsonGenerationException {
        parsers.peek().writeFieldName(s);
    }

    @Override
    public void writeObject(final Object o) throws IOException, JsonProcessingException {
        parsers.peek().writeObject(o);
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {
        parsers.peek().writeStartArray();
    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {
        parsers.peek().writeEndArray();
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void close() throws IOException {
        logBuilders();
    }

    public Graph<DefinitionSet, Node> createGraph() {
        Graph<DefinitionSet, Node> graph = (Graph<DefinitionSet, Node>) factoryManager.newElement(UUID.uuid(),
                                                                                                  diagramDefinitionSetClass);
        // TODO: Where are the BPMN diagram bounds in the Oryx json structure? Exist?
        if (null == graph.getContent().getBounds()) {
            graph.getContent().setBounds(new BoundsImpl(
                    new BoundImpl(0d,
                                  0d),
                    new BoundImpl(BPMNGraphFactory.GRAPH_DEFAULT_WIDTH,
                                  BPMNGraphFactory.GRAPH_DEFAULT_HEIGHT)
            ));
        }
        builderContext
                // Initialize the builder context.
                .init(graph)
                // Clears the nodes present, if any, on the recently new graph instance for BPMN. This generator
                // provides the adf for the complete graph structure and nodes.
                .execute(builderContext.getCommandFactory().clearGraph());
        NodeObjectBuilder diagramBuilder = getDiagramBuilder(builderContext);
        if (diagramBuilder == null) {
            throw new RuntimeException("No diagrams found!");
        }
        Node<View<BPMNDefinition>, Edge> diagramNode = (Node<View<BPMNDefinition>, Edge>) diagramBuilder.build(builderContext);
        graph.addNode(diagramNode);

        logBuilders();

        return graph;
    }

    @SuppressWarnings("unchecked")
    protected NodeObjectBuilder getDiagramBuilder(final GraphObjectBuilder.BuilderContext context) {
        Collection<GraphObjectBuilder<?, ?>> builders = context.getBuilders();
        if (builders != null && !builders.isEmpty()) {
            for (GraphObjectBuilder<?, ?> builder : builders) {
                try {
                    NodeObjectBuilder nodeBuilder = (NodeObjectBuilder) builder;
                    if (diagramDefinitionClass.equals(nodeBuilder.getDefinitionClass())) {
                        return nodeBuilder;
                    }
                } catch (ClassCastException e) {
                    // Not a node. Continue with the search...
                }
            }
        }
        return null;
    }

    final GraphObjectBuilder.BuilderContext builderContext = new GraphObjectBuilder.BuilderContext() {

        Graph<DefinitionSet, Node> graph;
        Index<?, ?> index;

        @Override
        public GraphObjectBuilder.BuilderContext init(final Graph<DefinitionSet, Node> graph) {
            this.graph = graph;
            this.index = indexBuilder.build(graph);
            return this;
        }

        @Override
        public Index<?, ?> getIndex() {
            return index;
        }

        @Override
        public Collection<GraphObjectBuilder<?, ?>> getBuilders() {
            return builders;
        }

        @Override
        public DefinitionManager getDefinitionManager() {
            return definitionManager;
        }

        @Override
        public FactoryManager getFactoryManager() {
            return factoryManager;
        }

        @Override
        public DefinitionsCacheRegistry getDefinitionsRegistry() {
            return definitionsCacheRegistry;
        }

        @Override
        public OryxManager getOryxManager() {
            return oryxManager;
        }

        @SuppressWarnings("unchecked")
        public CommandResult<RuleViolation> execute(final Command<GraphCommandExecutionContext, RuleViolation> command) {
            GraphCommandExecutionContext executionContext =
                    new EmptyRulesCommandExecutionContext(definitionManager,
                                                          factoryManager,
                                                          ruleManager,
                                                          index);
            return commandManager.execute(executionContext,
                                          command);
        }

        public GraphCommandFactory getCommandFactory() {
            return commandFactory;
        }
    };

    // For local testing...
    private void logBuilders() {
        log("Logging builders at creation time...");
        for (GraphObjectBuilder<?, ?> builder : builders) {
            log(builder.toString());
        }
    }

    private interface GraphObjectParser {

        void writeStartObject();

        void writeEndObject();

        void writeFieldName(String s);

        void writeObject(Object o);

        void writeStartArray();

        void writeEndArray();
    }

    /*
     Handles these fields:
     - resourceId -> The node identifier.
     - properties -> Delegates to properties object parser
     - stencil -> Delegates to stencil object parser
     - childShapes -> Delegates to other root object parsers
     - outgoing -> Delegates to outgoing object parser
     - bounds -> Delegates to bounds object parser
     */
    final class RootObjectParser implements GraphObjectParser {

        String fieldName;
        NodeObjectBuilder parentNodeBuilder;

        public RootObjectParser(final NodeObjectBuilder parentNodeBuilder) {
            this.parentNodeBuilder = parentNodeBuilder;
        }

        @Override
        public void writeStartObject() {
            if (fieldName == null) {
                nodeBuilders.push(bpmnGraphBuilderFactory.bootstrapBuilder());
            } else if ("properties".equals(fieldName)) {
                parsers.push(new PropertiesObjectParser());
            } else if ("stencil".equals(fieldName)) {
                parsers.push(new StencilObjectParser());
            } else if ("childShapes".equals(fieldName)) {
                RootObjectParser rootObjectParser = nodeBuilders.empty() ? null :
                        new RootObjectParser((NodeObjectBuilder) nodeBuilders.peek());
                parsers.push(rootObjectParser);
                nodeBuilders.push(bpmnGraphBuilderFactory.bootstrapBuilder());
            } else if ("outgoing".equals(fieldName)) {
                parsers.push(new OutgoingObjectParser());
            } else if ("bounds".equals(fieldName)) {
                parsers.push(new BoundsObjectParser());
            } else if ("dockers".equals(fieldName)) {
                parsers.push(new DockersObjectParser());
            } else {
                parsers.push(new DummyObjectParser());
            }
        }

        @Override
        public void writeEndObject() {
            GraphObjectBuilder builder = nodeBuilders.pop();
            builders.add(builder);
            parsers.pop();
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            String value = o.toString();
            if ("resourceId".equals(fieldName)) {
                nodeBuilders.peek().nodeId(value);
                if (null != parentNodeBuilder) {
                    parentNodeBuilder.child(value);
                }
            }
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    ;

    final class PropertiesObjectParser implements GraphObjectParser {

        String fieldName;

        @Override
        public void writeStartObject() {
        }

        @Override
        public void writeEndObject() {
            parsers.pop();
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            nodeBuilders.peek().property(fieldName,
                                         o.toString());
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    final class StencilObjectParser implements GraphObjectParser {

        String fieldName;

        @Override
        public void writeStartObject() {
        }

        @Override
        public void writeEndObject() {
            parsers.pop();
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            if ("id".equals(fieldName)) {
                // Replace the current node builder by the implementation for the specific stencil identifier.
                GraphObjectBuilder builder = nodeBuilders.pop().stencil(o.toString());
                nodeBuilders.push(builder);
            }
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    final class OutgoingObjectParser implements GraphObjectParser {

        String fieldName;

        @Override
        public void writeStartObject() {
        }

        @Override
        public void writeEndObject() {
            parsers.pop();
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            if ("resourceId".equals(fieldName)) {
                nodeBuilders.peek().out(o.toString());
            }
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    final class BoundsObjectParser implements GraphObjectParser {

        String fieldName;
        boolean isLR = false;
        boolean isUL = false;
        boolean end = false;
        Double ulX;
        Double ulY;
        Double lrX;
        Double lrY;

        @Override
        public void writeStartObject() {
            if ("lowerRight".equals(fieldName)) {
                isLR = true;
            }
            if ("upperLeft".equals(fieldName)) {
                isUL = true;
            }
        }

        @Override
        public void writeEndObject() {
            if (end) {
                nodeBuilders.peek().boundUL(ulX,
                                            ulY);
                nodeBuilders.peek().boundLR(lrX,
                                            lrY);
                parsers.pop();
            }
            if (isLR && isUL) {
                end = true;
            }
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            String value = o.toString();
            Double d = Double.valueOf(value);
            if ("x".equals(fieldName)) {
                if (isUL && ulX == null) {
                    ulX = d;
                }
                if (isLR && lrX == null) {
                    lrX = d;
                }
            }
            if ("y".equals(fieldName)) {
                if (isUL && ulY == null) {
                    ulY = d;
                }
                if (isLR && lrY == null) {
                    lrY = d;
                }
            }
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    final class DockersObjectParser implements GraphObjectParser {

        String fieldName;
        Double x = 0d;
        Double y = 0d;

        @Override
        public void writeStartObject() {
        }

        @Override
        public void writeEndObject() {
            nodeBuilders.peek().docker(x,
                                       y);
        }

        @Override
        public void writeFieldName(final String s) {
            this.fieldName = s;
        }

        @Override
        public void writeObject(final Object o) {
            String value = o.toString();
            Double d = Double.valueOf(value);
            if ("x".equals(fieldName)) {
                this.x = d;
            }
            if ("y".equals(fieldName)) {
                this.y = d;
            }
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
            parsers.pop();
        }
    }

    final class DummyObjectParser implements GraphObjectParser {

        @Override
        public void writeStartObject() {
            parsers.push(new DummyObjectParser());
        }

        @Override
        public void writeEndObject() {
            parsers.pop();
        }

        @Override
        public void writeFieldName(final String s) {
        }

        @Override
        public void writeObject(final Object o) {
        }

        @Override
        public void writeStartArray() {
        }

        @Override
        public void writeEndArray() {
        }
    }

    private void log(final String message) {
        System.out.println(message);
    }

    /***********************************************************************************
     * NOT IMPLEMENTED METHODS.
     ***********************************************************************************/
    @Override
    public void flush() throws IOException {
        // Not called...
    }

    @Override
    public JsonGenerator enable(final Feature feature) {
        return null;
    }

    @Override
    public JsonGenerator disable(final Feature feature) {
        return null;
    }

    @Override
    public boolean isEnabled(final Feature feature) {
        return false;
    }

    @Override
    public JsonGenerator setCodec(final ObjectCodec objectCodec) {
        return null;
    }

    @Override
    public ObjectCodec getCodec() {
        return null;
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return null;
    }

    @Override
    public void writeString(final String s) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeString(final char[] chars,
                            final int i,
                            final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRawUTF8String(final byte[] bytes,
                                   final int i,
                                   final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeUTF8String(final byte[] bytes,
                                final int i,
                                final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRaw(final String s) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRaw(final String s,
                         final int i,
                         final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRaw(final char[] chars,
                         final int i,
                         final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRaw(final char c) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRawValue(final String s) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRawValue(final String s,
                              final int i,
                              final int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeRawValue(char[] chars,
                              int i,
                              int i1) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final int i) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final long l) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final BigInteger bigInteger) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final double v) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final float v) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final BigDecimal bigDecimal) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNumber(final String s) throws IOException, JsonGenerationException, UnsupportedOperationException {
    }

    @Override
    public void writeBoolean(final boolean b) throws IOException, JsonGenerationException {
    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {
    }

    @Override
    public void writeTree(final TreeNode treeNode) throws IOException, JsonProcessingException {
    }

    @Override
    public void copyCurrentEvent(final JsonParser jsonParser) throws IOException, JsonProcessingException {
    }

    @Override
    public void copyCurrentStructure(final JsonParser jsonParser) throws IOException, JsonProcessingException {
    }

    @Override
    public JsonStreamContext getOutputContext() {
        return null;
    }

    @Override
    public Version version() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFeatureMask() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JsonGenerator setFeatureMask(int values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeFieldName(SerializableString name) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeString(SerializableString text) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }
}

