package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class SocialHomePageSideView extends Composite implements SocialHomePageSidePresenter.View {


    interface SocialHomePageSideViewBinder
            extends
            UiBinder<Widget, SocialHomePageSideView> {
    }

    private static SocialHomePageSideViewBinder uiBinder = GWT.create( SocialHomePageSideViewBinder.class );

    @UiField
    FlowPanel panel;

    private SocialHomePageSidePresenter presenter = null;

    @Inject
    PlaceManager placeManager;

    @AfterInitialization
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final SocialHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget( SimpleSocialTimelineWidgetModel model ) {
        panel.clear();
        panel.add( new SimpleSocialTimelineWidget( model ) );
    }

}
