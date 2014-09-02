package org.kie.uberfire.social.activities.client.widgets.timeline.regular;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.kie.uberfire.social.activities.client.widgets.item.SimpleItemWidget;
import org.kie.uberfire.social.activities.client.widgets.item.SocialItemExpandedWidget;
import org.kie.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.item.model.SocialItemExpandedWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.RecentUpdatesModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelineRulesQueryAPI;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

public class SocialTimelineWidget extends Composite {

    @UiField
    FlowPanel titlePanel;

    @UiField
    FluidContainer itemsPanel;

    public void init( SocialTimelineWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        titlePanel.add( new Legend( model.getTitle() ) );
        if ( model.isDroolsQuery() ) {
            createDroolsQuerySocialItemsWidget( model );
        } else {
            createRegularQuerySocialItemsWidget( model );
        }
    }

    private void createRegularQuerySocialItemsWidget( final SocialTimelineWidgetModel model ) {

        MessageBuilder.createCall( new RemoteCallback<List<SocialActivitiesEvent>>() {
            public void callback( List<SocialActivitiesEvent> events ) {
                for ( final SocialActivitiesEvent event : events ) {
                    if ( event.hasLink() ) {
                        MessageBuilder.createCall( new RemoteCallback<Path>() {
                            public void callback( Path path ) {
                                SimpleItemWidgetModel rowModel = new SimpleItemWidgetModel( model, event.getTimestamp(), event.getLinkLabel(), path, event.getAdicionalInfos() );
                                FluidRow row = SimpleItemWidget.createRow( rowModel );
                                itemsPanel.add( row );
                            }
                        }, VFSService.class ).get( event.getLinkTarget() );
                    } else {
                        SimpleItemWidgetModel rowModel = new SimpleItemWidgetModel( model, event.getTimestamp(), event.getDescription(), event.getAdicionalInfos() );
                        FluidRow row = SimpleItemWidget.createRow( rowModel );
                        itemsPanel.add( row );
                    }
                }
            }
        }, SocialTimeLineRepositoryAPI.class ).getLastEventTimeline( model.getSocialEventType().name() );
    }

    private void createDroolsQuerySocialItemsWidget( final SocialTimelineWidgetModel model ) {

        MessageBuilder.createCall( new RemoteCallback<List<SocialActivitiesEvent>>() {
            public void callback( List<SocialActivitiesEvent> events ) {

                RecentUpdatesModel recentUpdatesModel = RecentUpdatesModel.generate( events );
                Map<String, List<UpdateItem>> updateItems = recentUpdatesModel.getUpdateItems();
                for ( final String fileName : updateItems.keySet() ) {
                    SocialItemExpandedWidget.createItem(new SocialItemExpandedWidgetModel(itemsPanel, fileName, recentUpdatesModel.getUpdateItems( fileName ), model ));
                }

            }
        }, SocialTimelineRulesQueryAPI.class ).executeSpecificRule( model.getGlobals(), model.getDrlName(), model.getMaxResults() );
    }

    private void createMockExpandedWidget() {
        //TODO implement it with real data
//        SocialActivitiesEvent event = new SocialActivitiesEvent( new SocialUser( "Dan" ), new SocialEventType() {
//            @Override
//            public String name() {
//                return "Process Changed";
//            }
//        }, new Date() ).withAdicionalInfo( "Process 13 has changed", "\"Added another service task to this process - for the new data we need to capture from customers.\"" );
//        FluidRow firstRow = SocialItemExpandedWidget.createFirstRow( event );
//        itemsPanel.add( firstRow );
//        itemsPanel.add( SocialItemExpandedWidget.createSecondRow( event ) );
    }

    interface MyUiBinder extends UiBinder<Widget, SocialTimelineWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
