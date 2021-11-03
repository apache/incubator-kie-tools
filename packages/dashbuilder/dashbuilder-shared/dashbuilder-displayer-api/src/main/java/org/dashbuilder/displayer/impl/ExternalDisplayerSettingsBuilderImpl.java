/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.impl;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.ExternalDisplayerSettingsBuilder;

public class ExternalDisplayerSettingsBuilderImpl extends AbstractChartSettingsBuilder<ExternalDisplayerSettingsBuilderImpl> implements ExternalDisplayerSettingsBuilder<ExternalDisplayerSettingsBuilderImpl> {

    protected DisplayerSettings createDisplayerSettings() {
        return new DisplayerSettings(DisplayerType.EXTERNAL_COMPONENT);
    }

    @Override
    public ExternalDisplayerSettingsBuilderImpl componentId(String id) {
        this.displayerSettings.setComponentId(id);
        return this;
    }

    @Override
    public ExternalDisplayerSettingsBuilderImpl componentProperty(String key, String value) {
        this.displayerSettings.setComponentProperty(key, value);
        return this;
    }

}