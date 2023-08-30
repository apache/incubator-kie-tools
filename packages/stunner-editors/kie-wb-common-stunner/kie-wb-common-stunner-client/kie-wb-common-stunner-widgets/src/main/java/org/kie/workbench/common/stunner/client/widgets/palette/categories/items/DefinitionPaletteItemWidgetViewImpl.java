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


package org.kie.workbench.common.stunner.client.widgets.palette.categories.items;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Span;
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
    private Anchor itemAnchor;

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
        if (!StringUtils.isEmpty(title)) {
            name.setTextContent(presenter.getItem().getTitle());
        } else {
            name.getStyle().setProperty(DISPLAY, DISPLAY_NONE);
            icon.getStyle().setProperty(PADDING_RIGHT, "0");
        }
        final String tooltip = presenter.getItem().getTooltip();
        if (!StringUtils.isEmpty(tooltip)) {
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

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(itemAnchor);
        DOMUtil.removeAllChildren(icon);
        DOMUtil.removeAllChildren(name);
        presenter = null;
    }
}
