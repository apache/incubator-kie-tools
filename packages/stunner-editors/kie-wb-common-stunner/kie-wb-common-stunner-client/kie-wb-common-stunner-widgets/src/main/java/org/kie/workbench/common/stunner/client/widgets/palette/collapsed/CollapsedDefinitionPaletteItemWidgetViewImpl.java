/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Templated
@Dependent
public class CollapsedDefinitionPaletteItemWidgetViewImpl implements CollapsedDefinitionPaletteItemWidgetView,
                                                                     IsElement {

    @DataField
    private HTMLButtonElement icon;

    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    public CollapsedDefinitionPaletteItemWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public CollapsedDefinitionPaletteItemWidgetViewImpl(final HTMLButtonElement icon,
                                                        final DOMGlyphRenderers domGlyphRenderers) {
        this.icon = icon;
        this.domGlyphRenderers = domGlyphRenderers;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(final Glyph glyph,
                       final double width,
                       final double height) {
        final IsElement glyphElement = domGlyphRenderers.render(glyph, width, height);
        icon.appendChild(glyphElement.getElement());

        final String tooltip = presenter.getItem().getTooltip();
        if (!StringUtils.isEmpty(tooltip)) {
            icon.title = (tooltip);
        } else {
            icon.title = ("");
        }
    }

    @EventHandler("icon")
    public void onMouseDown(@ForEvent("mousedown") final MouseEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.clientX,
                              mouseDownEvent.clientY,
                              mouseDownEvent.x,
                              mouseDownEvent.y);
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(icon);
        presenter = null;
    }
}
