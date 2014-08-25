package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class SocialHomePageSideView extends Composite implements SocialHomePageSidePresenter.View {

    private final FlowPanel panel = new FlowPanel();

    private SocialHomePageSidePresenter presenter = null;

    @Inject
    PlaceManager placeManager;

    @AfterInitialization
    public void setup() {
        initWidget( panel );
    }

    @Override
    public void init( final SocialHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget( SimpleSocialTimelineWidgetModel model,
                             Command linkCommand ) {
        panel.clear();
        panel.add( new SimpleSocialTimelineWidget( model ) );
        panel.add(createMoreLink(linkCommand));
    }

    private NavLink createMoreLink( final Command linkCommand ) {
        NavLink link = new NavLink();
        link.setText("(more...)" );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                linkCommand.execute();
            }
        } );
        return link;
    }
}
