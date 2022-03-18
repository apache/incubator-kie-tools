/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.service.shared.impl.validation;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FieldConstraint {

    private String annotationType;

    private Map<String, Object> params = new HashMap();

    public FieldConstraint(@MapsTo("annotationType") String annotationType,
                           @MapsTo("params") Map<String, Object> params) {
        this.annotationType = annotationType;
        this.params = params;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
