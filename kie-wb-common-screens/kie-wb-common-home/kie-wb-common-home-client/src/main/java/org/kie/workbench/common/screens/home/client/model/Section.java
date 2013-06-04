package org.kie.workbench.common.screens.home.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.commons.validation.PortablePreconditions;

/**
 * A Section on the Home Page
 */
public class Section {

    private final String heading;
    private final List<SectionEntry> entries = new ArrayList<SectionEntry>();

    public Section( final String heading ) {
        this.heading = PortablePreconditions.checkNotNull( "heading",
                                                           heading );
    }

    public String getHeading() {
        return heading;
    }

    public void addEntry( final SectionEntry entry ) {
        entries.add( PortablePreconditions.checkNotNull( "entry",
                                                         entry ) );
    }

    public List<SectionEntry> getEntries() {
        return Collections.unmodifiableList( entries );
    }

}
