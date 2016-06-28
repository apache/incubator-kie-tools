package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.AlignAndDistribute;
import com.ait.lienzo.client.core.types.Point2D;

import java.util.Set;

/**
 * The Align and Distribute control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well. So common logic
 * can be shared to provide drag and the operations support and attached to the necessary event handler type.
 */
public interface AlignAndDistributeControl {

    void refresh();

    void refresh( boolean transforms, boolean attributes );

    boolean isDraggable();

    void dragStart();

    void dragEnd();

    boolean dragAdjust(Point2D dxy);

    void remove();

    Set<AlignAndDistribute.DistributionEntry> getHorizontalDistributionEntries();

    Set<AlignAndDistribute.DistributionEntry> getVerticalDistributionEntries();

    double getLeft();

    double getRight();

    double getTop();

    double getBottom();

    double getHorizontalCenter();

    double getVerticalCenter();

    boolean isIndexed();

    void setIndexed(boolean indexed);

    void updateIndex();

}
