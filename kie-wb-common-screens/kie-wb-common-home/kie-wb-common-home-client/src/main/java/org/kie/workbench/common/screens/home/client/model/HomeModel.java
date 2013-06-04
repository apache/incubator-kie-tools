package org.kie.workbench.common.screens.home.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.commons.validation.PortablePreconditions;

/**
 * Model defining the Home Screen content
 */
public class HomeModel {

    private final List<CarouselEntry> carouselEntries = new ArrayList<CarouselEntry>();
    private List<Section> sections = new ArrayList<Section>();

    public void addCarouselEntry( final CarouselEntry entry ) {
        carouselEntries.add( PortablePreconditions.checkNotNull( "entry",
                                                                 entry ) );
    }

    public void addSection( final Section section ) {
        sections.add( PortablePreconditions.checkNotNull( "section",
                                                          section ) );
    }

    public List<CarouselEntry> getCarouselEntries() {
        return Collections.unmodifiableList( carouselEntries );
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList( sections );
    }

}
