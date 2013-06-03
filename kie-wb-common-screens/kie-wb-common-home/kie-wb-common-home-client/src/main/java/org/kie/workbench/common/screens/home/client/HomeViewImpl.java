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
package org.kie.workbench.common.screens.home.client;

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
import org.kie.workbench.common.screens.home.client.carousel.Carousel;
import org.kie.workbench.common.screens.home.client.carousel.CarouselEntry;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;
import org.kie.workbench.common.screens.home.client.sections.VerticalSection;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

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

    @UiField
    Carousel carousel;

    @UiField
    HorizontalPanel columnsContainer;

    public HomeViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final HomePresenter presenter ) {
        this.presenter = presenter;
        carousel.addCarouselEntry( makeCarouselEntry( "Author",
                                                      "Formalize your Business Knowledge",
                                                      "/images/flowers.jpg",
                                                      true ) );
        carousel.addCarouselEntry( makeCarouselEntry( "Deploy",
                                                      "Learn how to configure your environment",
                                                      "/images/flowers.jpg",
                                                      false ) );

        final VerticalSection vs1 = new VerticalSection();
        vs1.setHeaderText( "Discover and Author:" );
        vs1.add( makeSectionEntry( "Author",
                                   new DefaultPlaceRequest( "org.drools.workbench.client.perspectives.AuthoringPerspective" ) ) );
        this.columnsContainer.add( vs1 );

        final VerticalSection vs2 = new VerticalSection();
        vs2.setHeaderText( "Deploy:" );
        vs2.add( makeSectionEntry( "Manage and Deploy Your Assets",
                                   new DefaultPlaceRequest( "org.drools.workbench.client.perspectives.AdministrationPerspective" ) ) );
        vs2.add( makeSectionEntry( "Assets Repository",
                                   new DefaultPlaceRequest( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" ) ) );
        this.columnsContainer.add( vs2 );
    }

    private CarouselEntry makeCarouselEntry( final String heading,
                                             final String subHeading,
                                             final String imageUri,
                                             final boolean active ) {
        final CarouselEntry item = new CarouselEntry();
        item.setHeading( SafeHtmlUtils.fromString( heading ) );
        item.setSubHeading( SafeHtmlUtils.fromString( subHeading ) );
        item.setImageUri( UriUtils.fromString( imageUri ) );
        item.setActive( active );
        return item;
    }

    private Widget makeSectionEntry( final String caption,
                                     final PlaceRequest place ) {
        final Anchor anchor = new Anchor( caption );
        anchor.setStyleName( HomeResources.INSTANCE.CSS().sectionBody() );
        anchor.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                placeManager.goTo( place );
            }

        } );
        return anchor;
    }

}
