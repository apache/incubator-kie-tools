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

package org.kie.workbench.common.dmn.client.marshaller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

@Dependent
public class DMNMarshallerService {

    private static final Logger LOGGER = Logger.getLogger(DMNMarshallerService.class.getName());

    private final DMNUnmarshaller dmnUnmarshaller;

    private final DMNMarshaller dmnMarshaller;

    private final DMNDiagramFactory dmnDiagramFactory;

    private final DefinitionManager definitionManager;

    private final Promises promises;

    private final DMNDiagramsSession dmnDiagramsSession;

    private final ClientTranslationService translationService;

    private ServiceCallback<Diagram> onDiagramLoad = emptyService();

    private Metadata metadata;

    @Inject
    public DMNMarshallerService(final DMNUnmarshaller dmnUnmarshaller,
                                final DMNMarshaller dmnMarshaller,
                                final DMNDiagramFactory dmnDiagramFactory,
                                final DefinitionManager definitionManager,
                                final Promises promises,
                                final DMNDiagramsSession dmnDiagramsSession,
                                final ClientTranslationService translationService) {
        this.dmnUnmarshaller = dmnUnmarshaller;
        this.dmnMarshaller = dmnMarshaller;
        this.dmnDiagramFactory = dmnDiagramFactory;
        this.definitionManager = definitionManager;
        this.promises = promises;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.translationService = translationService;
    }

    public void unmarshall(final Path path,
                           final String xml,
                           final ServiceCallback<Diagram> callback) {
        unmarshall(buildMetadataInstance(path), xml, callback);
    }

    public void unmarshall(final Metadata metadata,
                           final String xml,
                           final ServiceCallback<Diagram> callback) {

        setOnDiagramLoad(callback);
        setMetadata(metadata);

        try {
            final DMN12UnmarshallCallback jsCallback = dmn12 -> {
                final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
                dmnUnmarshaller.unmarshall(getMetadata(), definitions).then(graph -> {
                    final String fileName = getMetadata().getPath().getFileName();
                    onDiagramLoad(dmnDiagramFactory.build(fileName, getMetadata(), graph));
                    return promises.resolve();
                });
            };
            MainJs.unmarshall(xml, "", jsCallback);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            String contentMessage = generateUnsupportedVersionMessage(xml);

            if (contentMessage != null) {
                callback.onError(
                        new ClientRuntimeError(translationService.getValue(DMNEditorConstants.DMNMarshaller_UnsupportedMessageTitle), contentMessage, e.getMessage(),
                                new DiagramParsingException(getMetadata(), xml)));
            } else {
                callback.onError(new ClientRuntimeError(e.getMessage(), new DiagramParsingException(getMetadata(), xml)));
            }
        }
    }

    private String generateUnsupportedVersionMessage(String xml) {
        String errorMessage = null;

        if (xml.contains("https://www.omg.org/spec/DMN/20191111/MODEL/")) {
            errorMessage = translationService.getValue(DMNEditorConstants.DMNMarshaller_UnsupportedMessage, "1.3");
        } else if (xml.contains("https://www.omg.org/spec/DMN/20211108/MODEL/")) {
            errorMessage = translationService.getValue(DMNEditorConstants.DMNMarshaller_UnsupportedMessage, "1.4");
        } else if (xml.contains("https://www.omg.org/spec/DMN/20230324/MODEL/")) {
            errorMessage = translationService.getValue(DMNEditorConstants.DMNMarshaller_UnsupportedMessage, "1.5");
        }

        return errorMessage;
    }

    public void marshall(final Diagram diagram,
                         final ServiceCallback<String> contentServiceCallback) {
        final DMN12MarshallCallback jsCallback = contentServiceCallback::onSuccess;

        if (Objects.isNull(diagram)) {
            contentServiceCallback.onError(new ClientRuntimeError("The Diagram cannot be null."));
            return;
        }

        final Graph graph = diagram.getGraph();
        if (Objects.isNull(graph)) {
            contentServiceCallback.onError(new ClientRuntimeError("The Diagram graph cannot be null."));
            return;
        }

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshaller.marshall();
            final DMN12 dmn12 = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(dmn12, makeJSINameForDMN12());
            JsUtils.setValueOnWrapped(dmn12, jsitDefinitions);

            final JavaScriptObject namespaces = createNamespaces(jsitDefinitions.getOtherAttributes(),
                    jsitDefinitions.getNamespace());
            MainJs.marshall(dmn12, namespaces, jsCallback);
        } catch (final Exception e) {
            contentServiceCallback.onError(new ClientRuntimeError("Error during the marshaller: " + e.getMessage()));
        }
    }

    public void registerDiagramInstance(final Diagram diagram,
                                        final String title,
                                        final String shapeSetId) {

        registerMetadata(diagram, title, shapeSetId);

        final Node<?, ?> dmnDiagramRoot = DMNGraphUtils.findDMNDiagramRoot(diagram.getGraph());
        final DMNDiagram definition = ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition();
        final DMNDiagramElement drgDiagram = DRGDiagramUtils.newDRGInstance();
        final String diagramId = drgDiagram.getId().getValue();

        final Map<String, Diagram> diagramsByDiagramElementId = new HashMap<>();
        final Map<String, DMNDiagramElement> dmnDiagramsByDiagramElementId = new HashMap<>();

        definition.getDefinitions().getDiagramElements().add(drgDiagram);
        diagramsByDiagramElementId.put(diagramId, diagram);
        dmnDiagramsByDiagramElementId.put(diagramId, drgDiagram);

        dmnDiagramsSession.setState(getMetadata(), diagramsByDiagramElementId, dmnDiagramsByDiagramElementId);
    }

    private JavaScriptObject createNamespaces(final Map<QName, String> otherAttributes,
                                              final String defaultNamespace) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(defaultNamespace, new JSONString(""));
        otherAttributes.forEach((key, value) -> jsonObject.put(value, new JSONString(key.getLocalPart())));
        return jsonObject.getJavaScriptObject();
    }

    private JSIName makeJSINameForDMN12() {
        final org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName jsiName = JSITDefinitions.getJSIName();
        jsiName.setPrefix("dmn");
        jsiName.setLocalPart("definitions");
        final String key = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getLocalPart();
        final String keyString = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getPrefix() + ":" + jsiName.getLocalPart();
        jsiName.setKey(key);
        jsiName.setString(keyString);
        return jsiName;
    }

    public void onDiagramSelected(final @Observes DMNDiagramSelected selected) {

        final DMNDiagramElement dmnDiagramElement = selected.getDiagramElement();

        if (isActiveService()) {

            final String diagramId = dmnDiagramElement.getId().getValue();
            final Diagram stunnerDiagram = dmnDiagramsSession.getDiagram(diagramId);
            final Metadata metadata = dmnDiagramsSession.getDRGDiagram().getMetadata();
            final String fileName = metadata.getPath().getFileName();
            final Diagram diagram = dmnDiagramFactory.build(fileName, metadata, stunnerDiagram.getGraph());

            onDiagramLoad(diagram);
        }
    }

    private boolean isActiveService() {
        final String serviceKey = dmnDiagramsSession.getSessionKey(getMetadata());
        final String currentKey = dmnDiagramsSession.getCurrentSessionKey();
        return Objects.equals(serviceKey, currentKey);
    }

    private org.kie.workbench.common.stunner.core.diagram.Metadata buildMetadataInstance(final Path path) {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);

        return new MetadataImpl.MetadataImplBuilder(defSetId,
                definitionManager)
                .setPath(path)
                .setShapeSetId(shapeSetId)
                .build();
    }

    private void updateClientShapeSetId(final Diagram diagram) {
        if (Objects.nonNull(diagram)) {
            final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
                metadata.setShapeSetId(shapeSetId);
            }
        }
    }

    private void registerMetadata(final Diagram diagram,
                                  final String title,
                                  final String shapeSetId) {

        final Metadata metadata = diagram.getMetadata();
        metadata.setShapeSetId(shapeSetId);
        metadata.setTitle(title);

        setMetadata(metadata);
    }

    private void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    private Metadata getMetadata() {
        return metadata;
    }

    public void setOnDiagramLoad(final ServiceCallback<Diagram> onDiagramLoad) {
        this.onDiagramLoad = onDiagramLoad;
    }

    private void onDiagramLoad(final Diagram diagram) {
        updateClientShapeSetId(diagram);
        onDiagramLoad.onSuccess(diagram);
    }

    private ServiceCallback<Diagram> emptyService() {
        return new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess(final Diagram item) {
                // empty.
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                // empty.
            }
        };
    }
}
