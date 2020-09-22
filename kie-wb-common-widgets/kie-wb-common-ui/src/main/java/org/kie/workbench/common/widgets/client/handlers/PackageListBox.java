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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.services.shared.project.KieModulePackages;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.PackageItem;
import org.uberfire.mvp.Command;

/**
 * A ListBox that shows a list of Packages from which the user can select
 */
@Dependent
public class PackageListBox
        implements IsElement {

    private PackageListBoxView view;
    private WorkspaceProjectContext projectContext;
    protected Caller<KieModuleService> moduleService;
    private Map<String, PackageItem> packages;
    private Package selectedPackage = null;

    @Inject
    public PackageListBox(final PackageListBoxView view,
                          final WorkspaceProjectContext projectContext,
                          final Caller<KieModuleService> moduleService) {
        this.view = view;
        this.projectContext = projectContext;
        this.moduleService = moduleService;
        packages = new HashMap<>();
        view.setPresenter(this);
    }

    public void setUp(final boolean includeDefaultPackage) {
        setUp(includeDefaultPackage,
              null);
    }

    public void setUp(final boolean includeDefaultPackage,
                      final Command packagesLoadedCommand) {

        packages.clear();

        showListOfPackages(includeDefaultPackage,
                           packagesLoadedCommand);
    }

    public void clearSelectElement() {
        view.clearSelectElement();
    }

    private void showListOfPackages(final boolean includeDefaultPackage,
                                    final Command packagesLoadedCommand) {
        final Module activeModule = projectContext.getActiveModule().orElse(null);
        if (activeModule != null) {
            moduleService.call((KieModulePackages kieModulePackages) -> {
                //Sort by caption
                final List<PackageItem> sortedPackages = getSortedPackages(includeDefaultPackage,
                                                                           kieModulePackages.getPackages());

                // Disable and set default content if no Packages available
                if (sortedPackages.isEmpty()) {
                    return;
                }
                addPackagesToSelect(sortedPackages,
                                    kieModulePackages.getDefaultPackage());

                if (packagesLoadedCommand != null) {
                    packagesLoadedCommand.execute();
                }
            }).resolveModulePackages(activeModule);
        }
    }

    private List<PackageItem> getSortedPackages(final boolean includeDefaultPackage,
                                                final Set<PackageItem> pkgs) {
        final List<PackageItem> sortedPackages = new ArrayList<>(pkgs);
        Collections.sort(sortedPackages,
                         (p1, p2) -> p1.getCaption().compareTo(p2.getCaption()));

        // Remove default package, if not required (after sorting it is guaranteed to be at index 0)

        if (!includeDefaultPackage
                && !sortedPackages.isEmpty()
                && "".equals(sortedPackages.get(0).getPackageName())) {
            sortedPackages.remove(0);
        }
        return sortedPackages;
    }

    private void addPackagesToSelect(final List<PackageItem> sortedPackages,
                                     final Package activePackage) {

        final Map<String, String> packageNames = new HashMap<>();

        for (PackageItem pkg : sortedPackages) {
            packageNames.put(pkg.getCaption(),
                             pkg.getPackageName());
            packages.put(pkg.getCaption(),
                         pkg);
        }

        setSelectedPackage(activePackage);

        String selectedPackageName = resolve(getSelectedPackageName(activePackage,
                                                                    packageNames));

        view.setUp(selectedPackageName,
                   packageNames);
    }

    private String getSelectedPackageName(final Package activePackage,
                                          final Map<String, String> packageNames) {
        if (packageNames.containsKey(activePackage.getCaption())) {

            return activePackage.getCaption();
        } else if (!packageNames.isEmpty()) {
            final String next = packageNames.keySet().iterator().next();
            setSelectedPackage(next);
            return next;
        } else {
            return null;
        }
    }

    private void setSelectedPackage(final String selectedPackage) {
        final Module activeModule = projectContext.getActiveModule().orElse(null);
        if (activeModule == null) {
            return;
        } else {
            moduleService
                    .call((RemoteCallback<Package>) this::setSelectedPackage)
                    .resolvePackage(activeModule,
                                    resolve(selectedPackage));
        }
    }

    private void setSelectedPackage(final Package selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public Package getSelectedPackage() {
        return selectedPackage;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void onPackageSelected(final String selectedPackage) {

        setSelectedPackage(resolve(selectedPackage));
    }

    private String resolve(final String selectedPackage) {
        if (Objects.equals(PackageItem.DEFAULT_PACKAGE_NAME, selectedPackage)) {
            return "";
        } else {
            return selectedPackage;
        }
    }
}
