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

package org.kie.workbench.common.screens.socialscreen.client;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.socialscreen.place.SocialScreenPlace;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class SocialScreenManager {

    private PlaceManager placeManager;
    private Caller<MetadataService> metadataService;
    private Path currentPath;
    private Metadata currentMetaData;

    public SocialScreenManager() {
    }

    @Inject
    public SocialScreenManager(
            final PlaceManager placeManager,
            final Caller<MetadataService> metadataService) {
        this.placeManager = placeManager;
        this.metadataService = metadataService;
    }

    public void onResourceOpenedEvent(@Observes ResourceOpenedEvent event) {
        currentPath = event.getPath();
        currentMetaData = null;
        placeManager.goTo(new SocialScreenPlace());
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void getMetaData(final Callback<Metadata> callback) {
        checkNotNull("currentPath", currentPath);

        if (currentMetaData == null) {
            loadMetaData(callback);
        } else {
            callback.callback(currentMetaData);
        }
    }

    private void loadMetaData(final Callback<Metadata> callback) {
        metadataService.call(
                new RemoteCallback<Metadata>() {
                    @Override
                    public void callback(Metadata metadata) {
                        currentMetaData = metadata;
                        callback.callback(metadata);
                    }
                }).getMetadata(getCurrentPath());
    }
}
