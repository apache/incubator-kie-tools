package org.kie.workbench.screens.explorer.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.kie.workbench.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.screens.explorer.client.resources.Resources;
import org.kie.workbench.screens.explorer.model.BreadCrumb;

import java.util.List;

/**
 * A Simple Bread Crumbs Widget
 */
public class BreadCrumbsWidget extends HorizontalPanel {

    private List<BreadCrumb> breadCrumbs;
    private ExplorerPresenter presenter;

    public BreadCrumbsWidget() {
        setStyleName( Resources.INSTANCE.CSS().breadCrumbs() );
    }

    public void setPresenter( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    public void setBreadCrumbs( final List<BreadCrumb> breadCrumbs ) {
        clear();
        this.breadCrumbs = breadCrumbs;
        for ( final BreadCrumb breadCrumb : breadCrumbs ) {
            if ( breadCrumbs.indexOf( breadCrumb ) < breadCrumbs.size() - 1 ) {
                add( makeLink( breadCrumb ) );
            } else {
                add( makeLabel( breadCrumb ) );
            }
            add( new Label( "/" ) );
        }
    }

    private IsWidget makeLabel( final BreadCrumb breadCrumb ) {
        return new Label( breadCrumb.getCaption() );
    }

    private IsWidget makeLink( final BreadCrumb breadCrumb ) {
        final Anchor link = new Anchor( breadCrumb.getCaption() );
        link.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                if ( presenter != null ) {
                    presenter.setContext( breadCrumb.getPath() );
                }
            }
        } );
        return link;
    }

}
