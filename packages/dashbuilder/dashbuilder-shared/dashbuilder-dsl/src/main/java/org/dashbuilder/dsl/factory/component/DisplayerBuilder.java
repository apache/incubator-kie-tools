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
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.dsl.model.Component;

public class DisplayerBuilder extends AbstractComponentBuilder<DisplayerBuilder> {

    private static final String JSON_PROP = "json";
    DisplayerSettings settings;

    DisplayerBuilder(DisplayerSettings settings) {
        this.settings = settings;
    }

    public static DisplayerBuilder create(DisplayerSettings settings) {

        if (isInvalid(settings)) {
            throw new IllegalArgumentException("You must provide a data set or an UUID to build a displayer component");
        }
        return new DisplayerBuilder(settings);
    }

    @Override
    public Component build() {
        property(JSON_PROP, DisplayerSettingsJSONMarshaller.get().toJsonString(this.settings));
        return super.build();
    }

    @Override
    String getDragType() {
        return "org.dashbuilder.client.editor.DisplayerDragComponent";
    }

    private static boolean isInvalid(DisplayerSettings settings) {
        String dataSetUUID = settings.getDataSetLookup().getDataSetUUID();
        return (dataSetUUID == null || dataSetUUID.trim().isEmpty()) && settings.getDataSet() == null;
    }

}