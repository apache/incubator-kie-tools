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

package org.kie.workbench.common.screens.server.management.backend.utils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.kie.workbench.common.screens.server.management.backend.storage.ServerTemplateOCPStorage;
import org.kie.workbench.common.screens.server.management.backend.storage.ServerTemplateVFSStorage;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExtension.class);

    public <T> void processStandaloneController(@WithAnnotations({StandaloneController.class}) @Observes ProcessAnnotatedType<T> event) {
        LOGGER.info("Processing standalone controller class: {}", event.getAnnotatedType().getJavaClass());
        StandaloneController a = event.getAnnotatedType().getJavaClass().getAnnotation(StandaloneController.class);
        if (a != null && ControllerUtils.useEmbeddedController()) {
            removeControllerClassFromCDIContext(event);
        }
    }

    public <T> void processEmbeddedController(@WithAnnotations({EmbeddedController.class}) @Observes ProcessAnnotatedType<T> event) {
        LOGGER.info("Processing embedded controller class: {}", event.getAnnotatedType().getJavaClass());
        EmbeddedController a = event.getAnnotatedType().getJavaClass().getAnnotation(EmbeddedController.class);
        if (a != null) {
            if (ControllerUtils.useEmbeddedController()) {
                /**
                 * Select server template storage implementation for embedded controller
                 * based on system properties loaded by ControllerUtils
                 */
                if (ControllerUtils.isOpenShiftSupported()) {
                    if (ServerTemplateVFSStorage.class
                                                      .isAssignableFrom(event.getAnnotatedType().getJavaClass())) {
                        removeControllerClassFromCDIContext(event);
                    }
                } else {
                    if (ServerTemplateOCPStorage.class
                                                      .isAssignableFrom(event.getAnnotatedType().getJavaClass())) {
                        removeControllerClassFromCDIContext(event);
                    }
                }
            } else {
                removeControllerClassFromCDIContext(event);
            }
        }

    }

    private <T> void removeControllerClassFromCDIContext(ProcessAnnotatedType<T> event) {
        LOGGER.info("Removing controller class {} from CDI context", event.getAnnotatedType().getJavaClass());
        event.veto();
    }

}
