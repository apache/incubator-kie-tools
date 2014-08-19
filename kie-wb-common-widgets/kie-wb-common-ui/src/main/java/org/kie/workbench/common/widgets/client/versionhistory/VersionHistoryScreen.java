/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.client.versionhistory;

import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.version.VersionService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.versionhistory.event.VersionSelectedEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen(identifier = "versionHistoryScreen", preferredWidth = 300)
public class VersionHistoryScreen
        implements VersionHistoryScreenView.Presenter {

    private VersionHistoryScreenView view;
    private Caller<VersionService> versionService;

    private Event<VersionSelectedEvent> versionSelectedEvent;

    private Path path;
    private String version;

    @Inject
    public VersionHistoryScreen(
            VersionHistoryScreenView view,
            Caller<VersionService> versionService,
            Event<VersionSelectedEvent> versionSelectedEvent) {
        this.view = view;
        this.versionService = versionService;
        this.versionSelectedEvent = versionSelectedEvent;

        view.setPresenter(this);
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {

        path = PathFactory.newPath(
                place.getParameter(VersionHistoryScreenPlace.FILENAME, null),
                place.getParameter(VersionHistoryScreenPlace.URI, null));

        version = place.getParameter(VersionHistoryScreenPlace.VERSION, "");

        loadContent();

    }

    private void loadContent() {
        versionService.call(getRemoteCallback()).getVersion(path);
    }

    private RemoteCallback<List<VersionRecord>> getRemoteCallback() {
        return new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(List<VersionRecord> records) {

                view.clear();

                int number = 1;

                for (final VersionRecord record : records) {
                    addVersionRecord(record, number++);
                }
            }
        };
    }

    private void addVersionRecord(final VersionRecord record, int number) {
        view.addLine(
                record,
                number,
                version.equals(record.id()));
    }

    @Override
    public void onSelect(VersionRecord record) {
        versionSelectedEvent.fire(
                new VersionSelectedEvent(
                        path,
                        record
                ));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Versions";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    public void onVersionChange(@Observes VersionSelectedEvent event) {
        if (path.toURI().equals(event.getPathToFile().toURI())) {
            version = event.getVersionRecord().id();
            loadContent();
        }
    }
}
