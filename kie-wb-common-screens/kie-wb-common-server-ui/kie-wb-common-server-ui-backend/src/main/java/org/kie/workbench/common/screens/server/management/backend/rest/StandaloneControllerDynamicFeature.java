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

package org.kie.workbench.common.screens.server.management.backend.rest;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.kie.server.controller.rest.RestKieServerControllerImpl;
import org.kie.server.controller.rest.RestRuntimeManagementServiceImpl;
import org.kie.server.controller.rest.RestSpecManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class StandaloneControllerDynamicFeature implements DynamicFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneControllerDynamicFeature.class);

    @Override
    public void configure(final ResourceInfo resourceInfo,
                          final FeatureContext context) {
        if (ControllerUtils.useEmbeddedController()) {
            return;
        }

        if (resourceInfo.getResourceClass().isAssignableFrom(RestKieServerControllerImpl.class)) {
            registerFilter(resourceInfo.getResourceClass(),
                           context);
        } else if (resourceInfo.getResourceClass().isAssignableFrom(RestSpecManagementServiceImpl.class)) {
            registerFilter(resourceInfo.getResourceClass(),
                           context);
        } else if (resourceInfo.getResourceClass().isAssignableFrom(RestRuntimeManagementServiceImpl.class)) {
            registerFilter(resourceInfo.getResourceClass(),
                           context);
        }
    }

    private void registerFilter(final Class resourceClass,
                                final FeatureContext context) {
        LOGGER.debug("Adding standalone controller REST filter to resource class: {}",
                     resourceClass);
        context.register(StandaloneControllerFilter.class);
    }
}
