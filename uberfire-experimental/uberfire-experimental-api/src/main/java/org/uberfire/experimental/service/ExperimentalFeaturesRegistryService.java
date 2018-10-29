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

package org.uberfire.experimental.service;

import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;

/**
 * Basic definition of the service that manages the ExperimentalFeaturesFramework
 */
public interface ExperimentalFeaturesRegistryService {

    /**
     * Retrieves the actual {@link ExperimentalFeaturesRegistry} for the user
     * @return
     */
    ExperimentalFeaturesRegistry getFeaturesRegistry();

    /**
     * Determines if a given feature is enabled or not.
     * @param featureId a String containing the identifier of an existing feature
     * @return true if the feature is enabled, false if it is disabled
     */
    boolean isFeatureEnabled(String featureId);

    /**
     * Determines if the experimental framework is enabled or not
     * @return true if it is enabled, false if not
     */
    Boolean isExperimentalEnabled();
}
