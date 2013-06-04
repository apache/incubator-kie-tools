package org.kie.workbench.common.screens.home.client.model;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

/**
 * A Section Entry within a Section on the Home Page
 */
public class SectionEntry {

    private final String caption;
    private final Command onClickCommand;

    public SectionEntry( final String caption,
                         final Command onClickCommand ) {
        this.caption = PortablePreconditions.checkNotNull( "caption",
                                                           caption );
        this.onClickCommand = PortablePreconditions.checkNotNull( "onClickCommand",
                                                                  onClickCommand );
    }

    public String getCaption() {
        return caption;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }
}
