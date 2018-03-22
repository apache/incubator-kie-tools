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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Templated
@Dependent
public class DefinitionPaletteItemWidgetViewImpl implements DefinitionPaletteItemWidgetView,
                                                            IsElement {

    private static final String DISPLAY = "display";
    private static final String DISPLAY_INLINE = "inline";
    private static final String DISPLAY_NONE = "none";
    private static final String PADDING_RIGHT = "padding-right";

    @Inject
    @DataField
    private Paragraph itemAnchor;

    @Inject
    @DataField
    private Span icon;

    @Inject
    @DataField
    private Span name;

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
        if (!isEmpty(title)) {
            name.setTextContent(presenter.getItem().getTitle());
            name.getStyle().setProperty(DISPLAY, DISPLAY_INLINE);
        } else {
            name.getStyle().setProperty(DISPLAY, DISPLAY_NONE);
            icon.getStyle().setProperty(PADDING_RIGHT, "0");
        }
        final String tooltip = presenter.getItem().getTooltip();
        if (!isEmpty(tooltip)) {
            itemAnchor.setTitle(tooltip);
        } else {
            itemAnchor.setTitle("");
        }
    }

    @EventHandler("itemAnchor")
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }

    private static boolean isEmpty(final String s) {
        return null == s || s.trim().length() == 0;
    }
}
