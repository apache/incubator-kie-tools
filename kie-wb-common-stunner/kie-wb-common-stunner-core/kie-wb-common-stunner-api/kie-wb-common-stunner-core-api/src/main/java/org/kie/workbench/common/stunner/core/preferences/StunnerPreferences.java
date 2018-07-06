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

package org.kie.workbench.common.stunner.core.preferences;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "StunnerPreferences",
        bundleKey = "StunnerPreferences.Label")
public class StunnerPreferences implements BasePreference<StunnerPreferences>,
                                           Cloneable {

    @Property(bundleKey = "StunnerPreferences.StunnerDiagramEditorPreferences")
    StunnerDiagramEditorPreferences diagramEditorPreferences;

    @Override
    public StunnerPreferences defaultValue(final StunnerPreferences defaultValue) {
        defaultValue.diagramEditorPreferences.setAutoHidePalettePanel(false);
        defaultValue.diagramEditorPreferences.setCanvasWidth(2800);
        defaultValue.diagramEditorPreferences.setCanvasHeight(1400);
        defaultValue.diagramEditorPreferences.setEnableHiDPI(false);
        return defaultValue;
    }

    public StunnerDiagramEditorPreferences getDiagramEditorPreferences() {
        return diagramEditorPreferences;
    }
}