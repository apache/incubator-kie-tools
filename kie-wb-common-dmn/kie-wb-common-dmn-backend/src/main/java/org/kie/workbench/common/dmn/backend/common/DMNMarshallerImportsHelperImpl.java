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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DMNMarshallerImportsHelperImpl implements DMNMarshallerImportsHelper {

    private final DMNPathsHelperImpl pathsHelper;

    private final WorkspaceProjectService projectService;

    private final IOService ioService;

    private DMNMarshaller marshaller;

    public DMNMarshallerImportsHelperImpl() {
        this(null, null, null);
    }

    @Inject
    public DMNMarshallerImportsHelperImpl(final DMNPathsHelperImpl pathsHelper,
                                          final WorkspaceProjectService projectService,
                                          final @Named("ioStrategy") IOService ioService) {
        this.pathsHelper = pathsHelper;
        this.projectService = projectService;
        this.ioService = ioService;
    }

    public void init(final DMNMarshaller marshaller) {
        this.marshaller = marshaller;
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

    List<ItemDefinition> getItemDefinitionsWithNamespace(final Definitions definitions,
                                                         final Import anImport) {

        final List<ItemDefinition> itemDefinitions = definitions.getItemDefinition();
        final String namespace = anImport.getName();

        return setItemDefinitionsNamespace(itemDefinitions, namespace);
    }

    private List<ItemDefinition> setItemDefinitionsNamespace(final List<ItemDefinition> itemDefinitions,
                                                             final String namespace) {
        return itemDefinitions
                .stream()
                .map(itemDefinition -> setItemDefinitionNamespace(itemDefinition, namespace))
                .collect(Collectors.toList());
    }

    private ItemDefinition setItemDefinitionNamespace(final ItemDefinition itemDefinition,
                                                      final String namespace) {

        final String nameWithNamespace = namespace + "." + itemDefinition.getName();
        final List<ItemDefinition> itemComponents = itemDefinition.getItemComponent();

        if (itemDefinition.getTypeRef() != null && !isBuiltInType(itemDefinition.getTypeRef())) {
            itemDefinition.setTypeRef(makeQNameWithNamespace(itemDefinition.getTypeRef(), namespace));
        }

        itemDefinition.setName(nameWithNamespace);
        setItemDefinitionsNamespace(itemComponents, namespace);

        return itemDefinition;
    }

    private boolean isBuiltInType(final QName typeRef) {
        return Arrays
                .stream(BuiltInType.values())
                .anyMatch(builtInType -> {

                    final String builtInTypeName = builtInType.getName();
                    final String typeRefName = typeRef.getLocalPart();

                    return Objects.equals(builtInTypeName, typeRefName);
                });
    }

    private QName makeQNameWithNamespace(final QName qName,
                                         final String namespace) {

        final String namespaceURI = qName.getNamespaceURI();
        final String localPart = namespace + "." + qName.getLocalPart();
        final String prefix = qName.getPrefix();

        return new QName(namespaceURI, localPart, prefix);
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

        drgElement.setId(namespace + ":" + drgElement.getId());
        drgElement.setName(namespace + "." + drgElement.getName());

        return drgElement;
    }

    private Optional<Import> findImportByDefinitions(final Definitions definitions,
                                                     final List<Import> imports) {
        return imports
                .stream()
                .filter(anImport -> Objects.equals(anImport.getNamespace(), definitions.getNamespace()))
                .findAny();
    }

    List<Definitions> getOtherDMNDiagramsDefinitions(final Metadata metadata) {

        final List<Path> diagramPaths = pathsHelper.getDiagramsPaths(getProject(metadata));

        return diagramPaths
                .stream()
                .filter(path -> !Objects.equals(metadata.getPath(), path))
                .map(path -> loadPath(path).map(marshaller::unmarshal).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    Optional<InputStreamReader> loadPath(final Path path) {

        InputStreamReader mutableInputStream = null;

        try {

            final byte[] bytes = ioService.readAllBytes(convertPath(path));
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            mutableInputStream = new InputStreamReader(inputStream);

            return Optional.of(mutableInputStream);
        } catch (final Exception e) {
            closeInputStreamReader(mutableInputStream);
            return Optional.empty();
        }
    }

    void closeInputStreamReader(final InputStreamReader mutableInputStream) {
        if (mutableInputStream != null) {
            try {
                mutableInputStream.close();
            } catch (final IOException e) {
                // Ignore.
            }
        }
    }

    private WorkspaceProject getProject(final Metadata metadata) {
        try {
            return projectService.resolveProject(metadata.getPath());
        } catch (final NullPointerException e) {
            // There's not project when the webapp is running on standalone mode, thus NullPointerException is raised.
            return null;
        }
    }

    org.uberfire.java.nio.file.Path convertPath(final Path path) {
        return Paths.convert(path);
    }
}
