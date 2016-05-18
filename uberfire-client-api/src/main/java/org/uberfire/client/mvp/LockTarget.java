/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * Holds information about the target of a lock.
 */
public class LockTarget {

    public interface TitleProvider {

        String getTitle();

    }

    private final Path path;
    private final IsWidget isWidget;
    private final PlaceRequest place;
    private final TitleProvider titleProvider;
    private final Runnable reloadRunnable;

    public LockTarget( Path path,
                       IsWidget isWidget,
                       PlaceRequest place,
                       TitleProvider titleProvider,
                       Runnable reloadRunnable ) {

        checkNotNull( "path", path );
        checkNotNull( "isWidget", isWidget );
        checkNotNull( "place", place );
        checkNotNull( "titleProvider", titleProvider );
        checkNotNull( "reloadRunnable", reloadRunnable );

        this.path = path;
        this.isWidget = isWidget;
        this.place = place;
        this.titleProvider = titleProvider;
        this.reloadRunnable = reloadRunnable;
    }

    public Path getPath() {
        return path;
    }

    public IsWidget getWidget() {
        return isWidget;
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