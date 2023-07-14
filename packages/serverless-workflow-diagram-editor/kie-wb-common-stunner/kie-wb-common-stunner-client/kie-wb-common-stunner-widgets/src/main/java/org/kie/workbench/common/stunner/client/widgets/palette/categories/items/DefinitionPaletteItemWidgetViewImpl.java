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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.items;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.MouseDownEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Templated
@Dependent
public class DefinitionPaletteItemWidgetViewImpl implements DefinitionPaletteItemWidgetView,
                                                            IsElement {

    private static final String DISPLAY = "display";
    private static final String DISPLAY_NONE = "none";
    private static final String PADDING_RIGHT = "padding-right";

    @Inject
    @DataField
    private HTMLAnchorElement itemAnchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement icon;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement name;

    @Inject
    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(final Glyph glyph,
                       final double width,
                       final double height) {
        final org.jboss.errai.common.client.api.IsElement glyphElement =
                domGlyphRenderers.render(glyph,
                                         width,
                                         height);
        icon.appendChild(glyphElement.getElement());
        final String title = presenter.getItem().getTitle();
        if (!StringUtils.isEmpty(title)) {
            name.textContent = presenter.getItem().getTitle();
        } else {
            name.style.setProperty(DISPLAY, DISPLAY_NONE);
            icon.style.setProperty(PADDING_RIGHT, "0");
        }
        final String tooltip = presenter.getItem().getTooltip();
        if (!StringUtils.isEmpty(tooltip)) {
            itemAnchor.title = tooltip;
        } else {
            itemAnchor.title = "";
        }
    }

    @EventHandler("itemAnchor")
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(itemAnchor);
        DOMUtil.removeAllChildren(icon);
        DOMUtil.removeAllChildren(name);
        presenter = null;
    }
}
