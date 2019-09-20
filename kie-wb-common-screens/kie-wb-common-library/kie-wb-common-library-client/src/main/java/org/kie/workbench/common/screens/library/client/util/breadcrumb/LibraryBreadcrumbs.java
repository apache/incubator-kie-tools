/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util.breadcrumb;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;

import static org.kie.workbench.common.screens.library.client.util.LibraryPlaces.LIBRARY_PERSPECTIVE;

@Dependent
public class LibraryBreadcrumbs {

    private LibraryPlaces libraryPlaces;

    private UberfireBreadcrumbs breadcrumbs;

    private TranslationUtils translationUtils;

    private TranslationService ts;

    private ResourceUtils resourceUtils;

    private ProjectBranchBreadcrumb projectBranchBreadcrumb;

    @Inject
    public LibraryBreadcrumbs(UberfireBreadcrumbs breadcrumbs,
                              TranslationUtils translationUtils,
                              TranslationService ts,
                              ResourceUtils resourceUtils,
                              ProjectBranchBreadcrumb projectBranchBreadcrumb) {
        this.breadcrumbs = breadcrumbs;
        this.translationUtils = translationUtils;
        this.ts = ts;
        this.resourceUtils = resourceUtils;
        this.projectBranchBreadcrumb = projectBranchBreadcrumb;
    }

    public void init(final LibraryPlaces libraryPlaces) {
        this.libraryPlaces = libraryPlaces;
    }

    public void clear() {
        breadcrumbs.clearBreadcrumbs(LIBRARY_PERSPECTIVE);
    }

    // Spaces
    public void setupForSpacesScreen() {
        clear();
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  translationUtils.getOrganizationalUnitAliasInPlural(),
                                  libraryPlaces::goToOrganizationalUnits);
    }

    // Spaces -> {spaceName}
    public void setupForSpace(final OrganizationalUnit space) {
        setupForSpacesScreen();
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  space.getName(),
                                  libraryPlaces::goToLibrary);
    }

    // Spaces -> {spaceName} -> {projectName} -> {branchName}
    public void setupForProject(final WorkspaceProject project) {

        setupForSpace(project.getOrganizationalUnit());

        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  project.getName(),
                                  () -> libraryPlaces.goToProject(libraryPlaces.getActiveWorkspace()));

        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  projectBranchBreadcrumb.setup(project.getRepository().getBranches()));
    }

    // Spaces -> {spaceName} -> Try Samples
    public void setupForTrySamples(final OrganizationalUnit space) {
        setupForSpace(space);
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.TrySamples),
                                  libraryPlaces::goToTrySamples);
    }

    // Spaces -> {spaceName} -> {projectName} -> {branchName} -> {assetName}
    public void setupForAsset(final WorkspaceProject project, final Path path) {
        setupForProject(project);
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  resourceUtils.getBaseFileName(path),
                                  () -> libraryPlaces.goToAsset(path));
    }

    // Spaces -> {spaceName} -> {projectName} -> {branchName} -> Submit Change Request
    public void setupForSubmitChangeRequest(final WorkspaceProject project) {
        setupForProject(project);
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  ts.getTranslation(LibraryConstants.SubmitChangeRequest),
                                  () -> libraryPlaces.goToSubmitChangeRequestScreen());
    }

    // Spaces -> {spaceName} -> {projectName} -> {branchName} -> Change Request ({#id})
    public void setupForChangeRequestReview(final WorkspaceProject project,
                                            final long changeRequestId) {
        setupForProject(project);
        breadcrumbs.addBreadCrumb(LIBRARY_PERSPECTIVE,
                                  ts.format(LibraryConstants.ChangeRequestAndId, changeRequestId),
                                  () -> libraryPlaces.goToChangeRequestReviewScreen(changeRequestId));
    }
}