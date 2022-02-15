package org.dashbuilder.client.widgets.dataset.event;

import java.util.List;

import org.dashbuilder.common.client.event.ContextualEvent;
import org.dashbuilder.dataset.def.DataColumnDef;

/**
 * <p>CDI event when data set definition instance columns have changed.</p>
 *
 * @since 0.4.0
 */
public class ColumnsChangedEvent extends ContextualEvent {

    private List<DataColumnDef> columns;

    public ColumnsChangedEvent(Object context, List<DataColumnDef> columns) {
        super(context);
        this.columns = columns;
    }

    public List<DataColumnDef> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return "ColumnsChangedEvent [Context=" + getContext().toString() + "]";
    }

}
