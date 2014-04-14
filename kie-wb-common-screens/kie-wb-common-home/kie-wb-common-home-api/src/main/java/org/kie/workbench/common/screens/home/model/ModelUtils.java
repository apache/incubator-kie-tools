package org.kie.workbench.common.screens.home.model;

import java.util.Collection;

import org.uberfire.mvp.Command;

/**
 * Utility methods for creating the HomeModel.
 */
public class ModelUtils {

    public static CarouselEntry makeCarouselEntry( final String heading,
                                                   final String subHeading,
                                                   final String imageUri ) {
        final CarouselEntry item = new CarouselEntry( heading,
                                                      subHeading,
                                                      imageUri );
        return item;
    }

    public static SectionEntry makeSectionEntry( final String caption,
                                                 final Command command ) {
        final SectionEntry entry = new SectionEntry( caption,
                                                     command );
        return entry;
    }

    public static SectionEntry makeSectionEntry( final String caption,
                                                 final Command command,
                                                 final Collection<String> roles ) {
        final SectionEntry entry = new SectionEntry( caption,
                                                     command );
        entry.setRoles( roles );
        return entry;
    }

}
