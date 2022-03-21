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

package org.kie.workbench.common.stunner.sw.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.sw.Definitions;
import org.kie.workbench.common.stunner.sw.factory.DiagramFactory;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class ClientDiagramService {

    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final DiagramFactory diagramFactory;
    private final ShapeManager shapeManager;
    private final Promises promises;
    private final Marshaller marshaller;

    //CDI proxy
    protected ClientDiagramService() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public ClientDiagramService(final DefinitionManager definitionManager,
                                final FactoryManager factoryManager,
                                final DiagramFactory diagramFactory,
                                final ShapeManager shapeManager,
                                final Promises promises,
                                final Marshaller marshaller) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.diagramFactory = diagramFactory;
        this.shapeManager = shapeManager;
        this.promises = promises;
        this.marshaller = marshaller;
    }

    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform("default", xml, callback);
    }

    public void transform(final String fileName,
                          final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform(fileName, xml, callback);
    }

    private void doTransform(final String fileName,
                             final String xml,
                             final ServiceCallback<Diagram> callback) {

        if (Objects.isNull(xml) || xml.isEmpty()) {
            Diagram newDiagram = createNewDiagram(fileName);
            callback.onSuccess(newDiagram);
        } else {
            parse(fileName, xml, callback);
        }
    }

    public Promise<String> transform(final Diagram diagram) {
        return marshaller.marshall(diagram.getGraph());
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    private Diagram createNewDiagram(String fileName) {
        final String title = "default";
        final String defSetId = getDefinitionSetId();
        final Metadata metadata = createMetadata();
        metadata.setTitle(title);
        final Diagram diagram = factoryManager.newDiagram(title,
                                                          defSetId,
                                                          metadata);
        updateClientMetadata(diagram);
        return diagram;
    }

    private static String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(Definitions.class);
    }

    @SuppressWarnings("all")
    private void parse(final String fileName,
                       final String raw,
                       ServiceCallback<Diagram> serviceCallback) {
        final Metadata metadata = createMetadata();
        final Promise<Graph> promise = unmarshall(metadata, raw);
        promise.then(new IThenable.ThenOnFulfilledCallbackFn<Graph, Object>() {
            @Override
            public IThenable<Object> onInvoke(Graph graph) {
                final String title = "SW Test Diagram";
                metadata.setTitle(title);
                final Diagram diagram = diagramFactory.build(title,
                                                             metadata,
                                                             graph);
                updateClientMetadata(diagram);

                serviceCallback.onSuccess(diagram);
                return null;
            }
        }, new IThenable.ThenOnRejectedCallbackFn<Object>() {
            @Override
            public IThenable<Object> onInvoke(Object o) {
                serviceCallback.onError(new ClientRuntimeError((Throwable) o));
                return null;
            }
        });
    }

    private Promise<Graph> unmarshall(final Metadata metadata,
                                      final String raw) {
        return marshaller.unmarshall(raw);
    }

    private Metadata createMetadata() {
        return new MetadataImpl.MetadataImplBuilder(getDefinitionSetId(),
                                                    definitionManager)
                .build();
    }

    private void updateClientMetadata(final Diagram diagram) {
        final Metadata metadata = diagram.getMetadata();
        String rootUUID = marshaller.getContext().getWorkflowRootNode().getUUID();
        metadata.setCanvasRootUUID(rootUUID);
        if (isEmpty(metadata.getShapeSetId())) {
            final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
            metadata.setShapeSetId(sId);
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }
}
