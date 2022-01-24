package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.widget.panel.IsResizable;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.ResizeCallback;
import com.ait.lienzo.client.widget.panel.ResizeObserver;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

/**
 * Automatically fits its size to the parent's one.
 */
public class LienzoResizablePanel
        extends LienzoPanelDelegate<LienzoResizablePanel>
        implements IsResizable {

    private final LienzoFixedPanel panel;
    private ResizeObserver resizeObserver;
    private final ResizeCallback m_resizeCallback;

    public static LienzoResizablePanel newPanel() {
        LienzoFixedPanel panel = LienzoFixedPanel.newPanel();
        return new LienzoResizablePanel(panel);
    }

    public LienzoResizablePanel(LienzoFixedPanel panel) {
        this.panel = panel;
        this.m_resizeCallback = e -> fitToParentSize();
    }

    public void initResizeObserver() {
        if (null == resizeObserver &&
                null != panel.getElement().parentNode &&
                null != panel.getElement().parentNode.parentNode) {
            resizeObserver = new ResizeObserver(m_resizeCallback);
            resizeObserver.observe((Element) panel.getElement().parentNode.parentNode);
        }
    }

    @Override
    public void onResize() {
        initResizeObserver();
    }

    @Override
    protected LienzoPanel getPanel() {
        return panel;
    }

    @Override
    public void destroy() {
        resizeObserver.disconnect();
        resizeObserver = null;
        super.destroy();
    }

    private void fitToParentSize() {
        if (null != panel.getElement().parentNode &&
                null != panel.getElement().parentNode.parentNode) {
            HTMLDivElement parent = (HTMLDivElement) panel.getElement().parentNode.parentNode;
            int offsetWidth = parent.offsetWidth;
            int offsetHeight = parent.offsetHeight;
            if (offsetWidth > 0 && offsetHeight > 0) {
                panel.setPixelSize(offsetWidth, offsetHeight);
            }
        }
    }
}
