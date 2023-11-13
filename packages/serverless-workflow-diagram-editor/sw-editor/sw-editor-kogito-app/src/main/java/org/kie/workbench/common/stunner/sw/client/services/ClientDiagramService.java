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


package org.kie.workbench.common.stunner.sw.client.services;

import java.util.Objects;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.sw.SWDefinitionSet;
import org.kie.workbench.common.stunner.sw.marshall.Context;
import org.kie.workbench.common.stunner.sw.marshall.DocType;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;
import org.kie.workbench.common.stunner.sw.marshall.Message;
import org.kie.workbench.common.stunner.sw.marshall.ParseResult;
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

    public void transform(final String fileName,
                          final String xml,
                          final DocType docType,
                          final ServiceCallback<ParseResult> callback) {
        doTransform(fileName, xml, docType, callback);
    }

    private void doTransform(final String fileName,
                             final String xml,
                             final DocType docType,
                             final ServiceCallback<ParseResult> callback) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            Diagram newDiagram = createNewDiagram(fileName);
            callback.onSuccess(new ParseResult(newDiagram, new Message[0]));
        } else {
            parse(fileName, xml, docType, callback);
        }
    }

    public Promise<String> transform(final Diagram diagram, DocType docType) {
        return marshaller.marshallGraph(diagram.getGraph(), docType);
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
        return BindableAdapterUtils.getDefinitionSetId(SWDefinitionSet.class);
    }

    @SuppressWarnings("all")
    private void parse(final String fileName,
                       final String raw,
                       final DocType docType,
                       ServiceCallback<ParseResult> serviceCallback) {
        final Metadata metadata = createMetadata();
        final Promise<ParseResult> promise = unmarshall(metadata, raw, docType);
        promise.then(new IThenable.ThenOnFulfilledCallbackFn<ParseResult, Object>() {
            @Override
            public IThenable<Object> onInvoke(ParseResult parseResult) {
                final String title = "SW Test Diagram";
                metadata.setTitle(title);
                final Diagram diagram = diagramFactory.build(title,
                                                             metadata,
                                                             parseResult.getDiagram().getGraph());
                updateClientMetadata(diagram);

                serviceCallback.onSuccess(new ParseResult(diagram, parseResult.getMessages()));
                return null;
            }
        }, new IThenable.ThenOnRejectedCallbackFn<Object>() {
            @Override
            public IThenable<Object> onInvoke(Object o) {
                final ClientRuntimeError e;
                if (o instanceof ClientRuntimeError) {
                    e = (ClientRuntimeError) o;
                } else {
                    e = new ClientRuntimeError((Throwable) o);
                }
                serviceCallback.onError(e);
                return null;
            }
        });
    }

    private Promise<ParseResult> unmarshall(final Metadata metadata,
                                            final String raw,
                                            final DocType docType) {
        return marshaller.unmarshallGraph(raw, docType);
    }

    private Metadata createMetadata() {
        return new MetadataImpl.MetadataImplBuilder(getDefinitionSetId(),
                                                    definitionManager)
                .build();
    }

    private void updateClientMetadata(final Diagram diagram) {
        final Metadata metadata = diagram.getMetadata();

        Context context = marshaller.getContext();
        if (context != null) {
            String rootUUID = context.getWorkflowRootNode().getUUID();
            metadata.setCanvasRootUUID(rootUUID);
        }

        if (isEmpty(metadata.getShapeSetId())) {
            final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
            metadata.setShapeSetId(sId);
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }
}
