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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

public class DMNSimulationUtils {

    private DMNSimulationUtils() {
    }

    public static DMNModel extractDMNModel(DMNRuntime dmnRuntime, String path) {
        return dmnRuntime.getModels().stream()
                .filter(model -> path.endsWith(model.getResource().getSourcePath()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find a DMN model with resource=" + path));
    }

    public static DMNRuntime extractDMNRuntime(KieContainer kieContainer) {
        return kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }
}
