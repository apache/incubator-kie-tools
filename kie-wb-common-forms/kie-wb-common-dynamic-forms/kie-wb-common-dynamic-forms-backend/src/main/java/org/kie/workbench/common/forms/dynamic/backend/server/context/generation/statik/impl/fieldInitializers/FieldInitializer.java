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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.fieldInitializers;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.DMOBasedTransformerContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.model.FieldDefinition;

public interface FieldInitializer<T extends FieldDefinition> {
    public boolean supports( FieldDefinition field );

    public void initializeField( T field, FieldSetting setting, DMOBasedTransformerContext context );
}
