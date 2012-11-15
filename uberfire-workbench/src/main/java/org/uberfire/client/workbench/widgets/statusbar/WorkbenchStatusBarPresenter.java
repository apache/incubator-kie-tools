/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench.widgets.statusbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.MinimizePlaceEvent;
import org.uberfire.client.workbench.widgets.events.RestorePlaceEvent;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Presenter for WorkbenchStatusBar.
 */
@ApplicationScoped
public class WorkbenchStatusBarPresenter {

    public interface View
            extends
            UberView<WorkbenchStatusBarPresenter> {

        void addPlace( final PlaceRequest place );

        void removePlace( final PlaceRequest place );

    }

    @Inject
    private View view;

    @Inject
    private Event<RestorePlaceEvent> restorePlaceEvent;

    @PostConstruct
    private void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return this.view;
    }

    public void panelMinimized( @Observes MinimizePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        view.addPlace( place );
    }

    public void partRestored( @Observes RestorePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        view.removePlace( place );
    }

    void onWorkbenchPartClose( @Observes ClosePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        view.removePlace( place );
    }

    public void addMinimizedPlace( final PlaceRequest place ) {
        view.addPlace( place );
    }

    public void restoreMinimizedPlace( final PlaceRequest place ) {
        restorePlaceEvent.fire( new RestorePlaceEvent( place ) );
    }

}
