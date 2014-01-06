package org.uberfire.workbench.events;

public class NewWorkbenchScreenEvent extends UberFireEvent {

    private final String screenName;

    public NewWorkbenchScreenEvent( final String screenName ) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    @Override
    public String toString() {
      return "NewWorkbenchScreenEvent [screenName=" + screenName + "]";
    }
}
