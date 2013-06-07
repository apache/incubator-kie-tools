package org.kie.workbench.common.screens.home.model;

import org.kie.commons.validation.PortablePreconditions;

/**
 * An Entry in the Carousel
 */
public class CarouselEntry {

    private final String heading;
    private final String subHeading;
    private final String imageUrl;

    public CarouselEntry( final String heading,
                          final String subHeading,
                          final String imageUrl ) {
        this.heading = PortablePreconditions.checkNotNull( "heading",
                                                           heading );
        this.subHeading = PortablePreconditions.checkNotNull( "subHeading",
                                                              subHeading );
        this.imageUrl = PortablePreconditions.checkNotNull( "imageUrl",
                                                            imageUrl );
    }

    public String getHeading() {
        return heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
