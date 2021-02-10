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

package org.uberfire.ext.widgets.core.client.editors.texteditor;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.SetContent;

@Dependent
@WorkbenchClientEditor(identifier = "TextEditor")
public class TextEditorWorkbenchEditor
        extends TextEditorPresenter {

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @SetContent
    public Promise setContent(final String path, final String value) {
        return Promise.resolve("");
    }

    @GetContent
    public Promise getContent() {
        return Promise.resolve("");
    }
}
