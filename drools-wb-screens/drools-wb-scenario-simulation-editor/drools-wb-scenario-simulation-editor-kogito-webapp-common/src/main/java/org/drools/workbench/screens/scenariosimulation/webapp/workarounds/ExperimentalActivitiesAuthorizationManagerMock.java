/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.workarounds;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

@ApplicationScoped
public class ExperimentalActivitiesAuthorizationManagerMock implements ExperimentalActivitiesAuthorizationManager {

    @Override
    public void init() {
        // Not used in KogitoScesim
    }

    @Override
    public boolean authorizeActivity(Object activity) {
        return true;
    }

    @Override
    public boolean authorizeActivityClass(Class<?> activityClass) {
        return true;
    }

    @Override
    public boolean authorizeActivityId(String activityId) {
        return true;
    }

    @Override
    public void securePart(PartDefinition part, PanelDefinition panel) {
        // Not used in KogitoScesim
    }
}
