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

import java.util.Date;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface OverviewScreenView
        extends IsWidget,
                HasBusyIndicator {

    interface Presenter {

        void onDescriptionEdited( String description );

    }

    void setPresenter( Presenter presenter );

    void refresh( String version );

    void setReadOnly( boolean isReadOnly );

    void setVersionHistory( Path path );

    void setDescription( String description );

    void setResourceType( ClientResourceType type );

    void setProject( String project );

    void setLastModified( String lastContributor,
                          Date lastModified );

    void setCreated( String creator,
                     Date dateCreated );

    void setMetadata( Metadata metadata,
                      boolean isReadOnly );
    
    void setCurrentUser (String currentUser);
    
    void setLockStatus (LockInfo lockInfo);

    void showVersionHistory();

    void setForceUnlockHandler(Runnable handler);

}
