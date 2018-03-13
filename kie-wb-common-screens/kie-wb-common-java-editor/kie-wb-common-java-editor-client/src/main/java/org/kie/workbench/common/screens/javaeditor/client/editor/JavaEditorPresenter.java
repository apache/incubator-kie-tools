/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.javaeditor.client.editor;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.javaeditor.client.type.JavaResourceType;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "JavaEditor", supportedTypes = {JavaResourceType.class})
public class JavaEditorPresenter
        extends KieEditor<String> {

    @Inject
    private Caller<VFSService> vfsServices;

    private JavaSourceView view;

    @Inject
    private JavaResourceType type;

    @Inject
    public JavaEditorPresenter(JavaSourceView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void init(final Path path,
                     final PlaceRequest place) {
        init(path,
             place);
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    protected void loadContent() {
        vfsServices.call(new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                if (response == null) {
                    view.setContent("-- empty --");
                } else {
                    view.setContent(response);
                }
            }
        }).readAllString(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void makeMenuBar() {
        fileMenuBuilder.addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

}
