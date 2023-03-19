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
import org.dashbuilder.dsl.model.Component;

public class ComponentFactory {

    private ComponentFactory() {
        // empty
    }

    public static Component html(String htmlCode) {
        return HtmlComponentBuilder.create().html(htmlCode).build();
    }

    public static Component external(String componentId) {
        return ExternalComponentBuilder.create(componentId).build();
    }

    public static Component external(String componentId, DisplayerSettings settings) {
        return ExternalDisplayerBuilder.create(componentId, settings).build();
    }

    public static Component displayer(DisplayerSettings settings) {
        return DisplayerBuilder.create(settings).build();
    }

    public static Component logo(String src) {
        return LogoBuilder.create(src).build();
    }

    public static Component processHeatmap(String serverTemplate, String container, String process, DisplayerSettings settings) {
        return ProcessHeatmapBuilder.create(serverTemplate, container, process, settings).build();
    }

    public static Component allProcessesHeatmap(String serverTemplate, DisplayerSettings settings) {
        return AllProcessesHeatmapBuilder.create(serverTemplate, settings).build();
    }

    public static HtmlComponentBuilder newHtmlComponentBuilder() {
        return HtmlComponentBuilder.create();
    }

    public static ExternalComponentBuilder externalBuilder(String componentId) {
        return ExternalComponentBuilder.create(componentId);
    }

    public static LogoBuilder logoBuilder(String src) {
        return LogoBuilder.create(src);
    }

    public static ProcessHeatmapBuilder processHeatmapBuilder(String serverTemplate, String container, String process, DisplayerSettings settings) {
        return ProcessHeatmapBuilder.create(serverTemplate, container, process, settings);
    }

    public static AllProcessesHeatmapBuilder allProcessesHeatmapBuilder(String serverTemplate, DisplayerSettings settings) {
        return AllProcessesHeatmapBuilder.create(serverTemplate, settings);
    }

}
