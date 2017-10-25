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

package org.guvnor.asset.management.client.editors.repository.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.structure.security.RepositoryFeatures;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;

@Dependent
public class RepositoryStructureMenu
        implements org.uberfire.workbench.model.menu.Menus {

    private final ProjectContext projectContext;

    private final ConfigureScreenPopupViewImpl configureScreenPopupView;

    public enum MenuItems {
        CONFIGURE_MENU_ITEM
    }

    private final List<MenuItem> items = new ArrayList<MenuItem>();

    private MenuItem configure;

    private boolean configureIsGranted = false;

    private Caller<AssetManagementService> assetManagementServices;

    @Inject
    public RepositoryStructureMenu(final ProjectContext projectContext,
                                   final Caller<AssetManagementService> assetManagementServices,
                                   final ConfigureScreenPopupViewImpl configureScreenPopupView) {
        this.projectContext = projectContext;
        this.assetManagementServices = assetManagementServices;
        this.configureScreenPopupView = configureScreenPopupView;
    }

    public void init(final HasModel<RepositoryStructureModel> hasModel) {
        configure = MenuFactory
                .newTopLevelMenu(Constants.INSTANCE.Configure())
                .withPermission(RepositoryFeatures.CONFIGURE_REPOSITORY)
                .respondsWith(getConfigureCommand(hasModel))
                .endMenu()
                .build().getItems().get(0);

        items.add(configure);

        MenuItem item;
        item = getItem(MenuItems.CONFIGURE_MENU_ITEM);
        configureIsGranted = item != null && item.isEnabled();
    }

    private Command getConfigureCommand(final HasModel<RepositoryStructureModel> hasModel) {
        return new Command() {
            @Override
            public void execute() {
                final RepositoryStructureModel model = hasModel.getModel();

                if (model != null && (model.isSingleProject() || model.isMultiModule())) {
                    configureScreenPopupView.configure(projectContext.getActiveRepository().getAlias(),
                                                       projectContext.getActiveBranch(),
                                                       model.getActivePom().getGav().getVersion(),
                                                       new com.google.gwt.user.client.Command() {
                                                           @Override
                                                           public void execute() {
                                                               configureRepository();
                                                               configureScreenPopupView.hide();
                                                           }
                                                       });
                    configureScreenPopupView.show();
                }
            }
        };
    }

    private void configureRepository() {
        assetManagementServices.call(new RemoteCallback<Long>() {
                                         @Override
                                         public void callback(Long taskId) {
                                             //view.displayNotification( "Repository Configuration Started!" );
                                         }
                                     },
                                     new ErrorCallback<Message>() {
                                         @Override
                                         public boolean error(Message message,
                                                              Throwable throwable) {
                                             ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                                             return true;
                                         }
                                     }).configureRepository(projectContext.getActiveRepository().getAlias(),
                                                            projectContext.getActiveBranch(),
                                                            configureScreenPopupView.getDevBranch(),
                                                            configureScreenPopupView.getReleaseBranch(),
                                                            configureScreenPopupView.getVersion());
    }

    private MenuItem getItem(final MenuItems itemKey) {
        return this.getItemsMap().get(itemKey);
    }

    @Override
    public List<MenuItem> getItems() {
        return items;
    }

    @Override
    public Map<Object, MenuItem> getItemsMap() {

        return new HashMap<Object, MenuItem>() {
            {
                put(MenuItems.CONFIGURE_MENU_ITEM,
                    configure);
            }
        };
    }

    @Override
    public void accept(MenuVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (final MenuItem item : items) {
                item.accept(visitor);
            }
            visitor.visitLeave(this);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void enableAssetsManagementMenu(final boolean enable) {
        enableConfigure(configureIsGranted && enable);
    }

    private void enableConfigure(final boolean enable) {
        configure.setEnabled(enable);
    }
}
