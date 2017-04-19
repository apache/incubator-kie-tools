/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class BPMNDiagramShapeDef
        extends AbstractShapeDef<BPMNDiagramImpl>
        implements SVGMutableShapeDef<BPMNDiagramImpl, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final BPMNDiagramImpl element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BPMNDiagramImpl element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BPMNDiagramImpl element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BPMNDiagramImpl element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BPMNDiagramImpl element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BPMNDiagramImpl element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BPMNDiagramImpl element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BPMNDiagramImpl element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final BPMNDiagramImpl element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BPMNDiagramImpl element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BPMNDiagramImpl element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final BPMNDiagramImpl element) {
        return 0;
    }

    @Override
    public double getWidth(final BPMNDiagramImpl element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final BPMNDiagramImpl element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final BPMNDiagramImpl element) {
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BPMNDiagramImpl diagram) {
        return factory.rectangle(getWidth(diagram),
                                 getHeight(diagram),
                                 true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }
}