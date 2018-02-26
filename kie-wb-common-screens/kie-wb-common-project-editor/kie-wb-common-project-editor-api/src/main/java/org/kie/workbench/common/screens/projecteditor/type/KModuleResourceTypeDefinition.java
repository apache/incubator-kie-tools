/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.categories.Model;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class KModuleResourceTypeDefinition
        implements ResourceTypeDefinition {

    private Category category;

    public KModuleResourceTypeDefinition() {
    }

    @Inject
    public KModuleResourceTypeDefinition(final Others category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public String getShortName() {
        return "kmodule xml config";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "kmodule";
    }

    @Override
    public String getSuffix() {
        return "xml";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getPrefix() + "." + getSuffix();
    }

    @Override
    public boolean accept(final Path path) {
        return path.getFileName().equals(getPrefix() + "." + getSuffix());
    }
}
