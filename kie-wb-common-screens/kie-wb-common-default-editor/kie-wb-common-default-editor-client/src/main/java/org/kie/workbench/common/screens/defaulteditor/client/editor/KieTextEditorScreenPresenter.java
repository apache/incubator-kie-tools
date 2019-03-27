/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextResourceType;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = KieTextEditorScreenPresenter.EDITOR_ID, supportedTypes = {TextResourceType.class, XmlResourceType.class, PackageNameWhiteListResourceType.class}, priority = 1)
public class KieTextEditorScreenPresenter
        extends KieTextEditorPresenter {

    public static final String EDITOR_ID = "GuvnorTextEditor";

    @Inject
    private TextResourceType typeText;

    @Inject
    private XmlResourceType typeXML;

    @Inject
    private PackageNameWhiteListResourceType typeWhiteList;
    private AceEditorMode mode;

    @Inject
    public KieTextEditorScreenPresenter(final KieTextEditorView baseView) {
        super(baseView);
    }

    @OnStartup
    @Override
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.onStartup(path,
                        place);
        if (typeText.accept(path)) {
            mode = AceEditorMode.TEXT;
        } else if (typeXML.accept(path)) {
            mode = AceEditorMode.XML;
        } else if (typeWhiteList.accept(path)) {
            mode = AceEditorMode.TEXT;
        } else {
            mode = AceEditorMode.TEXT;
        }

        // set xml mode for business processes
        if (path.getFileName().endsWith(".bpmn") || path.getFileName().endsWith(".bpmn2")) {
            mode = AceEditorMode.XML;
        }
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return mode;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }
}
