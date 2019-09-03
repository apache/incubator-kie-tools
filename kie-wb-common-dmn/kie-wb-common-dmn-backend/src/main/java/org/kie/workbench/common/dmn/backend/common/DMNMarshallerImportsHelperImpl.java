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

package org.kie.workbench.common.dmn.backend.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.Invocable;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.backend.editors.common.PMMLIncludedDocumentFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ImportedItemDefinitionConverter.withNamespace;

@ApplicationScoped
public class DMNMarshallerImportsHelperImpl implements DMNMarshallerImportsHelper {

    private final DMNPathsHelper pathsHelper;

    private final WorkspaceProjectService projectService;

    private final IOService ioService;

    private final DMNMarshaller marshaller;

    private final DMNIOHelper dmnIOHelper;

    public static final QName NAMESPACE = new QName("Namespace");

    private final PMMLIncludedDocumentFactory pmmlDocumentFactory;

    public DMNMarshallerImportsHelperImpl() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public DMNMarshallerImportsHelperImpl(final DMNPathsHelper pathsHelper,
                                          final WorkspaceProjectService projectService,
                                          final DMNMarshaller marshaller,
                                          final DMNIOHelper dmnIOHelper,
                                          final PMMLIncludedDocumentFactory pmmlDocumentFactory,
                                          final @Named("ioStrategy") IOService ioService) {
        this.pathsHelper = pathsHelper;
        this.projectService = projectService;
        this.marshaller = marshaller;
        this.dmnIOHelper = dmnIOHelper;
        this.pmmlDocumentFactory = pmmlDocumentFactory;
        this.ioService = ioService;
    }

    @Override
    public Map<Import, Definitions> getImportDefinitions(final Metadata metadata,
                                                         final List<Import> imports) {

        final Map<Import, Definitions> importDefinitions = new HashMap<>();

        if (imports.size() > 0) {
            for (final Definitions definitions : getOtherDMNDiagramsDefinitions(metadata)) {
                findImportByDefinitions(definitions, imports).ifPresent(anImport -> {
                    importDefinitions.put(anImport, definitions);
                });
            }
        }

        return importDefinitions;
    }

    @Override
    public Map<Import, PMMLDocumentMetadata> getPMMLDocuments(final Metadata metadata,
                                                              final List<Import> imports) {
        final Map<Import, PMMLDocumentMetadata> pmmlDocuments = new HashMap<>();

        if (imports.size() > 0) {
            for (final Path pmmlDocumentPath : getPMMLDocumentPaths(metadata)) {
                findImportByPMMLDocument(metadata.getPath(), pmmlDocumentPath, imports).ifPresent(anImport -> {
                    pmmlDocuments.put(anImport, pmmlDocumentFactory.getDocumentByPath(pmmlDocumentPath));
                });
            }
        }

        return pmmlDocuments;
    }

    @Override
    public Map<Import, String> getImportXML(final Metadata metadata,
                                            final List<Import> imports) {

        final Map<Import, String> importXML = new HashMap<>();

        if (imports.size() > 0) {
            for (final String xml : getOtherDMNDiagramsXML(metadata)) {
                try (StringReader sr = toStringReader(xml)) {
                    final Definitions definitions = marshaller.unmarshal(sr);
                    findImportByDefinitions(definitions, imports).ifPresent(anImport -> {
                        importXML.put(anImport, xml);
                    });
                }
            }
        }

        return importXML;
    }

    @Override
    public Path getDMNModelPath(final Metadata metadata,
                                final String modelNamespace,
                                final String modelName) {

        final WorkspaceProject workspaceProject = getProject(metadata);

        for (final Path dmnModelPath : pathsHelper.getDMNModelsPaths(workspaceProject)) {

            final Optional<Definitions> definitions = getDefinitionsByPath(dmnModelPath);

            if (definitions.map(d -> Objects.equals(d.getNamespace(), modelNamespace) && Objects.equals(d.getName(), modelName)).orElse(false)) {
                return dmnModelPath;
            }
        }

        throw new UnsupportedOperationException("No DMN model could be found for the following namespace: " + modelNamespace);
    }

    @Override
    public List<DRGElement> getImportedDRGElements(final Map<Import, Definitions> importDefinitions) {

        final List<DRGElement> importedNodes = new ArrayList<>();

        importDefinitions.forEach((anImport, definitions) -> {
            importedNodes.addAll(getDrgElementsWithNamespace(definitions, anImport));
        });

        return importedNodes;
    }

    @Override
    public List<ItemDefinition> getImportedItemDefinitions(final Map<Import, Definitions> importDefinitions) {

        final List<ItemDefinition> itemDefinitions = new ArrayList<>();

        importDefinitions.forEach((anImport, definitions) -> {
            itemDefinitions.addAll(getItemDefinitionsWithNamespace(definitions, anImport));
        });

        return itemDefinitions;
    }

    @Override
    public List<ItemDefinition> getImportedItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                                      final String modelName,
                                                                      final String namespace) {

        return findDefinitionsByNamespace(workspaceProject, namespace)
                .map(Definitions::getItemDefinition)
                .orElse(emptyList());
    }

    private Optional<Definitions> findDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                             final String namespace) {
        return pathsHelper
                .getDMNModelsPaths(workspaceProject)
                .stream()
                .map(path -> getDefinitionsByPath(path).orElse(null))
                .filter(Objects::nonNull)
                .filter(definitions -> Objects.equals(definitions.getNamespace(), namespace))
                .findAny();
    }

    List<ItemDefinition> getItemDefinitionsWithNamespace(final Definitions definitions,
                                                         final Import anImport) {

        final List<ItemDefinition> itemDefinitions = definitions.getItemDefinition();
        final String prefix = anImport.getName();

        return itemDefinitions
                .stream()
                .map(itemDefinition -> withNamespace(itemDefinition, prefix))
                .collect(Collectors.toList());
    }

    List<DRGElement> getDrgElementsWithNamespace(final Definitions definitions,
                                                 final Import anImport) {
        return definitions
                .getDrgElement()
                .stream()
                .map(drgElement -> drgElementWithNamespace(drgElement, anImport))
                .collect(Collectors.toList());
    }

    private DRGElement drgElementWithNamespace(final DRGElement drgElement,
                                               final Import anImport) {

        final String namespace = anImport.getName();

        drgElement.getAdditionalAttributes().put(NAMESPACE, anImport.getNamespace());
        drgElement.setId(namespace + ":" + drgElement.getId());
        drgElement.setName(namespace + "." + drgElement.getName());
        updateInformationItem(namespace, drgElement);

        return drgElement;
    }

    private void updateInformationItem(final String namespace,
                                       final DRGElement drgElement) {

        getInformationItem(drgElement).ifPresent(informationItem -> {

            final InformationItem tInformationItem = new TInformationItem();
            final QName qName = informationItem.getTypeRef();

            if (qName != null && !isBuiltInType(qName.getLocalPart())) {
                tInformationItem.setTypeRef(new QName(qName.getNamespaceURI(), namespace + "." + qName.getLocalPart(), qName.getPrefix()));
            }

            setInformationItem(drgElement, tInformationItem);
        });
    }

    private Optional<InformationItem> getInformationItem(final DRGElement drgElement) {

        if (drgElement instanceof Decision) {
            return Optional.of(((Decision) drgElement).getVariable());
        }

        if (drgElement instanceof InputData) {
            return Optional.of(((InputData) drgElement).getVariable());
        }

        if (drgElement instanceof Invocable) {
            return Optional.of(((Invocable) drgElement).getVariable());
        }

        return Optional.empty();
    }

    private void setInformationItem(final DRGElement drgElement,
                                    final InformationItem informationItem) {

        if (drgElement instanceof Decision) {
            ((Decision) drgElement).setVariable(informationItem);
        }

        if (drgElement instanceof InputData) {
            ((InputData) drgElement).setVariable(informationItem);
        }

        if (drgElement instanceof Invocable) {
            ((Invocable) drgElement).setVariable(informationItem);
        }
    }

    private Optional<Import> findImportByDefinitions(final Definitions definitions,
                                                     final List<Import> imports) {
        return imports
                .stream()
                .filter(anImport -> Objects.equals(anImport.getNamespace(), definitions.getNamespace()))
                .findAny();
    }

    private Optional<Import> findImportByPMMLDocument(final Path dmnModelPath,
                                                      final Path includedModelPath,
                                                      final List<Import> imports) {
        return imports
                .stream()
                .filter(anImport -> Objects.equals(anImport.getLocationURI(), pathsHelper.getRelativeURI(dmnModelPath, includedModelPath)))
                .findAny();
    }

    List<Definitions> getOtherDMNDiagramsDefinitions(final Metadata metadata) {
        final List<Path> diagramPaths = pathsHelper.getDMNModelsPaths(getProject(metadata));
        return diagramPaths
                .stream()
                .filter(path -> !Objects.equals(metadata.getPath(), path))
                .map(path -> loadPath(path).orElse(null))
                .filter(Objects::nonNull)
                .map(this::toDefinitions)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Definitions toDefinitions(final InputStream inputStream) {
        try (InputStream inputStreamAutoClosable = inputStream;
             InputStreamReader reader = toInputStreamReader(inputStreamAutoClosable)) {
            return marshaller.unmarshal(reader);
        } catch (IOException ioe) {
            //Swallow. null is returned by default.
        }
        return null;
    }

    List<Path> getPMMLDocumentPaths(final Metadata metadata) {
        return pathsHelper.getPMMLModelsPaths(getProject(metadata));
    }

    InputStreamReader toInputStreamReader(final InputStream inputStream) {
        return new InputStreamReader(inputStream);
    }

    Optional<Definitions> getDefinitionsByPath(final Path dmnModelPath) {
        return loadPath(dmnModelPath).map(this::toInputStreamReader).map(marshaller::unmarshal);
    }

    StringReader toStringReader(final String xml) {
        return new StringReader(xml);
    }

    private List<String> getOtherDMNDiagramsXML(final Metadata metadata) {
        final List<Path> diagramPaths = pathsHelper.getDMNModelsPaths(getProject(metadata));
        return diagramPaths
                .stream()
                .filter(path -> !Objects.equals(metadata.getPath(), path))
                .map(path -> loadPath(path).orElse(null))
                .filter(Objects::nonNull)
                .map(dmnIOHelper::isAsString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    Optional<InputStream> loadPath(final Path path) {
        return loadPath(convertPath(path));
    }

    @Override
    public Optional<InputStream> loadPath(final org.uberfire.java.nio.file.Path path) {
        try {
            return Optional.ofNullable(ioService.newInputStream(path));
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

    private WorkspaceProject getProject(final Metadata metadata) {
        try {
            return projectService.resolveProject(metadata.getPath());
        } catch (final Exception e) {
            // There's not project when the webapp is running on standalone mode, thus NullPointerException is raised.
            return null;
        }
    }

    org.uberfire.java.nio.file.Path convertPath(final Path path) {
        return Paths.convert(path);
    }
}
