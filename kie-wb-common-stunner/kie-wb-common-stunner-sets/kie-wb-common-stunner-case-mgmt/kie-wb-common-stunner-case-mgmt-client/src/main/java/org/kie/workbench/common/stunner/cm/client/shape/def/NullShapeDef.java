/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

public class NullShapeDef implements CaseManagementShapeDef<BPMNDefinition> {

    @Override
    public double getAlpha(final BPMNDefinition element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BPMNDefinition element) {
        return "";
    }

    @Override
    public double getBackgroundAlpha(final BPMNDefinition element) {
        return 1d;
    }

    @Override
    public String getBorderColor(final BPMNDefinition element) {
        return "";
    }

    @Override
    public double getBorderSize(final BPMNDefinition element) {
        return 1d;
    }

    @Override
    public double getBorderAlpha(final BPMNDefinition element) {
        return 1d;
    }

    @Override
    public String getFontFamily(final BPMNDefinition element) {
        return "";
    }

    @Override
    public String getFontColor(final BPMNDefinition element) {
        return "";
    }

    @Override
    public String getFontBorderColor(final BPMNDefinition element) {
        return "";
    }

    @Override
    public double getFontSize(final BPMNDefinition element) {
        return 0;
    }

    @Override
    public double getFontBorderSize(final BPMNDefinition element) {
        return 0;
    }

    @Override
    public HasTitle.Position getFontPosition(final BPMNDefinition element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final BPMNDefinition element) {
        return 0;
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return NullShapeDef.class;
    }
}
