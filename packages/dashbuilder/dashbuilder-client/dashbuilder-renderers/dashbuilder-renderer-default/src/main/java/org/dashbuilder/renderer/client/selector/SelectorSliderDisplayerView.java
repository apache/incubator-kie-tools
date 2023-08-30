/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.selector;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.StyleInjector;
import elemental2.dom.CSSProperties.MarginBottomUnionType;
import elemental2.dom.CSSProperties.MarginLeftUnionType;
import elemental2.dom.CSSProperties.MarginRightUnionType;
import elemental2.dom.CSSProperties.MarginTopUnionType;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.displayer.client.AbstractDisplayerView;
import org.dashbuilder.patternfly.label.Label;
import org.dashbuilder.patternfly.label.LabelColor;
import org.dashbuilder.patternfly.slider.Slider;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.dashbuilder.renderer.client.resources.i18n.SliderConstants;
import org.jboss.errai.ui.shared.api.annotations.Templated;

// TODO: Need to create a slider component - for now it is not available for use
@Dependent
@Templated
public class SelectorSliderDisplayerView extends AbstractDisplayerView<SelectorSliderDisplayer>
                                         implements SelectorSliderDisplayer.View {

    @Inject
    HTMLDivElement container;

    @Inject
    Slider slider;

    @Inject
    Label label;

    @Override
    public void init(SelectorSliderDisplayer presenter) {
        super.init(presenter);
        super.setVisualization(container);

        // Enlarge the tooltip max width
        StyleInjector.inject(".slider .tooltip-inner { max-width: 900px; }");

        // TODO: Slider setup
    }

    @Override
    public String getColumnsTitle() {
        return SliderConstants.INSTANCE.sliderColumnName();
    }

    @Override
    public void showTitle(String title) {
        // TBD
    }

    @Override
    public void setWidth(int width) {
        slider.getElement().style.width = WidthUnionType.of(width + "px");
    }

    @Override
    public void margins(int top, int bottom, int left, int right) {
        container.style.marginTop = MarginTopUnionType.of(top + "px");
        container.style.marginBottom = MarginBottomUnionType.of(bottom + "px");
        container.style.marginLeft = MarginLeftUnionType.of(left + "px");
        container.style.marginRight = MarginRightUnionType.of(right + "px");
    }

    @Override
    public void setSliderEnabled(boolean enabled) {
        slider.setEnabled(enabled);
        slider.setShowTooltip(enabled);
    }

    @Override
    public void showSlider(double min, double max, double step, double minSelected, double maxSelected) {
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(minSelected, maxSelected);
        slider.setStep(step);

    }

    @Override
    public void showInputs(HTMLElement minValueEditor, HTMLElement maxValueEditor) {
        minValueEditor.style.set("margin-top", "5px");
        maxValueEditor.style.set("margin-top", "5px");
        minValueEditor.style.set("margin-bottom", "5px");
        maxValueEditor.style.set("margin-bottom", "5px");
        maxValueEditor.style.set("float", "right");
    }

    @Override
    public String formatRange(String from, String to) {
        return SliderConstants.INSTANCE.sliderTooltip(from, to);
    }

    @Override
    public void textColumnsNotSupported() {
        error(SliderConstants.INSTANCE.textColumnsNotSupported());
    }

    @Override
    public void noData() {
        error(SelectorConstants.INSTANCE.selectorDisplayer_noDataAvailable());
    }

    protected void error(String msg) {
        label.setLabelColor(LabelColor.RED);
        label.setText(msg);
    }
}
