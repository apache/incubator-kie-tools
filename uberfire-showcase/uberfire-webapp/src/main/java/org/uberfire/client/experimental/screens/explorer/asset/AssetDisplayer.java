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

package org.uberfire.client.experimental.screens.explorer.asset;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class AssetDisplayer implements AssetDisplayerView.Presenter,
                                       IsElement {

    private AssetDisplayerView view;
    private PlaceManager placeManager;

    private Path path;

    private Command deleteCommand;

    @Inject
    public AssetDisplayer(AssetDisplayerView view, PlaceManager placeManager) {
        this.view = view;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void render(Path assetPath, Command deleteCommand) {
        this.path = assetPath;
        this.deleteCommand = deleteCommand;

        view.show(assetPath.getFileName().substring(0, assetPath.getFileName().lastIndexOf(".")));
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void open() {
        placeManager.goTo(path);
    }

    @Override
    public void delete() {
        if (deleteCommand != null) {
            deleteCommand.execute();
        }
    }

    public Path getPath() {
        return path;
    }
}
