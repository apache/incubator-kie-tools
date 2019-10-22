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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import com.google.gwt.core.client.GWT;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogitoMarshaller;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.uberfire.client.promise.Promises;

/**
 * Holder for the final client-side marshaller integration.
 * These methods should be pasted into DMNClientDiagramServiceImpl
 */
@SuppressWarnings("unused")
public final class DMNClientSideMarshalling {

    private DMNMarshallerKogitoMarshaller dmnMarshallerKogitoMarshaller;
    private Promises promises;

    public Promise<String> transform(final KogitoDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.create((resolveCallbackFn, rejectCallbackFn) -> {
                if (resource.projectDiagram().isPresent()) {
                    final Diagram diagram = resource.projectDiagram().get();
                    marshall(diagram,
                             resolveCallbackFn,
                             rejectCallbackFn);
                } else {
                    rejectCallbackFn.onInvoke(new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present"));
                }
            });
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    @SuppressWarnings("unchecked")
    private void marshall(final Diagram diagram,
                          final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String> resolveCallbackFn,
                          final Promise.PromiseExecutorCallbackFn.RejectCallbackFn rejectCallbackFn) {
        if (Objects.isNull(diagram)) {
            return;
        }
        final Graph graph = diagram.getGraph();
        if (Objects.isNull(graph)) {
            return;
        }

        final DMN12MarshallCallback jsCallback = resolveCallbackFn::onInvoke;

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshallerKogitoMarshaller.marshall(graph);
            final JSIName jsiName = JSITDefinitions.getJSIName();
            jsiName.setPrefix("dmn");
            jsiName.setLocalPart("definitions");
            final String key = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getLocalPart();
            jsiName.setKey(key);
            final String keyString = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getPrefix() + ":" + jsiName.getLocalPart();
            jsiName.setString(keyString);
            final DMN12 dmn12 = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(dmn12, jsiName);
            JsUtils.setValueOnWrapped(dmn12, jsitDefinitions);

            MainJs.marshall(dmn12, jsitDefinitions.getNamespace(), jsCallback);
        } catch (Exception e) {
            GWT.log(e.getMessage(), e);
            rejectCallbackFn.onInvoke(e);
        }
    }
}
