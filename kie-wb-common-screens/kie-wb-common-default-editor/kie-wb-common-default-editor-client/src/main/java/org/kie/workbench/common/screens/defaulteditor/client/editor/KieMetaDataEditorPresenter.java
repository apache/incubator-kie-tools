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
import org.guvnor.common.services.shared.security.AppRoles;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = KieMetaDataEditorPresenter.EDITOR_ID, supportedTypes = {DotResourceType.class}, priority = Integer.MAX_VALUE - 1)
public class KieMetaDataEditorPresenter
        extends KieTextEditorPresenter {

    public static final String EDITOR_ID = "KieMetaFileTextEditor";

    @Inject
    private DotResourceType type;

    @Inject
    private User identity;

    @Inject
    public KieMetaDataEditorPresenter(final KieTextEditorView baseView) {
        super(baseView);
    }

    @OnStartup
    @Override
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        if (!identity.getRoles().contains(new RoleImpl(AppRoles.ADMIN.getName()))) {
            makeReadOnly(place);
        }

        super.onStartup(path,
                        place);
    }

    private void makeReadOnly(final PlaceRequest place) {
        place.getParameters().put("readOnly",
                                  "true");
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
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }
}