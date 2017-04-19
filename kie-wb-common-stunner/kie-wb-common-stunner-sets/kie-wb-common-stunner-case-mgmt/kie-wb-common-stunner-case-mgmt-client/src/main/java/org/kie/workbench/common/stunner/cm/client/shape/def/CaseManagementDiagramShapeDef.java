/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.shape.def;

import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;

public final class CaseManagementDiagramShapeDef
        extends AbstractShapeDef<CaseManagementDiagram>
        implements RectangleShapeDef<CaseManagementDiagram> {

    @Override
    public double getAlpha(final CaseManagementDiagram element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final CaseManagementDiagram element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final CaseManagementDiagram element) {
        return 0.8;
    }

    @Override
    public String getBorderColor(final CaseManagementDiagram element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final CaseManagementDiagram element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final CaseManagementDiagram element) {
        return 1;
    }

    @Override
    public String getFontFamily(final CaseManagementDiagram element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final CaseManagementDiagram element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final CaseManagementDiagram element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final CaseManagementDiagram element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final CaseManagementDiagram element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final CaseManagementDiagram element) {
        return 0;
    }

    @Override
    public double getWidth(final CaseManagementDiagram element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final CaseManagementDiagram element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getCornerRadius(final CaseManagementDiagram element) {
        return 0;
    }
}
