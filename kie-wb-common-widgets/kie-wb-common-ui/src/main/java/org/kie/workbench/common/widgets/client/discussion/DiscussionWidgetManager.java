package org.kie.workbench.common.widgets.client.discussion;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.ResourceOpenedEvent;

public class DiscussionWidgetManager {

    private PlaceManager placeManager;
    private Caller<MetadataService> metadataService;
    private Path currentPath;
    private Metadata currentMetaData;

    public DiscussionWidgetManager() {
    }

    @Inject
    public DiscussionWidgetManager(
            final PlaceManager placeManager,
            final Caller<MetadataService> metadataService) {
        this.placeManager = placeManager;
        this.metadataService = metadataService;
    }

    public void onResourceOpenedEvent(@Observes ResourceOpenedEvent event) {
        currentPath = event.getPath();
        currentMetaData = null;
// placeManager.goTo(new SocialScreenPlace());
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void getMetaData(final Callback<Metadata> callback) {
// checkNotNull("currentPath", currentPath);

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
