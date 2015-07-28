/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.home.client.widgets.home;

import java.util.Iterator;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;
import org.kie.workbench.common.screens.home.client.widgets.carousel.CarouselEntryWidget;
import org.kie.workbench.common.screens.home.client.widgets.carousel.CarouselWidget;
import org.kie.workbench.common.screens.home.client.widgets.sections.VerticalSectionWidget;
import org.kie.workbench.common.screens.home.model.CarouselEntry;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.Section;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

public class HomeViewImpl extends Composite
        implements
        HomePresenter.HomeView {

    interface HomeViewImplBinder
            extends
            UiBinder<Widget, HomeViewImpl> {

    }

    private static HomeViewImplBinder uiBinder = GWT.create( HomeViewImplBinder.class );

    private HomePresenter presenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private RuntimeAuthorizationManager authzManager;

    @Inject
    private User identity;

    @UiField
    CarouselWidget carousel;

    @UiField
    Heading title;

    @UiField
    HorizontalPanel columns;

    public HomeViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final HomePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setModel( final HomeModel model ) {
        if ( model == null ) {
            return;
        }
        //Add Carousel entries
        for ( int index = 0; index < model.getCarouselEntries().size(); index++ ) {
            final CarouselEntry entry = model.getCarouselEntries().get( index );
            carousel.addCarouselEntry( makeCarouselEntry( entry.getHeading(),
                                                          entry.getSubHeading(),
                                                          entry.getImageUrl(),
                                                          ( index == 0 ) ) );
        }

        //Title
        title.setText( SafeHtmlUtils.htmlEscape( model.getTitle() ) );

        //Add Sections
        for ( Section section : model.getSections() ) {
            if ( authzManager.authorize( section,
                                         identity ) ) {
                if ( doesSectionContainAuthorizedEntries( section ) ) {
                    final VerticalSectionWidget vs = new VerticalSectionWidget();
                    vs.setHeaderText( section.getHeading() );
                    for ( SectionEntry sectionEntry : section.getEntries() ) {
                        if ( authzManager.authorize( sectionEntry, identity ) ) {
                            vs.add( makeSectionEntry( sectionEntry.getCaption(),
                                                      sectionEntry.getOnClickCommand() ) );
                        }
                    }
                    vs.addStyleName( "well" );
                    this.columns.add( vs );
                }
            }
        }

        int cols = columns.getWidgetCount();
        int colSize = ( cols > 0 ? ( 1140 / cols ) : 1140 );

        int index = 0;
        for ( Iterator<Widget> it = columns.iterator(); it.hasNext(); ) {
            Widget widget = it.next();
            widget.setWidth( ( index < cols - 1 ? colSize - 4 : colSize ) + "px" );
            index++;
        }
    }

    private boolean doesSectionContainAuthorizedEntries( final Section section ) {
        for ( SectionEntry sectionEntry : section.getEntries() ) {
            if ( authzManager.authorize( sectionEntry, identity ) ) {
                return true;
            }
        }
        return false;
    }

    private CarouselEntryWidget makeCarouselEntry( final String heading,
                                                   final String subHeading,
                                                   final String imageUri,
                                                   final boolean active ) {
        final CarouselEntryWidget item = GWT.create( CarouselEntryWidget.class );
        item.setHeading( SafeHtmlUtils.fromString( heading ) );
        item.setSubHeading( SafeHtmlUtils.fromString( subHeading ) );
        item.setImageUri( UriUtils.fromString( imageUri ) );
        item.setActive( active );
        return item;
    }

    private Widget makeSectionEntry( final String caption,
                                     final Command command ) {
        final Anchor anchor = new Anchor( caption );
        anchor.setStyleName( HomeResources.INSTANCE.CSS().sectionBody() );
        anchor.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                command.execute();
            }

        } );
        return anchor;
    }

}
