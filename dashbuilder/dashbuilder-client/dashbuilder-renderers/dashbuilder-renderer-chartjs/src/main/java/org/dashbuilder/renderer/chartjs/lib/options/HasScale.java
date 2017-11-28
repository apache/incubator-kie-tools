package org.dashbuilder.renderer.chartjs.lib.options;

import com.google.gwt.dom.client.Style;

public interface HasScale {

    final String SHOWSCALE = "showScale";
    final String SCALEOVERRIDE = "scaleOverride";
    final String SCALESTEPS = "scaleSteps";
    final String SCALESTEPWIDTH = "scaleStepWidth";
    final String SCALESTARTVALUE = "scaleStartValue";
    final String SCALELINECOLOR = "scaleLineColor";
    final String SCALELINEWIDTH = "scaleLineWidth";
    final String SCALESHOWLABELS = "scaleShowLabels";
    final String SCALELABEL = "scaleLabel";
    final String SCALEINTEGERSONLY = "scaleIntegersOnly";
    final String SCALEBEGINATZERO = "scaleBeginAtZero";
    final String SCALEFONTFAMILY = "scaleFontFamily";
    final String SCALEFONTSIZE = "scaleFontSize";
    final String SCALEFONTSTYLE = "scaleFontStyle";
    final String SCALEFONTCOLOR = "scaleFontColor";

    /**
     * Default true
     */
    public void showScale(boolean scale);

    /**
     * Default false
     */
    public void setScaleOverride(boolean override);

    /**
     * Scale steps
     * Default null
     */
    public void setScaleSteps(int steps);

    /**
     * Default null
     */
    public void setScaleStepWidth(int width);

    /**
     * Double
     */
    public void setScaleStartValue(double startValue);

    /**
     *
     */
    public void setScaleLineColor(String color);

    /**
     * Default 1
     */
    public void setScaleLineWidth(int pixelWidth);

    /**
     * true
     */
    public void setScaleShowLabels(boolean showLabels);

    /**
     * Default <code>"<%=value%>"</code>
     */
    public void setScaleLabel(String template);

    /**
     * Default true
     */
    public void setScaleIntegersOnly(boolean only);

    /**
     * Default false
     */
    public void setScaleBeginAtZero(boolean beginAtZero);

    /**
     * Default "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"
     */
    public void setScaleFontFamily(String fontFamily);

    /**
     * Default 12
     */
    public void setScaleFontSize(int size);

    /**
     * Default normal
     */
    public void setScaleFontStyle(Style.FontStyle style);

    /**
     * Default #666
     */
    public void setScaleFontColor(String color);

}
