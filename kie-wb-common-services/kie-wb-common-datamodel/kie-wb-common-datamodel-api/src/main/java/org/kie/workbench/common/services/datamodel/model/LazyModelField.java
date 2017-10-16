/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;

/**
 * A sub-class to indicate that the true ModelFields are yet to be loaded
 */
@Portable
public class LazyModelField extends ModelField {

    public LazyModelField() {
        super();
    }

    /**
     * Creates a new ModelField instance
     *
     * @param name                 field's name
     * @param clazz                the class of the field. For fields defined as a type declaration
     * @param fieldClassType       tells if this is a field for a regular POJO class or for a object type declaration
     *                             this clazz should be null.
     * @param fieldOrigin          gives info about this field's origin
     * @param accessorsAndMutators Whether the field has an Accessor, Mutator or both
     * @param type                 the generic type of the clazz (from ClassToGenericClassConverter).
     */
    public LazyModelField(final String name,
                          final String clazz,
                          final FIELD_CLASS_TYPE fieldClassType,
                          final FIELD_ORIGIN fieldOrigin,
                          final FieldAccessorsAndMutators accessorsAndMutators,
                          final String type) {
        super(name,
              clazz,
              fieldClassType,
              fieldOrigin,
              accessorsAndMutators,
              type);
    }
}
