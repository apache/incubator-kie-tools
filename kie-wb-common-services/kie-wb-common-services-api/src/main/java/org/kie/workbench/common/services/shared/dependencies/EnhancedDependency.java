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

package org.kie.workbench.common.services.shared.dependencies;


import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;

public abstract class EnhancedDependency {

    protected Dependency dependency;

    protected final Set<String> packageNames = new HashSet<>();

    public EnhancedDependency() {
    }

    public EnhancedDependency( final Dependency dependency,
                               final Set<String> packageNames ) {
        this.dependency = dependency;
        this.packageNames.addAll( packageNames );
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Set<String> getPackages() {
        return packageNames;
    }
}
