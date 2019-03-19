/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.util;

import java.util.Collections;
import java.util.List;

public class ModelReaderUtil {

    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final List<String> RESTRICTED_PROPERTY_NAMES = Collections.singletonList(SERIAL_VERSION_UID);

    public static final String PERSISTENCE_ANNOTATION = "javax.persistence.Id";
    private static final List<String> RESTRICTED_ANNOTATIONS = Collections.singletonList(PERSISTENCE_ANNOTATION);

    public static final String LABEL_ANNOTATION = "org.kie.api.definition.type.Label";
    public static final String LABEL_ANNOTATION_VALUE_PARAM = "value";

    public static boolean isPropertyAllowed(String propertyName) {
        return !RESTRICTED_PROPERTY_NAMES.contains(propertyName);
    }

    public static boolean isAnnotationAllowed(String annotationClassName) {
        return !RESTRICTED_ANNOTATIONS.contains(annotationClassName);
    }
}
