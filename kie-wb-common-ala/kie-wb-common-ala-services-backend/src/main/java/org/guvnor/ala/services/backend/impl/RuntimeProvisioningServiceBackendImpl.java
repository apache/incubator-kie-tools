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

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.RuntimeQuery;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RuntimeProvisioningServiceBackendImpl
        implements RuntimeProvisioningServiceBackend {

    private RuntimeProvisioningService runtimeProvisioningService;

    public RuntimeProvisioningServiceBackendImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public RuntimeProvisioningServiceBackendImpl(final RuntimeProvisioningService runtimeProvisioningService) {
        this.runtimeProvisioningService = runtimeProvisioningService;
    }

    @Override
    public List<ProviderType> getProviderTypes(Integer page,
                                               Integer pageSize,
                                               String sort,
                                               boolean sortOrder) throws BusinessException {
        return runtimeProvisioningService.getProviderTypes(page,
                                                           pageSize,
                                                           sort,
                                                           sortOrder).getItems();
    }

    @Override
    public List<Provider> getProviders(Integer page,
                                       Integer pageSize,
                                       String sort,
                                       boolean sortOrder) throws BusinessException {
        return runtimeProvisioningService.getProviders(page,
                                                       pageSize,
                                                       sort,
                                                       sortOrder).getItems();
    }

    @Override
    public List<Runtime> getRuntimes(Integer page,
                                     Integer pageSize,
                                     String sort,
                                     boolean sortOrder) throws BusinessException {
        return runtimeProvisioningService.getRuntimes(page,
                                                      pageSize,
                                                      sort,
                                                      sortOrder).getItems();
    }

    @Override
    public void registerProvider(ProviderConfig conf) throws BusinessException {
        runtimeProvisioningService.registerProvider(conf);
    }

    @Override
    public void unregisterProvider(String name) throws BusinessException {
        runtimeProvisioningService.deregisterProvider(name);
    }

    @Override
    public String newRuntime(RuntimeConfig conf) throws BusinessException {
        return runtimeProvisioningService.newRuntime(conf);
    }

    @Override
    public void destroyRuntime(String runtimeId,
                               boolean forced) throws BusinessException {
        runtimeProvisioningService.destroyRuntime(runtimeId,
                                                  forced);
    }

    @Override
    public void startRuntime(String runtimeId) throws BusinessException {
        runtimeProvisioningService.startRuntime(runtimeId);
    }

    @Override
    public void stopRuntime(String runtimeId) throws BusinessException {
        runtimeProvisioningService.stopRuntime(runtimeId);
    }

    @Override
    public void restartRuntime(String runtimeId) throws BusinessException {
        runtimeProvisioningService.restartRuntime(runtimeId);
    }

    @Override
    public List<RuntimeQueryResultItem> executeQuery(RuntimeQuery query) throws BusinessException {
        return runtimeProvisioningService.executeQuery(query).getItems();
    }
}
