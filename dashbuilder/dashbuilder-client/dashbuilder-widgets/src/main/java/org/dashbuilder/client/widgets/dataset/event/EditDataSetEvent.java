package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.dataset.def.DataSetDef;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>CDI event to request a data set definition edition.</p>
 * 
 * @since 0.4.0
 */
public class EditDataSetEvent implements UberFireEvent {

    private final DataSetDef def;

    public EditDataSetEvent(final DataSetDef def) {
        this.def = def;
    }

    public DataSetDef getDef() {
        return def;
    }

    @Override
    public String toString() {
        return "EditDataSetEvent [UUID=" + def.getUUID() + "]";
    }

}
