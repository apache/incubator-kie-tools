/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.pmmltext.client.editor;

import java.util.function.Consumer;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.pmmltext.client.type.PMMLResourceType;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(
        identifier = PMMLTextEditorPresenter.EDITOR_ID,
        supportedTypes = {PMMLResourceType.class},
        priority = 2)
public class PMMLTextEditorPresenter extends KieTextEditorPresenter {

    public static final String EDITOR_ID = "PMMLEditor";

    private final PMMLResourceType resourceType;

    @Inject
    public PMMLTextEditorPresenter(final KieTextEditorView baseView,
                                   final PMMLResourceType resourceType) {
        super(baseView);
        this.resourceType = resourceType;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        //This causes loadContent() to be called (which for this sub-class loads the Overview not the Text/XML etc)
        super.init(path,
                   place,
                   resourceType);

        //This causes the view's content (Text/XML etc) to be loaded, after which we need to get the original HashCode to support "dirty" content
        view.onStartup(path);
        view.setReadOnly(isReadOnly);
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.XML;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }
}
