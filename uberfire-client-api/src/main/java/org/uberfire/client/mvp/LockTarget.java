package org.uberfire.client.mvp;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.Widget;

/**
 * Holds information about the target of a lock.  
 */
public class LockTarget {

    public interface TitleProvider {
        String getTitle();
    }
    
    private final Path path;
    private final Widget widget;
    private final PlaceRequest place;
    private final TitleProvider titleProvider;
    private final Runnable reloadRunnable;
    
    public LockTarget( Path path,
                       Widget widget,
                       PlaceRequest place,
                       TitleProvider titleProvider,
                       Runnable reloadRunnable) {
        
        checkNotNull( "path", path );
        checkNotNull( "widget", widget );
        checkNotNull( "place", place );
        checkNotNull( "titleProvider", titleProvider );
        checkNotNull( "reloadRunnable", reloadRunnable );
        
        this.path = path;
        this.widget = widget;
        this.place = place;
        this.titleProvider = titleProvider;
        this.reloadRunnable = reloadRunnable;
    }
    
    public Path getPath() {
        return path;
    }

    
    public Widget getWidget() {
        return widget;
    }

    
    public PlaceRequest getPlace() {
        return place;
    }
    
    public String getTitle() {
        return titleProvider.getTitle();
    }

    public Runnable getReloadRunnable() {
        return reloadRunnable;
    }
}