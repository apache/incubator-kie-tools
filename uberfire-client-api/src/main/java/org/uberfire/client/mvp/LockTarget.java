/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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