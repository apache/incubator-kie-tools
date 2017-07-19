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

package org.kie.workbench.common.forms.editor.model.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.editor.model.TypeConflict;
import org.kie.workbench.common.forms.model.TypeInfo;

@Portable
public class TypeConflictImpl implements TypeConflict {

    private String propertyName;

    private TypeInfo before;

    private TypeInfo now;

    public TypeConflictImpl(@MapsTo("propertyName") String propertyName,
                            @MapsTo("before") TypeInfo before,
                            @MapsTo("now") TypeInfo now) {
        this.propertyName = propertyName;
        this.before = before;
        this.now = now;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public TypeInfo getBefore() {
        return before;
    }

    @Override
    public TypeInfo getNow() {
        return now;
    }
}
