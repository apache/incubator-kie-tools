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

package org.uberfire.client.experimental.screens.explorer;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.experimental.screens.explorer.asset.AssetDisplayer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.experimental.ExperimentalAssetRemoved;
import org.uberfire.shared.experimental.ExperimentalEditorService;

@Dependent
public class ExperimentalExplorer implements ExperimentalExplorerView.Presenter,
                                             IsElement {

    private final ExperimentalExplorerView view;
    private final ManagedInstance<AssetDisplayer> displayers;
    private final Caller<ExperimentalEditorService> service;
    private final NewAssetPopup newAssetPopup;
    private final PlaceManager placeManager;

    @Inject
    public ExperimentalExplorer(final ExperimentalExplorerView view, final ManagedInstance<AssetDisplayer> displayers, final Caller<ExperimentalEditorService> service, final NewAssetPopup newAssetPopup, final PlaceManager placeManager) {
        this.view = view;
        this.displayers = displayers;
        this.service = service;
        this.newAssetPopup = newAssetPopup;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);

        newAssetPopup.init(this::doCreate);
    }

    public void load() {
        clean();
        service.call((RemoteCallback<List<Path>>) paths -> paths.stream().forEach(ExperimentalExplorer.this::newAsset)).listAll();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void newAsset(Path path) {
        final AssetDisplayer displayer = displayers.get();

        displayer.render(path, () -> doDelete(displayer));

        view.show(displayer);
    }

    @PreDestroy
    public void clean() {
        displayers.destroyAll();
        view.clean();
    }

    @Override
    public void createNew() {
        newAssetPopup.show();
    }

    private void doCreate() {
        String assetName = newAssetPopup.getAssetName();

        service.call((RemoteCallback<Path>) path -> {
            newAsset(path);
            newAssetPopup.hide();
            placeManager.goTo(path);
        }).create(assetName);
    }

    private void doDelete(AssetDisplayer asset) {
        service.call().delete(asset.getPath(), "");
    }

    public void onDelete(@Observes ExperimentalAssetRemoved event) {
        view.findDisplayer(event.getPath())
                .ifPresent(this::removeAssetDisplayer);
    }

    private void removeAssetDisplayer(AssetDisplayer asset) {
        view.delete(asset);
        displayers.destroy(asset);
    }
}
