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

package org.kie.workbench.common.stunner.bpmn.integration.backend.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNBackendService;
import org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateRequest;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateResult;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST;
import static org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService.ServiceError.STUNNER_PROCESS_ALREADY_EXIST;

@ApplicationScoped
@Service
public class IntegrationServiceImpl implements IntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationServiceImpl.class);
    private static final String BPMN2_EXTENSION = ".bpmn2";
    private static final String BPMN_EXTENSION = "." + BPMNDefinitionSetResourceType.BPMN_EXTENSION;

    private ProjectDiagramService diagramService;
    private IOService ioService;
    private CommentedOptionFactory optionFactory;
    private FactoryManager factoryManager;
    private BPMNBackendService definitionService;
    private KieModuleService moduleService;
    private KieServiceOverviewLoader overviewLoader;
    private String defSetId;

    public IntegrationServiceImpl() {
        //proxying constructor
    }

    @Inject
    public IntegrationServiceImpl(final ProjectDiagramService diagramService,
                                  final BPMNBackendService definitionService,
                                  final FactoryManager factoryManager,
                                  final KieModuleService moduleService,
                                  final KieServiceOverviewLoader overviewLoader,
                                  final @Named("ioStrategy") IOService ioService,
                                  final CommentedOptionFactory optionFactory) {
        this.diagramService = diagramService;
        this.definitionService = definitionService;
        this.factoryManager = factoryManager;
        this.moduleService = moduleService;
        this.overviewLoader = overviewLoader;
        this.ioService = ioService;
        this.optionFactory = optionFactory;
        this.defSetId = BindableAdapterUtils.getDefinitionSetId(definitionService.getResourceType().getDefinitionSetType());
    }

    @Override
    public MigrateResult migrateDiagram(MigrateRequest request) {
        checkNotNull("request", request);
        if (request.getType() == MigrateRequest.Type.STUNNER_TO_JBPM_DESIGNER) {
            return migrateFromStunnerToJBPMDesigner(request);
        } else {
            return migrateFromJBPMDesignerToStunner(request);
        }
    }

    private MigrateResult migrateFromStunnerToJBPMDesigner(final MigrateRequest request) {
        validateRequest(request);
        final org.uberfire.java.nio.file.Path _path = Paths.convert(request.getPath());
        final org.uberfire.java.nio.file.Path _target = resolveTargetPath(_path, request.getNewName(), request.getNewExtension());
        final Path target = Paths.convert(_target);
        if (ioService.exists(_target)) {
            return new MigrateResult(target, JBPM_DESIGNER_PROCESS_ALREADY_EXIST, JBPM_DESIGNER_PROCESS_ALREADY_EXIST.i18nKey(), Collections.singletonList(request.getPath()));
        }
        try {
            ioService.startBatch(_target.getFileSystem());
            ioService.move(_path,
                           _target,
                           optionFactory.makeCommentedOption(request.getCommitMessage()));
        } catch (Exception e) {
            final String message = String.format("An error was produced during diagram migration from Stunner to jBPMDesigner for diagram: %s", request.getPath());
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        } finally {
            ioService.endBatch();
        }
        return new MigrateResult(target);
    }

    private MigrateResult migrateFromJBPMDesignerToStunner(final MigrateRequest request) {
        validateRequest(request);
        final org.uberfire.java.nio.file.Path _path = Paths.convert(request.getPath());
        final org.uberfire.java.nio.file.Path _target = _path.resolveSibling(request.getNewName() + request.getNewExtension());
        final Path target = Paths.convert(_target);
        if (ioService.exists(_target)) {
            return new MigrateResult(target, STUNNER_PROCESS_ALREADY_EXIST, STUNNER_PROCESS_ALREADY_EXIST.i18nKey(), Collections.singletonList(request.getPath()));
        }
        request.getProjectDiagram().getMetadata().setPath(target);
        request.getProjectDiagram().getMetadata().setTitle(request.getNewName());
        diagramService.saveOrUpdate(request.getProjectDiagram());
        return new MigrateResult(target);
    }

    @SuppressWarnings("unchecked")
    public MarshallingResponse<ProjectDiagram> getDiagramByPath(final Path path, final MarshallingRequest.Mode mode) {
        checkNotNull("path", path);
        checkNotNull("mode", mode);

        final String fileName = path.getFileName();
        String name;
        if (fileName.endsWith(BPMN_EXTENSION)) {
            name = fileName.substring(0, fileName.length() - BPMN_EXTENSION.length());
        } else {
            name = fileName.substring(0, fileName.length() - BPMN2_EXTENSION.length());
        }
        final Package modulePackage = moduleService.resolvePackage(path);
        final KieModule kieModule = moduleService.resolveModule(path);
        final ProjectMetadata metadata = new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(defSetId)
                .forModuleName(kieModule.getModuleName())
                .forProjectPackage(modulePackage)
                .forOverview(overviewLoader.loadOverview(path))
                .forTitle(name)
                .forPath(path)
                .build();

        try (InputStream is = loadPath(path)) {
            final MarshallingResponse<Graph<DefinitionSet, ?>> marshallingResponse =
                    definitionService.getDiagramMarshaller().unmarshallWithValidation(MarshallingRequest.builder()
                                                                                              .metadata(metadata)
                                                                                              .input(is)
                                                                                              .mode(mode)
                                                                                              .build());
            if (marshallingResponse.getState() == MarshallingResponse.State.ERROR) {
                return new MarshallingResponse.MarshallingResponseBuilder<ProjectDiagram>()
                        .messages(marshallingResponse.getMessages())
                        .state(marshallingResponse.getState())
                        .result(null)
                        .build();
            } else {
                final Graph<DefinitionSet, ?> graph = marshallingResponse.getResult().orElseThrow(() -> new RuntimeException("Unexpected error, diagram parsing api must return a value"));
                final DiagramFactory<ProjectMetadata, ?> factory =
                        factoryManager.registry().getDiagramFactory(graph.getContent().getDefinition(), ProjectMetadata.class);
                final ProjectDiagram diagram = (ProjectDiagram) factory.build(name, metadata, graph);
                return new MarshallingResponse.MarshallingResponseBuilder<ProjectDiagram>()
                        .messages(marshallingResponse.getMessages())
                        .state(marshallingResponse.getState())
                        .result(diagram)
                        .build();
            }
        } catch (Exception e) {
            final String message = String.format("An error was produced while diagram loading from file %s:", path);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private static org.uberfire.java.nio.file.Path resolveTargetPath(final org.uberfire.java.nio.file.Path currentPath, final String newName, final String newExtension) {
        return currentPath.resolveSibling(newName + newExtension);
    }

    private static void validateRequest(final MigrateRequest request) {
        checkNotNull("request", request);
        checkNotNull("request.path", request.getPath());
        checkNotNull("request.newName", request.getNewName());
        checkNotNull("request.newExtension", request.getNewExtension());
        if (request.getType() == MigrateRequest.Type.JBPM_DESIGNER_TO_STUNNER) {
            checkNotNull("request.projectDiagram", request.getProjectDiagram());
        }
    }

    private InputStream loadPath(final org.uberfire.backend.vfs.Path _path) {
        org.uberfire.java.nio.file.Path path = Paths.convert(_path);
        final byte[] bytes = ioService.readAllBytes(path);
        return new ByteArrayInputStream(bytes);
    }
}
