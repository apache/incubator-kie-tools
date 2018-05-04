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

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "StunnerDiagramEditorPreferences",
        bundleKey = "StunnerDiagramEditorPreferences.Label")
public class StunnerDiagramEditorPreferences implements BasePreference<StunnerDiagramEditorPreferences>,
                                                        Cloneable {

    @Property(bundleKey = "StunnerDiagramEditorPreferences.AutoHidePalettePanel.Label",
            helpBundleKey = "StunnerDiagramEditorPreferences.AutoHidePalettePanel.Help",
            formType = PropertyFormType.BOOLEAN)
    boolean autoHidePalettePanel;

    @Property(bundleKey = "StunnerDiagramEditorPreferences.CanvasWidth.Label",
            helpBundleKey = "StunnerDiagramEditorPreferences.CanvasWidth.Help",
            formType = PropertyFormType.NATURAL_NUMBER,
            validators = CanvasWidthValidator.class)
    int canvasWidth;

    @Property(bundleKey = "StunnerDiagramEditorPreferences.CanvasHeight.Label",
            helpBundleKey = "StunnerDiagramEditorPreferences.CanvasHeight.Help",
            formType = PropertyFormType.NATURAL_NUMBER,
            validators = CanvasHeightValidator.class)
    int canvasHeight;

    public boolean isAutoHidePalettePanel() {
        return autoHidePalettePanel;
    }

    public void setAutoHidePalettePanel(boolean autoHidePalettePanel) {
        this.autoHidePalettePanel = autoHidePalettePanel;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public Object clone() {
        StunnerDiagramEditorPreferences clone = new StunnerDiagramEditorPreferences();
        clone.setAutoHidePalettePanel(this.isAutoHidePalettePanel());
        clone.setCanvasWidth(this.getCanvasWidth());
        clone.setCanvasHeight(this.getCanvasHeight());
        return clone;
    }
}