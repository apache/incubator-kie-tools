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


package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Button;
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
public class CollapsedDefinitionPaletteItemWidgetViewImpl implements CollapsedDefinitionPaletteItemWidgetView,
                                                                     IsElement {

    @DataField
    private Button icon;

    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    public CollapsedDefinitionPaletteItemWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public CollapsedDefinitionPaletteItemWidgetViewImpl(final Button icon,
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
        final org.jboss.errai.common.client.api.IsElement glyphElement = domGlyphRenderers.render(glyph, width, height);
        icon.appendChild(glyphElement.getElement());

        final String tooltip = presenter.getItem().getTooltip();
        if (!StringUtils.isEmpty(tooltip)) {
            icon.setTitle(tooltip);
        } else {
            icon.setTitle("");
        }
    }

    @EventHandler("icon")
    public void onMouseDown(final MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(icon);
        presenter = null;
    }
}
