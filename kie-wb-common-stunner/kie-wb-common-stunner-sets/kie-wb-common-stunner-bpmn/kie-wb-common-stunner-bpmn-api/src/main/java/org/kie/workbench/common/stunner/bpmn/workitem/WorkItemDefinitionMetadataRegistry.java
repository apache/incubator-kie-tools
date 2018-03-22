/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

@Dependent
@Typed(WorkItemDefinitionMetadataRegistry.class)
public class WorkItemDefinitionMetadataRegistry
        implements WorkItemDefinitionRegistry {

    private final Function<Metadata, Path> rootPathMetadataSupplier;
    private BiConsumer<Path, Consumer<Collection<WorkItemDefinition>>> workItemsByPathSupplier;
    private Supplier<WorkItemDefinitionCacheRegistry> registrySupplier;

    @Inject
    public WorkItemDefinitionMetadataRegistry() {
        this(Metadata::getRoot);
    }

    protected WorkItemDefinitionMetadataRegistry(final Function<Metadata, Path> rootPathMetadataSupplier) {
        this.rootPathMetadataSupplier = rootPathMetadataSupplier;
    }

    public WorkItemDefinitionMetadataRegistry setWorkItemsByPathSupplier(final BiConsumer<Path, Consumer<Collection<WorkItemDefinition>>> workItemsByPathSupplier) {
        this.workItemsByPathSupplier = workItemsByPathSupplier;
        return this;
    }

    public WorkItemDefinitionMetadataRegistry setRegistrySupplier(final Supplier<WorkItemDefinitionCacheRegistry> registrySupplier) {
        this.registrySupplier = registrySupplier;
        return this;
    }

    public void load(final Metadata metadata,
                     final Command callback) {
        final Path rootPath = rootPathMetadataSupplier.apply(metadata);
        workItemsByPathSupplier.accept(rootPath, workItemDefinitions -> {
            workItemDefinitions.forEach(getMemoryRegistry()::register);
            callback.execute();
        });
    }

    @Override
    public Collection<WorkItemDefinition> items() {
        return getMemoryRegistry().items();
    }

    @Override
    public WorkItemDefinition get(final String name) {
        return getMemoryRegistry().get(name);
    }

    public WorkItemDefinitionCacheRegistry getMemoryRegistry() {
        return registrySupplier.get();
    }
}
