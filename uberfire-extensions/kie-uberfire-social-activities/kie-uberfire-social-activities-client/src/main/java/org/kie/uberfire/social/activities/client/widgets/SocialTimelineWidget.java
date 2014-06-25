package org.kie.uberfire.social.activities.client.widgets;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Legend;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;

public class SocialTimelineWidget extends Composite {

    @UiField
    FlowPanel titlePanel;

    @UiField
    FluidContainer itemsPanel;

    public void init( String title,
                      SocialEventType type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        titlePanel.add( new Legend( title ) );
        if ( type != null ) {
            createSocialItemsWidget( type );
        } else {
            createMock();
        }
    }

    private void createMock() {
        SocialActivitiesEvent event = new SocialActivitiesEvent( new SocialUser( "Dan" ), new SocialEventType() {
            @Override
            public String name() {
                return "Process Changed";
            }
        }, new Date(), "Process 13 has changed", "\"Added another service task to this process - for the new data we need to capture from customers.\"" );
        FluidRow firstRow = SocialItemExpandedWidget.createFirstRow( event );
        itemsPanel.add( firstRow );
        itemsPanel.add( SocialItemExpandedWidget.createSecondRow( event ) );
    }

    private void createSocialItemsWidget( SocialEventType type ) {
        MessageBuilder.createCall( new RemoteCallback<List<SocialActivitiesEvent>>() {
            public void callback( List<SocialActivitiesEvent> events ) {
                for ( SocialActivitiesEvent event : events ) {
                    SimpleItemWidget itemWidget = GWT.create( SimpleItemWidget.class );
                    itemsPanel.add( itemWidget.createRow( event, null ) );
                }
            }
        }, SocialTimeLineRepositoryAPI.class ).getLastEventTimeline( type.name(), new HashMap() );
    }


    interface MyUiBinder extends UiBinder<Widget, SocialTimelineWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
