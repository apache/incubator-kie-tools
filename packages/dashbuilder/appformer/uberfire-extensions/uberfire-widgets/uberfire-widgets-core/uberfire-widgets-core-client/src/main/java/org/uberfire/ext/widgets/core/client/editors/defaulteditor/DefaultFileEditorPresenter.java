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

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchEditor(identifier = "DefaultFileEditor", supportedTypes = {AnyResourceType.class}, priority = Integer.MIN_VALUE)
public class DefaultFileEditorPresenter {

    @Inject
    public DefaultFileEditorView view;
    private Path path;

    @OnStartup
    public void onStartup(final ObservablePath path) {
        this.path = path;
        view.setPath(path);
        view.setIsUpdate(true);
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.DefaultEditor() + " [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    interface View {

        void setPath(Path path);
        
        void setIsUpdate(boolean isUpdate);
    }
}
