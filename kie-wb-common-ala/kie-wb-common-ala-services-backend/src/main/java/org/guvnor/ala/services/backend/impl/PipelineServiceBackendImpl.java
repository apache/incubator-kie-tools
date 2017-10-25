/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.services.backend.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.backend.PipelineServiceBackend;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class PipelineServiceBackendImpl
        implements PipelineServiceBackend {

    private PipelineService pipelineService;

    public PipelineServiceBackendImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public PipelineServiceBackendImpl(final PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    public List<PipelineConfig> getPipelineConfigs(Integer page,
                                                   Integer pageSize,
                                                   String sort,
                                                   boolean sortOrder) throws BusinessException {
        return pipelineService.getPipelineConfigs(page,
                                                  pageSize,
                                                  sort,
                                                  sortOrder).getItems();
    }

    @Override
    public List<PipelineConfig> getPipelineConfigs(ProviderType providerType,
                                                   Integer page,
                                                   Integer pageSize,
                                                   String sort,
                                                   boolean sortOrder) throws BusinessException {
        return pipelineService.getPipelineConfigs(providerType.getProviderTypeName(),
                                                  providerType.getVersion(),
                                                  page,
                                                  pageSize,
                                                  sort,
                                                  sortOrder).getItems();
    }

    @Override
    public List<String> getPipelineNames(ProviderType providerType,
                                         Integer page,
                                         Integer pageSize,
                                         String sort,
                                         boolean sortOrder) throws BusinessException {
        return pipelineService.getPipelineNames(providerType.getProviderTypeName(),
                                                providerType.getVersion(),
                                                page,
                                                pageSize,
                                                sort,
                                                sortOrder);
    }

    @Override
    public String newPipeline(PipelineConfig pipelineConfig,
                              ProviderType providerType) throws BusinessException {
        return pipelineService.newPipeline(pipelineConfig,
                                           providerType);
    }

    @Override
    public String newPipeline(PipelineConfig config) throws BusinessException {
        return pipelineService.newPipeline(config);
    }

    @Override
    public String runPipeline(String id,
                              Input input,
                              boolean async) throws BusinessException {
        return pipelineService.runPipeline(id,
                                           input,
                                           async);
    }

    @Override
    public void stopPipelineExecution(final String executionId) throws BusinessException {
        pipelineService.stopPipelineExecution(executionId);
    }

    @Override
    public void deletePipelineExecution(final String executionId) throws BusinessException {
        pipelineService.deletePipelineExecution(executionId);
    }
}
