/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

public abstract class Expression extends DMNElement {

    @Property
    @FormField(afterElement = "description")
    protected QName typeRef;

    public Expression() {
    }

    public Expression(final Id id,
                      final Description description,
                      final QName typeRef) {
        super(id,
              description);
        this.typeRef = typeRef;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public QName getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(final QName typeRef) {
        this.typeRef = typeRef;
    }
}
