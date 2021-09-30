package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;
import org.dashbuilder.dataset.filter.DataSetFilter;

/**
 * <p>CDI event when data set definition's filter instance has changed.</p>
 *
 * @since 0.4.0
 */
public class FilterChangedEvent extends ContextualEvent {

    private DataSetFilter oldFilter;
    private DataSetFilter filter;

    public FilterChangedEvent(Object context, DataSetFilter oldFilter, DataSetFilter filter) {
        super(context);
        this.oldFilter = oldFilter;
        this.filter = filter;
    }

    public DataSetFilter getFilter() {
        return filter;
    }

    public DataSetFilter getOldFilter() {
        return oldFilter;
    }

    @Override
    public String toString() {
        return "FilterChangedEvent [Context=" + getContext().toString() + "]";
    }

}
