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

import java.util.Collection;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
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
import org.kie.workbench.common.screens.home.client.model.CarouselEntry;
import org.kie.workbench.common.screens.home.client.model.HomeModel;
import org.kie.workbench.common.screens.home.client.model.Section;
import org.kie.workbench.common.screens.home.client.model.SectionEntry;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;
import org.kie.workbench.common.screens.home.client.widgets.carousel.CarouselEntryWidget;
import org.kie.workbench.common.screens.home.client.widgets.carousel.CarouselWidget;
import org.kie.workbench.common.screens.home.client.widgets.sections.VerticalSectionWidget;
import org.uberfire.backend.group.Group;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.security.Identity;
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
    private Identity identity;

    @UiField
    CarouselWidget carousel;

    @UiField
    HeadingElement title;

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
        title.setInnerText( SafeHtmlUtils.htmlEscape( model.getTitle() ) );

        //Add Sections
        for ( Section section : model.getSections() ) {
            if ( authzManager.authorize( section,
                                         identity ) ) {
                final VerticalSectionWidget vs = new VerticalSectionWidget();
                vs.setHeaderText( section.getHeading() );
                for ( SectionEntry sectionEntry : section.getEntries() ) {
                    vs.add( makeSectionEntry( sectionEntry.getCaption(),
                                              sectionEntry.getOnClickCommand() ) );
                }
                this.columns.add( vs );
            }
        }
    }

    @Override
    public void setGroups( final Collection<Group> groups ) {
        final VerticalSectionWidget vs = new VerticalSectionWidget();
        vs.setHeaderText( "Groups:" );
        for ( Group group : groups ) {
            if ( authzManager.authorize( group,
                                         identity ) ) {
                vs.add( makeSectionEntry( group.getName() ) );
            }
        }
        this.columns.add( vs );
    }

    private CarouselEntryWidget makeCarouselEntry( final String heading,
                                                   final String subHeading,
                                                   final String imageUri,
                                                   final boolean active ) {
        final CarouselEntryWidget item = new CarouselEntryWidget();
        item.setHeading( SafeHtmlUtils.fromString( heading ) );
        item.setSubHeading( SafeHtmlUtils.fromString( subHeading ) );
        item.setImageUri( UriUtils.fromString( imageUri ) );
        item.setActive( active );
        return item;
    }

    private Widget makeSectionEntry( final String caption ) {
        return makeSectionEntry( caption, new Command() {

            @Override
            public void execute() {
            }

        } );
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
