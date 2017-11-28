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

package org.dashbuilder.client.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;

import static org.dashbuilder.perspectives.PerspectiveIds.*;

/**
 * This is an example of how to customize some of the permission tree nodes.
 */
@ApplicationScoped
public class PermissionTreeSetup {

    private PerspectiveTreeProvider perspectiveTreeProvider;
    private AppConstants i18n = AppConstants.INSTANCE;
    private ContentManagerI18n cmsI18n;

    public PermissionTreeSetup() {
    }

    @Inject
    public PermissionTreeSetup(PerspectiveTreeProvider perspectiveTreeProvider, ContentManagerI18n cmsI18n) {
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.cmsI18n = cmsI18n;
    }

    public void configureTree() {
        perspectiveTreeProvider.setPerspectiveName(HOME, i18n.menu_home());
        perspectiveTreeProvider.setPerspectiveName(DATA_SETS, i18n.menu_dataset_authoring());
        perspectiveTreeProvider.setPerspectiveName(GALLERY, i18n.menu_gallery());
        perspectiveTreeProvider.setPerspectiveName(CONTENT_MANAGER, i18n.menu_content_manager());
        perspectiveTreeProvider.setPerspectiveName(SECURITY, i18n.menu_security());
        perspectiveTreeProvider.setPerspectiveName(SALES_DASHBOARD, i18n.menu_dashboards_salesdb());
        perspectiveTreeProvider.setPerspectiveName(SALES_REPORTS, i18n.menu_dashboards_salesreports());

        // Exclude some perspectives
        perspectiveTreeProvider.excludePerspectiveId("StandaloneEditorPerspective"); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(APPS); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(PLUGINS); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(AdminPagePerspective.IDENTIFIER); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(PreferencesCentralPerspective.IDENTIFIER); /* uberfire */

        // Rename perspective to dashboard in CMS
        //perspectiveTreeProvider.setResourceName(cmsI18n.capitalizeFirst(cmsI18n.getPerspectiveResourceName()));
    }
}