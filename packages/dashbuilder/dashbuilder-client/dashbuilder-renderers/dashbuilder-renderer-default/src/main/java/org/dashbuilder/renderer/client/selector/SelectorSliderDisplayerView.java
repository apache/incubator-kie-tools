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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import elemental2.dom.CSSProperties.WidthUnionType;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.displayer.client.AbstractDisplayerView;
import org.dashbuilder.patternfly.label.Label;
import org.dashbuilder.patternfly.label.LabelColor;
import org.dashbuilder.patternfly.slider.Slider;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.dashbuilder.renderer.client.resources.i18n.SliderConstants;

@Dependent
public class SelectorSliderDisplayerView extends AbstractDisplayerView<SelectorSliderDisplayer>
                                         implements SelectorSliderDisplayer.View {

    FlowPanel container = new FlowPanel();
    HTML header = new HTML();
    FlowPanel body = new FlowPanel();
    HorizontalPanel inputs = new HorizontalPanel();
    FlowPanel error = new FlowPanel();

    @Inject
    Slider slider;

    @Inject
    Label label;

    @Override
    public void init(SelectorSliderDisplayer presenter) {
        super.init(presenter);
        super.setVisualization(Js.cast(container.getElement()));

        // Enlarge the tooltip max width
        StyleInjector.inject(".slider .tooltip-inner { max-width: 900px; }");

        header.setVisible(false);

        // TODO: Slider setup
        body.getElement().appendChild(Js.cast(slider.getElement()));
        body.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        container.add(header);
        container.add(body);
        container.add(inputs);
        container.add(error);
    }

    @Override
    public String getColumnsTitle() {
        return SliderConstants.INSTANCE.sliderColumnName();
    }

    @Override
    public void showTitle(String title) {
        error.setVisible(false);
        header.setVisible(true);
        header.setText(title);
    }

    @Override
    public void setWidth(int width) {
        slider.getElement().style.width = WidthUnionType.of(width + "px");
        inputs.getElement().getStyle().setWidth(width, Style.Unit.PX);
    }

    @Override
    public void margins(int top, int bottom, int left, int right) {
        container.getElement().getStyle().setMarginTop(top, Style.Unit.PX);
        container.getElement().getStyle().setMarginBottom(bottom, Style.Unit.PX);
        container.getElement().getStyle().setMarginLeft(left, Style.Unit.PX);
        container.getElement().getStyle().setMarginRight(right, Style.Unit.PX);
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

        header.setVisible(true);
        body.setVisible(true);
        error.setVisible(false);
    }

    @Override
    public void showInputs(HTMLElement minValueEditor, HTMLElement maxValueEditor) {
        minValueEditor.style.set("margin-top", "5px");
        maxValueEditor.style.set("margin-top", "5px");
        minValueEditor.style.set("margin-bottom", "5px");
        maxValueEditor.style.set("margin-bottom", "5px");
        maxValueEditor.style.set("float", "right");

        inputs.clear();
        // change inputs to other panel
        //inputs.add(minValueEditor);
        //inputs.add(maxValueEditor);

        header.setVisible(true);
        inputs.setVisible(true);
        error.setVisible(false);
    }

    @Override
    public String formatRange(String from, String to) {
        return SliderConstants.INSTANCE.sliderTooltip(from, to);
    }

    @Override
    public void textColumnsNotSupported() {
        header.setVisible(false);
        error(SliderConstants.INSTANCE.textColumnsNotSupported());
    }

    @Override
    public void noData() {
        error(SelectorConstants.INSTANCE.selectorDisplayer_noDataAvailable());
    }

    protected void error(String msg) {
        body.setVisible(false);
        inputs.setVisible(false);
        error.setVisible(true);
        error.clear();
        label.setLabelColor(LabelColor.RED);
        label.setText(msg);
        error.getElement().appendChild(Js.cast(label.getElement()));
    }
}
