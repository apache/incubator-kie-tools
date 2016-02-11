/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ForceUnlockEvent;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class OverviewWidgetPresenter
        implements OverviewScreenView.Presenter, IsWidget {

    private ClientTypeRegistry clientTypeRegistry;

    private Overview overview;
    private boolean isReadOnly = false;

    private OverviewScreenView view;
    private int                originalHash;
    
    @Inject
    private Event<ForceUnlockEvent> lockReleaseEvent;
    
    @Inject
    User user;

    public OverviewWidgetPresenter() {
    }

    @Inject
    public OverviewWidgetPresenter(
            final ClientTypeRegistry clientTypeRegistry,
            final OverviewScreenView view) {
        this.view = view;

        view.setPresenter(this);
        
        this.clientTypeRegistry = clientTypeRegistry;
    }

    public void setContent(final Overview overview,
                           final ObservablePath path) {

        this.overview = overview;
        originalHash = this.overview.hashCode();

        view.setVersionHistory(path);

        view.setResourceType(clientTypeRegistry.resolve(path));

        view.setProject(overview.getProjectName());
        view.setMetadata(overview.getMetadata(), isReadOnly);

        view.setDescription(overview.getMetadata().getDescription());
        view.setLastModified(overview.getMetadata().getLastContributor(), overview.getMetadata().getLastModified());
        view.setCreated(overview.getMetadata().getCreator(), overview.getMetadata().getDateCreated());

        view.hideBusyIndicator();
        
        view.setForceUnlockHandler( new Runnable() {

            @Override
            public void run() {
                lockReleaseEvent.fire( new ForceUnlockEvent( overview.getMetadata().getPath() ) );
            }
            
        });
        
        view.setCurrentUser( user.getIdentifier() );
    }

    @Override
    public void onDescriptionEdited(String description) {
        overview.getMetadata().setDescription(description);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void showVersionsTab() {
        view.showVersionHistory();
    }

    public void refresh(String version) {
        view.refresh(version);
    }

    public boolean isDirty() {
        if (overview == null) {
            return false;
        } else {
            return originalHash != overview.hashCode();
        }
    }

    public void resetDirty() {
        if (overview != null) {
            originalHash = overview.hashCode();
        }
    }
    
    public void onForceLockRelease(final Path path) {
        lockReleaseEvent.fire( new ForceUnlockEvent(path) );
    }
    
    void onLockChange( @Observes LockInfo lockInfo ) {                
        if ( overview != null && overview.getMetadata() != null && 
                lockInfo.getFile().equals( overview.getMetadata().getPath() ) ) {
            
            overview.getMetadata().setLockInfo( lockInfo );
            view.setLockStatus( lockInfo );
        }
    }
    
}
