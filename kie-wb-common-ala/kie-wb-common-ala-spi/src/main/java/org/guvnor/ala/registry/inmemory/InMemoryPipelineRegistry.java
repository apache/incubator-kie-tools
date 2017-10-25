/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.inmemory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.inmemory.util.PageSortUtil;
import org.guvnor.ala.runtime.providers.ProviderType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class InMemoryPipelineRegistry
        implements PipelineRegistry {

    public InMemoryPipelineRegistry() {
        //Empty constructor for Weld proxying
    }

    protected Map<String, PipelineRegistryEntry> pipelineByName = new ConcurrentHashMap<>();

    @Override
    public void registerPipeline(final Pipeline pipeline) {
        checkNotNull("pipeline",
                     pipeline);
        pipelineByName.put(pipeline.getName(),
                           new PipelineRegistryEntry(pipeline));
    }

    @Override
    public void registerPipeline(final Pipeline pipeline,
                                 final ProviderType providerType) {
        checkNotNull("pipeline",
                     pipeline);
        checkNotNull("providerType",
                     providerType);
        pipelineByName.put(pipeline.getName(),
                           new PipelineRegistryEntry(pipeline,
                                                     providerType));
    }

    @Override
    public Pipeline getPipelineByName(final String pipelineId) {
        checkNotNull("pipelineId",
                     pipelineId);
        return pipelineByName.get(pipelineId).getPipeline();
    }

    @Override
    public List<Pipeline> getPipelines(final int page,
                                       final int pageSize,
                                       final String sort,
                                       final boolean sortOrder) {
        final List<Pipeline> values = pipelineByName.values()
                .stream()
                .map(PipelineRegistryEntry::getPipeline).collect(Collectors.toList());
        return sortPagedResult(values,
                               page,
                               pageSize,
                               sort,
                               sortOrder);
    }

    @Override
    public List<Pipeline> getPipelines(final String providerType,
                                       final String version,
                                       final int page,
                                       final int pageSize,
                                       final String sort,
                                       final boolean sortOrder) {
        final ProviderTypeFilter filter = ProviderTypeFilter.newInstance()
                .withProviderTypeName(providerType)
                .withVersion(version);
        final List<Pipeline> values = pipelineByName.values()
                .stream()
                .filter(entry -> filter.test(entry.getProviderType()))
                .map(PipelineRegistryEntry::getPipeline)
                .collect(Collectors.toList());

        return sortPagedResult(values,
                               page,
                               pageSize,
                               sort,
                               sortOrder);
    }

    @Override
    public ProviderType getProviderType(final String pipelineId) {
        checkNotNull("pipelineId",
                     pipelineId);
        PipelineRegistryEntry entry = pipelineByName.get(pipelineId);
        return entry != null ? entry.getProviderType() : null;
    }

    private List<Pipeline> sortPagedResult(List<Pipeline> values,
                                           int page,
                                           int pageSize,
                                           String sort,
                                           boolean sortOrder) {
        return PageSortUtil.pageSort(values,
                                     (Pipeline p1, Pipeline p2) -> {
                                         switch (sort) {
                                             case PIPELINE_NAME_SORT:
                                                 return p1.getName().compareTo(p2.getName());
                                             default:
                                                 return p1.toString().compareTo(p2.toString());
                                         }
                                     },
                                     page,
                                     pageSize,
                                     sort,
                                     sortOrder);
    }

    private static class PipelineRegistryEntry {

        private Pipeline pipeline;

        private ProviderType providerType;

        public PipelineRegistryEntry(Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        public PipelineRegistryEntry(Pipeline pipeline,
                                     ProviderType providerType) {
            this.pipeline = pipeline;
            this.providerType = providerType;
        }

        public Pipeline getPipeline() {
            return pipeline;
        }

        public ProviderType getProviderType() {
            return providerType;
        }
    }

    static class ProviderTypeFilter implements Predicate<ProviderType> {

        private String providerTypeName;

        private String version;

        private ProviderTypeFilter() {
        }

        public static ProviderTypeFilter newInstance() {
            return new ProviderTypeFilter();
        }

        public ProviderTypeFilter withProviderTypeName(final String providerTypeName) {
            this.providerTypeName = providerTypeName;
            return this;
        }

        public ProviderTypeFilter withVersion(final String version) {
            this.version = version;
            return this;
        }

        @Override
        public boolean test(final ProviderType providerType) {
            return providerTypeName != null &&
                    providerType != null &&
                    providerTypeName.equals(providerType.getProviderTypeName()) &&
                    version != null && version.equals(providerType.getVersion());
        }
    }
}
