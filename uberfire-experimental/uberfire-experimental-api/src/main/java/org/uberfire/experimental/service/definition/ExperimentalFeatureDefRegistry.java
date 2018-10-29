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

package org.uberfire.experimental.service.definition;

import java.util.Collection;

/**
 * Registry of all the {@link ExperimentalFeatureDefinition} on the platform
 */
public interface ExperimentalFeatureDefRegistry {

    /**
     * Returns the {@link ExperimentalFeatureDefinition} identified by the definitionId param
     * @param definitionId the definitionId to get the {@link ExperimentalFeatureDefinition}
     * @return the {@link ExperimentalFeatureDefinition} identified by the definitionId param or null if it doesn't exist
     */
    ExperimentalFeatureDefinition getFeatureById(String definitionId);

    /**
     * Lists all the {@link ExperimentalFeatureDefinition} available on the platform
     * @return a Collection containing all the {@link ExperimentalFeatureDefinition}
     */
    Collection<ExperimentalFeatureDefinition> getAllFeatures();

    /**
     * Lists all the global {@link ExperimentalFeatureDefinition}
     * @return a Collection containing all the {@link ExperimentalFeatureDefinition}
     */
    Collection<ExperimentalFeatureDefinition> getGlobalFeatures();

    /**
     * Lists the user-level {@link ExperimentalFeatureDefinition}
     * @return a Collection containing {@link ExperimentalFeatureDefinition}
     */
    Collection<ExperimentalFeatureDefinition> getUserFeatures();
}
