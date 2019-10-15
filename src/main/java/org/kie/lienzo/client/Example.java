package org.kie.lienzo.client;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.HTMLDivElement;

public interface Example
{
    public void init(LienzoPanel panel, HTMLDivElement topDiv);

    public void run();

    public void onResize();

    public String getTitle();

    public int getWidthOffset();

    public int getHeightOffset();

    public void destroy();

}
