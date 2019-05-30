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

package org.kie.workbench.common.stunner.bpmn.integration.client;

import java.util.Iterator;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class IntegrationHandlerProvider {

    private Optional<IntegrationHandler> integrationHandler;

    public IntegrationHandlerProvider() {
        //required for proxying
    }

    @Inject
    public IntegrationHandlerProvider(@Any final Instance<IntegrationHandler> integrationHandlerInstance) {
        final Iterator<IntegrationHandler> iterator = integrationHandlerInstance.iterator();
        integrationHandler = Optional.ofNullable(iterator.hasNext() ? iterator.next() : null);
    }

    public Optional<IntegrationHandler> getIntegrationHandler() {
        return integrationHandler;
    }
}
