/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.BPMNPictures;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class EndEventShapeDef
        extends AbstractShapeDef<BaseEndEvent>
        implements SVGMutableShapeDef<BaseEndEvent, BPMNSVGViewFactory> {

    private static final String EVENT_END_NONE = "eventEndNone";
    private static final String EVENT_END_TERMINATE = "eventEndTerminate";

    @Override
    public double getAlpha(final BaseEndEvent element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BaseEndEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BaseEndEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BaseEndEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BaseEndEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BaseEndEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BaseEndEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BaseEndEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final BaseEndEvent element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final BaseEndEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BaseEndEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BaseEndEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final BaseEndEvent element) {
        return 0;
    }

    private static final PictureGlyphDef<BaseEndEvent, BPMNPictures> ENDEVENT_GLYPH_DEF = new PictureGlyphDef<BaseEndEvent, BPMNPictures>() {

        private final Map<Class<?>, BPMNPictures> PICTURES = new HashMap<Class<?>, BPMNPictures>(3) {{
            put(EndNoneEvent.class,
                BPMNPictures.EVENT_END_NONE);
            put(EndTerminateEvent.class,
                BPMNPictures.EVENT_END_TERMINATE);
        }};

        @Override
        public String getGlyphDescription(final BaseEndEvent element) {
            return element.getGeneral().getName().getValue();
        }

        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return PICTURES.get(type);
        }
    };

    @Override
    public GlyphDef<BaseEndEvent> getGlyphDef() {
        return ENDEVENT_GLYPH_DEF;
    }

    @Override
    public double getWidth(final BaseEndEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public double getHeight(final BaseEndEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final BaseEndEvent element) {
        switch (viewName) {
            case EVENT_END_NONE:
                return element instanceof EndNoneEvent;
            case EVENT_END_TERMINATE:
                return element instanceof EndTerminateEvent;
        }
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseEndEvent endEvent) {
        return factory.eventEnd(getWidth(endEvent),
                                getHeight(endEvent),
                                false);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }
}