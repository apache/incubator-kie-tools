package org.drools.workbench.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
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

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( "The KIE Knowledge Development Cycle" );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Author",
                                                              "Formalize your Business Knowledge",
                                                              url + "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Deploy",
                                                              "Learn how to configure your environment",
                                                              url + "/images/flowers.jpg" ) );
        final Section s1 = new Section( "Discover and Author:" );
        s1.addEntry( ModelUtils.makeSectionEntry( "Author",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.drools.workbench.client.perspectives.AuthoringPerspective" );
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
                                                  } ) );
        s2.addEntry( ModelUtils.makeSectionEntry( "Assets Repository",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" );
                                                      }
                                                  } ) );
        model.addSection( s2 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
