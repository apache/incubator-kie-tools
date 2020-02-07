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

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.util.Map;
import java.util.function.Function;

import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.TypeSource;

/**
 * Simple builder for Fact Types
 */
public class SimpleFactBuilder extends BaseFactBuilder {

    public SimpleFactBuilder(final ModuleDataModelOracleBuilder builder,
                             final String factType,
                             final boolean isEvent,
                             final Function<String, TypeSource> typeSourceResolver) {
        super(builder,
              factType,
              false,
              isEvent,
              typeSourceResolver);
    }

    public SimpleFactBuilder addField(final ModelField field) {
        super.addField(field);
        return this;
    }

    @Override
    public void addInternalBuilders(Map<String, FactBuilder> builders) {
    }
}
