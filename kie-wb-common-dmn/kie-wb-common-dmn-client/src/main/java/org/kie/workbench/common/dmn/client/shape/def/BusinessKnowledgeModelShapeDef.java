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
package org.kie.workbench.common.dmn.client.shape.def;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class BusinessKnowledgeModelShapeDef implements DMNSVGShapeDef<BusinessKnowledgeModel> {

    @Override
    public double getAlpha(final BusinessKnowledgeModel element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BusinessKnowledgeModel element) {
        return element.getBackgroundSet().getBgColour().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BusinessKnowledgeModel element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BusinessKnowledgeModel element) {
        return element.getBackgroundSet().getBorderColour().getValue();
    }

    @Override
    public double getBorderSize(final BusinessKnowledgeModel element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BusinessKnowledgeModel element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BusinessKnowledgeModel element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BusinessKnowledgeModel element) {
        return element.getFontSet().getFontColour().getValue();
    }

    @Override
    public double getFontSize(final BusinessKnowledgeModel element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BusinessKnowledgeModel element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public String getFontBorderColor(final BusinessKnowledgeModel element) {
        return null;
    }

    @Override
    public HasTitle.Position getFontPosition(final BusinessKnowledgeModel element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final BusinessKnowledgeModel element) {
        return 0;
    }

    @Override
    public double getWidth(final BusinessKnowledgeModel element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final BusinessKnowledgeModel element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final BusinessKnowledgeModel element) {
        return true;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final DMNSVGViewFactory factory,
                                           final BusinessKnowledgeModel task) {
        return factory.businessKnowledgeModel(getWidth(task),
                                              getHeight(task),
                                              true);
    }

    @Override
    public Class<DMNSVGViewFactory> getViewFactoryType() {
        return DMNSVGViewFactory.class;
    }
}
