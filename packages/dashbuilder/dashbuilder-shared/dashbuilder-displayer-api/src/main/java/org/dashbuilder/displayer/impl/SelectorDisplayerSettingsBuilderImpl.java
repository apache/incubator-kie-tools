/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.SelectorDisplayerSettingsBuilder;

public class SelectorDisplayerSettingsBuilderImpl extends AbstractSelectorSettingsBuilder<SelectorDisplayerSettingsBuilderImpl> implements SelectorDisplayerSettingsBuilder<SelectorDisplayerSettingsBuilderImpl> {

    protected DisplayerSettings createDisplayerSettings() {
        return new DisplayerSettings(DisplayerType.SELECTOR, DisplayerSubType.SELECTOR_DROPDOWN);
    }

    @Override
    public SelectorDisplayerSettingsBuilderImpl subType_Dropdown() {
        displayerSettings.setSubtype(DisplayerSubType.SELECTOR_DROPDOWN);
        return this;
    }

    @Override
    public SelectorDisplayerSettingsBuilderImpl subType_Slider() {
        displayerSettings.setSubtype(DisplayerSubType.SELECTOR_SLIDER);
        return this;
    }

    @Override
    public SelectorDisplayerSettingsBuilderImpl subType_Labels() {
        displayerSettings.setSubtype(DisplayerSubType.SELECTOR_LABELS);
        return this;
    }
}
