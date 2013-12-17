/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.categories.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.categories.client.resources.i18n.Constants;
import org.drools.workbench.screens.categories.client.type.CategoryDefinitionResourceType;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

/**
 *
 */
@Dependent
@WorkbenchEditor(identifier = "CategoryFileManager", supportedTypes = {CategoryDefinitionResourceType.class})
public class CategoriesEditorPresenter
        extends CategoriesEditorBasePresenter {

    @Inject
    private Caller<CategoriesService> categoryService;

    @Inject
    private CategoryDefinitionResourceType type;

    @OnStartup
    public void onStartup(final Path path,
            final PlaceRequest place ) {
        this.path = path;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;

        if (isReadOnly) {
            makeRestoreMenuBar();
        } else {
            makeMenuBar();
        }

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        categoryService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(path);
    }

    private RemoteCallback<Categories> getModelSuccessCallback() {
        return new RemoteCallback<Categories>() {

            @Override
            public void callback(final Categories content) {
                multiPage.clear();
                multiPage.addWidget(view,
                        CommonConstants.INSTANCE.EditTabTitle());

                addMetadataPage();

                view.setContent(content);
                view.hideBusyIndicator();

            }
        };
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if (isDirty()) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.CategoriesEditor() + " [" + FileNameUtil.removeExtension(path,
                type) + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
