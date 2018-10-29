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

package org.uberfire.experimental.service.auth;

import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Handles authorization management for any activity marked as experimental
 */
public interface ExperimentalActivitiesAuthorizationManager {

    /**
     * Initializes the manager
     */
    void init();

    /**
     * Determines if the experimental framework enables rendering a given activity
     * @param activity The activity to check
     * @return true or false depending on the settings.
     */
    boolean authorizeActivity(Object activity);

    /**
     * Determines if the experimental framework enables rendering a given Class
     * @param activityClass The Class to check
     * @return true or false depending on the settings.
     */
    boolean authorizeActivityClass(Class<?> activityClass);

    /**
     * Determines if the experimental framework enables rendering a the activity identified by the activityId param.
     * @param activityId the activity identifier
     * @return true or false depending on the settings.
     */
    boolean authorizeActivityId(String activityId);

    /**
     * Checks if the {@link PartDefinition} place points to an experimental activity and replaces it to a {@link ConditionalPlaceRequest}
     * @param part The {@link PartDefinition} to check
     * @param panel The {@link PanelDefinition} that owns the plart
     */
    void securePart(PartDefinition part, PanelDefinition panel);
}
