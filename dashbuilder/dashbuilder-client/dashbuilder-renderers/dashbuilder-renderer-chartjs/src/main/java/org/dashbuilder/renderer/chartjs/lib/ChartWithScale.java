package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.dom.client.Style;
import org.dashbuilder.renderer.chartjs.lib.options.HasScale;

public abstract class ChartWithScale extends ChartWithTooltip implements HasScale{

    @Override
    public void showScale(boolean scale) {
        if(!scale)
            options.clearProperty(SHOWSCALE);
        else
            options.setProperty(SHOWSCALE, true);
    }

    @Override
    public void setScaleOverride(boolean override) {
        if(!override)
            options.clearProperty(SCALEOVERRIDE);
        else
            options.setProperty(SCALEOVERRIDE, override);
    }

    @Override
    public void setScaleSteps(int steps) {
        options.setProperty(SCALESTEPS, steps);
    }

    @Override
    public void setScaleStepWidth(int width) {
        options.setProperty(SCALESTEPWIDTH, width);
    }

    @Override
    public void setScaleStartValue(double startValue) {
        options.setProperty(SCALESTARTVALUE, startValue);
    }

    @Override
    public void setScaleLineColor(String color) {
        options.setProperty(SCALELINECOLOR, color);
    }

    @Override
    public void setScaleLineWidth(int pixelWidth) {
        options.setProperty(SCALELINEWIDTH, pixelWidth);
    }

    @Override
    public void setScaleShowLabels(boolean showLabels) {
        if(!showLabels)
            options.clearProperty(SCALESHOWLABELS);
        else
            options.setProperty(SCALESHOWLABELS, true);
    }

    @Override
    public void setScaleLabel(String template) {
        options.setProperty(SCALELABEL, template);
    }

    @Override
    public void setScaleIntegersOnly(boolean only) {
        if(!only)
            options.clearProperty(SCALEINTEGERSONLY);
        else
            options.setProperty(SCALEINTEGERSONLY, only);
    }

    @Override
    public void setScaleBeginAtZero(boolean beginAtZero) {
        options.setProperty(SCALEBEGINATZERO, beginAtZero);
    }

    @Override
    public void setScaleFontFamily(String fontFamily) {
        options.setProperty(SCALEFONTFAMILY, fontFamily);
    }

    @Override
    public void setScaleFontSize(int size) {
        options.setProperty(SCALEFONTSIZE, size);
    }

    @Override
    public void setScaleFontStyle(Style.FontStyle style) {
        options.setProperty(SCALEFONTSTYLE, style.getCssName());
    }

    @Override
    public void setScaleFontColor(String color) {
        options.setProperty(SCALEFONTCOLOR, color);
    }
}
