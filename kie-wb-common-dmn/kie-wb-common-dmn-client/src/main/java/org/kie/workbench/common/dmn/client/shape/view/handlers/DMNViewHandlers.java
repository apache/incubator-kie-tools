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

package org.kie.workbench.common.dmn.client.shape.view.handlers;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;

public class DMNViewHandlers {

    public static final TitleHandler<ShapeView> TITLE_HANDLER = new TitleHandler<>();

    public static final ViewAttributesHandler<DMNDefinition, ShapeView> CONNECTOR_ATTRIBUTES_HANDLER =
            new ViewAttributesHandler.Builder<DMNDefinition, ShapeView>()
                    .alpha(def -> 1d)
                    .fillAlpha(def -> 1d)
                    .fillColor(def -> "#000000")
                    .strokeColor(def -> "#000000")
                    .strokeWidth(def -> 1d)
                    .build();

    public static class FontHandlerBuilder<W extends DMNViewDefinition, V extends ShapeView>
            extends FontHandler.Builder<W, V> {

        public FontHandlerBuilder() {
            this.fontFamily(bean -> bean.getFontSet().getFontFamily().getValue())
                    .fontColor(bean -> bean.getFontSet().getFontColour().getValue())
                    .fontSize(bean -> bean.getFontSet().getFontSize().getValue())
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE)
                    .strokeColor(bean -> null)
                    .strokeAlpha(bean -> 0.0)
                    .strokeSize(bean -> 0.0);
        }
    }

    public static class SizeHandlerBuilder<W extends DMNViewDefinition, V extends ShapeView>
            extends SizeHandler.Builder<W, V> {

        public SizeHandlerBuilder() {
            this.width(e -> e.getDimensionsSet().getWidth().getValue())
                    .height(e -> e.getDimensionsSet().getHeight().getValue())
                    .minWidth(p -> p.getDimensionsSet().getMinimumWidth())
                    .maxWidth(p -> p.getDimensionsSet().getMaximumWidth())
                    .minHeight(p -> p.getDimensionsSet().getMinimumHeight())
                    .maxHeight(p -> p.getDimensionsSet().getMaximumHeight());
        }
    }

    public static class ViewAttributesHandlerBuilder<W extends DMNViewDefinition, V extends ShapeView>
            extends ViewAttributesHandler.Builder<W, V> {

        public ViewAttributesHandlerBuilder() {
            this.fillColor(bean -> bean.getBackgroundSet().getBgColour().getValue())
                    .strokeColor(bean -> bean.getBackgroundSet().getBorderColour().getValue())
                    .strokeWidth(bean -> bean.getBackgroundSet().getBorderSize().getValue());
        }
    }
}
