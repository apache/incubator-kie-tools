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

package org.uberfire.experimental.service.registry;

import java.util.Collection;
import java.util.Optional;

/**
 * Registry of all {@link ExperimentalFeature} present on the platform
 */
public interface ExperimentalFeaturesRegistry {

    /**
     * Determines if a given feature is enabled or not.
     * @param featureId a String containing the identifier of an existing feature
     * @return true if the feature is enabled, false if it is disabled
     */
    boolean isFeatureEnabled(String featureId);

    /**
     * Returns the {@link ExperimentalFeature} identified by the given featureID
     * @param featureId a String containing the identifier of an existing feature
     * @return the {@link ExperimentalFeature} instance identified by the featureId if is present on the platform,
     * null if it doesn't exist
     */
    Optional<ExperimentalFeature> getFeature(String featureId);

    /**
     * Returns all the {@link ExperimentalFeature} present on the platform
     * @return a List containing all {@link ExperimentalFeature} present on the platform
     */
    Collection<ExperimentalFeature> getAllFeatures();
}
