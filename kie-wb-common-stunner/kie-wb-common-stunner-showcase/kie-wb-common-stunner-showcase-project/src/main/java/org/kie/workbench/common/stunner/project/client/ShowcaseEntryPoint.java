/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.project.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;
import org.kie.workbench.common.stunner.project.shared.ShowcaseService;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.ActivityBeansCache;

@EntryPoint
public class ShowcaseEntryPoint extends DefaultWorkbenchEntryPoint {

    private MenuDevCommandsBuilder menuDevCommandsBuilder;
    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;
    protected Caller<ShowcaseService> showcaseService;
    protected Event<WorkspaceProjectContextChangeEvent> contextChangeEvent;

    @Inject
    public ShowcaseEntryPoint(final Caller<AppConfigService> appConfigService,
                              final ActivityBeansCache activityBeansCache,
                              final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                              final MenuDevCommandsBuilder menuDevCommandsBuilder,
                              final Caller<ShowcaseService> showcaseService,
                              final Event<WorkspaceProjectContextChangeEvent> contextChangeEvent) {
        super(appConfigService,
              activityBeansCache);
        this.menusHelper = menusHelper;
        this.menuDevCommandsBuilder = menuDevCommandsBuilder;
        this.showcaseService = showcaseService;
        this.contextChangeEvent = contextChangeEvent;
    }

    @PostConstruct
    public void init() {
        menuDevCommandsBuilder.enable();
        onApplicationStart();
    }

    @Override
    protected void setupMenu() {
        menusHelper.addUserMenuItems();
    }

    protected void onApplicationStart() {
        showcaseService.call((WorkspaceProject project) -> {
            if (project != null) {
                contextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(project));
            } else {
                Window.alert("No project was found");
            }
        }).getShowcaseProject();
    }
}

