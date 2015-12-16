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

import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.shared.security.AppRoles;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.Section;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private static String[] PERMISSIONS_ADMIN = new String[]{ AppRoles.ADMIN.getName() };

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

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
        final Section s1 = new Section( "Discover and Author:" );
        s1.addEntry( ModelUtils.makeSectionEntry( "Author",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "AuthoringPerspective" );
                                                      }
                                                  } ) );
        model.addSection( s1 );

        final Section s2 = new Section( "Deploy:" );
        s2.addEntry( ModelUtils.makeSectionEntry( "Manage and Deploy Your Assets",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.drools.workbench.client.perspectives.AdministrationPerspective" );
                                                      }
                                                  },
                                                  Arrays.asList( PERMISSIONS_ADMIN ) ) );
        s2.addEntry( ModelUtils.makeSectionEntry( "Assets Repository",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" );
                                                      }
                                                  },
                                                  Arrays.asList( PERMISSIONS_ADMIN ) ) );
        model.addSection( s2 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
