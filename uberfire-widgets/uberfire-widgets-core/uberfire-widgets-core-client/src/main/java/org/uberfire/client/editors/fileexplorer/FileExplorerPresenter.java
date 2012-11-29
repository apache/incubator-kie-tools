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

package org.uberfire.client.editors.fileexplorer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.IdentifierUtils;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.Position;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View view;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IdentifierUtils idUtils;

    public interface View
            extends
            UberView<FileExplorerPresenter> {

        void setFocus();

        void reset();

        void removeIfExists(final Root root);

        void addNewRoot(final Root root);
    }

    public static interface FileExplorerItem {

        void addDirectory(final Path child);

        void addFile(final Path child);
    }

    @OnStart
    public void onStart() {

        view.reset();

        rootService.call(new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback(Collection<Root> response) {
                for (final Root root : response) {
                    view.removeIfExists(root);
                    view.addNewRoot(root);
                }
            }
        }).listRoots();
    }

    public void loadDirectoryContent(final FileExplorerItem item, final Path path) {
        vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback(DirectoryStream<Path> response) {
                for (final Path child : response) {
                    vfsService.call(new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(response);
                            if (attrs.isDirectory()) {
                                item.addDirectory(child);
                            } else {
                                item.addFile(child);
                            }
                        }
                    }).readAttributes(child);
                }
            }
        }).newDirectoryStream(path);
    }

    private PlaceRequest getPlace(final Path path) {

        final String fileType = getFileType(path.getFileName());
        if (fileType == null) {
            return defaultPlace(path);
        }

        //Lookup an Activity that can handle the file extension and create a corresponding PlaceRequest.
        //We could simply construct a PlaceRequest for the fileType and leave PlaceManager to determine whether
        //an Activity for the fileType exists however that would place the decision as to what default editor
        //to use within PlaceManager. It is a design decision to let FileExplorer determine the default editor.
        //Consequentially we check for an Activity here and, if none found, define the default editor.
        final Set<IOCBeanDef<Activity>> activityBeans = idUtils.getActivities(fileType);
        if (activityBeans.size() > 0) {
            final PlaceRequest place = new DefaultPlaceRequest(fileType);
            place.addParameter("path:uri",
                    path.toURI()).addParameter("path:name",
                    path.getFileName());
            return place;
        }

        //If a specific handler was not found use a TextEditor
        return defaultPlace(path);
    }

    private PlaceRequest defaultPlace(final Path path) {
        PlaceRequest defaultPlace = new DefaultPlaceRequest("TextEditor");
        defaultPlace.addParameter("path:uri",
                path.toURI()).addParameter("path:name",
                path.getFileName());
        return defaultPlace;
    }

    private String getFileType(final String fileName) {
        final int dotIndex = fileName.indexOf(".");
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "File Explorer";
    }

    @WorkbenchPartView
    public UberView<FileExplorerPresenter> getWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    public void redirect(final Path path) {
        vfsService.call(new RemoteCallback<Map>() {
            @Override
            public void callback(final Map response) {
                final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(response);
                if (attrs.isRegularFile()) {
                    PlaceRequest placeRequest = getPlace(path);
                    placeManager.goTo(placeRequest);
                }
            }
        }).readAttributes(path);

    }

    public void redirectRepositoryList() {
        placeManager.goTo(new DefaultPlaceRequest("RepositoriesEditor"));
    }

    public void redirect(Root root) {
        placeManager.goTo(root.getPlaceRequest());
    }

    public void newRootDirectory(@Observes Root root) {
        view.removeIfExists(root);
        view.addNewRoot(root);
    }

}