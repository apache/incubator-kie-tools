package org.kie.uberfire.social.activities.client.widgets.timeline.simple;

import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.client.widgets.item.SimpleItemWidget;
import org.kie.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserTimelinePagedRepositoryAPI;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.mvp.ParameterizedCommand;

public class SimpleSocialTimelineWidget extends Composite {

    private SimpleSocialTimelineWidgetModel model;

    @UiField
    Container itemsPanel;

    @UiField
    Fieldset pagination;

    public SimpleSocialTimelineWidget( SimpleSocialTimelineWidgetModel model ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.model = model;
        setupPaginationLinks();
        refreshTimelineWidget();
    }

    private void refreshTimelineWidget() {
        itemsPanel.clear();
        createWidgets();
    }
    private void createWidgets() {
        pagination.clear();
        if ( model.isSocialTypeWidget() ) {
            createSociaTypelItemsWidget();
        } else {
            createUserTimelineItemsWidget();
        }
    }


    private void createUserTimelineItemsWidget() {
        MessageBuilder.createCall( new RemoteCallback<PagedSocialQuery>() {
            public void callback( PagedSocialQuery paged ) {
                createTimeline( paged );
            }
        }, SocialUserTimelinePagedRepositoryAPI.class ).getUserTimeline( model.getSocialUser(), model.getSocialPaged() );
    }

    private void createSociaTypelItemsWidget() {
        MessageBuilder.createCall( new RemoteCallback<PagedSocialQuery>() {
            public void callback( PagedSocialQuery paged ) {
                createTimeline( paged );
            }
        }, SocialTypeTimelinePagedRepositoryAPI.class ).getEventTimeline( model.getSocialEventType().name(), model.getSocialPaged() );
    }

    private void createTimeline( PagedSocialQuery paged ) {
        if ( thereIsNoEvents( paged ) ) {
            displayNoEvents();
        } else {
            displayEvents( paged );
        }

    }

    private void displayNoEvents() {
        pagination.add( new Paragraph( "There are no social events...yet!" ) );
    }

    private boolean thereIsNoEvents( PagedSocialQuery paged ) {
        return paged.socialEvents().isEmpty() && !paged.socialPaged().canIGoBackward();
    }

    private void displayEvents( PagedSocialQuery paged ) {
        model.updateSocialPaged( paged.socialPaged() );
        for ( final SocialActivitiesEvent event : paged.socialEvents() ) {
            if ( event.hasLink() ) {
                createSimpleWidgetWithFileLink( event );
            } else {
                createSimpleWidget( event );
            }
        }
        setupPaginationButtonsSocial();
    }

    private void createSimpleWidgetWithFileLink( final SocialActivitiesEvent event ) {
        MessageBuilder.createCall( new RemoteCallback<Path>() {
            public void callback( Path path ) {
                SimpleItemWidgetModel rowModel = new SimpleItemWidgetModel( model, event.getTimestamp(), event.getLinkLabel(), path, event.getAdicionalInfos() )
                            .withLinkCommand( model.getLinkCommand() );
                SimpleItemWidget item = GWT.create( SimpleItemWidget.class );
                item.init( rowModel );
                itemsPanel.add( item );
            }
        }, VFSService.class ).get( event.getLinkTarget() );
    }

    private void createSimpleWidget( SocialActivitiesEvent event ) {
        SimpleItemWidgetModel rowModel = new SimpleItemWidgetModel( model, event.getTimestamp(), event.getDescription(), event.getAdicionalInfos() );
        SimpleItemWidget item = GWT.create( SimpleItemWidget.class );
        item.init( rowModel );
        itemsPanel.add( item );
    }

    private void setupPaginationButtonsSocial() {
        NavList list = GWT.create( NavList.class );
        if ( canICreateLessLink() ) {
            list.add( model.getLess() );
        }
        if ( canICreateMoreLink() ) {
            list.add( model.getMore() );
        }
        if(canICreateLessLink()||canICreateMoreLink()){
            pagination.add( list );
        }
    }

    private boolean canICreateMoreLink() {
        return model.getSocialPaged().canIGoForward() && model.getMore() != null;
    }

    private boolean canICreateLessLink() {
        return model.getSocialPaged().canIGoBackward() && model.getLess() != null;
    }

    private void setupPaginationLinks() {
        if ( model.getLess() != null ) {
            createLessLink();
        }
        if ( model.getMore() != null ) {
            createMoreLink();
        }
    }

    private void createMoreLink() {
        model.getMore().addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getSocialPaged().forward();
                createWidgets();
            }
        } );
    }

    private void createLessLink() {
        model.getLess().addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                model.getSocialPaged().backward();
                refreshTimelineWidget();
            }
        } );
    }


    interface MyUiBinder extends UiBinder<Widget, SimpleSocialTimelineWidget> {

    }

    static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
