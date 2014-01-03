package org.uberfire.workbench.events;

/**
 * Created with IntelliJ IDEA.
 * Date: 6/25/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
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
