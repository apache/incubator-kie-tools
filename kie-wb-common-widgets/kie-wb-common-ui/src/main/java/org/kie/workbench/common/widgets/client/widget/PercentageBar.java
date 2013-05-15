package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PercentageBar extends Composite
        implements
        HasValue<Integer> {

    public static final String FAILURE = "#CC0000";
    public static final String COMPLETE_SUCCESS = "GREEN";
    public static final String INCOMPLETE = "YELLOW";

    interface PercentageBarBinder
            extends
            UiBinder<Widget, PercentageBar> {

    }

    private static PercentageBarBinder uiBinder = GWT.create(PercentageBarBinder.class);

    @UiField
    Label percentage;

    @UiField
    DivElement wrapper;

    @UiField
    DivElement text;

    @UiField
    DivElement bar;

    private int percent = 0;

    private String inCompleteBarColor = FAILURE;

    public PercentageBar() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public PercentageBar(String color,
            int width,
            float percent) {
        this();
        setColor(color);
        setWidth(width);
        setPercent((int) percent);
    }

    public PercentageBar(String color,
            int width,
            int numerator,
            int denominator) {
        this(color,
                width,
                PercentageCalculator.calculatePercent(numerator,
                        denominator));
    }

    private void setColor(String color) {
        bar.getStyle().setBackgroundColor(color);
    }

    public void setBackgroundColor(String color) {
        wrapper.getStyle().setBackgroundColor(color);
    }

    public void setWidth(String width) {
        setWidth(Integer.parseInt(width));
    }

    public void setWidth(int width) {
        text.getStyle().setWidth(width,
                Unit.PX);
        wrapper.getStyle().setWidth(width,
                Unit.PX);
    }

    public void setPercent(int percent) {
        setValue(percent);
    }

    public void setPercent(int numerator,
            int denominator) {
        setPercent(PercentageCalculator.calculatePercent(numerator,
                denominator));
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
        return addHandler(handler,
                ValueChangeEvent.getType());
    }

    public Integer getValue() {
        return percent;
    }

    public void setValue(Integer value) {
        setValue(value,
                false);
    }

    public void setValue(Integer value,
            boolean fireEvents) {

        percent = value;

        setColor();

        percentage.setText(Integer.toString(value) + " %");
        bar.getStyle().setWidth(value,
                Unit.PCT);

        if (fireEvents) {
            ValueChangeEvent.fire(this,
                    value);
        }

    }

    private void setColor() {
        if (percent < 100) {
            setColor(inCompleteBarColor);
        } else {
            setColor(COMPLETE_SUCCESS);
        }
    }

    public void setInCompleteBarColor(String color) {
        this.inCompleteBarColor = color;
    }
}
