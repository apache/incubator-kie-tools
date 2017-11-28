package org.dashbuilder.renderer.chartjs.lib.options;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;

/**
 * Interface describes options available for configuring tooltips for Chart
 */
public interface HasTooltip {

    final String SHOWTOOLTIPS = "showTooltips";
    final String TOOLTIPEVENTS = "tooltipEvents";
    final String TOOLTIPFILLCOLOR = "tooltipFillColor";
    final String TOOLTIPFONTFAMILY = "tooltipFontFamily";
    final String TOOLTIPFONTSIZE = "tooltipFontSize";
    final String TOOLTIPFONTSTYLE = "tooltipFontStyle";
    final String TOOLTIPFONTCOLOR = "tooltipFontColor";
    final String TOOLTIPTITLEFONTFAMILY = "tooltipTitleFontFamily";
    final String TOOLTIPTITLEFONTSIZE = "tooltipTitleFontSize";
    final String TOOLTIPTITLEFONTSTYLE = "tooltipTitleFontStyle";
    final String TOOLTIPTITLEFONTCOLOR = "tooltipTitleFontColor";
    final String TOOLTIPYPADDING = "tooltipYPadding";
    final String TOOLTIPXPADDING = "tooltipXPadding";
    final String TOOLTIPCARETSIZE = "tooltipCaretSize";
    final String TOOLTIPCORNERRADIUS = "tooltipCornerRadius";
    final String TOOLTIPXOFFSET = "tooltipXOffset";
    final String TOOLTIPTEMPLATE = "tooltipTemplate";
    final String MULTITOOLTIPTEMPLATE = "multiTooltipTemplate";

    /**
     * Specify event on which tooltip should be shown
     * By default {@link com.google.gwt.user.client.Event#ONMOUSEMOVE},
     * {@link com.google.gwt.user.client.Event#ONTOUCHSTART},
     * {@link com.google.gwt.user.client.Event#ONTOUCHMOVE}
     *
     */
    public void setTooltipEvents(Event...events);

    /**
     * Specify tooltip background color in String representation of CSS property (like red or #FFFFFF or rgb(0,0,0) etc)
     *
     * Default rgba(0,0,0,.8)
     * @param color
     */
    public void setTooltipFillColor(String color);

    /**
     * Indicates should tooltips been shown or not
     *
     * Default value is true
     * @param show
     */
    public void showTooltips(boolean show);

    /**
     * Specify font family which will be used for label
     * Default one "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"
     */
    public void setTooltipFontFamily(String fontFamily);

    /**
     * Specify font-size of tooltip label
     * @param fontSize
     */
    public void setTooltipFontSize(int fontSize);

    /**
     * Default is {@link com.google.gwt.dom.client.Style.FontStyle#NORMAL}
     * @param fontStyle
     */
    public void setTooltipFontStyle(Style.FontStyle fontStyle);

    /**
     * Color of tooltip font
     * Default is #FFF
     * @param color
     */
    public void setTooltipFontColor(String color);

    /**
     * Font-family for tooltip title
     *
     * Default one is "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"
     * @param fontFamily
     */
    public void setTooltipTitleFontFamily(String fontFamily);

    /**
     *
     *
     * Default 14
     * @param size
     */
    public void setTooltipTitleFontSize(int size);

    /**
     * Default is {@link com.google.gwt.dom.client.Style.FontStyle#FONT_WEIGHT_BOLD}
     * @param style
     */
    public void setTooltipTitleFontStyle(Style.FontStyle style);

    /**
     *
     * Default is #fff
     * @param color
     */
    public void setTooltipTitleFontColor(String color);

    /**
     * Default is 6
     * @param padding
     */
    public void setTooltipYPadding(int padding);

    /**
     * Default is 6
     * @param padding
     */
    public void setTooltipXPadding(int padding);

    /**
     *
     * Default is 6
     * @param radius
     */
    public void setCornerRadius(int radius);

    /**
     * Default 8
     * @param size
     */
    public void setTooltipCaretSize(int size);

    /**
     * Default 10
     * @param offset
     */
    public void setTooltipXOffset(int offset);

    /**
     * Default <code>"<%if (label){%><%=label%>: <%}%><%= value %>"</code>
     * @param template
     */
    public void setTooltipTemplate(String template);

    /**
     * Default <code>"<%= value %>"</code>
     * @param template
     */
    public void setMultiTooltipTemplate(String template);
}
