package org.drools.workbench.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.workbench.common.screens.home.client.model.CarouselEntry;
import org.kie.workbench.common.screens.home.client.model.HomeModel;
import org.kie.workbench.common.screens.home.client.model.Section;
import org.kie.workbench.common.screens.home.client.model.SectionEntry;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

/**
 * Producer method for the Home Page content
 */
public class HomeProducer {

    private HomeModel model = new HomeModel();

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        model.addCarouselEntry( makeCarouselEntry( "Author",
                                                   "Formalize your Business Knowledge",
                                                   "/images/flowers.jpg" ) );
        model.addCarouselEntry( makeCarouselEntry( "Deploy",
                                                   "Learn how to configure your environment",
                                                   "/images/flowers.jpg" ) );
        final Section s1 = new Section( "Discover and Author:" );
        s1.addEntry( makeSectionEntry( "Author",
                                       new Command() {

                                           @Override
                                           public void execute() {
                                               placeManager.goTo( "org.drools.workbench.client.perspectives.AuthoringPerspective" );
                                           }
                                       } ) );
        model.addSection( s1 );

        final Section s2 = new Section( "Deploy:" );
        s2.addEntry( makeSectionEntry( "Manage and Deploy Your Assets",
                                       new Command() {

                                           @Override
                                           public void execute() {
                                               placeManager.goTo( "org.drools.workbench.client.perspectives.AdministrationPerspective" );
                                           }
                                       } ) );
        s2.addEntry( makeSectionEntry( "Assets Repository",
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

    private CarouselEntry makeCarouselEntry( final String heading,
                                             final String subHeading,
                                             final String imageUri ) {
        final CarouselEntry item = new CarouselEntry( heading,
                                                      subHeading,
                                                      imageUri );
        return item;
    }

    private SectionEntry makeSectionEntry( final String caption,
                                           final Command command ) {
        final SectionEntry entry = new SectionEntry( caption,
                                                     command );
        return entry;
    }

}
