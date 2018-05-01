/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;

@EntryPoint
public class ActiveContextItems {

    protected Event<WorkspaceProjectContextChangeEvent> contextChangedEvent;

    protected Caller<ExplorerService> explorerService;

    private WorkspaceProject activeProject;
    private Module activeModule;
    private Package activePackage;
    private FolderItem activeFolderItem;
    private FolderListing activeContent;

    public ActiveContextItems() {
    }

    @Inject
    public ActiveContextItems(final Event<WorkspaceProjectContextChangeEvent> contextChangedEvent,
                              final Caller<ExplorerService> explorerService) {
        this.contextChangedEvent = contextChangedEvent;
        this.explorerService = explorerService;
    }

    public WorkspaceProject getActiveProject() {
        return activeProject;
    }

    public Module getActiveModule() {
        return activeModule;
    }

    public Package getActivePackage() {
        return activePackage;
    }

    public FolderItem getActiveFolderItem() {
        return activeFolderItem;
    }

    public FolderListing getActiveContent() {
        return activeContent;
    }

    boolean setupActiveModule(final ProjectExplorerContent content) {
        if (Utils.hasModuleChanged(content.getModule(),
                                   activeModule)) {
            activeModule = content.getModule();
            return true;
        } else {
            return false;
        }
    }

    boolean setupActiveProject(final ProjectExplorerContent content) {
        if (Utils.hasProjectChanged(content.getProject(),
                                    activeProject)) {
            activeProject = content.getProject();
            return true;
        } else {
            return false;
        }
    }

    public void flush() {
        activeProject = null;
        activeModule = null;
        activePackage = null;
        activeFolderItem = null;
    }

    boolean setupActiveFolderAndPackage(final ProjectExplorerContent content) {
        if (Utils.hasFolderItemChanged(content.getFolderListing().getItem(),
                                       activeFolderItem)) {
            activeFolderItem = content.getFolderListing().getItem();
            if (activeFolderItem != null && activeFolderItem.getItem() != null && activeFolderItem.getItem() instanceof Package) {
                activePackage = (Package) activeFolderItem.getItem();
            } else if (activeFolderItem == null || activeFolderItem.getItem() == null) {
                activePackage = null;
            }

            return true;
        } else {
            return false;
        }
    }

    void fireContextChangeEvent() {
        if (activeFolderItem.getItem() instanceof Package) {
            activePackage = (Package) activeFolderItem.getItem();
            contextChangedEvent.fire(new WorkspaceProjectContextChangeEvent(activeProject,
                                                                            activeModule,
                                                                            activePackage));
        } else if (activeFolderItem.getType().equals(FolderItemType.FOLDER)) {
            explorerService.call(getResolvePackageRemoteCallback()).resolvePackage(activeFolderItem);
        }
    }

    private RemoteCallback<Package> getResolvePackageRemoteCallback() {
        return new RemoteCallback<Package>() {
            @Override
            public void callback(final Package pkg) {
                if (Utils.hasPackageChanged(pkg,
                                            activePackage)) {
                    activePackage = pkg;
                    contextChangedEvent.fire(new WorkspaceProjectContextChangeEvent(activeProject,
                                                                                    activeModule,
                                                                                    activePackage));
                }
            }
        };
    }

    public void setActiveContent(final FolderListing activeContent) {
        this.activeContent = activeContent;
    }

    public void setActiveFolderItem(final FolderItem activeFolderItem) {
        this.activeFolderItem = activeFolderItem;
    }
}
