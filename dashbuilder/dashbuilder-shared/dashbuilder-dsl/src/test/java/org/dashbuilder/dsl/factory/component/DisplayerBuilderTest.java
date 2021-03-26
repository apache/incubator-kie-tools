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

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.dsl.model.Component;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DisplayerBuilderTest {

    @Test
    public void testDisplayerJson() {
        DisplayerSettings settings = DisplayerSettingsFactory.newAreaChartSettings()
                                                             .subType_Area()
                                                             .dataset("test")
                                                             .buildSettings();
        String json = DisplayerSettingsJSONMarshaller.get().toJsonString(settings);
        Component comp = DisplayerBuilder.create(settings).build();
        assertEquals(json, comp.getLayoutComponent().getProperties().get("json"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingsMissingDataSetUUID() {
        DisplayerSettings settings = DisplayerSettingsFactory.newAreaChartSettings()
                                                             .subType_Area()
                                                             .buildSettings();
        DisplayerBuilder.create(settings).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingsEmptyDataSetUUID() {
        DisplayerSettings settings = DisplayerSettingsFactory.newAreaChartSettings()
                                                             .subType_Area()
                                                             .dataset("    ")
                                                             .buildSettings();
        DisplayerBuilder.create(settings).build();
    }

    @Test
    public void testWithDataSet() {
        DisplayerSettings settings = DisplayerSettingsFactory.newAreaChartSettings()
                                                             .subType_Area()
                                                             .dataset(DataSetFactory.newEmptyDataSet())
                                                             .buildSettings();
        assertNotNull(DisplayerBuilder.create(settings).build());
    }

}