/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.CONTENT_MANAGEMENT;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.PROVISIONING;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SERVER_MANAGEMENT;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

@ApplicationScoped
public class HomeProducer implements HomeModelProvider {

    @Inject
    private PlaceManager placeManager;

    @Override
    public void initialize(Runnable done) {
        done.run();
    }

    public HomeModel get(ProfilePreferences profilePreferences) {
        final HomeModel model = new HomeModel("Welcome to KIE Workbench",
                                              "KIE Workbench offers a set of flexible tools, that support the way you need to work. Select a tool below to get started.",
                                              "images/home_bg.jpg");

        final HomeShortcut design = ModelUtils.makeShortcut("pficon pficon-blueprint",
                                                            "Design",
                                                            "Create and modify {0} and {1}.",
                                                            () -> placeManager.goTo(LIBRARY),
                                                            LIBRARY,
                                                            PERSPECTIVE);
        design.addLink(new HomeShortcutLink("projects",
                                            LIBRARY));
        design.addLink(new HomeShortcutLink("pages",
                                            CONTENT_MANAGEMENT));

        final HomeShortcut devOps = ModelUtils.makeShortcut("fa fa-gears",
                                                            "DevOps",
                                                            "Administer {0} and {1}.",
                                                            () -> placeManager.goTo(SERVER_MANAGEMENT),
                                                            SERVER_MANAGEMENT,
                                                            PERSPECTIVE);
        devOps.addLink(new HomeShortcutLink("provisioning",
                                            PROVISIONING));
        devOps.addLink(new HomeShortcutLink("servers",
                                            SERVER_MANAGEMENT));

        model.addShortcut(design);
        model.addShortcut(devOps);

        return model;
    }
}
