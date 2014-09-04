package org.uberfire.client.workbench.events;

import org.uberfire.workbench.events.UberFireEvent;

public class NewPerspectiveEvent implements UberFireEvent {

    private final String perspectiveName;

    public NewPerspectiveEvent( final String perspectiveName ) {
        this.perspectiveName = perspectiveName;
    }

    public String getPerspectiveName() {
        return perspectiveName;
    }

    @Override
    public String toString() {
        return "NewPerspectiveEvent [perspectiveName=" + perspectiveName + "]";
    }

}
