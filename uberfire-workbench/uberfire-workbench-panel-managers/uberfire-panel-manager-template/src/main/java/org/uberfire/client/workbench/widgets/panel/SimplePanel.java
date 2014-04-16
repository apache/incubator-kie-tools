package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

public class SimplePanel
        extends Composite {

    private FlowPanel container = new FlowPanel();

    private PartDefinition partDefinition;

    public SimplePanel() {
        initWidget( container );
    }


    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        container.add( part );
    }

    public void clear() {
        partDefinition = null;
        container.clear();
    }

}
