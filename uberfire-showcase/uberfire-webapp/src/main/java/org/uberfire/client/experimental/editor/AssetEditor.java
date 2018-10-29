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

package org.uberfire.client.experimental.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class AssetEditor implements AssetEditorView.Presenter {

    private AssetEditorView view;

    @Inject
    public AssetEditor(AssetEditorView view) {
        this.view = view;
    }

    public void showContent(String content) {
        view.showContent(content);
    }

    public String getContent() {
        return view.getContent();
    }

    public AssetEditorView getView() {
        return view;
    }
}
