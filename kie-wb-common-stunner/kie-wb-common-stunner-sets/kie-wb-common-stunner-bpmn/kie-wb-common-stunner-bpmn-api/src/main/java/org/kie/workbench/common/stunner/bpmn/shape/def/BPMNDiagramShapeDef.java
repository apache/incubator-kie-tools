/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.shape.def;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;

public final class BPMNDiagramShapeDef
        extends AbstractShapeDef<BPMNDiagram>
        implements RectangleShapeDef<BPMNDiagram> {

    @Override
    public String getBackgroundColor( final BPMNDiagram element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha( final BPMNDiagram element ) {
        return 0.8;
    }

    @Override
    public String getBorderColor( final BPMNDiagram element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final BPMNDiagram element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha( final BPMNDiagram element ) {
        return 1;
    }

    @Override
    public String getFontFamily( final BPMNDiagram element ) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor( final BPMNDiagram element ) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize( final BPMNDiagram element ) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public String getNamePropertyValue( final BPMNDiagram element ) {
        return element.getGeneral().getName().getValue();
    }

    @Override
    public double getFontBorderSize( final BPMNDiagram element ) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public String getGlyphBackgroundColor( final BPMNDiagram element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getWidth( final BPMNDiagram element ) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight( final BPMNDiagram element ) {
        return element.getDimensionsSet().getHeight().getValue();
    }

}
