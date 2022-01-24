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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelWidget;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public abstract class LienzoElementGlyphRenderer<G extends Glyph> implements DOMGlyphRenderer<G> {

    private final Supplier<WidgetElementRendererView> viewInstances;

    protected LienzoElementGlyphRenderer(final Supplier<WidgetElementRendererView> viewInstances) {
        this.viewInstances = viewInstances;
    }

    protected abstract LienzoGlyphRenderer<G> getLienzoGlyphRenderer();

    @Override
    public IsElement render(final G glyph,
                            final double width,
                            final double height) {
        final WidgetElementRendererView view = viewInstances.get();

        final LienzoPanelWidget panel = newPanel(glyph,
                                                 (int) width,
                                                 (int) height);
        return view.setWidget(panel);
    }

    private LienzoPanelWidget newPanel(final G glyph,
                                       final int width,
                                       final int height) {
        final Group glyphGroup = getLienzoGlyphRenderer().render(glyph,
                                                                 width,
                                                                 height);
        final LienzoPanelWidget panel = LienzoPanelWidget.create(width,
                                                                 height);
        final Layer layer = new Layer();
        panel.add(layer.setTransformable(true));
        layer.add(glyphGroup);
        return panel;
    }
}
