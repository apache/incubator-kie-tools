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

package org.guvnor.ala.ui.service;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for holding validations oriented to the different provisioning-ui screens and client widgets.
 */
@Remote
public interface ProvisioningValidationService {

    /**
     * Checks if a given container name is valid.
     * @param containerName a container name to check.
     * @return true if the provided name is valid, false in any other case.
     */
    boolean isValidContainerName(final String containerName);
}
