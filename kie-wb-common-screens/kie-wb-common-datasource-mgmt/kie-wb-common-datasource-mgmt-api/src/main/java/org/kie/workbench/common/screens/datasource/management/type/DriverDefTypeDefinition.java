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

package org.kie.workbench.common.screens.datasource.management.type;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class DriverDefTypeDefinition
        implements ResourceTypeDefinition {

    private Category category;

    public DriverDefTypeDefinition() {
    }

    public DriverDefTypeDefinition(final Others category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public String getShortName() {
        return "Driver";
    }

    @Override
    public String getDescription() {
        return "Driver Definition";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "driver";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*.driver";
    }

    @Override
    public boolean accept(Path path) {
        return path.getFileName().endsWith(getPrefix() + "." + getSuffix());
    }
}
