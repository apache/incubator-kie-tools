package org.uberfire.workbench.events;

/**
 * Created with IntelliJ IDEA.
 * Date: 6/25/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewPerspectiveEvent {

    private final String perspectiveName;

    public NewPerspectiveEvent( final String perspectiveName ) {
        this.perspectiveName = perspectiveName;
    }

    public String getPerspectiveName() {
        return perspectiveName;
    }
}
