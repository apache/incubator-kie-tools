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

package org.uberfire.ext.editor.commons.client.history;

import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

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
    private ParameterizedCommand<VersionRecord> onCurrentVersionRefreshed;

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

    public void init(final Path path) {
        this.path = path;
    }

    private void loadContent() {
        versionService.call(getRemoteCallback()).getVersions(path);
    }

    private RemoteCallback<List<VersionRecord>> getRemoteCallback() {
        return new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(List<VersionRecord> records) {
                view.setup(version, dataProvider);
                Collections.reverse( records );
                VersionHistoryPresenter.this.records = records;
                view.refreshGrid();
                doOnCurrentVersionRefreshed( version );
            }
        };
    }

    @Override
    public void onSelect(VersionRecord record) {
        if (!record.id().equals(version)) {
            view.showLoading();
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

    public void refresh(String version) {
        this.version = version;
        loadContent();
    }

    public void setOnCurrentVersionRefreshed( ParameterizedCommand<VersionRecord> onCurrentVersionRefreshed ) {
        this.onCurrentVersionRefreshed = onCurrentVersionRefreshed;
    }

    private void doOnCurrentVersionRefreshed( String version ) {
        if ( onCurrentVersionRefreshed != null && records != null && version != null ) {
            for ( VersionRecord record : records ) {
                if ( version.equals( record.id() ) ) {
                    onCurrentVersionRefreshed.execute( record );
                    break;
                }
            }
        }
    }
}