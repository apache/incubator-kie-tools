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

package org.kie.workbench.common.stunner.backend.service;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.StandardDeleteOption;

@ApplicationScoped
@Service
public class DiagramServiceImpl
        extends AbstractVFSDiagramService<Metadata, Diagram<Graph, Metadata>>
        implements DiagramService {

    private static final Logger LOG = LoggerFactory.getLogger(DiagramServiceImpl.class.getName());

    private static final String METADATA_EXTENSION = "meta";
    private static final String DIAGRAMS_PATH = "diagrams";

    private final BackendFileSystemManagerImpl backendFileSystemManager;

    // CDI proxy.
    protected DiagramServiceImpl() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DiagramServiceImpl(final DefinitionManager definitionManager,
                              final FactoryManager factoryManager,
                              final Instance<DefinitionSetService> definitionSetServiceInstances,
                              final BackendRegistryFactory registryFactory,
                              final BackendFileSystemManagerImpl backendFileSystemManager) {
        super(definitionManager,
              factoryManager,
              definitionSetServiceInstances,
              registryFactory);
        this.backendFileSystemManager = backendFileSystemManager;
    }

    @PostConstruct
    public void init() {
        // Initialize caches.
        super.initialize();
        // Register packaged diagrams into VFS.
        registerAppDefinitions();
        // Load vfs diagrams and put into the parent registry.
        final Collection<Diagram<Graph, Metadata>> diagrams = getAllDiagrams();
        if (null != diagrams) {
            diagrams.forEach(diagram -> getRegistry().register(diagram));
        }
    }

    @Override
    public Path create(final Path path,
                       final String name,
                       final String defSetId) {
        return super.create(path,
                            name,
                            defSetId,
                            buildMetadataInstance(path,
                                                  defSetId,
                                                  name));
    }

    @Override
    protected Metadata buildMetadataInstance(final org.uberfire.backend.vfs.Path path,
                                             final String defSetId,
                                             final String title) {
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    getDefinitionManager())
                .setRoot(getRoot())
                .setPath(path)
                .setTitle(title)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Metadata obtainMetadata(DefinitionSetService services,
                                      final org.uberfire.backend.vfs.Path diagramFilePath,
                                      final String defSetId,
                                      final String fileName) {
        Metadata metadata = null;
        final InputStream metaDataStream = loadMetadataForPath(diagramFilePath);
        if (null != metaDataStream) {
            try {
                metadata = services.getDiagramMarshaller().getMetadataMarshaller().unmarshall(metaDataStream);
                if (null == metadata.getRoot() || null == metadata.getRoot().toURI()) {
                    metadata.setRoot(getRoot());
                }
            } catch (java.io.IOException e) {
                LOG.error("Cannot unmarshall metadata for diagram's path [" + diagramFilePath + "]",
                          e);
            }
        }
        return metadata;
    }

    private org.uberfire.backend.vfs.Path getRoot() {
        return Paths.convert(backendFileSystemManager.getRootPath());
    }

    @Override
    protected Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    private InputStream loadMetadataForPath(final Path path) {
        return doLoadMetadataStreamByDiagramPath(path);
    }

    @Override
    protected Metadata doSave(final Diagram diagram,
                              final String raw,
                              final String metadata) {
        try {
            getIoService().startBatch(backendFileSystemManager.getFileSystem());
            final Path _path = diagram.getMetadata().getPath();
            final String name = null != _path ? _path.getFileName() : getNewFileName(diagram);
            final org.uberfire.java.nio.file.Path path =
                    null != _path ? Paths.convert(_path) : getDiagramsPath().resolve(name);
            // Serialize the diagram's raw data.
            LOG.debug("Serializing raw data: " + raw);
            getIoService().write(path,
                                 raw);
            final String metadataFileName = getMetadataFileName(name);
            final org.uberfire.java.nio.file.Path metadataPath =
                    getDiagramsPath().resolve(metadataFileName);
            LOG.debug("Serializing raw metadadata: " + metadata);
            getIoService().write(metadataPath,
                                 metadata);
            diagram.getMetadata().setPath(Paths.convert(path));
        } catch (Exception e) {
            LOG.error("Error serializing diagram with UUID [" + diagram.getName() + "].",
                      e);
        } finally {
            getIoService().endBatch();
        }
        return diagram.getMetadata();
    }

    private String getNewFileName(final Diagram diagram) {
        final String defSetId = diagram.getMetadata().getDefinitionSetId();
        final DefinitionSetService defSetService = getServiceById(defSetId);
        return UUID.uuid(8) + "." + defSetService.getResourceType().getSuffix();
    }

    @Override
    protected boolean doDelete(final Path _path) {
        final org.uberfire.java.nio.file.Path path = Paths.convert(_path);
        if (getIoService().exists(path)) {
            getIoService().startBatch(backendFileSystemManager.getFileSystem());
            try {
                getIoService().deleteIfExists(path,
                                              StandardDeleteOption.NON_EMPTY_DIRECTORIES);
            } catch (Exception e) {
                LOG.error("Error deleting diagram for path [" + path + "].",
                          e);
                return false;
            } finally {
                getIoService().endBatch();
            }
        }
        return true;
    }

    @Override
    protected IOService getIoService() {
        return backendFileSystemManager.getIoService();
    }

    public org.uberfire.java.nio.file.Path getDiagramsPath() {
        return backendFileSystemManager.getRootPath().resolve(DIAGRAMS_PATH);
    }

    private InputStream doLoadMetadataStreamByDiagramPath(final Path dPath) {
        org.uberfire.java.nio.file.Path path = getDiagramsPath().resolve(getMetadataFileName(dPath.getFileName()));
        if (null != path) {
            try {
                return loadPath(path);
            } catch (Exception e) {
                LOG.warn("Cannot load metadata for [" + dPath.toString() + "].",
                         e);
            }
        }
        return null;
    }

    private String getMetadataFileName(final String uri) {
        return uri + "." + METADATA_EXTENSION;
    }

    private Collection<Diagram<Graph, Metadata>> getAllDiagrams() {
        return getDiagramsByPath(getDiagramsPath());
    }

    private void registerAppDefinitions() {
        final String diagramsAppPath = backendFileSystemManager.getPathRelativeToApp(DIAGRAMS_PATH);
        backendFileSystemManager.findAndDeployFiles(new File(diagramsAppPath),
                                                    (dir, name) -> isFileNameAccepted(name),
                                                    getDiagramsPath());
    }

    private boolean isFileNameAccepted(final String name) {
        if (name != null && name.trim().length() > 0) {
            return getExtensionsAccepted().stream().anyMatch(s -> name.endsWith("." + s)) || isMetadataFile(name);
        }
        return false;
    }

    private boolean isMetadataFile(final String name) {
        return null != name && name.endsWith("." + METADATA_EXTENSION);
    }
}
