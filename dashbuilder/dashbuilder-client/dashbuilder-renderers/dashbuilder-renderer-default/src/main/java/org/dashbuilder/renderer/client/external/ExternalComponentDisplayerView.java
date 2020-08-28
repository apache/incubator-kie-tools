/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.client.external;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.CSSProperties.HeightUnionType;
import elemental2.dom.CSSProperties.MarginUnionType;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ExternalComponentDisplayerView extends AbstractErraiDisplayerView<ExternalComponentDisplayer>
                                            implements ExternalComponentDisplayer.View {

    @Inject
    @DataField
    HTMLDivElement externalComponentDisplayerRoot;

    @Inject
    Elemental2DomUtil domUtil;

    private HTMLElement externalComponentView;

    @Override
    public void init(ExternalComponentDisplayer presenter) {
        super.setPresenter(presenter);
        externalComponentView = Js.cast(presenter.getExternalComponentPresenter().getView().asWidget().getElement());
        externalComponentDisplayerRoot.appendChild(externalComponentView);
        super.setVisualization(Js.cast(externalComponentDisplayerRoot));
    }

    @Override
    public org.jboss.errai.common.client.dom.HTMLElement getElement() {
        return Js.cast(externalComponentDisplayerRoot);
    }

    @Override
    public void setSize(int chartWidth, int chartHeight) {
        externalComponentDisplayerRoot.style.width = WidthUnionType.of(asPixel(chartWidth));
        externalComponentDisplayerRoot.style.height = HeightUnionType.of(asPixel(chartHeight));
    }

    @Override
    public void setMargin(int top, int right, int bottom, int left) {
        externalComponentDisplayerRoot.style.margin = MarginUnionType.of(String.join(" ",
                                                                                     asPixel(top),
                                                                                     asPixel(right),
                                                                                     asPixel(bottom),
                                                                                     asPixel(left)));
    }

    private String asPixel(int value) {
        return value + "px";
    }

    @Override
    public void setFilterLabelSet(FilterLabelSet widget) {
        org.jboss.errai.common.client.dom.HTMLElement element = widget.getElement();
        element.getStyle().setProperty("position", "absolute");
        element.getStyle().setProperty("z-index", "20");
        externalComponentDisplayerRoot.insertBefore(Js.cast(element), externalComponentView);
    }

}