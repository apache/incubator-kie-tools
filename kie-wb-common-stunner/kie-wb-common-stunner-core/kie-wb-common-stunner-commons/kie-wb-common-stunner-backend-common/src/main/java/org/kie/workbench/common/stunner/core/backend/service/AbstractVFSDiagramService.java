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

package org.kie.workbench.common.stunner.core.backend.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.enterprise.inject.Instance;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

// TODO: Use the diagram registry cache.
public abstract class AbstractVFSDiagramService<M extends Metadata, D extends Diagram<Graph, M>> implements BaseDiagramService<M, D> {

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractVFSDiagramService.class.getName());

    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final Instance<DefinitionSetService> definitionSetServiceInstances;
    private final BackendRegistryFactory registryFactory;
    private Collection<DefinitionSetService> definitionSetServices = new LinkedList<>();
    private DiagramRegistry<D> registry;

    public AbstractVFSDiagramService(final DefinitionManager definitionManager,
                                     final FactoryManager factoryManager,
                                     final Instance<DefinitionSetService> definitionSetServiceInstances,
                                     final BackendRegistryFactory registryFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.definitionSetServiceInstances = definitionSetServiceInstances;
        this.registryFactory = registryFactory;
    }

    protected abstract IOService getIoService();

    protected void initialize() {
        for (DefinitionSetService definitionSetService : definitionSetServiceInstances) {
            definitionSetServices.add(definitionSetService);
        }
        this.registry = registryFactory.newDiagramSynchronizedRegistry();
    }

    public Path create(final Path path,
                       final String name,
                       final String defSetId,
                       final Metadata metadata) {
        final DefinitionSetService services = getServiceById(defSetId);
        if (null == services) {
            throw new IllegalStateException("No backend Definition Set services for [" + defSetId + "]");
        }
        final String fName = buildFileName(name,
                                           services.getResourceType());
        final org.uberfire.java.nio.file.Path kiePath = Paths.convert(path).resolve(fName);
        try {
            if (getIoService().exists(kiePath)) {
                throw new FileAlreadyExistsException(kiePath.toString());
            }
            final D diagram = factoryManager.newDiagram(name,
                                                        defSetId,
                                                        metadata);
            final String[] raw = serialize(diagram);
            getIoService().write(kiePath,
                                 raw[0]);
            return Paths.convert(kiePath);
        } catch (final Exception e) {
            LOG.error("Cannot create diagram in path [" + kiePath + "]",
                      e);
        }
        return null;
    }

    @Override
    public String getRawContent(D diagram) {
        try {
            return serialize(diagram)[0];
        } catch (java.io.IOException e) {
            LOG.error("Error while getting raw content for diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<? extends Metadata> getMetadataType();

    private String buildFileName(final String baseFileName,
                                 final ResourceTypeDefinition resourceType) {
        final String suffix = resourceType.getSuffix();
        final String prefix = resourceType.getPrefix();
        final String extension = !(suffix == null || "".equals(suffix)) ? "." + resourceType.getSuffix() : "";
        if (baseFileName.endsWith(extension)) {
            return prefix + baseFileName;
        }
        return prefix + baseFileName + extension;
    }

    @SuppressWarnings("unchecked")
    public D getDiagramByPath(final org.uberfire.backend.vfs.Path file) {
        if (accepts(file)) {
            DefinitionSetService services = getServiceByPath(file);
            if (null != services) {
                final String defSetId = getDefinitionSetId(services);
                final String name = parseFileName(file,
                                                  services);
                // Check if any metadata definition exist.
                M metadata = obtainMetadata(services,
                                            file,
                                            defSetId,
                                            name);
                if (null == metadata) {
                    metadata = (M) buildMetadataInstance(file,
                                                         defSetId,
                                                         name);
                }
                metadata.setPath(file);
                // Parse and load the diagram raw data.
                final InputStream is = loadPath(file);
                try {
                    Graph<DefinitionSet, ?> graph = services.getDiagramMarshaller().unmarshall(metadata,
                                                                                               is);
                    DiagramFactory<M, ?> factory = factoryManager.registry().getDiagramFactory(graph.getContent().getDefinition(),
                                                                                               getMetadataType());
                    return (D) factory.build(name,
                                             metadata,
                                             graph);
                } catch (java.io.IOException e) {
                    LOG.error("Cannot unmarshall diagram for diagram's path [" + file + "]",
                              e);
                    return null;
                }
            }
        }
        throw new UnsupportedOperationException("Diagram format not supported [" + file + "]");
    }

    @SuppressWarnings("unchecked")
    protected abstract M obtainMetadata(DefinitionSetService services,
                                        final org.uberfire.backend.vfs.Path diagramFilePath,
                                        final String defSetId,
                                        final String fileName);

    private String parseFileName(final org.uberfire.backend.vfs.Path file,
                                 final DefinitionSetService services) {
        final String n = file.getFileName();
        final String ext = services.getResourceType().getSuffix();
        if (!n.endsWith(ext)) {
            throw new RuntimeException("File [" + n + "] should have the suffix [" + ext + "]");
        }
        return n.substring(0,
                           n.length() - ext.length() - 1);
    }

    public M saveOrUpdate(final D diagram) {
        return register(diagram);
    }

    public boolean delete(final D diagram) {
        Path path = diagram.getMetadata().getPath();
        return doDelete(path);
    }

    protected abstract boolean doDelete(final Path path);

    protected abstract M doSave(final D diagram,
                                final String raw,
                                final String metadata);

    @SuppressWarnings("unchecked")
    private M register(final D diagram) {
        try {
            String[] raw = serialize(diagram);
            return doSave(diagram,
                          raw[0],
                          raw[1]);
        } catch (Exception e) {
            LOG.error("Error while saving diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected String[] serialize(final D diagram) throws java.io.IOException {
        final String defSetId = diagram.getMetadata().getDefinitionSetId();
        final DefinitionSetService services = getServiceById(defSetId);
        // Serialize using the concrete marshalling service.
        DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = services.getDiagramMarshaller();
        final String rawData = marshaller.marshall((Diagram<Graph, Metadata>) diagram);
        final Metadata metadata = diagram.getMetadata();
        final String metadataRaw = marshaller.getMetadataMarshaller().marshall(metadata);
        return new String[]{rawData, metadataRaw};
    }

    public boolean contains(final D item) {
        return null != getDiagramByPath(item.getMetadata().getPath());
    }

    public Collection<D> getDiagramsByPath(final org.uberfire.java.nio.file.Path root) {
        try {
            final Collection<D> result = new ArrayList<D>();
            if (getIoService().exists(root)) {
                walkFileTree(checkNotNull("root",
                                          root),
                             new SimpleFileVisitor<org.uberfire.java.nio.file.Path>() {
                                 @Override
                                 public FileVisitResult visitFile(final org.uberfire.java.nio.file.Path _file,
                                                                  final BasicFileAttributes attrs) throws IOException {
                                     checkNotNull("file",
                                                  _file);
                                     checkNotNull("attrs",
                                                  attrs);
                                     org.uberfire.backend.vfs.Path file = org.uberfire.backend.server.util.Paths.convert(_file);
                                     if (accepts(file)) {
                                         try {
                                             // portable diagram representation.
                                             D diagram = getDiagramByPath(file);
                                             if (null != diagram) {
                                                 result.add(diagram);
                                             }
                                         } catch (Exception e) {
                                             //skipping add diagram to the result
                                             LOG.error("Error getting diagram for path {}",
                                                       file,
                                                       e);
                                         }
                                     }
                                     return FileVisitResult.CONTINUE;
                                 }
                             });
            }
            return result;
        } catch (Exception e) {
            LOG.error("Error while obtaining diagrams.",
                      e);
            throw e;
        }
    }

    protected abstract Metadata buildMetadataInstance(final org.uberfire.backend.vfs.Path path,
                                                      final String defSetId,
                                                      final String title);

    protected InputStream loadPath(final org.uberfire.backend.vfs.Path _path) {
        org.uberfire.java.nio.file.Path path = Paths.convert(_path);
        final byte[] bytes = getIoService().readAllBytes(path);
        return new ByteArrayInputStream(bytes);
    }

    protected InputStream loadPath(final org.uberfire.java.nio.file.Path _path) {
        final byte[] bytes = getIoService().readAllBytes(_path);
        return new ByteArrayInputStream(bytes);
    }

    public boolean accepts(final org.uberfire.backend.vfs.Path path) {
        if (path != null) {
            // Look for the specific services definition.
            for (DefinitionSetService definitionSetService : definitionSetServices) {
                if (definitionSetService.getResourceType().accept(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Set<String> getExtensionsAccepted() {
        Set<String> result = new LinkedHashSet<>();
        // Look for the specific services definition.
        for (DefinitionSetService definitionSetService : definitionSetServices) {
            result.add(definitionSetService.getResourceType().getSuffix());
        }
        return result;
    }

    protected DefinitionSetService getServiceByPath(final org.uberfire.backend.vfs.Path path) {
        // Look for the specific services definition.
        for (DefinitionSetService definitionSetService : definitionSetServices) {
            if (definitionSetService.getResourceType().accept(path)) {
                return definitionSetService;
            }
        }
        return null;
    }

    protected String getDefinitionSetId(final DefinitionSetService services) {
        Class<?> type = services.getResourceType().getDefinitionSetType();
        return BindableAdapterUtils.getDefinitionSetId(type);
    }

    protected DefinitionSetService getServiceById(final String defSetId) {
        // Look for the specific services definition.
        for (DefinitionSetService definitionSetService : definitionSetServices) {
            if (definitionSetService.accepts(defSetId)) {
                return definitionSetService;
            }
        }
        return null;
    }

    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    protected DiagramRegistry<D> getRegistry() {
        return registry;
    }
}
