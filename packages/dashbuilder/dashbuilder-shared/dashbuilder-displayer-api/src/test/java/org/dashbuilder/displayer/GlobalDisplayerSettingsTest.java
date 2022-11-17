/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GlobalDisplayerSettingsTest {

    GlobalDisplayerSettings globalDisplayerSettings = new GlobalDisplayerSettings() {

        private DisplayerSettings displayerSettings;

        @Override
        public void setDisplayerSettings(DisplayerSettings settings) {
            this.displayerSettings = settings;
        }

        @Override
        public Optional<DisplayerSettings> getSettings() {
            return Optional.ofNullable(displayerSettings);
        };
    };

    @Test
    public void testApply() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();
        var title = "Testing";

        globalSettings.setTitle(title);
        globalDisplayerSettings.setDisplayerSettings(globalSettings);

        globalDisplayerSettings.apply(settings);

        assertEquals(title, settings.getTitle());

    }

    @Test
    public void testDoNotOverrideUserSetting() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();
        var userTitle = "User Title";
        var globalTitle = "Global Title";

        settings.setTitle(userTitle);
        globalSettings.setTitle(globalTitle);
        
        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(userTitle, settings.getTitle());

    }

}
