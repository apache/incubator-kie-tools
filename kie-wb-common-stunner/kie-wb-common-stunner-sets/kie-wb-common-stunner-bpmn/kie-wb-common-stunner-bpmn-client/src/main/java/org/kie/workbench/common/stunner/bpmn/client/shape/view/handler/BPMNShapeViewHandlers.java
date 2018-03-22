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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
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
                    .strokeSize(bean -> bean.getFontSet().getFontBorderSize().getValue());
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
