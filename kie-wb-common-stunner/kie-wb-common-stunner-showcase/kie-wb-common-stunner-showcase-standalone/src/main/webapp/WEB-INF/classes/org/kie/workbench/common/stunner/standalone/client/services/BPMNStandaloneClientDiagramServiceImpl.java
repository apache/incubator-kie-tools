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

package org.kie.workbench.common.stunner.standalone.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.api.service.KogitoDiagramService;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class BPMNStandaloneClientDiagramServiceImpl implements KogitoClientDiagramService {

    private ShapeManager shapeManager;
    private Caller<VFSService> vfsServiceCaller;
    private Caller<KogitoDiagramService> kogitoDiagramServiceCaller;
    private Promises promises;

    public BPMNStandaloneClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public BPMNStandaloneClientDiagramServiceImpl(final ShapeManager shapeManager,
                                                  final Caller<VFSService> vfsServiceCaller,
                                                  final Caller<KogitoDiagramService> kogitoDiagramServiceCaller,
                                                  final Promises promises) {
        this.shapeManager = shapeManager;
        this.vfsServiceCaller = vfsServiceCaller;
        this.kogitoDiagramServiceCaller = kogitoDiagramServiceCaller;
        this.promises = promises;
    }

    public void saveAsXml(final Path path,
                          final String xml,
                          final ServiceCallback<String> callback) {
        vfsServiceCaller.call((Path p) -> {
            callback.onSuccess(xml);
        }).write(path, xml);
    }

    public void loadAsXml(final Path path,
                          final ServiceCallback<String> callback) {
        vfsServiceCaller.call((RemoteCallback<String>) callback::onSuccess).readAllString(path);
    }

    //Kogito requirements

    @Override
    public void transform(final String fileName, final String xml,
                          final ServiceCallback<Diagram> callback) {
        kogitoDiagramServiceCaller.call((Diagram d) -> {
            updateClientMetadata(d);
            callback.onSuccess(d);
        }).transform(fileName, xml);
    }

    @Override
    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        kogitoDiagramServiceCaller.call((Diagram d) -> {
            updateClientMetadata(d);
            callback.onSuccess(d);
        }).transform(xml);
    }

    @Override
    public Promise<String> transform(final KogitoDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.promisify(kogitoDiagramServiceCaller,
                                      s -> {
                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
                                      });
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    private void updateClientMetadata(final Diagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }
}
