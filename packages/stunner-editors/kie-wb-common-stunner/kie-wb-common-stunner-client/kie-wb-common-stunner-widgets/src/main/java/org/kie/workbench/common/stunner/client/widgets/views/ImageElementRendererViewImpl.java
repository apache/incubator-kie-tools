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

package org.kie.workbench.common.stunner.client.widgets.views;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import jsinterop.base.Js;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtproject.dom.client.Style;
import org.gwtproject.safehtml.shared.SafeUri;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;

@Templated
@Dependent
public class ImageElementRendererViewImpl implements ImageElementRendererView,
                                                     IsElement {

    @Inject
    @DataField
    private HTMLDivElement content;

    @Inject
    @DataField
    private HTMLDivElement icon;

    public ImageElementRendererView setDOMContent(final String content,
                                                  final int width,
                                                  final int height) {
        icon.innerHTML = (content);
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
        icon.style.setProperty("width",
                               widthPx + Style.Unit.PX.name());
        icon.style.setProperty("height",
                               heightPx + Style.Unit.PX.name());

        HTMLElement svgElement = Js.uncheckedCast(icon.firstElementChild);

        svgElement.style.setProperty("width",
                                     widthPx + Style.Unit.PX.name());
        svgElement.style.setProperty("height",
                                     heightPx + Style.Unit.PX.name());
        svgElement.style.setProperty("position",
                                     "absolute");
        svgElement.style.setProperty("top",
                                     "0px");
        svgElement.style.setProperty("left",
                                     "0px");
    }
}
