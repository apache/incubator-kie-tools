/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.htmleditor;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.ScriptInjector;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;
import org.uberfire.ext.widgets.common.client.resources.HtmlEditorResources;

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;

@ApplicationScoped
public class HtmlEditorLibraryLoader {

    private boolean scriptsAreLoaded = false;

    public void ensureLibrariesAreAvailable() {
        if (!scriptsAreLoaded) {
            injectScripts();
            scriptsAreLoaded = true;
        }
    }

    void injectScripts() {
        ScriptInjector.fromString(HtmlEditorResources.INSTANCE.wysihtml().getText()).setWindow(TOP_WINDOW).inject();
        ScriptInjector.fromString(HtmlEditorResources.INSTANCE.wysihtmlAllCommands().getText()).setWindow(TOP_WINDOW).inject();
        ScriptInjector.fromString(HtmlEditorResources.INSTANCE.wysihtmlTableEditing().getText()).setWindow(TOP_WINDOW).inject();
        ScriptInjector.fromString(HtmlEditorResources.INSTANCE.wysihtmlToolbar().getText()).setWindow(TOP_WINDOW).inject();
        ScriptInjector.fromString(HtmlEditorResources.INSTANCE.parserRules().getText()).setWindow(TOP_WINDOW).inject();
        PatternFlyBootstrapper.ensurejQueryIsAvailable();
    }
}
