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


package org.kie.workbench.common.stunner.client.widgets.views;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeUri;
import org.gwtbootstrap3.client.ui.Image;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;

@Templated
@Dependent
public class ImageElementRendererViewImpl implements ImageElementRendererView,
                                                     IsElement {

    @Inject
    @DataField
    private Div content;

    @Inject
    @DataField
    private Div icon;

    public ImageElementRendererView setDOMContent(final String content,
                                                  final int width,
                                                  final int height) {
        icon.setInnerHTML(content);
        resize(width,
               height);
        return this;
    }

    public ImageElementRendererView setImage(final SafeUri safeUri,
                                             final int width,
                                             final int height) {
        Image image = new Image(safeUri);
        DOMUtil.appendWidgetToElement(icon,
                                      image);
        resize(width,
               height);
        return this;
    }

    private void resize(final int widthPx,
                        final int heightPx) {
        icon.getStyle().setProperty("width",
                                    widthPx + Style.Unit.PX.name());
        icon.getStyle().setProperty("height",
                                    heightPx + Style.Unit.PX.name());
        if (DOMUtil.getFirstChildElement(icon).isPresent()) {
            HTMLElement svgElement = (HTMLElement) DOMUtil.getFirstChildElement(icon).get();
            svgElement.getStyle().setProperty("width",
                                              widthPx + Style.Unit.PX.name());
            svgElement.getStyle().setProperty("height",
                                              heightPx + Style.Unit.PX.name());
            svgElement.getStyle().setProperty("position",
                                              "absolute");
            svgElement.getStyle().setProperty("top",
                                              "0px");
            svgElement.getStyle().setProperty("left",
                                              "0px");
        }
    }
}
