/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

import org.dashbuilder.displayer.DisplayerSettings;

public class AllProcessesHeatmapBuilder extends ExternalDisplayerBuilder {

    public static final String COMPONENT_ID = "processes-heatmaps-provided";
    public static final String SERVER_TEMPLATE_PARAM = "serverTemplate";

    public AllProcessesHeatmapBuilder(String serverTemplate, DisplayerSettings settings) {
        super(COMPONENT_ID, settings);
        this.componentProperty(SERVER_TEMPLATE_PARAM, serverTemplate);
    }

    public static AllProcessesHeatmapBuilder create(String serverTemplate,
                                                    DisplayerSettings settings) {
        return new AllProcessesHeatmapBuilder(serverTemplate, settings);
    }

}