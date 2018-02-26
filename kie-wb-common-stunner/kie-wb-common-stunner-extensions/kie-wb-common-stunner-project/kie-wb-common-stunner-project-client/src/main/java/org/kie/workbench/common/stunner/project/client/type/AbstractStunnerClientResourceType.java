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
package org.kie.workbench.common.stunner.project.client.type;

import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.category.Category;

public abstract class AbstractStunnerClientResourceType<R extends DefinitionSetResourceType> implements ClientResourceType {

    private final R definitionSetResourceType;
    private final Category category;

    protected AbstractStunnerClientResourceType(final R definitionSetResourceType,
                                                final
                                                Category category) {
        this.definitionSetResourceType = definitionSetResourceType;
        this.category = category;
    }

    @Override
    public String getShortName() {
        return getDefinitionSetResourceType().getShortName();
    }

    @Override
    public String getDescription() {
        return getDefinitionSetResourceType().getDescription();
    }

    @Override
    public String getPrefix() {
        return getDefinitionSetResourceType().getPrefix();
    }

    @Override
    public String getSuffix() {
        return getDefinitionSetResourceType().getSuffix();
    }

    @Override
    public int getPriority() {
        return getDefinitionSetResourceType().getPriority();
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getDefinitionSetResourceType().getSimpleWildcardPattern();
    }

    @Override
    public boolean accept(final Path path) {
        return getDefinitionSetResourceType().accept(path);
    }

    private R getDefinitionSetResourceType() {
        return definitionSetResourceType;
    }

    @Override
    public Category getCategory() {
        return category;
    }
}
