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


package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.BiFunction;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.processors.common.injectors.StyleInjector;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;

@Dependent
public class ImageStripDOMGlyphRenderer implements DOMGlyphRenderer<ImageStripGlyph> {

    private final ImageStripRegistry stripRegistry;
    private final ManagedInstance<WidgetElementRendererView> views;
    private final BiFunction<String, Integer[], HTMLDivElement> panelBuilder;

    @Inject
    public ImageStripDOMGlyphRenderer(final ImageStripRegistry stripRegistry,
                                      final ManagedInstance<WidgetElementRendererView> views) {
        this(stripRegistry,
             views,
             (className, clip) -> buildPanel(className,
                                             clip[0],
                                             clip[1]));
    }

    ImageStripDOMGlyphRenderer(final ImageStripRegistry stripRegistry,
                               final ManagedInstance<WidgetElementRendererView> views,
                               final BiFunction<String, Integer[], HTMLDivElement> panelBuilder) {
        this.stripRegistry = stripRegistry;
        this.views = views;
        this.panelBuilder = panelBuilder;
    }

    @Override
    public Class<ImageStripGlyph> getGlyphType() {
        return ImageStripGlyph.class;
    }

    @Override
    public IsElement render(final ImageStripGlyph glyph,
                            final double width,
                            final double height) {
        final ImageStrip strip = stripRegistry.get(glyph.getStripType());
        final int index = glyph.getIndex();
        final boolean isHorizontal = ImageStrip.Orientation.HORIZONTAL.equals(strip.getOrientation());
        final int clipX = isHorizontal ? (strip.getWide() + strip.getPadding()) * index : 0;
        final int clipY = !isHorizontal ? (strip.getHigh() + strip.getPadding()) * index : 0;
        final WidgetElementRendererView view = views.get();
        inject(strip.getCss().getCssResource());
        view.setWidget(panelBuilder.apply(strip.getCss().getClassName(), new Integer[]{clipX, clipY * -1}));
        return view;
    }

    protected void inject(final String css) {
        StyleInjector.fromString(css).inject();
    }

    @PreDestroy
    public void destroy() {
        views.destroyAll();
    }

    protected static HTMLDivElement buildPanel(final String className,
                                        final int clipX,
                                        final int clipY) {
        HTMLDivElement root = (HTMLDivElement) DomGlobal.document.createElement("div");
        root.className = className;
        root.style.setProperty("background-position", clipX + "px " + clipY + "px !important");
        return root;
    }

    protected static String backGroundPosition(int clipX, int clipY) {
        return "background-position: " + clipX + "px " + clipY + "px !important";
    }
}
