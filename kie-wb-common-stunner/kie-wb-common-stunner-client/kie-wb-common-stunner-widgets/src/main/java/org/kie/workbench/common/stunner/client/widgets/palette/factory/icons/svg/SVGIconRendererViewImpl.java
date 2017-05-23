/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.svg;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Image;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;

@Templated
@Dependent
public class SVGIconRendererViewImpl implements SVGIconRendererView,
                                                IsElement {

    private static final int LARGE_ICON_SIZE = 20;
    private static final int SMALL_ICON_SIZE = 15;

    @Inject
    @DataField
    private Div content;

    @Inject
    @DataField
    private Div icon;

    private SVGIconRenderer presenter;

    @Override
    public void init(SVGIconRenderer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render() {

        String svgContent = presenter.getSVGContent();
        if (svgContent != null) {
            icon.setInnerHTML(svgContent);
        } else {
            Image image = new Image(presenter.getIconResource().getResource().getSafeUri());
            DOMUtil.appendWidgetToElement(icon,
                                          image);
        }

        resize();
    }

    @Override
    public void resize() {
        if (icon != null) {

            int size;
            if (presenter.getSize().equals(IconRenderer.Size.LARGE)) {
                size = LARGE_ICON_SIZE;
            } else {
                size = SMALL_ICON_SIZE;
            }
            icon.getStyle().setProperty("width",
                                        size + "px");
            icon.getStyle().setProperty("height",
                                        size + "px");
            HTMLElement svgElement = (HTMLElement) DOMUtil.getFirstChildElement(icon).get();
            if (svgElement != null) {
                svgElement.getStyle().setProperty("width",
                                                  size + "px");
                svgElement.getStyle().setProperty("height",
                                                  size + "px");
                svgElement.getStyle().setProperty("position",
                                                  "absolute");
                svgElement.getStyle().setProperty("top",
                                                  "0px");
                svgElement.getStyle().setProperty("left",
                                                  "0px");
            }
        }
    }
}
