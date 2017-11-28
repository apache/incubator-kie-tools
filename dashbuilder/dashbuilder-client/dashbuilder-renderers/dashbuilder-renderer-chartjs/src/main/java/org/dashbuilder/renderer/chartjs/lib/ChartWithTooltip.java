package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import org.dashbuilder.renderer.chartjs.lib.options.HasTooltip;

/**
 * Adds tooltip configurations to Chart object
 */
public abstract class ChartWithTooltip extends Chart implements HasTooltip{

    @Override
    public void setTooltipEvents(Event... events) {
        String[] eventsString = new String[events.length];
        for(int i=0;i<events.length;i++){
            eventsString[i] = events[i].getType();
        }
        options.setArrayProperty(TOOLTIPEVENTS, eventsString);
    }

    @Override
    public void setTooltipFillColor(String color) {
        options.setProperty(TOOLTIPFILLCOLOR, color);
    }

    @Override
    public void showTooltips(boolean show) {
        if(!show)
            options.clearProperty(SHOWTOOLTIPS);
        else
            options.setProperty(SHOWTOOLTIPS, show);
    }

    @Override
    public void setTooltipFontFamily(String fontFamily) {
        options.setProperty(TOOLTIPFONTFAMILY, fontFamily);
    }

    @Override
    public void setTooltipFontSize(int fontSize) {
        options.setProperty(TOOLTIPFONTSIZE, fontSize);
    }

    @Override
    public void setTooltipFontStyle(Style.FontStyle fontStyle) {
        options.setProperty(TOOLTIPFONTSTYLE, fontStyle.getCssName());
    }

    @Override
    public void setTooltipFontColor(String color) {
        options.setProperty(TOOLTIPFONTCOLOR, color);
    }

    @Override
    public void setTooltipTitleFontFamily(String fontFamily) {
        options.setProperty(TOOLTIPTITLEFONTFAMILY, fontFamily);
    }

    @Override
    public void setTooltipTitleFontSize(int size) {
        options.setProperty(TOOLTIPTITLEFONTSIZE, size);
    }

    @Override
    public void setTooltipTitleFontStyle(Style.FontStyle style) {
        options.setProperty(TOOLTIPTITLEFONTSTYLE, style.getCssName());
    }

    @Override
    public void setTooltipTitleFontColor(String color) {
        options.setProperty(TOOLTIPTITLEFONTCOLOR, color);
    }

    @Override
    public void setTooltipYPadding(int padding) {
        options.setProperty(TOOLTIPYPADDING, padding);
    }

    @Override
    public void setTooltipXPadding(int padding) {
        options.setProperty(TOOLTIPXPADDING, padding);
    }

    @Override
    public void setCornerRadius(int radius) {
        options.setProperty(TOOLTIPCORNERRADIUS, radius);
    }

    @Override
    public void setTooltipCaretSize(int size) {
        options.setProperty(TOOLTIPCARETSIZE, size);
    }

    @Override
    public void setTooltipXOffset(int offset) {
        options.setProperty(TOOLTIPXOFFSET, offset);
    }

    @Override
    public void setTooltipTemplate(String template) {
        options.setProperty(TOOLTIPTEMPLATE, template);
    }

    @Override
    public void setMultiTooltipTemplate(String template) {
        options.setProperty(MULTITOOLTIPTEMPLATE, template);
    }
}
