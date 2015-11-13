/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.editor;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.plugin.client.widget.plugin.GeneralPluginEditor;
import org.uberfire.ext.plugin.model.*;
import org.uberfire.mvp.ParameterizedCommand;

public abstract class RuntimePluginBaseView extends BaseEditorViewImpl {

    @Inject
    protected GeneralPluginEditor editor;

    public void setupContent( final PluginContent response,
                              final ParameterizedCommand<Media> parameterizedCommand ) {
        editor.setupContent( response, parameterizedCommand );
    }

    public PluginSimpleContent getContent() {
        return editor.getContent();
    }

    public String getTemplate() {
        return editor.getTemplate();
    }

    public String getCss() {
        return editor.getCss();
    }

    public Map<CodeType, String> getCodeMap() {
        return editor.getCodeMap();
    }

    protected abstract void setFramework( Collection<Framework> frameworks );

    protected abstract Collection<Framework> getFrameworks();
}
