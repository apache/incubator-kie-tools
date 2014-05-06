package org.uberfire.client.workbench.widgets.panel;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * An UberFire panel that can contain zero or one parts.
 */
public class SimplePanel
extends Composite {

    private final FlowPanel container = new FlowPanel();

    private PartDefinition partDefinition;

    public SimplePanel() {
        initWidget( container );
    }

    /**
     * Makes the given view fill this panel. Replaces the existing view occupying this panel, if any.
     * 
     * @param part the view this panel should contain. Must not be null.
     */
    public void setPart( final WorkbenchPartPresenter.View part ) {
        clear();
        this.partDefinition = part.getPresenter().getDefinition();
        container.add( part );
    }

    /**
     * Removes the view from this panel, making it empty.
     */
    public void clear() {
        partDefinition = null;
        container.clear();
    }

    /**
     * Returns the PartDefinition of the part that currently occupies this panel.
     * 
     * @return the PartDefinition of the part in this panel, or null if the panel is empty.
     */
    public PartDefinition getPartDefinition() {
        return partDefinition;
    }
}
