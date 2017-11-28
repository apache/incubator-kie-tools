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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;
import org.dashbuilder.renderer.client.resources.i18n.SliderConstants;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.extras.slider.client.ui.Range;
import org.gwtbootstrap3.extras.slider.client.ui.RangeSlider;
import org.gwtbootstrap3.extras.slider.client.ui.base.constants.HandleType;
import org.gwtbootstrap3.extras.slider.client.ui.base.constants.TooltipType;

public class SelectorSliderDisplayerView extends AbstractGwtDisplayerView<SelectorSliderDisplayer>
        implements SelectorSliderDisplayer.View {

    FlowPanel container = new FlowPanel();
    HTML header = new HTML();
    FlowPanel body = new FlowPanel();
    RangeSlider slider = new RangeSlider();
    HorizontalPanel inputs = new HorizontalPanel();
    FlowPanel error = new FlowPanel();

    @Override
    public void init(SelectorSliderDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(container);

        // Enlarge the tooltip max width
        StyleInjector.inject(".slider .tooltip-inner { max-width: 900px; }");

        header.setVisible(false);
        slider.setHandle(HandleType.ROUND);
        slider.setFormatter(r -> presenter.formatRange(r.getMinValue(), r.getMaxValue()));
        slider.addSlideStopHandler(e -> presenter.onSliderChange(e.getValue().getMinValue(), e.getValue().getMaxValue()));
        slider.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        inputs.getElement().getStyle().setWidth(100, Style.Unit.PCT);

        body.add(slider);
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
        slider.getElement().getStyle().setWidth(width, Style.Unit.PX);
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
        slider.setTooltip(enabled ? TooltipType.SHOW : TooltipType.HIDE);
    }

    @Override
    public void showSlider(double min, double max, double step, double minSelected, double maxSelected) {
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(new Range(minSelected, maxSelected));
        slider.setStep(step);

        header.setVisible(true);
        body.setVisible(true);
        error.setVisible(false);
    }

    @Override
    public void showInputs(IsWidget minValueEditor, IsWidget maxValueEditor) {
        minValueEditor.asWidget().getElement().getStyle().setMarginTop(5, Style.Unit.PX);
        maxValueEditor.asWidget().getElement().getStyle().setMarginTop(5, Style.Unit.PX);
        minValueEditor.asWidget().getElement().getStyle().setMarginBottom(5, Style.Unit.PX);
        maxValueEditor.asWidget().getElement().getStyle().setMarginBottom(5, Style.Unit.PX);
        maxValueEditor.asWidget().getElement().getStyle().setFloat(Style.Float.RIGHT);

        inputs.clear();
        inputs.add(minValueEditor);
        inputs.add(maxValueEditor);

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
        error.add(new Label(msg));
    }
}