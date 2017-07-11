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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SubprocessShapeDef
        implements BPMNSvgShapeDef<BaseSubprocess> {

    public final static Map<Class<? extends BaseSubprocess>, String> VIEWS = new HashMap<Class<? extends BaseSubprocess>, String>(3) {{
        put(ReusableSubprocess.class,
            BPMNSVGViewFactory.VIEW_SUBPROCESS_REUSABLE);
        put(EmbeddedSubprocess.class,
            BPMNSVGViewFactory.VIEW_SUBPROCESS_ADHOC);
    }};

    private static final SvgDataUriGlyph.Builder GLYPH_BUILDER =
            SvgDataUriGlyph.Builder.create()
                    .setUri(BPMNImageResources.INSTANCE.subProcessGlyph().getSafeUri())
                    .addUri(BPMNSVGViewFactory.VIEW_SUBPROCESS_REUSABLE,
                            BPMNImageResources.INSTANCE.subProcessReusable().getSafeUri())
                    .addUri(BPMNSVGViewFactory.VIEW_SUBPROCESS_ADHOC,
                            BPMNImageResources.INSTANCE.subProcessAdHoc().getSafeUri());

    @Override
    public double getAlpha(final BaseSubprocess element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BaseSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BaseSubprocess element) {
        return 0.7d;
    }

    @Override
    public String getBorderColor(final BaseSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BaseSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BaseSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BaseSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BaseSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final BaseSubprocess element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final BaseSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BaseSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BaseSubprocess element) {
        return isReusableSubprocess(element) ? HasTitle.Position.CENTER : HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final BaseSubprocess element) {
        return 0;
    }

    @Override
    public double getWidth(final BaseSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final BaseSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final BaseSubprocess element) {
        return !viewName.equals(BPMNSVGViewFactory.VIEW_SUBPROCESS_ADHOC) &&
                viewName.equals(VIEWS.get(element.getClass()));
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseSubprocess element) {
        return factory.subProcess(getWidth(element),
                                  getHeight(element),
                                  true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseSubprocess> type) {
        return GLYPH_BUILDER.build(VIEWS.get(type));
    }

    private static boolean isReusableSubprocess(final BaseSubprocess element) {
        return element instanceof ReusableSubprocess;
    }
}