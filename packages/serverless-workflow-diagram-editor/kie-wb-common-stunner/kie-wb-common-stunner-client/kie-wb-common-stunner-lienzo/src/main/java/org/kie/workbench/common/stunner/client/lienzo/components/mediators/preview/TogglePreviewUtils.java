package org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.tools.client.event.MouseEventUtil;

public class TogglePreviewUtils {

    static final double PREVIEW_MARGIN = 25d;

    public static TogglePreviewEvent buildEvent(LienzoBoundsPanel lienzoBoundsPanel,
                                                TogglePreviewEvent.EventType eventType) {
        final int absoluteLeft = MouseEventUtil.getAbsoluteLeft(lienzoBoundsPanel.getElement());
        final int absoluteTop = MouseEventUtil.getAbsoluteTop(lienzoBoundsPanel.getElement());
        final int width = lienzoBoundsPanel.getWidePx();
        final int height = lienzoBoundsPanel.getHighPx() - (int) (PREVIEW_MARGIN * 2);

        return new TogglePreviewEvent(absoluteLeft,
                                      absoluteTop,
                                      width,
                                      height,
                                      eventType);
    }

    public static boolean IsPreviewAvailable(ScrollablePanel scrollablePanel) {
        final double internalWidth = scrollablePanel.calculateInternalScrollPanelWidth();
        final double internalHeight = scrollablePanel.calculateInternalScrollPanelHeight();

        return internalWidth != 1 || internalHeight != 1;
    }
}
