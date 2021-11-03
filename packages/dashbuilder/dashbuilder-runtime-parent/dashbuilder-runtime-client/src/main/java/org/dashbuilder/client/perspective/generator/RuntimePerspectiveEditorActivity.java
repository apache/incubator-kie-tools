/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.perspective.generator;

import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorActivity;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorScreenActivity;

public class RuntimePerspectiveEditorActivity extends PerspectiveEditorActivity {

    public RuntimePerspectiveEditorActivity(LayoutTemplate editor, PerspectiveEditorScreenActivity screen) {
        super(editor, screen);
    }

    @Override
    protected String getDefaultPanelType() {
        return StaticWorkbenchPanelPresenter.class.getName();
    }

}