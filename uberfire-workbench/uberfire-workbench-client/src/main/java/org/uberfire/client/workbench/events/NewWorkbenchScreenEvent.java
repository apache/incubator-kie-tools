package org.uberfire.client.workbench.events;

import org.uberfire.workbench.events.UberFireEvent;

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
