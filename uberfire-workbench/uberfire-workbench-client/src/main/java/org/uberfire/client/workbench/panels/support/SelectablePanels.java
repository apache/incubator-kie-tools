package org.uberfire.client.workbench.panels.support;

import org.uberfire.workbench.model.PanelDefinition;

/**
 * Decorates {@link org.uberfire.client.workbench.panels.WorkbenchPanelPresenter}'s so they get notified
 * when their containing parts have been selected.<p/>
 * This allows panels that support multiple parts to react to place changes and toggle the proper DOM structures.
 *
 * @see org.uberfire.client.workbench.AbstractPanelManagerImpl#onSelectPlaceEvent(org.uberfire.client.workbench.events.SelectPlaceEvent)
 */
public interface SelectablePanels {
    void onSelect(PanelDefinition panel);
}
