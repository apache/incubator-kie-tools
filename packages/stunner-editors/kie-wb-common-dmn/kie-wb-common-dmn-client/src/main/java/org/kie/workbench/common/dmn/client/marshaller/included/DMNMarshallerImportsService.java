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

package org.kie.workbench.common.dmn.client.marshaller.included;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.client.marshaller.converters.DefinitionsConverter;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class DMNMarshallerImportsService {

    private final NodeEntriesFactory modelToStunnerConverter;

    @Inject
    public DMNMarshallerImportsService(final NodeEntriesFactory modelToStunnerConverter) {
        this.modelToStunnerConverter = modelToStunnerConverter;
    }

    public void getDRGElements(final String dmnXml,
                               final ServiceCallback<List<DRGElement>> callback) {

        final DMN12UnmarshallCallback jsCallback = dmn12 -> {
            final JSITDefinitions dmnDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            callback.onSuccess(modelToStunnerConverter
                                       .makeNodes(dmnDefinitions, new HashMap<>(), false, (a, b) -> {/* Nothing. */})
                                       .stream()
                                       .map(e -> getDRGElement(e.getNode()))
                                       .filter(Optional::isPresent)
                                       .map(Optional::get)
                                       .collect(Collectors.toList()));
        };

        MainJs.unmarshall(dmnXml, "", jsCallback);
    }

    public void getDMNDefinitions(final String dmnXml,
                                  final ServiceCallback<JSITDefinitions> callback) {

        final DMN12UnmarshallCallback jsCallback = dmn12 -> {
            final JSITDefinitions dmnDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            callback.onSuccess(dmnDefinitions);
        };

        MainJs.unmarshall(dmnXml, "", jsCallback);
    }

    public void getWbDefinitions(final String dmnXml, final ServiceCallback<Definitions> callback) {

        final DMN12UnmarshallCallback jsCallback = dmn12 -> {

            final JSITDefinitions dmnDefinitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            final Map<JSITImport, JSITDefinitions> importDefinitions = new HashMap<>();
            final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments = new HashMap<>();
            final Definitions wbDefinitions = DefinitionsConverter.wbFromDMN(dmnDefinitions, importDefinitions, pmmlDocuments);

            callback.onSuccess(wbDefinitions);
        };

        try {
            MainJs.unmarshall(dmnXml, "", jsCallback);
        } catch (final Exception e) {
            callback.onError(new ClientRuntimeError(e.getMessage()));
        }
    }

    private Optional<DRGElement> getDRGElement(final Node node) {
        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);
        if (objectDefinition instanceof DRGElement) {
            return Optional.of((DRGElement) objectDefinition);
        } else {
            return Optional.empty();
        }
    }
}
