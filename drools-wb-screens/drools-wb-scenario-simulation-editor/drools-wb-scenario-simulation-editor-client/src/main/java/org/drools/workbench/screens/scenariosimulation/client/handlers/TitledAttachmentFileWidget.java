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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.constants.ElementTags;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
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

public class TitledAttachmentFileWidget extends Composite implements ValueChangeHandler<String> {

    protected VerticalPanel fields = GWT.create(VerticalPanel.class);
    protected Div divElement = GWT.create(Div.class);
    protected FormLabel titleLabel = GWT.create(FormLabel.class);
    protected Span errorLabel = GWT.create(Span.class);
    protected ComboBox comboBox = GWT.create(ComboBox.class);
    protected LibraryPlaces libraryPlaces;
    protected AssetQueryService assetQueryService;
    protected WorkspaceProject workspaceProject;
    protected String selectedPath;

    public TitledAttachmentFileWidget(String title, LibraryPlaces libraryPlaces, AssetQueryService assetQueryService) {
        titleLabel.setStyleName("control-label");
        titleLabel.setText(title);
        divElement.add(titleLabel);
        divElement.getElement().appendChild(createIconElement());
        errorLabel.setStyleName("help-block");
        errorLabel.setColor("#c00");
        errorLabel.setText(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset());
        fields.add(divElement);
        fields.add(this.comboBox);
        fields.add(this.errorLabel);
        this.libraryPlaces = libraryPlaces;
        this.assetQueryService = assetQueryService;
        this.workspaceProject = libraryPlaces.getActiveWorkspace();
        initWidget(fields);
        comboBox.addValueChangeHandler(this);
    }

    public void clearStatus() {
        updateAssetList();
        comboBox.setText(null);
        errorLabel.setText(null);
        selectedPath = null;
    }

    public void updateAssetList() {
        comboBox.clear();
        updateAssets(this::addAssets);
    }

    public String getSelectedPath() {
        return selectedPath;
    }

    public boolean validate() {
        boolean toReturn = selectedPath != null && !selectedPath.isEmpty();
        if (!toReturn) {
            errorLabel.setText(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset());
        } else {
            errorLabel.setText(null);
        }
        return toReturn;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        selectedPath = event.getValue();
        validate();
    }


    protected void updateAssets(RemoteCallback<AssetQueryResult> callback) {
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

    /**
     * @return a new icon element.
     */
    protected Element createIconElement() {
        Element e = Document.get().createElement(ElementTags.I);
        e.addClassName(Styles.FONT_AWESOME_BASE);
        e.addClassName(IconType.STAR.getCssName());
        Style s = e.getStyle();
        s.setFontSize(6, Style.Unit.PX);
        s.setPaddingLeft(2, Style.Unit.PX);
        s.setPaddingRight(5, Style.Unit.PX);
        s.setColor("#b94a48");
        Element sup = Document.get().createElement("sup");
        sup.appendChild(e);
        return sup;
    }
}
