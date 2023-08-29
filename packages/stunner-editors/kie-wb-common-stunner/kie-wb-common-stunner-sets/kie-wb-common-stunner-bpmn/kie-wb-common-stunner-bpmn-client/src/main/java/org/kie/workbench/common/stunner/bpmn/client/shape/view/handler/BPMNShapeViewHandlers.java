/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Orientation;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.ReferencePosition;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Size;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Size.SizeType;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.VerticalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;

public class BPMNShapeViewHandlers {

    public static final TitleHandler<ShapeView> TITLE_HANDLER = new TitleHandler<>();

    public static class FontHandlerBuilder<W extends BPMNViewDefinition, V extends ShapeView>
            extends FontHandler.Builder<W, V> {

        public FontHandlerBuilder() {
            this.fontFamily(bean -> bean.getFontSet().getFontFamily().getValue())
                    .fontColor(bean -> bean.getFontSet().getFontColor().getValue())
                    .fontSize(bean -> bean.getFontSet().getFontSize().getValue())
                    .strokeColor(bean -> bean.getFontSet().getFontBorderColor().getValue())
                    .strokeSize(bean -> bean.getFontSet().getFontBorderSize().getValue())
                    .strokeAlpha(bean -> getStrokeAlpha(bean.getFontSet().getFontBorderSize().getValue()))
                    .verticalAlignment(bean -> VerticalAlignment.MIDDLE)
                    .horizontalAlignment(bean -> HorizontalAlignment.CENTER)
                    .referencePosition(bean -> ReferencePosition.INSIDE)
                    .orientation(bean -> Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new Size(100, 100, SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
        }

        public static Double getStrokeAlpha(Double strokeWidth) {
            return Optional.ofNullable(strokeWidth)
                    .filter(value -> value > 0)
                    .map(value -> 1.0)
                    .orElse(0.0);
        }
    }

    public static class ViewAttributesHandlerBuilder<W extends BPMNViewDefinition, V extends ShapeView>
            extends ViewAttributesHandler.Builder<W, V> {

        public ViewAttributesHandlerBuilder() {
            this.fillColor(bean -> bean.getBackgroundSet().getBgColor().getValue())
                    .strokeColor(bean -> bean.getBackgroundSet().getBorderColor().getValue())
                    .strokeWidth(bean -> bean.getBackgroundSet().getBorderSize().getValue());
        }
    }
}