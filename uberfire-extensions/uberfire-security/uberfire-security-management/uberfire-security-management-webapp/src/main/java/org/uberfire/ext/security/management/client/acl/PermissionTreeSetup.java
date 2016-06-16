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

package org.uberfire.ext.security.management.client.acl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.authz.PerspectiveTreeProvider;

/**
 * This is an example of how to customize some of the permission tree nodes.
 */
@ApplicationScoped
public class PermissionTreeSetup {

    private PerspectiveTreeProvider perspectiveTreeProvider;
    private GeneralTreeProvider generalTreeProvider;
    private UIAssetsTreeProvider uiAssetsTreeProvider;

    public PermissionTreeSetup() {
    }

    @Inject
    public PermissionTreeSetup(PerspectiveTreeProvider perspectiveTreeProvider, GeneralTreeProvider generalTreeProvider, UIAssetsTreeProvider uiAssetsTreeProvider) {
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.generalTreeProvider = generalTreeProvider;
        this.uiAssetsTreeProvider = uiAssetsTreeProvider;
    }

    public void configureTree() {
        generalTreeProvider.setRootNodePosition(1);
        generalTreeProvider.setRootNodeName("General");
        generalTreeProvider.setActive(false);

        uiAssetsTreeProvider.setRootNodePosition(2);
        uiAssetsTreeProvider.setRootNodeName("UI Assets");
        uiAssetsTreeProvider.setActive(false);

        perspectiveTreeProvider.setRootNodePosition(2);
        perspectiveTreeProvider.setRootNodeName("Perspectives");
        perspectiveTreeProvider.setResourceName("Perspective");
        perspectiveTreeProvider.setPerspectiveName("HomePerspective", "Home");
        perspectiveTreeProvider.setPerspectiveName("SecurityManagementPerspective", "Security Management");
        perspectiveTreeProvider.setPerspectiveName("PlugInAuthoringPerspective", "Plugin Authoring");
        perspectiveTreeProvider.setPerspectiveName("AppsPerspective", "Apps");
        perspectiveTreeProvider.excludePerspectiveId("StandaloneEditorPerspective");
        perspectiveTreeProvider.setActive(true);
    }
}