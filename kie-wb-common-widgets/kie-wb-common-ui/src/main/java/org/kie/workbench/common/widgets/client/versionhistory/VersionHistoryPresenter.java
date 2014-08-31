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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.guvnor.common.services.shared.version.VersionService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.versionhistory.event.VersionSelectedEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class VersionHistoryPresenter
        implements VersionHistoryPresenterView.Presenter,
        IsWidget {

    private VersionHistoryPresenterView view;
    private Caller<VersionService> versionService;

    private AsyncDataProvider<VersionRecord> dataProvider;

    private Event<VersionSelectedEvent> versionSelectedEvent;

    private Path path;
    private String version;
    private List<VersionRecord> records;

    @Inject
    public VersionHistoryPresenter(
            final VersionHistoryPresenterView view,
            Caller<VersionService> versionService,
            Event<VersionSelectedEvent> versionSelectedEvent) {
        this.view = view;
        this.versionService = versionService;
        this.versionSelectedEvent = versionSelectedEvent;

        view.setPresenter(this);
        dataProvider = new AsyncDataProvider<VersionRecord>() {
            @Override
            protected void onRangeChanged(HasData<VersionRecord> display) {
                if (records != null) {
                    updateRowCount(records.size(), true);
                    updateRowData(0, records);
                }
            }
        };

    }

    public void init(final Path path, String version) {

        this.path = path;
        this.version = version;

        loadContent();

    }

    private void loadContent() {
        versionService.call(getRemoteCallback()).getVersion(path);
    }

    private RemoteCallback<List<VersionRecord>> getRemoteCallback() {
        return new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(List<VersionRecord> records) {
                view.setup(version, dataProvider);
                Collections.reverse(records);
                VersionHistoryPresenter.this.records = records;
            }
        };
    }


    @Override
    public void onSelect(VersionRecord record) {
        if (!record.id().equals(version)) {
            versionSelectedEvent.fire(
                    new VersionSelectedEvent(
                            path,
                            record
                    ));
        }
    }

    public void onVersionChange(@Observes VersionSelectedEvent event) {
        if (path != null) {
            if (path.toURI().equals(event.getPathToFile().toURI())) {
                version = event.getVersionRecord().id();
                loadContent();
            }
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void refresh() {
        view.refreshGrid();
    }
}
