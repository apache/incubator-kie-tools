/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.widgets.menu.megamenu;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.AbstractWorkbenchMenuProducer;
import org.uberfire.client.workbench.widgets.menu.megamenu.brand.MegaMenuBrand;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class WorkbenchMegaMenuProducer extends AbstractWorkbenchMenuProducer<WorkbenchMegaMenuPresenter, WorkbenchMegaMenuPresenter.View> {

    private ManagedInstance<MegaMenuBrand> megaMenuBrands;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;
    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;
    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;
    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;
    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;
    private Workbench workbench;

    public WorkbenchMegaMenuProducer() {
        //CDI proxy
    }

    @Inject
    public WorkbenchMegaMenuProducer(final AuthorizationManager authzManager,
                                     final PerspectiveManager perspectiveManager,
                                     final ActivityManager activityManager,
                                     final User identity,
                                     final WorkbenchMegaMenuPresenter.View view,
                                     final ManagedInstance<MegaMenuBrand> megaMenuBrands,
                                     final PlaceManager placeManager,
                                     final AuthorizationManager authorizationManager,
                                     final SessionInfo sessionInfo,
                                     final ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters,
                                     final ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters,
                                     final ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters,
                                     final ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters,
                                     final Workbench workbench,
                                     final ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager) {
        super(authzManager, perspectiveManager, placeManager, activityManager, identity, experimentalActivitiesAuthorizationManager, view);
        this.authorizationManager = authorizationManager;
        this.megaMenuBrands = megaMenuBrands;
        this.placeManager = placeManager;
        this.sessionInfo = sessionInfo;
        this.childMenuItemPresenters = childMenuItemPresenters;
        this.groupMenuItemPresenters = groupMenuItemPresenters;
        this.childContextMenuItemPresenters = childContextMenuItemPresenters;
        this.groupContextMenuItemPresenters = groupContextMenuItemPresenters;
        this.workbench = workbench;
        this.experimentalActivitiesAuthorizationManager = experimentalActivitiesAuthorizationManager;
    }


    @Produces
    public WorkbenchMegaMenuPresenter getInstance() {
        return getWorbenchMenu();
    }

    @Override
    protected WorkbenchMegaMenuPresenter makeDefaultPresenter() {
        return new WorkbenchMegaMenuPresenter(authzManager,
                                              perspectiveManager,
                                              activityManager,
                                              identity,
                                              view,
                                              megaMenuBrands,
                                              placeManager,
                                              authorizationManager,
                                              sessionInfo,
                                              childMenuItemPresenters,
                                              groupMenuItemPresenters,
                                              childContextMenuItemPresenters,
                                              groupContextMenuItemPresenters,
                                              workbench,
                                              experimentalActivitiesAuthorizationManager);
    }

    @Override
    protected WorkbenchMegaMenuStandalonePresenter makeStandalonePresenter() {
        return new WorkbenchMegaMenuStandalonePresenter(authzManager,
                                                        perspectiveManager,
                                                        activityManager,
                                                        identity,
                                                        view,
                                                        megaMenuBrands,
                                                        placeManager,
                                                        authorizationManager,
                                                        sessionInfo,
                                                        childMenuItemPresenters,
                                                        groupMenuItemPresenters,
                                                        childContextMenuItemPresenters,
                                                        groupContextMenuItemPresenters,
                                                        workbench,
                                                        experimentalActivitiesAuthorizationManager);
    }
}
