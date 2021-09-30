package org.dashbuilder.client.widgets.dataset.event;

import org.dashbuilder.common.client.event.ContextualEvent;

/**
 * <p>CDI event when a tab is changed.</p>
 *
 * @since 0.4.0
 */
public class TabChangedEvent extends ContextualEvent {

    private String tabId;

    public TabChangedEvent(Object context, String tabId) {
        super(context);
        this.tabId = tabId;
    }

    public String getTabId() {
        return tabId;
    }

    @Override
    public String toString() {
        return "TabChangedEvent [Context=" + getContext().toString() + "]";
    }

}
