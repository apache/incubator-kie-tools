package org.uberfire.workbench.events;

public class NewPerspectiveEvent extends UberFireEvent {

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
