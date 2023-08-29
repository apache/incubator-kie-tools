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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.kogito.client.service.AbstractKogitoClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.UUID;

@ApplicationScoped
public class DMNClientDiagramServiceImpl extends AbstractKogitoClientDiagramService {

    private FactoryManager factoryManager;

    private DefinitionManager definitionManager;

    private Promises promises;

    private DMNMarshallerService marshallerService;

    public DMNClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public DMNClientDiagramServiceImpl(final FactoryManager factoryManager,
                                       final DefinitionManager definitionManager,
                                       final Promises promises,
                                       final DMNMarshallerService marshallerService) {
        this.factoryManager = factoryManager;
        this.definitionManager = definitionManager;
        this.promises = promises;
        this.marshallerService = marshallerService;
    }

    @Override
    public void transform(final String fileName,
                          final String xml,
                          final ServiceCallback<Diagram> callback) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            doNewDiagram(fileName, callback);
        } else {
            doTransformation(fileName, xml, callback);
        }
    }

    @Override
    public String generateDefaultId() {
        return UUID.uuid();
    }

    @Override
    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        transform(UUID.uuid(), xml, callback);
    }

    void doNewDiagram(final String fileName,
                      final ServiceCallback<Diagram> callback) {
        try {

            final String title = createDiagramTitleFromFilePath(fileName);
            final Metadata metadata = buildMetadataInstance(fileName);
            final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
            final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
            final Diagram diagram = factoryManager.newDiagram(title, defSetId, metadata);

            marshallerService.setOnDiagramLoad(callback);
            marshallerService.registerDiagramInstance(diagram, title, shapeSetId);

            callback.onSuccess(diagram);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    Metadata buildMetadataInstance(final String fileName) {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setPath(PathFactory.newPath(".", "/" + fileName))
                .setShapeSetId(shapeSetId)
                .build();
    }

    void doTransformation(final String fileName,
                          final String xml,
                          final ServiceCallback<Diagram> callback) {
        final Metadata metadata = buildMetadataInstance(fileName);
        try {
            marshallerService.unmarshall(metadata, xml, callback);
        } catch (final Exception e) {
            GWT.log(e.getMessage(), e);
            callback.onError(new ClientRuntimeError(new DiagramParsingException(metadata, xml)));
        }
    }

    @Override
    public Promise<String> transform(final Diagram diagram) {
        return promises.create((resolveOnchangeFn, rejectOnchangeFn) -> {
            marshallerService.marshall(diagram, new ServiceCallback<String>() {

                @Override
                public void onSuccess(final String xml) {
                    resolveOnchangeFn.onInvoke(xml);
                }

                @Override
                public void onError(final ClientRuntimeError e) {
                    rejectOnchangeFn.onInvoke(e);
                }
            });
        });
    }
}
