/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.services.shared.project;

import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KieModulePackages {

    private Set<Package> packages;

    private Package defaultPackage;

    public KieModulePackages(@MapsTo("packages") Set<Package> packages, @MapsTo("defaultPackage") Package defaultPackage) {
        this.packages = packages;
        this.defaultPackage = defaultPackage;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public void setPackages(Set<Package> packages) {
        this.packages = packages;
    }

    public Package getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(Package defaultPackage) {
        this.defaultPackage = defaultPackage;
    }
}
