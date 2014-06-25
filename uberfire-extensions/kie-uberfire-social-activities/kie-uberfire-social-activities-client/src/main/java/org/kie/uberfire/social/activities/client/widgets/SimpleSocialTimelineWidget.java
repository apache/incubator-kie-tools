package org.kie.uberfire.social.activities.client.widgets;

import java.util.List;

import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Legend;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.service.SocialTimelineRulesQueryAPI;
import org.kie.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;

public class SimpleSocialTimelineWidget extends Composite {

    @UiField
    FlowPanel title;

    @UiField
    FluidContainer itemsPanel;

    private final SocialEventType type;
    private PlaceManager placeManager;

    private SocialPaged socialPage;

    public SimpleSocialTimelineWidget( String title,
                                       SocialEventType type,
                                       PlaceManager placeManager ) {
        this.type = type;
        this.placeManager = placeManager;
        socialPage = new SocialPaged( 2 );
        initWidget( uiBinder.createAndBindUi( this ) );
        setup( title, type );
    }

    private void setup( String titleText,
                        SocialEventType type ) {
        title.add( new Legend( titleText ) );
        createSocialItemsWidget();
    }

    private void createSocialItemsWidget() {

        MessageBuilder.createCall( new RemoteCallback<List<SocialActivitiesEvent>>() {
            public void callback( List<SocialActivitiesEvent> events ) {
                System.out.println( "" );
            }
        }, SocialTimelineRulesQueryAPI.class ).execute();

        MessageBuilder.createCall( new RemoteCallback<PagedSocialQuery>() {
            public void callback( PagedSocialQuery paged ) {
                socialPage = paged.socialPaged();
                for ( SocialActivitiesEvent event : paged.socialEvents() ) {
                    FluidRow row = SimpleItemWidget.createRow( event, placeManager );
                    itemsPanel.add( row );
                }
                if ( socialPage.canIGoBackward() ) {
                    itemsPanel.add( moreEventsButton( "<" ) );
                }
                if ( socialPage.canIGoForward() ) {
                    itemsPanel.add( moreEventsButton( ">" ) );
                }
            }
        }, SocialTypeTimelinePagedRepositoryAPI.class ).getEventTimeline( type.name(), socialPage );

    }

    private Button moreEventsButton( final String text ) {

        Button button = GWT.create( Button.class );
        button.setText( text );

        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                itemsPanel.clear();
                if ( text.equalsIgnoreCase( "<" ) ) {
                    socialPage.backward();
                } else {
                    socialPage.forward();
                }
                createSocialItemsWidget();
            }
        } );
        return button;
    }

    interface MyUiBinder extends UiBinder<Widget, SimpleSocialTimelineWidget> {

    }

    static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
