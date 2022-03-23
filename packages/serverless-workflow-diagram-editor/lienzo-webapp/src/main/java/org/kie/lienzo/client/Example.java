package org.kie.lienzo.client;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import elemental2.dom.HTMLDivElement;

public interface Example {

    void init(LienzoPanel panel, HTMLDivElement topDiv);

    default LienzoPanel createPanel() {
        ScrollablePanel panel = ScrollablePanel.newPanel(new BoundsProviderFactory.PrimitivesBoundsProvider());
        panel.addResizeEventListener(evt -> onResize());
        return panel;
    }

    void run();

    void onResize();

    String getTitle();

    int getWidthOffset();

    int getHeightOffset();

    void destroy();
}
