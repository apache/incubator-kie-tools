/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.examples.client.wizard.widgets.ComboBox;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetQueryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.util.URIUtil;

public class TitledAttachmentFileWidget extends Composite {

    protected VerticalPanel fields = GWT.create(VerticalPanel.class);
    protected FormLabel titleLabel = GWT.create(FormLabel.class);
    protected ComboBox comboBox = GWT.create(ComboBox.class);
    protected LibraryPlaces libraryPlaces;
    protected AssetQueryService assetQueryService;
    protected WorkspaceProject workspaceProject;

    public TitledAttachmentFileWidget() {
        this("", null, null);
    }

    public TitledAttachmentFileWidget(String title, LibraryPlaces libraryPlaces, AssetQueryService assetQueryService) {
        titleLabel.setStyleName("control-label");
        titleLabel.setText(title);
        fields.add(titleLabel);
        fields.add(this.comboBox);
        this.libraryPlaces = libraryPlaces;
        this.assetQueryService = assetQueryService;
        this.workspaceProject = libraryPlaces.getActiveWorkspace();
        initWidget(fields);
    }

    public void updateAssetList() {
        comboBox.clear();
        getAssets(this::addAssets);
    }

    protected void getAssets(RemoteCallback<AssetQueryResult> callback) {
        ProjectAssetsQuery query = createProjectQuery();
        assetQueryService.getAssets(query)
                .call(callback, new DefaultErrorCallback());
    }

    protected ProjectAssetsQuery createProjectQuery() {
        List<String> suffixes = Collections.singletonList("dmn");
        return new ProjectAssetsQuery(workspaceProject,
                                      "",
                                      0,
                                      1000,
                                      suffixes);
    }

    protected void addAssets(AssetQueryResult result) {
        if (Objects.equals(AssetQueryResult.ResultType.Normal, result.getResultType())) {
            List<AssetInfo> assetInfos = result.getAssetInfos().get();
            assetInfos.forEach(asset -> {
                if (asset.getFolderItem().getType().equals(FolderItemType.FILE)) {
                    comboBox.addItem(getAssetPath(asset));
                }
            });
        }
    }

    protected String getAssetPath(final AssetInfo asset) {
        final String fullPath = ((Path) asset.getFolderItem().getItem()).toURI();
        final String projectRootPath = workspaceProject.getRootPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length());
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);
        return decodedRelativeAssetPath;
    }
}
