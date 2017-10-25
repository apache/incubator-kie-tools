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

package org.guvnor.ala.ui.backend.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.services.api.RuntimeQuery;
import org.guvnor.ala.services.api.RuntimeQueryBuilder;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.services.api.backend.PipelineServiceBackend;
import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.events.PipelineExecutionChange;
import org.guvnor.ala.ui.events.PipelineExecutionChangeEvent;
import org.guvnor.ala.ui.events.RuntimeChange;
import org.guvnor.ala.ui.events.RuntimeChangeEvent;
import org.guvnor.ala.ui.exceptions.ServiceException;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.service.ProviderService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class RuntimeServiceImpl
        implements RuntimeService {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeServiceImpl.class);

    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    private PipelineServiceBackend pipelineService;

    private ProviderService providerService;

    private Event<RuntimeChangeEvent> runtimeChangeEvent;

    private Event<PipelineExecutionChangeEvent> pipelineExecutionChangeEvent;

    public RuntimeServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public RuntimeServiceImpl(final RuntimeProvisioningServiceBackend runtimeProvisioningService,
                              final PipelineServiceBackend pipelineService,
                              final ProviderService providerService,
                              final Event<RuntimeChangeEvent> runtimeChangeEvent,
                              final Event<PipelineExecutionChangeEvent> pipelineExecutionChangeEvent) {
        this.runtimeProvisioningService = runtimeProvisioningService;
        this.pipelineService = pipelineService;
        this.providerService = providerService;
        this.runtimeChangeEvent = runtimeChangeEvent;
        this.pipelineExecutionChangeEvent = pipelineExecutionChangeEvent;
    }

    @Override
    public Collection<RuntimeListItem> getRuntimeItems(final ProviderKey providerKey) {
        checkNotNull("providerKey",
                     providerKey);

        final RuntimeQuery query = RuntimeQueryBuilder.newInstance()
                .withProviderId(providerKey.getId())
                .build();
        return buildRuntimeQueryResult(runtimeProvisioningService.executeQuery(query));
    }

    @Override
    public RuntimeListItem getRuntimeItem(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        checkNotNull("pipelineExecutionTraceKey",
                     pipelineExecutionTraceKey);

        final RuntimeQuery query = RuntimeQueryBuilder.newInstance()
                .withPipelineExecutionId(pipelineExecutionTraceKey.getId())
                .build();
        return buildRuntimeQueryResult(runtimeProvisioningService.executeQuery(query)).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public RuntimeListItem getRuntimeItem(final RuntimeKey runtimeKey) {
        checkNotNull("runtimeKey",
                     runtimeKey);

        final RuntimeQuery query = RuntimeQueryBuilder.newInstance()
                .withRuntimeId(runtimeKey.getId())
                .build();
        return buildRuntimeQueryResult(runtimeProvisioningService.executeQuery(query)).stream()
                .findFirst()
                .orElse(null);
    }

    private Collection<RuntimeListItem> buildRuntimeQueryResult(List<RuntimeQueryResultItem> resultItems) {
        final Collection<RuntimeListItem> result = resultItems.stream()
                .map(item -> RuntimeListItemBuilder.newInstance().withItem(item).build())
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public Collection<PipelineKey> getPipelines(final ProviderTypeKey providerTypeKey) {
        checkNotNull("providerTypeKey",
                     providerTypeKey);

        return pipelineService.getPipelineNames(new org.guvnor.ala.runtime.providers.ProviderType() {
                                                    @Override
                                                    public String getProviderTypeName() {
                                                        return providerTypeKey.getId();
                                                    }

                                                    @Override
                                                    public String getVersion() {
                                                        return providerTypeKey.getVersion();
                                                    }
                                                },
                                                0,
                                                1000,
                                                "name",
                                                true
        ).stream().map(PipelineKey::new).collect(Collectors.toList());
    }

    @Override
    public PipelineExecutionTraceKey createRuntime(final ProviderKey providerKey,
                                                   final String runtimeName,
                                                   final PipelineKey pipelineKey,
                                                   final Map<String, String> params) {
        checkNotNull("providerKey",
                     providerKey);
        checkNotNull("runtimeName",
                     runtimeName);
        checkNotNull("pipelineKey",
                     pipelineKey);

        validateForCreateRuntime(providerKey,
                                 runtimeName);
        try {
            final Input input = PipelineInputBuilder.newInstance()
                    .withRuntimeName(runtimeName)
                    .withProvider(providerKey)
                    .withParams(params)
                    .build();
            return new PipelineExecutionTraceKey(pipelineService.runPipeline(pipelineKey.getId(),
                                                                             input,
                                                                             true));
        } catch (Exception e) {
            logger.error("Runtime creation failed.",
                         e);
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void stopPipelineExecution(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        checkNotNull("pipelineExecutionTraceKey",
                     pipelineExecutionTraceKey);
        pipelineService.stopPipelineExecution(pipelineExecutionTraceKey.getId());
        pipelineExecutionChangeEvent.fire(new PipelineExecutionChangeEvent(PipelineExecutionChange.STOPPED,
                                                                           pipelineExecutionTraceKey));
    }

    @Override
    public void deletePipelineExecution(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        checkNotNull("pipelineExecutionTraceKey",
                     pipelineExecutionTraceKey);
        pipelineService.deletePipelineExecution(pipelineExecutionTraceKey.getId());
        pipelineExecutionChangeEvent.fire(new PipelineExecutionChangeEvent(PipelineExecutionChange.DELETED,
                                                                           pipelineExecutionTraceKey));
    }

    @Override
    public void stopRuntime(final RuntimeKey runtimeKey) {
        checkNotNull("runtimeKey",
                     runtimeKey);
        runtimeProvisioningService.stopRuntime(runtimeKey.getId());
        runtimeChangeEvent.fire(new RuntimeChangeEvent(RuntimeChange.STOPPED,
                                                       runtimeKey));
    }

    @Override
    public void startRuntime(final RuntimeKey runtimeKey) {
        checkNotNull("runtimeKey",
                     runtimeKey);
        runtimeProvisioningService.startRuntime(runtimeKey.getId());
        runtimeChangeEvent.fire(new RuntimeChangeEvent(RuntimeChange.STARTED,
                                                       runtimeKey));
    }

    @Override
    public void deleteRuntime(final RuntimeKey runtimeKey,
                              final boolean forced) {
        checkNotNull("runtimeKey",
                     runtimeKey);
        runtimeProvisioningService.destroyRuntime(runtimeKey.getId(),
                                                  forced);
        runtimeChangeEvent.fire(new RuntimeChangeEvent(RuntimeChange.DELETED,
                                                       runtimeKey));
    }

    private void validateForCreateRuntime(final ProviderKey providerKey,
                                          final String runtimeName) {
        final Provider provider = providerService.getProvider(providerKey);
        if (provider == null) {
            //uncommon case
            logger.error("No provider was found for providerKey: " + providerKey);
            throw new ServiceException("No provider was found for providerKey: " + providerKey);
        }
        final Collection<RuntimeQueryResultItem> items = runtimeProvisioningService.executeQuery(RuntimeQueryBuilder.newInstance()
                                                                                                         .withRuntimeName(runtimeName)
                                                                                                         .build());
        if (!items.isEmpty()) {
            throw new ServiceException("A runtime with the given name already exists: " + runtimeName);
        }
    }
}
