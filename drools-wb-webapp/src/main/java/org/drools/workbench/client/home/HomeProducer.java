/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.kie.workbench.common.screens.library.client.monitor.LibraryMonitor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.uberfire.workbench.model.ActivityResourceType.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected LibraryMonitor libraryMonitor;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( "The KIE Knowledge Development Cycle" );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Author",
                                                              "Formalize your Business Knowledge",
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Deploy",
                                                              "Learn how to configure your environment",
                                                              url + "/images/HandHome.jpg" ) );

        final SectionEntry s1 = ModelUtils.makeSectionEntry( "Discover and Author:" );

        final DefaultPlaceRequest libraryPlaceRequest = new DefaultPlaceRequest( LIBRARY );
        final PlaceRequest authoringPlaceRequest = new ConditionalPlaceRequest( AUTHORING ).when( p -> libraryMonitor.thereIsAtLeastOneProjectAccessible() ).orElse( libraryPlaceRequest );
        s1.addChild( ModelUtils.makeSectionEntry( "Author",
                () -> placeManager.goTo( authoringPlaceRequest ),
                AUTHORING, PERSPECTIVE ) );

        model.addSection( s1 );

        final SectionEntry s2 = ModelUtils.makeSectionEntry( "Deploy:" );

        s2.addChild( ModelUtils.makeSectionEntry( "Manage and Deploy Your Assets",
                () -> placeManager.goTo( DROOLS_ADMIN ),
                DROOLS_ADMIN, PERSPECTIVE ) );

        s2.addChild( ModelUtils.makeSectionEntry( "Assets Repository",
                () -> placeManager.goTo( GUVNOR_M2REPO ),
                GUVNOR_M2REPO, PERSPECTIVE ) );

        model.addSection( s2 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
