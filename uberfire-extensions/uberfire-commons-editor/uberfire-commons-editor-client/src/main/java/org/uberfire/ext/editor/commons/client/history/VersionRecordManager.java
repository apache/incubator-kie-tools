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

import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.editor.commons.client.file.RestorePopup;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.history.event.VersionSelectedEvent;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

public class VersionRecordManager {

    private VersionMenuDropDownButton versionMenuDropDownButton;
    private Event<VersionSelectedEvent> versionSelectedEvent;

    private RestorePopup restorePopup;
    private RestoreUtil restoreUtil;
    private Caller<VersionService> versionService;

    private Callback<VersionRecord> selectionCallback;
    private List<VersionRecord> versions;
    private ObservablePath pathToLatest;
    private String version;
    private SaveButton saveButton;

    @Inject
    public VersionRecordManager(
            final VersionMenuDropDownButton versionMenuDropDownButton,
            final SaveButton saveButton,
            final RestorePopup restorePopup,
            final RestoreUtil restoreUtil,
            final Event<VersionSelectedEvent> versionSelectedEvent,
            final Caller<VersionService> versionService ) {
        this.restorePopup = restorePopup;
        this.versionMenuDropDownButton = versionMenuDropDownButton;
        this.saveButton = saveButton;
        this.versionSelectedEvent = versionSelectedEvent;

        versionMenuDropDownButton.addSelectionCallback( new Callback<VersionRecord>() {
            @Override
            public void callback( VersionRecord versionRecord ) {
                fireVersionSelected( versionRecord );
            }
        } );

        this.restoreUtil = restoreUtil;
        this.versionService = versionService;
    }

    private void fireVersionSelected( VersionRecord versionRecord ) {
        versionSelectedEvent.fire(
                new VersionSelectedEvent(
                        getPathToLatest(),
                        versionRecord
                ) );
    }

    public void init(
            String version,
            ObservablePath path,
            Callback<VersionRecord> selectionCallback ) {

        clear();

        PortablePreconditions.checkNotNull( "path", path );
        this.selectionCallback = PortablePreconditions.checkNotNull( "selectionCallback", selectionCallback );

        this.version = version;

        if ( version == null ) {
            setPathToLatest( path );
        }

        loadVersions( path );
    }

    public MenuItem buildMenu() {
        return new VersionMenuItem( versionMenuDropDownButton );
    }

    public void setVersions( List<VersionRecord> versions ) {

        if ( version == null ) {
            version = versions.get( versions.size() - 1 ).id();
        }

        setVersions( versions, version );
    }

    private void setVersions( List<VersionRecord> versions,
                              String version ) {
        PortablePreconditions.checkNotNull( "versions", versions );

        resolveVersions( versions );

        versionMenuDropDownButton.setItems( versions );
        versionMenuDropDownButton.setVersion( version );
    }

    public void setShowMoreCommand( Command showMore ) {
        versionMenuDropDownButton.setShowMoreCommand( showMore );
    }

    private void resolveVersions( List<VersionRecord> versions ) {
        if ( this.versions == null || versions.size() > this.versions.size() ) {
            this.versions = versions;
        }
    }

    public MenuItem newSaveMenuItem( Command command ) {
        saveButton.setCommand( command );
        return saveButton;
    }

    public boolean isLatest( VersionRecord versionRecord ) {
        return versions.get( versions.size() - 1 ).id().equals( versionRecord.id() );
    }

    private void setPathToLatest( ObservablePath pathToLatest ) {
        this.pathToLatest = PortablePreconditions.checkNotNull( "pathToLatest", pathToLatest );
    }

    public ObservablePath getPathToLatest() {
        return pathToLatest;
    }

    public void onVersionSelectedEvent( @Observes VersionSelectedEvent event ) {
        if ( event.getPathToFile().equals( getPathToLatest() ) && selectionCallback != null ) {
            selectionCallback.callback( event.getVersionRecord() );
        }
    }

    public void setVersion( String version ) {
        this.version = PortablePreconditions.checkNotNull( "version", version );

        versionMenuDropDownButton.setVersion( version );
        if ( saveButton != null ) {
            if ( isCurrentLatest() ) {
                saveButton.setTextToSave();
            } else if ( versions != null ) {
                saveButton.setTextToRestore();
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public ObservablePath getCurrentPath() {
        if ( isCurrentLatest() ) {
            return getPathToLatest();
        } else {
            return restoreUtil.createObservablePath(
                    getPathToLatest(),
                    getCurrentVersionRecordUri() );
        }
    }

    public boolean isCurrentLatest() {
        return versions == null || getLatestVersionRecord().id().equals( version );
    }

    private VersionRecord getLatestVersionRecord() {
        return versions.get( versions.size() - 1 );
    }

    private String getCurrentVersionRecordUri() {
        VersionRecord record = getCurrentVersionRecord();
        if ( record == null ) {
            return getPathToLatest().toURI();
        } else {
            return record.uri();
        }
    }

    private VersionRecord getCurrentVersionRecord() {
        for ( VersionRecord versionRecord : versions ) {
            if ( versionRecord.id().equals( version ) ) {
                return versionRecord;
            }
        }
        return null;
    }

    public void restoreToCurrentVersion() {
        restorePopup.show( getCurrentPath(), getCurrentVersionRecordUri() );
    }

    private void loadVersions( final ObservablePath path ) {
        loadVersions( path, new Callback<List<VersionRecord>>() {
            @Override
            public void callback( List<VersionRecord> records ) {
                doesTheVersionExist( records );
            }
        } );
    }

    private void doesTheVersionExist( List<VersionRecord> records ) {
        boolean found = false;
        for ( VersionRecord versionRecord : records ) {
            if ( versionRecord.id().equals( version ) ) {
                found = true;
                break;
            }
        }
        if ( !found ) {
            throw new IllegalArgumentException( "Unknown version" );
        }
    }

    public void reloadVersions( final Path path ) {
        loadVersions( path, new Callback<List<VersionRecord>>() {
            @Override
            public void callback( List<VersionRecord> records ) {
                setVersion( records.get( records.size() - 1 ).id() );
            }
        } );
    }

    private void loadVersions( final Path path,
                               final Callback<List<VersionRecord>> callback ) {
        versionService.call( new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback( List<VersionRecord> records ) {

                String uri = path.toURI();

                // We should not recreate the path to latest,
                // since the new path instance will not have version support
                if ( !path.equals( pathToLatest ) ) {
                    setPathToLatest( restoreUtil.createObservablePath( path, uri ) );
                }
                setVersions( records );
                callback.callback( records );
            }
        } ).getVersions( path );
    }

    private void onRestore( @Observes RestoreEvent restore ) {
        if ( getCurrentPath() != null &&
                getCurrentPath().equals( restore.getPath() ) &&
                saveButton != null ) {
            saveButton.setTextToSave();
        }
    }

    public void clear() {
        selectionCallback = null;
        versions = null;
        pathToLatest = null;
        version = null;
    }

}
