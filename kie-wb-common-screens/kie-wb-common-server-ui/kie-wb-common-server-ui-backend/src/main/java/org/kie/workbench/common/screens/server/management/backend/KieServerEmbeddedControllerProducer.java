/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend;

import java.util.concurrent.ExecutorService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.kie.server.controller.api.service.NotificationService;
import org.kie.server.controller.api.service.RuleCapabilitiesService;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerHealthCheckControllerImpl;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.service.RuleCapabilitiesServiceImpl;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.kie.server.controller.impl.service.SpecManagementServiceImpl;
import org.kie.server.controller.rest.RestKieServerControllerImpl;
import org.kie.server.controller.rest.RestRuntimeManagementServiceImpl;
import org.kie.server.controller.rest.RestSpecManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.backend.utils.EmbeddedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.concurrent.Managed;

@ApplicationScoped
@EmbeddedController
public class KieServerEmbeddedControllerProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerEmbeddedControllerProducer.class);

    @Produces
    @ApplicationScoped
    @EmbeddedController
    public RuleCapabilitiesService produceRuleService(final @EmbeddedController KieServerInstanceManager instanceManager,
                                                      final @EmbeddedController NotificationService notificationService,
                                                      final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating RuleCapabilitiesServiceImpl...");
        final RuleCapabilitiesServiceImpl service = new RuleCapabilitiesServiceImpl();
        service.setNotificationService(notificationService);
        service.setTemplateStorage(kieServerTemplateStorage);
        service.setKieServerInstanceManager(instanceManager);
        return service;
    }

    @Produces
    @ApplicationScoped
    public SpecManagementServiceImpl produceSpecManagementService(final @EmbeddedController KieServerInstanceManager instanceManager,
                                                                  final @EmbeddedController NotificationService notificationService,
                                                                  final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating SpecManagementServiceImpl...");
        final SpecManagementServiceImpl service = new SpecManagementServiceImpl();
        service.setNotificationService(notificationService);
        service.setTemplateStorage(kieServerTemplateStorage);
        service.setKieServerInstanceManager(instanceManager);
        return service;
    }

    @Produces
    @ApplicationScoped
    public RestSpecManagementServiceImpl produceRestSpecManagementService(final @EmbeddedController NotificationService notificationService,
                                                                          final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating RestSpecManagementServiceImpl...");
        final SpecManagementServiceImpl service = new SpecManagementServiceImpl();
        service.setNotificationService(notificationService);
        service.setTemplateStorage(kieServerTemplateStorage);
        service.setKieServerInstanceManager(KieServerInstanceManager.getInstance());

        final RestSpecManagementServiceImpl restSpecManagementService = new RestSpecManagementServiceImpl();
        restSpecManagementService.setSpecManagementService(service);
        return restSpecManagementService;
    }

    @Produces
    @ApplicationScoped
    public RuntimeManagementServiceImpl produceRuntimeManagementService(final @EmbeddedController KieServerInstanceManager instanceManager,
                                                                        final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating RuntimeManagementServiceImpl...");
        final RuntimeManagementServiceImpl service = new RuntimeManagementServiceImpl();
        service.setTemplateStorage(kieServerTemplateStorage);
        service.setKieServerInstanceManager(instanceManager);
        return service;
    }

    @Produces
    @ApplicationScoped
    public RestRuntimeManagementServiceImpl produceRestRuntimeManagementService(final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating RestRuntimeManagementServiceImpl...");
        final RuntimeManagementServiceImpl service = new RuntimeManagementServiceImpl();
        service.setTemplateStorage(kieServerTemplateStorage);
        service.setKieServerInstanceManager(KieServerInstanceManager.getInstance());

        RestRuntimeManagementServiceImpl restRuntimeManagementService = new RestRuntimeManagementServiceImpl();
        restRuntimeManagementService.setRuntimeManagementService(service);
        return restRuntimeManagementService;
    }

    @Produces
    @ApplicationScoped
    public RestKieServerControllerImpl produceRestKieServerController(final @EmbeddedController NotificationService notificationService,
                                                                      final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage) {
        LOGGER.debug("Creating RestKieServerControllerImpl...");
        final RestKieServerControllerImpl controller = new RestKieServerControllerImpl();
        controller.setNotificationService(notificationService);
        controller.setTemplateStorage(kieServerTemplateStorage);
        return controller;
    }

    @Produces
    @EmbeddedController
    public KieServerHealthCheckControllerImpl produceKieServerHealthCheckController(
                                                                                final @EmbeddedController NotificationService notificationService,
                                                                                final @EmbeddedController KieServerTemplateStorage kieServerTemplateStorage,
                                                                                final @Managed ExecutorService executorService) {
        LOGGER.debug("Creating KieServerHealthCheckController...");
        final KieServerHealthCheckControllerImpl controller = new KieServerHealthCheckControllerImpl();
        controller.setNotificationService(notificationService);
        controller.setTemplateStorage(kieServerTemplateStorage);
        controller.setExecutorService(executorService);
        return controller;
    }

}