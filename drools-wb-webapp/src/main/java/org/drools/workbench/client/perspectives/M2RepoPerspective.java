/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.client.perspectives;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.m2repo.client.event.M2RepoRefreshEvent;
import org.guvnor.m2repo.client.upload.UploadFormPresenter;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.services.shared.resources.PerspectiveIds;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Perspective to show M2_REPO related screen
 */
@Templated
@Dependent
@WorkbenchPerspective(identifier = PerspectiveIds.GUVNOR_M2REPO)
public class M2RepoPerspective implements IsElement {

    @Inject
    private Event<M2RepoRefreshEvent> refreshEvents;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    @DataField
    @WorkbenchPanel(parts = "M2RepoEditor")
    Div m2RepoEditor;

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory.newTopLevelMenu(AppConstants.INSTANCE.Upload())
                                     .respondsWith(() -> {
                                         UploadFormPresenter uploadFormPresenter = iocManager.lookupBean(UploadFormPresenter.class).getInstance();
                                         uploadFormPresenter.showView();
                                     })
                                     .endMenu()
                                     .newTopLevelMenu(AppConstants.INSTANCE.Refresh())
                                     .respondsWith(() -> refreshEvents.fire(new M2RepoRefreshEvent()))
                                     .endMenu()
                                     .build());
    }
}
